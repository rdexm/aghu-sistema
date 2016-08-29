package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamDiagnosticoDAO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class DiagnosticoON extends BaseBusiness {





private static final Log LOG = LogFactory.getLog(DiagnosticoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MamDiagnosticoDAO mamDiagnosticoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 945255300699579054L;

	/**
	 * Lista os diagnósticos Ativos e validados de acordo com paciente e cid.
	 * 
	 * @param paciente
	 * @param cid
	 * @return
	 */
	public List<MamDiagnostico> listarDiagnosticosPorPacienteCid(
			AipPacientes paciente, AghCid cid) {
		return getDiagnosticoDAO().listarDiagnosticosPorPacienteCid(paciente,
				cid);
	}

	/**
	 * Método que retorna os diagnósticos ativos relativos a um cidAtendimento.
	 * 
	 * @param cidAtendimento
	 * @return
	 */
	public List<MamDiagnostico> listarDiagnosticosAtivosPorCidAtendimento(
			MpmCidAtendimento cidAtendimento) {
		return this.getDiagnosticoDAO()
				.listarDiagnosticosAtivosPorCidAtendimento(cidAtendimento);
	}

	/**
	 * Retorna os diagnósticos de um atendimento.
	 * 
	 * @param atendimento
	 * @return
	 */
	public List<MamDiagnostico> listardiagnosticosPorAtendimento(
			AghAtendimentos atendimento) {
		return this.getDiagnosticoDAO().listarDiagnosticosPorAtendimento(
				atendimento);
	}
	
	private MamDiagnosticoDAO getDiagnosticoDAO() {
		return mamDiagnosticoDAO;
	}

}
