package br.gov.mec.aghu.exames.questionario.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExamesQuestionarioDAO;
import br.gov.mec.aghu.exames.dao.AelRespostaQuestaoDAO;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.AelExamesQuestionarioId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ExameQuestionarioON extends BaseBusiness {

	@EJB
	private ExameQuestionarioRN exameQuestionarioRN;

	private static final Log LOG = LogFactory.getLog(ExameQuestionarioON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AelRespostaQuestaoDAO aelRespostaQuestaoDAO;

	@Inject
	private AelExamesQuestionarioDAO aelExamesQuestionarioDAO;

	private static final long serialVersionUID = -6316855259236832195L;

	public enum ExameQuestionarioONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, ;
	}

	public void remover(final AelExamesQuestionarioId id) throws ApplicationBusinessException {
		final AelExamesQuestionario examesQuestionario = aelExamesQuestionarioDAO.obterPorChavePrimaria(id);
		
		if (examesQuestionario == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		if (examesQuestionario.getAelExQuestionarioOrigens().isEmpty() && this.getAelRespostaQuestaoDAO().contarRespostaQuestaoPorExameQuestionario(examesQuestionario.getId()) == 0) {
			this.getExameQuestionarioRN().remover(examesQuestionario);
		} else {
			final String table;
			if (examesQuestionario.getAelExQuestionarioOrigens().isEmpty()) {
				table = "AEL_RESPOSTAS_QUESTOES";
			} else {
				table = "AEL_EX_QUESTIONARIO_ORIGENS";
			}
			throw new ApplicationBusinessException(ExameQuestionarioONExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, table);
		}
	}

	public void persistir(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException {
		this.getExameQuestionarioRN().persistir(examesQuestionario);
	}

	protected ExameQuestionarioRN getExameQuestionarioRN() {
		return exameQuestionarioRN;
	}

	protected AelRespostaQuestaoDAO getAelRespostaQuestaoDAO() {
		return aelRespostaQuestaoDAO;
	}

	protected AelExamesQuestionarioDAO getAelExamesQuestionarioDAO() {
		return aelExamesQuestionarioDAO;
	}

}
