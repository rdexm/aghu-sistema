package br.gov.mec.aghu.paciente;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.paciente.business.IPacienteFacade;

public final class PacienteHelper {

	private static final Log LOG = LogFactory.getLog(PacienteHelper.class);

	private PacienteHelper(){}

	private static final String PACIENTE_FACADE = "java:global/aghu/aghu-paciente/PacienteFacade!br.gov.mec.aghu.paciente.business.IPacienteFacade";

	public synchronized static IPacienteFacade getFacade() {
		IPacienteFacade facade = null;
		try {
			facade = (IPacienteFacade) InitialContext.doLookup(PACIENTE_FACADE);
		} catch (NamingException ex) {
			LOG.error("Nao foi possivel localizar EJB: PacienteFacade");
		}
		return facade;
	}

}
