package br.gov.mec.aghu.exames.agendamento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrupoExameUnidExameDAO;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExameId;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoExameON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(GrupoExameON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelGrupoExameUnidExameDAO aelGrupoExameUnidExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1717868513495480521L;
	
	public enum GrupoExameONExceptionCode implements BusinessExceptionCode {
		ERRO_EXCLUIR_GRUPO_EXAME_UTILIZADO_EM_GRADES_DE_AGENDAMENTO, ERRO_INSERIR_EXAME_EM_GRUPO_DE_EXAME;
	}
	
	/**
	 * Valida se o grupo de exame está sendo utilizado em grades de agendamento
	 * de exames.
	 * 
	 * @param grupoExame
	 * @throws ApplicationBusinessException
	 */
	public void validarGrupoExameUtilizadoGradeAgendaExame(AelGrupoExames grupoExame) throws ApplicationBusinessException {
		if(grupoExame.getGradeAgendaExame() == null || grupoExame.getGradeAgendaExame().size() > 0) {
			throw new ApplicationBusinessException(
					GrupoExameONExceptionCode.ERRO_EXCLUIR_GRUPO_EXAME_UTILIZADO_EM_GRADES_DE_AGENDAMENTO);
		}
	}
	
	public void validarExameExistenteEmGrupoExame(AelGrupoExameUnidExameId id) throws ApplicationBusinessException {
		AelGrupoExameUnidExame exame = getAelGrupoExameUnidExameDAO().obterPorChavePrimaria(id);
		if(exame != null) {
			throw new ApplicationBusinessException(
					GrupoExameONExceptionCode.ERRO_INSERIR_EXAME_EM_GRUPO_DE_EXAME);
		}
	}
	
	protected AelGrupoExameUnidExameDAO getAelGrupoExameUnidExameDAO() {
		return aelGrupoExameUnidExameDAO;
	}
}