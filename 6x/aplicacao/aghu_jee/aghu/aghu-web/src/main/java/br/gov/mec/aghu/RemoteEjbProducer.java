package br.gov.mec.aghu;

import java.util.Hashtable;

import javax.enterprise.inject.Produces;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService;
import br.gov.mec.aghu.blococirurgico.service.IBlocoCirurgicoService;
import br.gov.mec.aghu.casca.service.ICascaService;
import br.gov.mec.aghu.certificacaodigital.service.ICertificacaoDigitalService;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.controlepaciente.service.IControlePacienteService;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.internacao.service.IInternacaoService;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.prescricaomedica.service.IPrescricaoMedicaService;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;

/**
 * Fornece os ejbs remotos.
 * 
 * @author cvagheti
 * 
 */
public class RemoteEjbProducer {

	private static final Log LOG = LogFactory.getLog(RemoteEjbProducer.class);

	private InitialContext createContext() {
		InitialContext context = null;
		final Hashtable<String, Object> jndiProperties = new Hashtable<String, Object>();// NOPMD
		// setup the ejb: namespace URL factory
		jndiProperties.put(Context.URL_PKG_PREFIXES,
				"org.jboss.ejb.client.naming");
		jndiProperties.put("jboss.naming.client.ejb.context", true);
	
		try {
			context = new javax.naming.InitialContext(jndiProperties);
		} catch (NamingException e) {
			LOG.error("Erro ao criar contexto para conexão remota jndi", e);
		}
		return context;
	}

	@Produces
	public IRegistroColaboradorService getRegistroColaboradorService() {
		String name = "ejb:aghu/aghu-registrocolaborador-service/RegistroColaboradorServiceImpl!br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService";
		InitialContext context = createContext();
		return (IRegistroColaboradorService) this.ejbLookup(context, name);
	}

	@Produces
	public IInternacaoService getInternacaoService() {
		String name = "ejb:aghu/aghu-internacao-service/InternacaoServiceImpl!br.gov.mec.aghu.internacao.service.IInternacaoService";
		InitialContext context = createContext();
		return (IInternacaoService) this.ejbLookup(context, name);
	}

	@Produces
	public IConfiguracaoService getConfiguracaoService() {
		String name = "ejb:aghu/aghu-configuracao-service/ConfiguracaoServiceImpl!br.gov.mec.aghu.configuracao.service.IConfiguracaoService";
		InitialContext context = createContext();
		return (IConfiguracaoService) this.ejbLookup(context, name);
	}
	
	@Produces
	public IAmbulatorioService getAmbulatorioService() {
		String name = "ejb:aghu/aghu-ambulatorio-service/AmbulatorioServiceImpl!br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService";
		InitialContext context = createContext();
		return (IAmbulatorioService) this.ejbLookup(context, name);
	}
	
	@Produces
	public IPacienteService getPacienteService() {
		String name = "ejb:aghu/aghu-paciente-service/PacienteServiceImpl!br.gov.mec.aghu.paciente.service.IPacienteService";
		InitialContext context = createContext();
		return (IPacienteService) this.ejbLookup(context, name);
	}
	
	@Produces
	public IControlePacienteService getControlePacienteService() {
		String name = "ejb:aghu/aghu-controlepaciente-service/ControlePacienteServiceImpl!br.gov.mec.aghu.controlepaciente.service.IControlePacienteService";
		InitialContext context = createContext();
		return (IControlePacienteService) this.ejbLookup(context, name);
	}
	
	@Produces
	public IPrescricaoMedicaService getPrescricaoMedicaService() {
		String name = "ejb:aghu/aghu-prescricaomedica-service/PrescricaoMedicaServiceImpl!br.gov.mec.aghu.prescricaomedica.service.IPrescricaoMedicaService";
		InitialContext context = createContext();
		return (IPrescricaoMedicaService) this.ejbLookup(context, name);
	}
	
	@Produces
	public IExamesService getExamesService() {
		String name = "ejb:aghu/aghu-exames-service/ExamesServiceImpl!br.gov.mec.aghu.exames.service.IExamesService";
		InitialContext context = createContext();
		return (IExamesService) this.ejbLookup(context, name);
	}
	
	@Produces
	public IBlocoCirurgicoService getBlocoCirurgicoService() {
		String name = "ejb:aghu/aghu-blococirurgico-service/BlocoCirurgicoService!br.gov.mec.aghu.blococirurgico.service.IBlocoCirurgicoService";
		InitialContext context = createContext();
		return (IBlocoCirurgicoService) this.ejbLookup(context, name);
	}
	
	@Produces
	public ICertificacaoDigitalService getCertificacaoDigitalService() {
		String name = "ejb:aghu/aghu-certificacaodigital-service/CertificacaoDigitalService!br.gov.mec.aghu.certificacaodigital.service.ICertificacaoDigitalService";
		InitialContext context = createContext();
		return (ICertificacaoDigitalService) this.ejbLookup(context, name);
	}
	
	@Produces
	public ICascaService getCascaService() {
		String name = "ejb:aghu/aghu-casca-service/CascaService!br.gov.mec.aghu.casca.service.ICascaService";
		InitialContext context = createContext();
		return (ICascaService) this.ejbLookup(context, name);
	}
 
	private Object ejbLookup(InitialContext context, String name) {
		Object result = null;
		try {
			result = context.lookup(name);
		} catch (NamingException e) {
			LOG.error("Erro no lookup jndiName =" + name, e);
		}
		return result;
	}

}
