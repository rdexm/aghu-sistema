package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaEstadoPaciente;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEstadoPacienteDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaEstadoPacienteON extends BaseBusiness {


@EJB
private ManterAltaEstadoPacienteRN manterAltaEstadoPacienteRN;

private static final Log LOG = LogFactory.getLog(ManterAltaEstadoPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaEstadoPacienteDAO mpmAltaEstadoPacienteDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -691283474783813060L;

	/**
	 * Atualizar AltaEstadoPaciente do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param novoSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaEstadoPaciente(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		MpmAltaEstadoPaciente altaEstadoPaciente = this.getMpmAltaEstadoPacienteDAO().obterMpmAltaEstadoPaciente(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		if (altaEstadoPaciente != null) {
			
			MpmAltaEstadoPaciente novoAltaEstadoPaciente = new MpmAltaEstadoPaciente();
			novoAltaEstadoPaciente.setAltaSumario(altaSumario);
			novoAltaEstadoPaciente.setEstadoPaciente(altaEstadoPaciente.getEstadoPaciente());
			this.getManterAltaEstadoPacienteRN().inserirAltaEstadoPaciente(novoAltaEstadoPaciente);
			
		}
	}
	
	/**
	 * Método que exclui MpmAltaEstadoPaciente
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaEstadoPaciente(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		altaSumario.setAltaEstadoPaciente(null);
		MpmAltaEstadoPaciente altaEstadoPaciente = this.getMpmAltaEstadoPacienteDAO().obterMpmAltaEstadoPaciente(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		if (altaEstadoPaciente != null) {
			
			this.getManterAltaEstadoPacienteRN().removerAltaEstadoPaciente(altaEstadoPaciente);
			
		}

	}
	
	protected MpmAltaEstadoPacienteDAO getMpmAltaEstadoPacienteDAO() {
		return mpmAltaEstadoPacienteDAO;
	}
	
	protected ManterAltaEstadoPacienteRN getManterAltaEstadoPacienteRN() {
		return manterAltaEstadoPacienteRN;
	}
	
}
