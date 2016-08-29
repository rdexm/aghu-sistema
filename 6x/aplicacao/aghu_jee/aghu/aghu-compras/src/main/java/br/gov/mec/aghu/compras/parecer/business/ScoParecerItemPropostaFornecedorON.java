package br.gov.mec.aghu.compras.parecer.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ON responsável pela gravação de análise técnica de item de proposta de
 * fornecedor.
 * 
 * @author mlcruz
 */
@Stateless
public class ScoParecerItemPropostaFornecedorON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoParecerItemPropostaFornecedorON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

@EJB
private IPacFacade pacFacade;

@Inject
private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

@Inject
private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;

@Inject
private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	private static final long serialVersionUID = -289184830063922969L;

	public void gravarAnaliseTecnica(ScoItemPropostaFornecedor itemProposta)
			throws BaseException {
		
		Integer numeroLicitacao = itemProposta.getId().getPfrLctNumero();
		Integer numeroFornecedor = itemProposta.getId().getPfrFrnNumero();
		Short numeroItemProposta = itemProposta.getId().getNumero();

		// RN02: Não é permitido efetuar alguma alteração quando já existir
		// Autorização de Fornecimento (AF) gerada para o item.
		if (this.getScoAutorizacaoFornDAO().verificarItemPropostaEmAf(
				numeroLicitacao, numeroFornecedor)) {
			if (!getScoItemAutorizacaoFornDAO()
					.verificarPropostaEmItemAfExcluido(numeroLicitacao,
							numeroFornecedor, numeroItemProposta)) {
				throw new ApplicationBusinessException(
						ParecerItemPropostaExceptionCode.MESSAGE_ALTERACAO_NAO_PERMITIDA_PARA_ITEM_COM_AF_GERADA);
			}
		}

		Boolean possuiOutraPropostaEscolhida = getScoItemPropostaFornecedorDAO()
				.verificarItemPossuiPropostaEscolhida(itemProposta); 
		
		ScoItemLicitacao itemLicitacao = getScoItemLicitacaoDAO().obterItemLicitacaoPorNumeroLicitacaoENumeroItem(numeroLicitacao, itemProposta.getItemLicitacao().getId().getNumero());
		
		if (itemLicitacao != null) {
			// RN03: Coluna ind_proposta_escolhida = ‘N’ quando para o item não
			// existir uma proposta escolhida, diferente daquela que esta sendo
			// desclassificada.
			if (itemProposta.getMotDesclassif() != null) {
				if (!possuiOutraPropostaEscolhida) {
					itemLicitacao.setPropostaEscolhida(false);
				}
			} else {
				ScoItemPropostaFornecedor original = getScoItemPropostaFornecedorDAO()
						.obterOriginal(itemProposta);
	
				// RN01: Não é permitido reverter a desclassificação de uma proposta
				// (retirar o motivo da desclassificação) quando já existe uma outra
				// proposta escolhida para o mesmo item do Processo Administrativo
				// de Compras (PAC).
				if (original != null && original.getMotDesclassif() != null && possuiOutraPropostaEscolhida) {
					throw new ApplicationBusinessException(
							ParecerItemPropostaExceptionCode.MESSAGE_RETIRADA_MOTIVO_DESCLASSIFICACAO_NAO_PERMITIDA);								
				}
					
				// RN04: Coluna ind_proposta_escolhida = ‘S’ quando a mesma
				// possuir status de escolhida.
				itemLicitacao.setPropostaEscolhida(itemProposta.getIndEscolhido());				
			}
			
			if (itemProposta.getIndAutorizUsr() != null && !itemProposta.getIndAutorizUsr()) {
				itemProposta.setJustifAutorizUsr(null);
			}
			
			itemProposta.setIndDesclassificado(itemProposta.getMotDesclassif() != null);
			getPacFacade().atualizarItemPropostaFornecedor(itemProposta,false);
			
			getPacFacade().atualizarItemLicitacao(itemLicitacao);			
		}
	}

	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	public enum ParecerItemPropostaExceptionCode implements BusinessExceptionCode {
		MESSAGE_ALTERACAO_NAO_PERMITIDA_PARA_ITEM_COM_AF_GERADA,
		MESSAGE_RETIRADA_MOTIVO_DESCLASSIFICACAO_NAO_PERMITIDA
	}
	
	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}
	
	protected IPacFacade getPacFacade() {
		return this.pacFacade;
	}
}