package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;






import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptControleFrequencia;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptControleFrequenciaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class MptControleFrequenciaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MptControleFrequenciaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = -7669468828965606045L;
	
	@Inject
	private MptControleFrequenciaDAO mptControleFrequenciaDAO;
	@Inject AipPacientesDAO aipPacientesDao;
	
	public void inserirControleFrequencia(MptControleFrequencia mptControleFrequencia) {
		mptControleFrequenciaDAO.persistir(mptControleFrequencia);
		
	}
	
	public List<AipPacientes> consultaPacientePorCodigo(Integer codigoPaciente) {
		return aipPacientesDao.executarCursorPac(codigoPaciente);
	}
	
	
}
