package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ValidacaoProgEntregaItemAfON extends BaseBusiness {
	private static final long serialVersionUID = 7276860333721573921L;
	
	private static final Log LOG = LogFactory.getLog(ValidacaoProgEntregaItemAfON.class);

	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@EJB
	private IComprasFacade comprasFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public enum ValidacaoProgEntregaItemAfONExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_ERRO_LIMITE_QTD_PROG_ENTREGA, MENSAGEM_ERRO_LIMITE_VALOR_PROG_ENTREGA,
		MENSAGEM_ERRO_DATA_PREVISAO_ENTREGA, MENSAGEM_ERRO_LIMITE_QTD_PROG_ENTREGA_INVERSA, MENSAGEM_ERRO_LIMITE_VALOR_PROG_ENTREGA_INVERSA,
		MENSAGEM_ERRO_QTD_NAO_INFORMADA_SC, MENSAGEM_ERRO_QTD_NAO_INFORMADA_SS, MENSAGEM_ERRO_NOVA_PARCELA_ITEM_AF_GERADO_AUTOMATICAMENTE,
		MENSAGEM_ERRO_QTD_LIBERADA, MENSAGEM_ERRO_VALOR_LIBERADA, MENSAGEM_ERRO_QTD_ALTERADA, 
		MENSAGEM_ERRO_VALOR_ALTERADO, MENSAGEM_ERRO_QTD_LT_ENTREGUE, MENSAGEM_ERRO_VALOR_LT_EFETIVADO,
		MENSAGEM_QTD_MULTIPLA, MENSAGEM_ERRO_VALOR_DIFERENTE_SC, MENSAGEM_VALOR_DIFERENTE_SS;
	}

	public Boolean verificarEntregaProgramada(boolean existeEntregaProgramada, Boolean geradoAutomaticamente) {
		return !existeEntregaProgramada && Boolean.TRUE.equals(geradoAutomaticamente);
	}
	
	/**
	 * Valida se a data de previsão de entrega de uma programação de entrega de item de AF é anterior ao dia de hoje
	 * @param previsaoEntrega
	 * @throws ApplicationBusinessException
	 */
	public void validarDataPrevisaoEntrega(Date previsaoEntrega) throws ApplicationBusinessException {
		if (CoreUtil.isMenorDatas(DateUtil.truncaData(previsaoEntrega), DateUtil.truncaData(new Date()))) {
			throw new ApplicationBusinessException(ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_DATA_PREVISAO_ENTREGA);
		}
	}
	
	/**
	 * Valida se a data de previsão de entrega de uma programação de entrega de item de AF já foi previamente utilizada em outra programação de entrega
	 * @param afnNumero
	 * @param numeroItem
	 * @param parcelaAtual
	 * @param previsaoEntrega
	 * @return
	 */
	public Integer validarDataPrevisaoEntregaDuplicada(Integer afnNumero, Integer numeroItem, Integer parcelaAtual, Date previsaoEntrega) {
		Integer numParcela = null;
		
		List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas = this.comprasFacade.
				pesquisaProgEntregaItemAf(afnNumero, numeroItem, false, false);
		
		for (ScoProgEntregaItemAutorizacaoFornecimento item : listaParcelas) {
			if (Objects.equals(DateUtil.truncaData(item.getDtPrevEntrega()), DateUtil.truncaData(previsaoEntrega)) && !item.getId().getParcela().equals(parcelaAtual)) {
				numParcela = item.getId().getParcela();
				break;
			}
		}
		return numParcela;
	}
	
	/**
	 * Valida se a quantidade detalhada (parcelas) extrapola a quantidade "principal" da programação de entrega do item de AF
	 * @param listaPea
	 * @param qtdParcelaAf
	 * @param valorParcelaAf
	 * @param qtdDetalhada
	 * @param valorDetalhado
	 * @param tipoSolicitacao
	 * @param indexEdicao
	 * @param validacaoAdicionar
	 * @throws ApplicationBusinessException
	 */
	public void validarQuantidadeLimiteParcela(List<ParcelaItemAutFornecimentoVO> listaPea, 
			Integer qtdParcelaAf, Double valorParcelaAf, Integer qtdDetalhada, Double valorDetalhado,
			DominioTipoSolicitacao tipoSolicitacao, Boolean validacaoAdicionar) throws ApplicationBusinessException {
		if (!listaPea.isEmpty()) {
			if (tipoSolicitacao == DominioTipoSolicitacao.SC) {
				if (qtdDetalhada == 0 && validacaoAdicionar) {
					throw new ApplicationBusinessException(
							ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_QTD_NAO_INFORMADA_SC);
				}
	
				Integer qtdGrade = 0;
				Integer valorAtual = 0;
	
				for (ParcelaItemAutFornecimentoVO item : listaPea) {
					qtdGrade = qtdGrade + item.getQtdeDetalhada();
				}
	
				qtdGrade += qtdDetalhada - valorAtual;
	
				if (!qtdGrade.equals(qtdParcelaAf)) {
					throw new ApplicationBusinessException(
							ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_VALOR_DIFERENTE_SC,
							qtdGrade, qtdParcelaAf);
				}
			} else {
				if (valorDetalhado == 0.00 && validacaoAdicionar) {
					throw new ApplicationBusinessException(
							ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_QTD_NAO_INFORMADA_SS);
				}
				
				Double valorGrade = 0.00;
				Double valorAtual = 0.00;
				
				for (ParcelaItemAutFornecimentoVO item : listaPea) {
					valorGrade = valorGrade + item.getValorDetalhado();
				}
	
				valorGrade += valorDetalhado - valorAtual;
				
				if (!valorGrade.equals(valorParcelaAf)) {
					throw new ApplicationBusinessException(
							ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_VALOR_DIFERENTE_SS,
							valorGrade, valorParcelaAf);
				}
			}
		}
	}
		
	public Boolean getEntregaProgramada(boolean existeEntregaProgramada, Boolean geradoAutomaticamente) {
		return !existeEntregaProgramada && Boolean.TRUE.equals(geradoAutomaticamente);
	}

	public void validarValorLiberado(
			ScoProgEntregaItemAutorizacaoFornecimentoId id,
			Double valorParcelaAf, Integer qtdParcelaAf, Double valorLiberar,
			Integer qtdLiberar, DominioTipoSolicitacao tipoSolicitacao,
			boolean existeEntregaProgramada) throws ApplicationBusinessException {
		ScoProgEntregaItemAutorizacaoFornecimento orig = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterOriginal(id);
		
		if (tipoSolicitacao.equals(DominioTipoSolicitacao.SC)) {
			int qtdeOrig = orig != null ? orig.getQtde() : 0;
			
			if (orig != null && orig.getQtdeEntregue() != null) {
				if (existeEntregaProgramada && CoreUtil.modificados(qtdParcelaAf, orig.getQtde())) {
					throw new ApplicationBusinessException(ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_QTD_ALTERADA);
				}			

				if (qtdParcelaAf < orig.getQtdeEntregue()) {
					throw new ApplicationBusinessException(ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_QTD_LT_ENTREGUE);
				}
			}
			
			if (qtdLiberar < (qtdParcelaAf - qtdeOrig)) {
				throw new ApplicationBusinessException(ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_QTD_LIBERADA);
			}
		} else {
			double valorOrig = orig != null ? orig.getValorTotal() : 0;
			
			if (orig != null) {
				if (existeEntregaProgramada && CoreUtil.modificados(valorParcelaAf, orig.getValorTotal())) {
					throw new ApplicationBusinessException(ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_VALOR_ALTERADO);
				}
				
				if (valorParcelaAf < (Double)CoreUtil.nvl(orig.getValorEfetivado(), 0.00)) {
					throw new ApplicationBusinessException(ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_VALOR_LT_EFETIVADO);
				}
			}
			
			if (valorLiberar < (valorParcelaAf - valorOrig)) {
				throw new ApplicationBusinessException(ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_VALOR_LIBERADA);
			}
		}
	}
	
	public void validarQuantidadeMultipla(ScoProgEntregaItemAutorizacaoFornecimentoId id, DominioTipoSolicitacao tipoSolicitacao,
											Integer qtdParcelaAf) throws ApplicationBusinessException {
		if (tipoSolicitacao.equals(DominioTipoSolicitacao.SC)) {
			ScoItemAutorizacaoForn itemAf = this.getScoItemAutorizacaoFornDAO().obterItemAutorizacaoFornPorId(id.getIafAfnNumero(), id.getIafNumero());
			
			if (itemAf != null) {
				Integer fatorForn = (Integer)CoreUtil.nvl(itemAf.getFatorConversaoForn(), 1);
				if (qtdParcelaAf % fatorForn != 0) {
					throw new ApplicationBusinessException(ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_QTD_MULTIPLA, fatorForn);
				}
			}
		}
	}
	
	public void validarNovoRegistro(Boolean novoRegistro, Boolean existeEntregaProgramada) throws ApplicationBusinessException{
		if (novoRegistro && existeEntregaProgramada) {
			throw new ApplicationBusinessException(
					ValidacaoProgEntregaItemAfONExceptionCode.MENSAGEM_ERRO_NOVA_PARCELA_ITEM_AF_GERADO_AUTOMATICAMENTE);
		}
	}
	
	public Boolean verificarItemNaoEntregue(ParcelaItemAutFornecimentoVO item, DominioTipoSolicitacao tipoSolicitacao) {
		return (((tipoSolicitacao == DominioTipoSolicitacao.SC && (item.getQtdeEntregue() == null || item.getQtdeEntregue() == 0)) ||
				(tipoSolicitacao == DominioTipoSolicitacao.SS && (item.getValorEfetivado() == null || item.getValorEfetivado() == 0.00))));
	}		
	
	public ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	public void setScoItemAutorizacaoFornDAO(ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO) {
		this.scoItemAutorizacaoFornDAO = scoItemAutorizacaoFornDAO;
	}

	public ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	public void setScoProgEntregaItemAutorizacaoFornecimentoDAO(
			ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO) {
		this.scoProgEntregaItemAutorizacaoFornecimentoDAO = scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}
}