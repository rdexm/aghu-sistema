package br.gov.mec.aghu.compras.pac.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class ScoItemPropostaFornecedorRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoItemPropostaFornecedorRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoCondicaoPagamentoProposDAO scoCondicaoPagamentoProposDAO;

@Inject
private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

@Inject
private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

@Inject
private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

@Inject
private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -893066524258218268L;

	public enum ScoItemPropostaFornecedorRNExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_JULGAMENTO_MSG015, MENSAGEM_JULGAMENTO_MSG006, MENSAGEM_JULGAMENTO_MSG004;
	}
	
	/**
	 * Realiza as acoes antes da insercao de um item de proposta do fornecedor
	 * @ORADB SCOT_IPF_BRI 
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	private void preInserirItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		this.validaItemPropostaEmAf(itemProposta);
	}
	
	/**
	 * Realiza as acoes apos a efetivacao da insercao de um item de proposta do fornecedor
	 * @param itemProposta
	 */
	private void posInserirItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) {
		//
	}
	
	/**
	 * Insere um item de proposta do fornecedor realizando as validacoes e operacoes necessarias
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void inserirItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		this.preInserirItemPropostaFornecedor(itemProposta);
		this.getScoItemPropostaFornecedorDAO().persistir(itemProposta);
		this.posInserirItemPropostaFornecedor(itemProposta);
	}
	
	
	/**
	 * Realiza as acoes antes da atualizacao de um item de proposta do fornecedor
	 * @ORADB SCOT_IPF_ARU, SCOT_IPF_BRU 
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	private void preAtualizarItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		ScoItemPropostaFornecedor itemPropostaOriginal = this.getScoItemPropostaFornecedorDAO().obterOriginal(itemProposta);
		
		this.validaItemPropostaEmAf(itemProposta);
		
		if (itemPropostaOriginal != null) {
			if (itemPropostaOriginal.getIndEscolhido().equals(DominioSimNao.N) && itemProposta.getIndEscolhido().equals(DominioSimNao.S)) {
				this.validarCriterioEscolha(itemProposta);
				this.validarCondicaoPagamento(itemProposta);
			}
			
			if (itemPropostaOriginal.getCriterioEscolhaProposta() == null && itemProposta.getCriterioEscolhaProposta() != null) {
				itemProposta.setDtEscolha(new Date());
			}
			
			if (itemPropostaOriginal.getCriterioEscolhaProposta() != null && itemProposta.getCriterioEscolhaProposta() == null) {
				itemProposta.setDtEscolha(null);
			}
		}
	}
	
	/**
	 * Verifica se a condicao de pagamento do item de proposta esta vazia
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	protected void validarCondicaoPagamento(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		if (itemProposta.getCondicaoPagamentoPropos() == null) {
			throw new ApplicationBusinessException(
					ScoItemPropostaFornecedorRNExceptionCode.MENSAGEM_JULGAMENTO_MSG004);
		}
	}
	
	/**
	 * Verifica se o criterio de escolha do item de proposta esta vazio
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	private void validarCriterioEscolha(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		if (itemProposta.getCriterioEscolhaProposta() == null) {
			throw new ApplicationBusinessException(
					ScoItemPropostaFornecedorRNExceptionCode.MENSAGEM_JULGAMENTO_MSG015);
		}
	}
	
	/**
	 * Realiza as acoes apos a efetivacao da atualizacao de um item de proposta do fornecedor
	 * @param itemProposta
	 */
	private void posAtualizarItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) {
		//
	}
	
	/**
	 * Persiste um item de proposta do fornecedor realizando as validacoes e operacoes necessarias
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	public void atualizarItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta, boolean novo) throws ApplicationBusinessException {
		this.preAtualizarItemPropostaFornecedor(itemProposta);
		if (novo) {
		    this.getScoItemPropostaFornecedorDAO().persistir(itemProposta);
		}
		else {
			this.getScoItemPropostaFornecedorDAO().atualizar(itemProposta);
		}
		this.posAtualizarItemPropostaFornecedor(itemProposta);
	}
	
	public void atualizarItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		this.atualizarItemPropostaFornecedor(itemProposta,true);
	}
	/**
	 * Realiza as acoes antes da efetivacao da remocao de um item de proposta do fornecedor
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	private void preRemoverItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		this.validaItemPropostaEmAf(itemProposta);
		if (itemProposta.getCondicoesPagamento() != null) {
			for (ScoCondicaoPagamentoPropos condPag : itemProposta.getCondicoesPagamento()) {
				this.getScoCondicaoPagamentoProposDAO().remover(condPag);
			}
		}
	}
	
	 
	/**
	 * Realiza as acoes apos a efetivacao da remocao de um item de proposta do fornecedor
	 * @ORADB SCOT_IPF_ARD 
	 * @param itemProposta
	 */
	private void posRemoverItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) {
		//
	}
	
	/**
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	public void removerItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		this.preRemoverItemPropostaFornecedor(itemProposta);
		this.getScoItemPropostaFornecedorDAO().remover(itemProposta);
		
		this.posRemoverItemPropostaFornecedor(itemProposta);
	}
	
	/**
	 * Valida se determinado item de proposta está em AF
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	public void validaItemPropostaEmAf(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		Integer numeroLCT = itemProposta.getItemLicitacao().getId().getLctNumero();
		Short numeroItem = itemProposta.getItemLicitacao().getId().getNumero();
		
		List<ScoFaseSolicitacao> fases = getScoFaseSolicitacaoDAO()
				.obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(
						numeroLCT, numeroItem);
		
		boolean isGeracaoAutomatica = false;
		
		for (ScoFaseSolicitacao fase : fases) {
			// Descobre se item de proposta foi gerado automaticamente.
			if (Boolean.TRUE.equals(fase.getGeracaoAutomatica())) {
				isGeracaoAutomatica = true;
				break;
			}
		}
		
		// Valida se item de proposta está em AF somente quando não foi gerado automaticamente.
		if (!isGeracaoAutomatica) {
			Boolean itemPropostaEmAf = this.getScoAutorizacaoFornDAO()
					.verificarItemPropostaEmAf(
							itemProposta.getId().getPfrLctNumero(),
							itemProposta.getId().getPfrFrnNumero());
			
			if (itemPropostaEmAf) {
				if (!this.getScoItemAutorizacaoFornDAO()
						.verificarPropostaEmItemAfExcluido(
								itemProposta.getId().getPfrLctNumero(),
								itemProposta.getId().getPfrFrnNumero(),
								itemProposta.getId().getNumero())) {
					throw new ApplicationBusinessException(
							ScoItemPropostaFornecedorRNExceptionCode.MENSAGEM_JULGAMENTO_MSG006);
				}
			}
		}
	}
	
	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}
	
	protected ScoCondicaoPagamentoProposDAO getScoCondicaoPagamentoProposDAO() {
		return scoCondicaoPagamentoProposDAO;
	}

	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}
	
	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
}