package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueObjectException;

import br.gov.mec.aghu.ambulatorio.dao.AacCondicaoAtendimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaAgendamentoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterCondicaoAtendimentoRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ManterCondicaoAtendimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AacFormaAgendamentoDAO aacFormaAgendamentoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AacCondicaoAtendimentoDAO aacCondicaoAtendimentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5889292908157270213L;

	public enum ManterCondicaoAtendimentoRNExceptionCode implements BusinessExceptionCode {
		AGH_00651, MSG_CONDICAO_ATENDIMENTO_EXISTENTE, MSG_CONDICAO_ATENDIMENTO_ERRO_INCLUSAO, MSG_CONDICAO_ATENDIMENTO_ERRO_ALTERACAO;
	}
	
	public void remover(Short codigoCondicaoAtendimento) throws ApplicationBusinessException  {
		AacCondicaoAtendimento condicaoAtendimento = this.getAacCondicaoAtendimentoDAO().obterPorChavePrimaria(codigoCondicaoAtendimento);
		if(this.getAacFormaAgendamentoDAO().existeFormaAgendamentoComCondicaoAtendimentoCount(condicaoAtendimento)) {
			throw new ApplicationBusinessException(ManterCondicaoAtendimentoRNExceptionCode.AGH_00651);
		} else {
			this.getAacCondicaoAtendimentoDAO().remover(condicaoAtendimento);
			this.getAacCondicaoAtendimentoDAO().flush();
		}
	}
	
	/**
	 * ORADB: AACT_CAA_BRI
	 * @throws BaseException
	 */
	public void persistirCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		condicaoAtendimento.setServidor(servidorLogado);
		condicaoAtendimento.setCriadoEm(new Date());
		
		try {
			this.getAacCondicaoAtendimentoDAO().flush();
			this.getAacCondicaoAtendimentoDAO().persistir(condicaoAtendimento);
		} catch (PersistenceException e) {
			if (e.getCause() instanceof ConstraintViolationException
					|| e.getCause() instanceof NonUniqueObjectException) {
				throw new ApplicationBusinessException(
						ManterCondicaoAtendimentoRNExceptionCode.MSG_CONDICAO_ATENDIMENTO_EXISTENTE,
						e);
			} else {
				throw new ApplicationBusinessException(
						ManterCondicaoAtendimentoRNExceptionCode.MSG_CONDICAO_ATENDIMENTO_ERRO_INCLUSAO,
						e);
			}
		}
	}
	
	public void atualizarCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		condicaoAtendimento.setServidorAlterado(servidorLogado);
		condicaoAtendimento.setAlteradoEm(new Date());
		
		try {
			this.getAacCondicaoAtendimentoDAO().atualizar(condicaoAtendimento);
			this.getAacCondicaoAtendimentoDAO().flush();
		} catch (PersistenceException e) {
			if (e.getCause() instanceof ConstraintViolationException
					|| e.getCause() instanceof NonUniqueObjectException) {
				throw new ApplicationBusinessException(
						ManterCondicaoAtendimentoRNExceptionCode.MSG_CONDICAO_ATENDIMENTO_EXISTENTE,
						e);
			} else {
				throw new ApplicationBusinessException(
						ManterCondicaoAtendimentoRNExceptionCode.MSG_CONDICAO_ATENDIMENTO_ERRO_ALTERACAO,
						e);
			}
		}
	}
	
	/** GET/SET **/
	protected AacCondicaoAtendimentoDAO getAacCondicaoAtendimentoDAO() {
		return aacCondicaoAtendimentoDAO;
	}
	
	protected AacFormaAgendamentoDAO getAacFormaAgendamentoDAO() {
		return aacFormaAgendamentoDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
