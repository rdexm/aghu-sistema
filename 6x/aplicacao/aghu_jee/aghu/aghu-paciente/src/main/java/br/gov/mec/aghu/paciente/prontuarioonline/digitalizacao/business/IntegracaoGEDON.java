package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesJn;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipPacientesJnDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class IntegracaoGEDON extends BaseBusiness {

	private static final long serialVersionUID = 1603243747328625591L;

	private static final Log LOG = LogFactory.getLog(IntegracaoGEDON.class);

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IGEDService gedServiceON;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@Inject
	private AipPacientesJnDAO aipPacientesJnDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private List<DocumentoGEDVO> consultarServicoGED(ParametrosGEDVO parametros, Integer ficha) throws ApplicationBusinessException {
		LoginSistemaGEDVO loginGED = obterLoginSistemaGed(parametros.getUsuarioLogado());
		return getGEDServiceON().consultarDocumentosGED(parametros.getCamposFicha(), loginGED.getUsuarioSistema(), loginGED.getSenhaSistema(), ficha);
	}

	public List<DocumentoGEDVO> obterUrlsDocumentosDigitalizados(ParametrosGEDVO parametros) throws ApplicationBusinessException {
		List<DocumentoGEDVO> retorno = new ArrayList<DocumentoGEDVO>();
		List<String> prontuariosUnificados = consultarProntuariosUnificados(parametros.getPacCodigo(), parametros.getProntuario());
		
		Integer ficha = -1;
		if (parametros instanceof ParametrosGEDAtivosVO) {
			ficha = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ID_FICHA_ATIVOS_SISTEMA_GED);
			
		} else if (parametros instanceof ParametrosGEDInativosVO) {
			ficha = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ID_FICHA_INATIVOS_SISTEMA_GED);
			
		} else if (parametros instanceof ParametrosGEDAdministrativosVO) {
			ficha = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ID_FICHA_ADMINISTRATIVOS_SISTEMA_GED);
		}
		
		if (ficha > -1) {
			for (String prontuario : prontuariosUnificados) {
				parametros.setProntuario(prontuario);
				List<DocumentoGEDVO> docs = consultarServicoGED(parametros, ficha);
				for (DocumentoGEDVO doc : docs) {
					if (!retorno.contains(doc)) {
						retorno.add(doc);
					}
				}
			}
		}
		return retorno;
	}

	private List<String> consultarProntuariosUnificados(Integer codigoPaciente, String prontuario) {
		List<String> prontuariosUnificados = new ArrayList<String>();
		prontuariosUnificados.add(prontuario);
		List<AipPacientesJn> aipPacienteJns = getAipPacientesJnDAO().listaPacientesJnComProntuarioAlterado(codigoPaciente);
		for (AipPacientesJn aipPacienteJn : aipPacienteJns) {
			AipPacientes paciente = getPacienteFacade().pesquisarPacientePorProntuario(aipPacienteJn.getProntuario());
			if (paciente == null || paciente.getCodigo().equals(codigoPaciente)) {
				if (!prontuariosUnificados.contains(aipPacienteJn.getProntuario().toString())) {
					prontuariosUnificados.add(aipPacienteJn.getProntuario().toString());
				}
			}
		}
		return prontuariosUnificados;
	}

	private LoginSistemaGEDVO obterLoginSistemaGed(String usuarioLogado) throws ApplicationBusinessException {
		boolean podeImprimir = getCascaFacade().usuarioTemPermissao(usuarioLogado, "permiteImprimirRelatoriosPOL");
		if (podeImprimir) {
			String usuarioGed = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_USUARIO_IMPRESSAO_SISTEMA_GED);
			String senhaGed = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_SENHA_USUARIO_IMPRESSAO_SISTEMA_GED);
			return new LoginSistemaGEDVO(usuarioGed, senhaGed);
		} else {
			String usuarioGed = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_USUARIO_SISTEMA_GED);
			String senhaGed = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_SENHA_USUARIO_SISTEMA_GED);
			return new LoginSistemaGEDVO(usuarioGed, senhaGed);
		}

	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}
	
	public IGEDService getGEDServiceON() {
		return gedServiceON;
	}
	
	private AipPacientesJnDAO getAipPacientesJnDAO() {
		return aipPacientesJnDAO;
	}
}
