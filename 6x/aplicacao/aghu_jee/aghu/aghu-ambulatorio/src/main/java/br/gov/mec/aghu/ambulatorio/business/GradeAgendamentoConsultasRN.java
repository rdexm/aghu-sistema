package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacHorarioGradeConsultaDAO;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacHorarioGradeConsulta;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class GradeAgendamentoConsultasRN extends BaseBusiness {
	
	private static final long serialVersionUID = 981762262306981889L;

	private static final Log LOG = LogFactory
			.getLog(GradeAgendamentoConsultasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AacConsultasDAO aacConsultasDAO;

	@EJB
	private ManterGradeAgendamentoRN manterGradeAgendamentoRN;
	
	@EJB
	private ManterGradeAgendamentoON manterGradeAgendamentoON;
	
	@Inject
	private AacHorarioGradeConsultaDAO aacHorarioGradeConsultaDAO;
	

	public String verificaSituacaoGrade(final AacGradeAgendamenConsultas grade,
			final Date data) throws ApplicationBusinessException {
		return getConsultasDAO().verificaSituacaoGrade(grade, data);
	}
	
	public void salvarHorarioGradeConsulta(AacHorarioGradeConsulta entity, 
			AacGradeAgendamenConsultas entityPai) throws ApplicationBusinessException {

		if (entity.getId() == null) {
			getManterGradeAgendamentoRN().preInserirAacHorarioGradeConsulta(entity);
			getAacHorarioGradeConsultaDAO().criaRegistroId(entity, entityPai);
			getManterGradeAgendamentoON().eventoPreInserirUpdate(entity);
			getAacHorarioGradeConsultaDAO().inserir(entity, entityPai);
		} else {
			AacHorarioGradeConsulta oldEntity = getAacHorarioGradeConsultaDAO().obterOriginal(entity);
			getManterGradeAgendamentoRN().preAtualzarAacHorarioGradeConsulta(entity, oldEntity);
			getAacHorarioGradeConsultaDAO().atualizar(entity);
			getAacHorarioGradeConsultaDAO().flush();
			getManterGradeAgendamentoRN().posUpdateAacHorarioGradeConsultaJournal(entity, oldEntity);
		}
	}

	private ManterGradeAgendamentoON getManterGradeAgendamentoON() {
		return this.manterGradeAgendamentoON;
	}

	private AacHorarioGradeConsultaDAO getAacHorarioGradeConsultaDAO() {
		return this.aacHorarioGradeConsultaDAO;
	}

	private ManterGradeAgendamentoRN getManterGradeAgendamentoRN() {
		return this.manterGradeAgendamentoRN;
	}

	protected AacConsultasDAO getConsultasDAO() {
		return aacConsultasDAO;
	}

}
