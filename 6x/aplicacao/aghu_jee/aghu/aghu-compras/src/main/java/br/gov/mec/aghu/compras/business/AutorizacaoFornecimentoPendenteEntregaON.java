package br.gov.mec.aghu.compras.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.vo.ScoDataEnvioFornecedorVO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoCompraServicoDAO;
import br.gov.mec.aghu.compras.vo.ParcelaAfPendenteEntregaVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AutorizacaoFornecimentoPendenteEntregaON extends BaseBusiness {

	private static final String _00FF7F = "#00FF7F";
	private static final String FF6347 = "#FF6347";
	private static final String FFE347 = "#ffe347";
	private static final long serialVersionUID = -1089855688544299589L;
	private static final Log LOG = LogFactory.getLog(AutorizacaoFornecimentoPendenteEntregaON.class);

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private ScoSolicitacaoCompraServicoDAO scoSolicitacaoCompraServicoDAO;

	public enum AutorizacaoFornecimentoPendenteEntregaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_INFORMAR_FORNECEDOR, MENSAGEM_ERRO_PERIODO_INCORRETO
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public void validarCamposParaPesquisaParcelasAfPendenteEntrega(ParcelasAFPendEntVO autorizacaoVO) throws ApplicationBusinessException {
		if (autorizacaoVO.getNumeroAF() == null && autorizacaoVO.getNumeroAFP() == null && autorizacaoVO.getComplemento() == null
				&& autorizacaoVO.getFornecedor() == null) {
			throw new ApplicationBusinessException(AutorizacaoFornecimentoPendenteEntregaONExceptionCode.MENSAGEM_ERRO_INFORMAR_FORNECEDOR);
		}
	}

	public void validarPeriodoInformadoParcelasAfPendenteEntrega(ParcelasAFPendEntVO vo) throws ApplicationBusinessException {
		if (vo.getPeriodoEntregaInicio() != null && vo.getPeriodoEntregaFim() != null
				&& DateUtil.validaDataMaiorIgual(vo.getPeriodoEntregaInicio(), vo.getPeriodoEntregaFim())) {
			throw new ApplicationBusinessException(AutorizacaoFornecimentoPendenteEntregaONExceptionCode.MENSAGEM_ERRO_PERIODO_INCORRETO);
		}
	}

	public List<ParcelaAfPendenteEntregaVO> pesquisarParcelasPendentesEntrega(ParcelasAFPendEntVO parcelaAfPendEnt) {

		List<ParcelaAfPendenteEntregaVO> listaResultados = getScoScolicitacaoCompraServicoDAO().consultarParcelasAfPendentesEntrega(
				parcelaAfPendEnt.getNumeroAF(), parcelaAfPendEnt.getComplemento(), parcelaAfPendEnt.getNumeroAFP(),
				getCodigoGrupoMaterial(parcelaAfPendEnt), getCodigoMaterial(parcelaAfPendEnt), getNumeroFornecedor(parcelaAfPendEnt),
				parcelaAfPendEnt.getPeriodoEntregaInicio(), parcelaAfPendEnt.getPeriodoEntregaFim(), parcelaAfPendEnt.getEmpenhada(),
				parcelaAfPendEnt.getEntregaAtrasada(), parcelaAfPendEnt.getRecebido());

		for (ParcelaAfPendenteEntregaVO vo : listaResultados) {

			if (vo.getContagemTemRecebimento() != null && vo.getContagemTemRecebimento() > 0) {
				vo.setCorPrevisaoEntrega(FFE347);
			}

			validarCoresPrevisaoEntrega(vo);

			if (vo.getPrevisaoEntrega() != null && DateUtil.validaDataMenor(vo.getPrevisaoEntrega(), new Date())
					&& vo.getQuantidade() != null && vo.getQuantidadeEntregue() < vo.getQuantidade() && "N".equals(vo.getIndCancelada())) {
				vo.setCorPrevisaoEntrega(FF6347);
			}
		}

		return listaResultados;
	}

	private Integer getNumeroFornecedor(ParcelasAFPendEntVO parcelaAfPendEnt) {
		return parcelaAfPendEnt.getFornecedor() != null ? parcelaAfPendEnt.getFornecedor().getNumero() : null;
	}

	private Integer getCodigoMaterial(ParcelasAFPendEntVO parcelaAfPendEnt) {
		return parcelaAfPendEnt.getMaterial() != null ? parcelaAfPendEnt.getMaterial().getCodigo() : null;
	}

	private Integer getCodigoGrupoMaterial(ParcelasAFPendEntVO parcelaAfPendEnt) {
		return parcelaAfPendEnt.getGrupoMaterial() != null ? parcelaAfPendEnt.getGrupoMaterial().getCodigo() : null;
	}

	private void validarCoresPrevisaoEntrega(ParcelaAfPendenteEntregaVO vo) {
		if ("S".equals(vo.getIndEmpenhada()) && vo.getJstCodigo() != null
				&& DateUtil.validaDataMaiorIgual(vo.getPrevisaoEntrega(), new Date()) && "N".equals(vo.getIndCancelada())) {
			vo.setCorPrevisaoEntrega(_00FF7F);
		}

		if (("S".equals(vo.getIndEmpenhada()) || vo.getJstCodigo() != null) && vo.getDataEntrega() != null
				&& vo.getQuantidadeEntregue() < vo.getQuantidade()) {
			vo.setCorEntrega(FFE347);
			vo.setCorPrevisaoEntrega(FFE347);
		}
	}

	public void selecionarParcelaAfPendenteEntrega(ParcelaAfPendenteEntregaVO vo) {
		ScoDataEnvioFornecedorVO scoDataEnvioFornecedorVO = getComprasFacade().consultarRegistroComDetalhesDasParcelas(vo);

		if (scoDataEnvioFornecedorVO == null) {
			vo.setDataEnvioFornecedor(null);
		} else {
			vo.setDataEnvioFornecedor(scoDataEnvioFornecedorVO.getDataEnvioFornecedor());
			vo.setDataEnvio(scoDataEnvioFornecedorVO.getDataEnvio());
			vo.setDataEmpenho(scoDataEnvioFornecedorVO.getDataEmpenho());
			vo.setSeqProgEntregaItemAutorizacaoFornecimento(scoDataEnvioFornecedorVO.getSeqProgEntregaItemAutorizacaoFornecimento());
			// adicionado na consulta, mas não estava informado na query da análise.
			vo.setObsParcelaPendente(scoDataEnvioFornecedorVO.getObservacao());
		}

		Date dataAux = DateUtil.adicionaDias(vo.getDataEmpenho(), 3);

		if (DateUtil.validaDataMaior(vo.getDataEnvio(), dataAux)) {
			vo.setCorDataEmpenho(FF6347);
			vo.setCorDataEnvioFornecedor(FFE347);
		}

		Long resultado = getEstoqueFacade().consultarCampoObservacaoParcelaPendente(vo.getNumeroIafAfnNumero(), vo.getNumeroIaf());

		if (resultado > 0L) {
			vo.setObsParcelaPendente("Recebimento Pend Confirmação Item: " + resultado + " " + vo.getCodigoUnidade());
			vo.setCorObsParcelaPendente(FFE347);
		}
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	protected ScoSolicitacaoCompraServicoDAO getScoScolicitacaoCompraServicoDAO() {
		return this.scoSolicitacaoCompraServicoDAO;
	}

}
