package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpRetencaoAliquotaDAO;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.MovimentacaoFornecedorVO;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por prover os metodos de negócio
 * para a listagem da movimentacao de fornecedores 
 *
 *
 */
@Stateless
public class MovimentacaoFornecedorON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3053567701684539156L;

	private static final Log LOG = LogFactory.getLog(MovimentacaoFornecedorON.class);
	private static final String TAG_QUEBRA_LINHA = "<br />";
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/** Injeção do objeto de título da camada de dados */
	@Inject
	private FcpTituloDAO tituloDAO;
	
	@Inject
	private FcpRetencaoAliquotaDAO fcpRetencaoAliquotaDAO;
	
	@Inject
	private TituloON tituloON;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static MovimentacaoFornecedorComparator fornecedorComparator = new MovimentacaoFornecedorComparator();
	
	public List<MovimentacaoFornecedorVO> pesquisarMovimentacaoFornecedores(Integer frnNumero, DominioSituacaoTitulo situacao, Integer notaRecebimento, String serie, 
			Integer nroAf, Short nroComplemento, Long nf, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException {
		String[] parametroNota, parametroISSQN;
		List<MovimentacaoFornecedorVO> lista = new ArrayList<MovimentacaoFornecedorVO>();
		List<MovimentacaoFornecedorVO> vo = new ArrayList<MovimentacaoFornecedorVO>();
		Boolean ordenar = Boolean.FALSE;
		
		if(orderProperty != null && orderProperty.equals("valorTributo")) {
			orderProperty = null;
			asc = true;
			ordenar = Boolean.TRUE;
		}
		
		lista = this.tituloDAO.pesquisarMovimentacaoFornecedor(frnNumero, situacao, notaRecebimento, serie, nroAf, nroComplemento, nf, firstResult, maxResults, orderProperty, asc);
		
		parametroNota = buscarVlrArrayAghParametro(AghuParametrosEnum.P_AGHU_LISTA_IMPOSTO_RETENCAO_ALIQUOTA);
		parametroISSQN = buscarVlrArrayAghParametro(AghuParametrosEnum.P_AGHU_IMPOSTO_ISSQN_RETENCAO_ALIQUOTA);
		
		for (MovimentacaoFornecedorVO movimentacao : lista) {
			
			if (movimentacao.getCgc() == null) {
				movimentacao.setCpfCnpj(CoreUtil.formataCPF(movimentacao.getCpf()));
			} else {
				movimentacao.setCpfCnpj(CoreUtil.formatarCNPJ(movimentacao.getCgc()));
			}
			
			movimentacao.setCorSituacao(tituloON.colorirCampoSituacao(movimentacao.getIndSituacao()));
			
			Double valorTributoNota = fcpRetencaoAliquotaDAO.obterValorTributosPorNotaRecebimento(movimentacao.getNrsSeq(), parametroNota);
			Double valorTributoISSQN = fcpRetencaoAliquotaDAO.obterValorTributosPorISSQN(movimentacao.getNrsSeq(), parametroISSQN);
			
			movimentacao = montarValores(movimentacao, valorTributoNota, valorTributoISSQN);
			
			StringBuilder toolTip = new StringBuilder(30);
			
			toolTip.append("NR: ");
			
			if (movimentacao.getNrsSeq() != null) {
				toolTip.append(movimentacao.getNrsSeq());
			}
			
			toolTip.append(TAG_QUEBRA_LINHA).append("NF: ");
			
			if(movimentacao.getNumeroDocumentoFiscal() != null) {
				toolTip.append(movimentacao.getNumeroDocumentoFiscal());
			}
			
			toolTip.append(TAG_QUEBRA_LINHA).append("Série: ");
			
			if (movimentacao.getSerie() != null) {
				toolTip.append(movimentacao.getSerie());
			}
			
			toolTip.append(TAG_QUEBRA_LINHA).append("Número AF: ");
			
			if (movimentacao.getPfrLctNumero() != null) {
				toolTip.append(movimentacao.getPfrLctNumero());
			}
			
			if (movimentacao.getNroComplemento() != null) {
				toolTip.append('/'+movimentacao.getNroComplemento());
			}
			
			movimentacao.setToolTipTitulo(toolTip.toString());
			
			vo.add(movimentacao);
		}
		
		if (ordenar) {
			Collections.sort(vo, fornecedorComparator);
		}
		
		return vo;
	}
	
	public MovimentacaoFornecedorVO montarValores(MovimentacaoFornecedorVO movimentacao, Double valorTributoNota, Double valorTributoISSQN) {
		movimentacao.setValorTributo(0.0);
		
		if (valorTributoNota != null && valorTributoISSQN != null) {
			movimentacao.setValorTributo(valorTributoNota + valorTributoISSQN);
		} else if (valorTributoNota != null) {
			movimentacao.setValorTributo(valorTributoNota);
		} else if (valorTributoISSQN != null){
			movimentacao.setValorTributo(valorTributoISSQN);
		} 
		
		if (movimentacao.getVlrDesconto() == null){
			movimentacao.setVlrDesconto(0.0);
		}
		
		if (movimentacao.getValorPagamento() == null){
			movimentacao.setValorPagamento(0.0);
		}
		
		return movimentacao;
	}
	
	public Long pesquisarMovimentacaoFornecedoresCount(Integer frnNumero, DominioSituacaoTitulo situacao, Integer notaRecebimento, String serie, 
			Integer nroAf, Short nroComplemento, Long nf) {
		return this.tituloDAO.pesquisarMovimentacaoFornecedorCount(frnNumero, situacao, notaRecebimento, serie, nroAf, nroComplemento, nf);
	}
	
	protected String[] buscarVlrArrayAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		AghParametros parametro = this.parametroFacade.buscarAghParametro(parametrosEnum);
		
		if (parametro == null || parametro.getVlrTexto() == null){
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE, parametrosEnum);
		}
		
		String result = parametro.getVlrTexto();
		return result.split("\\,");
	}
	
	private static class MovimentacaoFornecedorComparator implements Comparator<MovimentacaoFornecedorVO> {
		@Override
		public int compare(MovimentacaoFornecedorVO vo, MovimentacaoFornecedorVO vo2) {
			if(vo.getValorTributo() < vo2.getValorTributo()) {
				return -1;
			} else if(vo.getValorTributo() == vo2.getValorTributo()) { 
				return 0;
			}
			return 1;
		}
	}

	public TituloON getTituloON() {
		return tituloON;
	}

	public void setTituloON(TituloON tituloON) {
		this.tituloON = tituloON;
	}
}
