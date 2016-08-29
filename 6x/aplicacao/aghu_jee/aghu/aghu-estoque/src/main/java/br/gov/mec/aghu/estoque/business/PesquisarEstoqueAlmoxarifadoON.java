package br.gov.mec.aghu.estoque.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.vo.EstoqueAlmoxarifadoVO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO.NotaRecebimentoProvisorio;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class PesquisarEstoqueAlmoxarifadoON extends BaseBusiness implements Serializable {

	@EJB
	private PesquisarEstoqueAlmoxarifadoRN pesquisarEstoqueAlmoxarifadoRN;

	private static final Log LOG = LogFactory.getLog(PesquisarEstoqueAlmoxarifadoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;

	@Inject
	private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;

	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 5155681270598207008L;

	/**
	 * Obtem uma lista de VO's de estoque almoxarifado
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param estoqueAlmox
	 * @return
	 * @author bruno.mourao
	 * @throws ApplicationBusinessException
	 * @since 12/11/2011
	 */
	public List<EstoqueAlmoxarifadoVO> pesquisarEstoqueAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codMaterial, Integer numeroFrn, Short seqAlmox, DominioSituacao situacao, Boolean estocavel, Integer seq,
			String codigoUnidadeMedida, Integer codigoGrupoMaterial, String termoLivre, VScoClasMaterial classificacaoMaterial)
			throws ApplicationBusinessException {

		List<SceEstoqueAlmoxarifado> estoquesAlmoxarifado = getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifado(firstResult,
				maxResult, orderProperty, asc, codMaterial, numeroFrn, seqAlmox, situacao, estocavel, seq, codigoUnidadeMedida,
				codigoGrupoMaterial, termoLivre, classificacaoMaterial);

		List<EstoqueAlmoxarifadoVO> result = new ArrayList<EstoqueAlmoxarifadoVO>();

		for (SceEstoqueAlmoxarifado estoque : estoquesAlmoxarifado) {

			EstoqueAlmoxarifadoVO estoqueVO = new EstoqueAlmoxarifadoVO();
			estoqueVO.setAlmoxarifado(estoque.getAlmoxarifado());
			estoqueVO.setEstocavel(estoque.getIndEstocavel());
			estoqueVO.setFornecedor(estoque.getFornecedor());
			estoqueVO.setMaterial(estoque.getMaterial());
			estoqueVO.setQtdBloq(estoque.getQtdeBloqueada());
			if (estoque.getQtdeBloqConsumo() == null) {
				estoqueVO.setQtdBloqConsumo(0);
			} else {
				estoqueVO.setQtdBloqConsumo(estoque.getQtdeBloqConsumo());
			}
			estoqueVO.setQtdBloqTransf(estoque.getQtdeBloqEntrTransf());
			estoqueVO.setQtdDisp(estoque.getQtdeDisponivel());
			estoqueVO.setQtdEstqMax(estoque.getQtdeEstqMax());
			estoqueVO.setQtdEstqMin(estoque.getQtdeEstqMin());
			Integer qtdProblema = getPesquisarEstoqueAlmoxarifadoRN().calculaQtdeProblema(estoque.getSeq());
			estoqueVO.setQtdProb(qtdProblema);
			estoqueVO.setQtdPtPedido(estoque.getQtdePontoPedido());

			// Quantidade Recebimento Provisório
			Integer fornPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO).getVlrNumerico()
					.intValue();

			if (fornPadrao.equals(estoque.getFornecedor().getNumero())
					&& estoque.getAlmoxarifado().equals(estoque.getMaterial().getAlmoxarifado())) {
				QtdeRpVO qtdeRp = new QtdeRpVO();

				Long qtde = getSceItemRecebProvisorioDAO().somarQtdeItensNotaRecebProvisorio(estoque);

				qtdeRp.setQuantidade(qtde != null ?  qtde :  Long.valueOf("0"));

				qtdeRp.setNotasRecebimento(getSceItemRecebProvisorioDAO()
						.pesquisarNotasRecebimentoProvisorio(estoque, QtdeRpVO.MAX_RPS + 1));

				if (qtdeRp.getQuantidade() > 0 || (qtdeRp.getNotasRecebimento() != null && !qtdeRp.getNotasRecebimento().isEmpty())) {
					qtdeRp.setMostrarAlerta(Boolean.TRUE);
				} else {
					qtdeRp.setMostrarAlerta(Boolean.FALSE);
				}

				for (NotaRecebimentoProvisorio notaRP : qtdeRp.getNotasRecebimento()) {

					if (notaRP.getDocumentoFiscalEntrada() != null) {
						notaRP.setFornecedor(notaRP.getDocumentoFiscalEntrada().getFornecedor());
					}
				}
				estoqueVO.setQtdeRp(qtdeRp);
			} else {
				QtdeRpVO qtdeRp = new QtdeRpVO();
				qtdeRp.setQuantidade(Long.valueOf("0"));
				estoqueVO.setQtdeRp(qtdeRp);
			}

			estoqueVO.setTempoReposicao(estoque.getTempoReposicao());

			if (estoque.getServidor() != null) {
				estoque.getServidor().getPessoaFisica().getNome();
			}

			if (estoque.getServidorAlterado() != null) {
				estoque.getServidorAlterado().getPessoaFisica().getNome();
			}

			if (estoque.getServidorDesativado() != null) {
				estoque.getServidorDesativado().getPessoaFisica().getNome();
			}

			estoqueVO.setEstoque(estoque);
			result.add(estoqueVO);
		}

		return result;
	}

	private PesquisarEstoqueAlmoxarifadoRN getPesquisarEstoqueAlmoxarifadoRN() {
		return pesquisarEstoqueAlmoxarifadoRN;
	}

	private SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}

	private SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO() {
		return sceHistoricoProblemaMaterialDAO;
	}

	public Integer recupararQuantidadeDisponivelTodosEstoques(EstoqueAlmoxarifadoVO estoque) {
		if (estoque.getQuantidadeDisponivelTodosEstoques() == null) {
			Integer quantidade = 0;
			quantidade += getSceHistoricoProblemaMaterialDAO().obterQtdeProblemaPorCodMaterial(estoque.getMaterial().getCodigo(), null,
					null);
			quantidade += getSceEstoqueAlmoxarifadoDAO().obterQtdeDisponivelQtdeBloqueadaTodosEstoquesPorCodigoMaterial(
					estoque.getMaterial().getCodigo());
			estoque.setQuantidadeDisponivelTodosEstoques(quantidade);
		}
		return estoque.getQuantidadeDisponivelTodosEstoques();
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected SceItemRecebProvisorioDAO getSceItemRecebProvisorioDAO() {
		return sceItemRecebProvisorioDAO;
	}
}