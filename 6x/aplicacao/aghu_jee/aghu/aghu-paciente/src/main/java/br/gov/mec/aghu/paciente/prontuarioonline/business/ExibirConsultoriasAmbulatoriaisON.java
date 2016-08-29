package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ExibirConsultoriasAmbulatoriaisON extends BaseBusiness {


	@EJB
	private ExibirConsultoriasAmbulatoriaisRN exibirConsultoriasAmbulatoriaisRN;
	
	private static final Log LOG = LogFactory.getLog(ExibirConsultoriasAmbulatoriaisON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private static final long serialVersionUID = -7171376806770077929L;
	
	public List<MamInterconsultas> pesquisarConsultoriasAmbulatoriais(Integer codigoPaciente) {
		List<MamInterconsultas> interconsultas = getAmbulatorioFacade().pesquisarConsultoriasAmbulatoriais(codigoPaciente);
		for(MamInterconsultas interconsulta : interconsultas) {
			interconsulta.setRespostaConcatenada(getExibirConsultoriasAmbulatoriaisRN().pMontaResposta(interconsulta));
		}
		return interconsultas; 
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected ExibirConsultoriasAmbulatoriaisRN getExibirConsultoriasAmbulatoriaisRN() {
		return exibirConsultoriasAmbulatoriaisRN; 
	}
	
}
