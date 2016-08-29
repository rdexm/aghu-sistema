package br.gov.mec.aghu.exames.questionario.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelQuestoesQuestionarioDAO;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.model.AelQuestoesQuestionarioId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class QuestaoQuestionarioRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(QuestaoQuestionarioRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelQuestoesQuestionarioDAO aelQuestoesQuestionarioDAO;

	private static final long	serialVersionUID	= -6989829816713535677L;

	public enum QuestaoQuestionarioRNExceptionCode implements BusinessExceptionCode {
		VALORES_OBRIGATORIOS, QUESTAO_JA_ASSOCIADA, ;
	}

	public void persistir(final AelQuestoesQuestionario aelQuestoesQuestionario) throws BaseException  {
		if (aelQuestoesQuestionario.getId() == null) {
			aelQuestoesQuestionario.setId(gerarIdExamesQuestionario(aelQuestoesQuestionario));
			
			this.executarAntesInserir(aelQuestoesQuestionario);
			this.getAelQuestoesQuestionarioDAO().persistir(aelQuestoesQuestionario);
		} else {
			this.executarAntesAtualizar(aelQuestoesQuestionario);
			this.getAelQuestoesQuestionarioDAO().merge(aelQuestoesQuestionario);
		}
	}

	/**
	 * @ORADB AELT_QQU_BRU
	 * @param aelQuestoesQuestionario
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void executarAntesAtualizar(final AelQuestoesQuestionario aelQuestoesQuestionario) throws ApplicationBusinessException {
		aelQuestoesQuestionario.setAlteradoEm(new Date());
	}

	/**
	 * @ORADB AELT_QQU_BRI
	 * @param aelQuestoesQuestionario
	 * @param aelQuestoesQuestionarioOld 
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesInserir(final AelQuestoesQuestionario aelQuestoesQuestionario) throws BaseException  {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelQuestoesQuestionario aelQuestoesQuestionarioOld = this.getAelQuestoesQuestionarioDAO().obterOriginal(aelQuestoesQuestionario.getId());
		if ( aelQuestoesQuestionarioOld != null && aelQuestoesQuestionarioOld.getId() != null) {
			throw new ApplicationBusinessException(QuestaoQuestionarioRNExceptionCode.QUESTAO_JA_ASSOCIADA);
		}
		aelQuestoesQuestionario.setServidor(servidorLogado);
		aelQuestoesQuestionario.setAlteradoEm(new Date());
	}

	private AelQuestoesQuestionarioId gerarIdExamesQuestionario(final AelQuestoesQuestionario aelQuestoesQuestionario) throws ApplicationBusinessException {
		if (aelQuestoesQuestionario.getQuestao() == null || aelQuestoesQuestionario.getAelQuestionarios() == null) {
			throw new ApplicationBusinessException(QuestaoQuestionarioRNExceptionCode.VALORES_OBRIGATORIOS);
		}
		return new AelQuestoesQuestionarioId(aelQuestoesQuestionario.getQuestao().getSeq(), aelQuestoesQuestionario.getAelQuestionarios().getSeq());
	}

	public void excluir(final AelQuestoesQuestionarioId aelQuestoesQuestionario) throws ApplicationBusinessException {
		this.getAelQuestoesQuestionarioDAO().remover(this.getAelQuestoesQuestionarioDAO().obterPorChavePrimaria(aelQuestoesQuestionario));
	}

	protected AelQuestoesQuestionarioDAO getAelQuestoesQuestionarioDAO() {
		return aelQuestoesQuestionarioDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
