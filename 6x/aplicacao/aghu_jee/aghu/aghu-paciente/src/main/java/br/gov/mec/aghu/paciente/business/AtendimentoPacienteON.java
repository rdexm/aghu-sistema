package br.gov.mec.aghu.paciente.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentoPacientesId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class AtendimentoPacienteON extends BaseBusiness {


@EJB
private AtendimentoPacienteRN atendimentoPacienteRN;

private static final Log LOG = LogFactory.getLog(AtendimentoPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3217430401326632770L;

	/**
	 * Se não existe o registro em AGH_ATENDIMENTO_PACIENTES realiza o insert
	 * @param altan_atd_seq
	 * @param altan_apa_seq
	 */
	public Integer verificarAtendimentoPaciente(Integer altanAtdSeq, Integer altanApaSeq, MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		boolean existePaciente = this.getAghuFacade().verificarAghAtendimentoPacientes(altanAtdSeq, altanApaSeq);
		AghAtendimentoPacientes atendimentoPacientes;
		if (!existePaciente) {
			atendimentoPacientes = new AghAtendimentoPacientes();
			AghAtendimentoPacientesId id = new AghAtendimentoPacientesId();
			Short numeroRn = 0;
			altanApaSeq = this.getAghuFacade().obterValorSequencialIdAghAtendimentoPacientes();
			id.setAtdSeq(altanAtdSeq);
			id.setSeq(altanApaSeq);
			
			atendimentoPacientes.setId(id);
			atendimentoPacientes.setNumeroRn(numeroRn);
			atendimentoPacientes.setIndRn(false);
			
			incluirAtendimentoPaciente(atendimentoPacientes);
			this.flush();
		} else {
			atendimentoPacientes = this.getAghuFacade().obterAtendimentoPaciente(altanAtdSeq, altanApaSeq);
		}
		
		altaSumario.setAtendimentoPaciente(atendimentoPacientes);
		return altanApaSeq;
	}
	
	/**
	 * ORADB AGHT_APA_BRI
	 * @param atendimentoPacientes
	 */
	public void incluirAtendimentoPaciente(AghAtendimentoPacientes atendimentoPacientes) throws ApplicationBusinessException {
		this.getAtendimentoPacienteRN().incluirAtendimentoPaciente(atendimentoPacientes);
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected AtendimentoPacienteRN getAtendimentoPacienteRN() {
		return atendimentoPacienteRN;
	}

}
