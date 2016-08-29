package br.gov.mec.aghu.exames.questionario.business;

import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrupoQuestaoDAO;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoQuestaoON extends BaseBusiness {


@EJB
private GrupoQuestaoRN grupoQuestaoRN;

private static final Log LOG = LogFactory.getLog(GrupoQuestaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelGrupoQuestaoDAO aelGrupoQuestaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1717868513495480521L;
	
	public enum GrupoQuestaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_EXCLUSAO_GRUPO_QUESTAO;
	}
	
	/**
	 * Valida se o grupo possui relacionamento com AEL_QUESTOES_QUESTIONARIO,
	 * 
	 * @param seq
	 * @throws ApplicationBusinessException
	 */
	public void remover(Integer seq) throws ApplicationBusinessException {
		AelGrupoQuestao grupoQuestao = getAelGrupoQuestaoDAO().obterPorChavePrimaria(seq);
		
		if (grupoQuestao == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		Set<AelQuestoesQuestionario> questoesQuestionarios = grupoQuestao.getAelQuestoesQuestionarios();
		if(questoesQuestionarios != null && questoesQuestionarios.size() > 0) {
			throw new ApplicationBusinessException(GrupoQuestaoONExceptionCode.MENSAGEM_ERRO_EXCLUSAO_GRUPO_QUESTAO);
		} 

		getGrupoQuestaoRN().remover(grupoQuestao);
	}
	
	protected GrupoQuestaoRN getGrupoQuestaoRN() {
		return grupoQuestaoRN;
	}
	
	protected AelGrupoQuestaoDAO getAelGrupoQuestaoDAO() {
		return aelGrupoQuestaoDAO;
	}
}