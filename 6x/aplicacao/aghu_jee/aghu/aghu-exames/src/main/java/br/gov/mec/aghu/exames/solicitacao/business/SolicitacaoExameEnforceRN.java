package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Responsavel pelas regras das enforces da entidade AelSolicitacaoExames.
 * 
 * Regra 1 do documento de pre-analise.
 * 
 * Tabela: AEL_SOLICITACAO_EXAMES
 * ORADB Enforce AELP_ENFORCE_SOE_RULES.
 * 
 */
@Stateless
public class SolicitacaoExameEnforceRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SolicitacaoExameEnforceRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade faturamentoFacade;

@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1145926678869585476L;

	/**
	 * ORADB AELP_ENFORCE_SOE_RULES - EVENTO DE UPDATE
	 * 
	 * @param {AelSolicitacaoExames} solicitacao
	 * @throws BaseException
	 */
	public void enforceUpdate(final AelSolicitacaoExames solicitacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		final AelSolicitacaoExames solicitacaoOriginal = this.getAelSolicitacaoExameDAO().obterOriginal(solicitacao.getSeq());
		
		if (this.verificarConvenioPlanoModificados(solicitacao, solicitacaoOriginal)) {
			try {
				this.getFaturamentoFacade().atualizarFaturamentoSolicitacaoExames(solicitacao, solicitacaoOriginal, nomeMicrocomputador, dataFimVinculoServidor);
			} catch (final InactiveModuleException e) {
				logError(e.getMessage());
			}
		}
		
	}
	
	/**
	 * Verifica se houve alteração de convênio/plano
	 * se sim deve retornar verdadeiro, senão falso.
	 * 
	 * @param {AelSolicitacaoExames} solicitacao
	 * @param {AelSolicitacaoExames} solicitacaoOriginal
	 * @return {Boolean}
	 * @throws BaseException
	 */
	public Boolean verificarConvenioPlanoModificados(final AelSolicitacaoExames solicitacao, final AelSolicitacaoExames solicitacaoOriginal) throws BaseException {
		
		Boolean response = false;
		
		//Se o convênio ou o plano forem alterados
		if(CoreUtil.modificados(solicitacao.getConvenioSaudePlano(), solicitacaoOriginal.getConvenioSaudePlano())) {
			
			response = true;
			
		}
		
		return response;
		
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

}
