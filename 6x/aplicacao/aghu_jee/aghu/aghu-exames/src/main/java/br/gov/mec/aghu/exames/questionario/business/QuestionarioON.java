package br.gov.mec.aghu.exames.questionario.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelQuestionariosDAO;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class QuestionarioON extends BaseBusiness {

	
	@EJB
	private QuestionarioRN questionarioRN;
	
	private static final Log LOG = LogFactory.getLog(QuestionarioON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AelQuestionariosDAO aelQuestionariosDAO;

	private static final long serialVersionUID = 1717868513495480521L;
	
	public enum QuestionarioONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_REMOVER_DEPENDENCIAS;
	}
	
	/**
	 * Valida se o questionário possui relacionamento com AEL_EXAMES_QUESTIONARIO,
	 * AEL_QUESTIONARIOS_CONV_UNID ou AEL_QUESTOES_QUESTIONARIO
	 */
	public void validarRelacionamentosQuestionarioBeforeDelete(Integer seq) throws ApplicationBusinessException {
		AelQuestionarios questionario = getAelQuestionariosDAO().obterPorChavePrimaria(seq);
		
		if (questionario == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		if(questionario.getAelExamesQuestionarios() == null || questionario.getAelExamesQuestionarios().size() > 0) {
			throw new ApplicationBusinessException(QuestionarioONExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, "AEL_EXAMES_QUESTIONARIO");
			
		} else if(questionario.getAelQuestionariosConvUnids() == null || questionario.getAelQuestionariosConvUnids().size() > 0) {
			throw new ApplicationBusinessException(QuestionarioONExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, "AEL_QUESTIONARIOS_CONV_UNID");
			
		} else if(questionario.getAelQuestoesQuestionarios() == null || questionario.getAelQuestoesQuestionarios().size() > 0) {
			throw new ApplicationBusinessException(QuestionarioONExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, "AEL_QUESTOES_QUESTIONARIO");
		}
		
		getQuestionarioRN().executarBeforeDeleteQuestionario(questionario.getCriadoEm());
		getAelQuestionariosDAO().remover(questionario);
	}
	
	protected QuestionarioRN getQuestionarioRN() {
		return questionarioRN;
	}
	
	protected AelQuestionariosDAO getAelQuestionariosDAO() {
		return aelQuestionariosDAO;
	}
}