package br.gov.mec.aghu.compras.pac.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamentoComissaoLicitacao;
import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.suprimentos.vo.ScoFaseSolicitacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ScoPropostaFornecedorON extends BaseBusiness {

	@EJB
	private ScoItemPropostaFornecedorON scoItemPropostaFornecedorON;
	@EJB
	private ScoPropostaFornecedorRN scoPropostaFornecedorRN;
	@EJB
	private RegistraJulgamentoPacON registraJulgamentoPacON;
	
	private static final Log LOG = LogFactory.getLog(ScoPropostaFornecedorON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	
	@Inject
	private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;
	
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;
	
	private static final long serialVersionUID = -893064524258218268L;

	/**
	 * Insere ou altera na base de dados um ScoPropostaFornecedor
	 * @param proposta
	 * @param propostaEmEdicao
	 * @throws ApplicationBusinessException
	 */
	public void persistirPropostaFornecedor(ScoPropostaFornecedor proposta, Boolean propostaEmEdicao) throws ApplicationBusinessException {
		ScoPropostaFornecedor propostaFornecedor = null;
		Boolean propostaNova = Boolean.FALSE;
		
		if (propostaEmEdicao) {
			 propostaFornecedor = this.getScoPropostaFornecedorDAO().obterPorChavePrimaria(proposta.getId());
		} else {			
			
			propostaFornecedor = new ScoPropostaFornecedor();
			propostaFornecedor.setId(proposta.getId());
			propostaFornecedor.setDtExclusao(null);
			propostaFornecedor.setIndExclusao(Boolean.FALSE);	
			propostaFornecedor.setServidor(proposta.getServidor());
			
			ScoFornecedor fornecedor = this.getScoFornecedorDAO().obterPorChavePrimaria(proposta.getId().getFrnNumero());
			ScoLicitacao licitacao = this.getScoLicitacaoDAO().obterPorChavePrimaria(proposta.getId().getLctNumero());
			
			if (licitacao != null) {
				propostaFornecedor.setLicitacao(licitacao);
			}
			
			if (fornecedor != null) {
				propostaFornecedor.setFornecedor(fornecedor);
			}
			
			propostaNova = Boolean.TRUE;
		}
		
		if (propostaFornecedor != null) {
			propostaFornecedor.setDtApresentacao(proposta.getDtApresentacao());
			propostaFornecedor.setDtDigitacao(proposta.getDtDigitacao());
			propostaFornecedor.setPrazoEntrega(proposta.getPrazoEntrega());
			propostaFornecedor.setValorTotalFrete(proposta.getValorTotalFrete());
			if (propostaEmEdicao) {
				getScoPropostaFornecedorRN().alterarPropostaFornecedor(propostaFornecedor);
			} else {
				getScoPropostaFornecedorRN().inserirPropostaFornecedor(propostaFornecedor);
			}
		}
		
		this.copiarCpgLicitacao(propostaNova, propostaFornecedor);
	}
	
	private void copiarCpgLicitacao(Boolean propostaNova, ScoPropostaFornecedor proposta) throws ApplicationBusinessException {
		if (propostaNova) {
			this.getScoItemPropostaFornecedorON().copiarCondicaoPagamentoLicitacao(proposta.getId().getLctNumero(), 
					proposta.getId().getFrnNumero(), null, null);
		}
	}
	
	public boolean verificarItemProposta(ScoLicitacao scoLicitacao){
		Long retorno  =  this.getScoItemPropostaFornecedorDAO().listarItemPropostaFornecedorPorLicitacaoCount(scoLicitacao.getNumero());
		if(retorno>0L){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Realiza o julgamento, o cancelamento ou a pendencia de um item de proposta do fornecedor
	 * @param itemProposta
	 * @param faseSolicitacao
	 * @param condicaoPagamentoEscolhida
	 * @param motivoCancelamento
	 * @param pendentePor
	 * @param criterioEscolha
	 * @param motivoDesclassificacao
	 * @throws ApplicationBusinessException
	 */
	public void executarAcoesJulgamento(ScoItemPropostaFornecedor itemProposta, 
			ScoFaseSolicitacaoVO faseSolicitacao,
			ScoCondicaoPagamentoPropos condicaoPagamentoEscolhida,
			DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento,
			DominioSituacaoJulgamento pendentePor,
			ScoCriterioEscolhaProposta criterioEscolha,
			DominioMotivoDesclassificacaoItemProposta motivoDesclassificacao) throws ApplicationBusinessException {
		
		ScoItemPropostaFornecedor itProp = null;
		
		if (itemProposta != null) {
			itProp = this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(itemProposta.getId());
		
			if (itProp != null) {
				if (criterioEscolha != null) {
					this.getRegistraJulgamentoPacON().julgarPac(itemProposta, condicaoPagamentoEscolhida, criterioEscolha);
				} 
				if (criterioEscolha == null) {
					this.getRegistraJulgamentoPacON().reverterJulgamentoPac(itemProposta);
				} 
				if (motivoDesclassificacao != null) {
					this.getRegistraJulgamentoPacON().desclassificarItemProposta(itemProposta, motivoDesclassificacao);
				} 
				if (motivoDesclassificacao == null) {
					this.getRegistraJulgamentoPacON().reverterDesclassificacaoItemProposta(itemProposta);
				}

			}
		}
	}
	
	protected ScoItemPropostaFornecedorON getScoItemPropostaFornecedorON() {
		return scoItemPropostaFornecedorON;
	}
	
	protected ScoPropostaFornecedorDAO getScoPropostaFornecedorDAO() {
		return scoPropostaFornecedorDAO;
	}

	protected ScoFornecedorDAO getScoFornecedorDAO() {
		return scoFornecedorDAO;
	}
	
	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}
	
	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}
	
	protected RegistraJulgamentoPacON getRegistraJulgamentoPacON() {
		return registraJulgamentoPacON;
	}

	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	protected ScoPropostaFornecedorRN getScoPropostaFornecedorRN() {
		return scoPropostaFornecedorRN;
	}
}