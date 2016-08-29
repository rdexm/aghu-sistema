package br.gov.mec.aghu.paciente.prontuario.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;



public class AdministraSituacaoProntuarioPaginatorController extends
		ActionController implements ActionPaginator {

	
	private static final long serialVersionUID = 6124842381062796325L;
	private static final Log LOG = LogFactory.getLog(AdministraSituacaoProntuarioPaginatorController.class);
	private static final String REDIRECT_MANTER_SITUACAO_PRONTUARIO = "administraSituacaoProntuarioCRUD";
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;	

	@Inject
	private AdministraSituacaoProntuarioController administraSituacaoProntuarioController;
	
	@Inject @Paginator
	private DynamicDataModel<AipPacientes> dataModel;
	
	private AipPacientes paciente = new AipPacientes();
	
	private Integer filtroProntuario;
	
	private Boolean consideraDigito = false;
	
	private Boolean exibeCheckbox;
	
	private AipPacientes pacienteSelecionado;

	
	/**
	 * Método que inicializa a controller.
	 *  
	 */
	@PostConstruct
	public void inicio() {
		LOG.info("Inicio conversação");
		this.begin(this.conversation);
		this.consideraDigito = true;
		this.exibeCheckbox = pacienteFacade.verificarUtilizacaoDigitoVerificadorProntuario();
	}
	
	public void pesquisar() {
		this.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.paciente = new AipPacientes();
		this.filtroProntuario = null;
		this.dataModel.setPesquisaAtiva(false);
	}


	public String editar(){
		administraSituacaoProntuarioController.inicio(pacienteSelecionado.getCodigo());
		return REDIRECT_MANTER_SITUACAO_PRONTUARIO;
	}

	// ### Paginação ###

	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return pacienteFacade.pesquisarSituacaoProntuarioCount(paciente
				.getCodigo(), paciente.getNome(), filtroProntuario,
				paciente.getIndPacienteVip(), paciente.getIndPacProtegido(), consideraDigito);
	}

	@Override
	public List<AipPacientes> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		List<AipPacientes> result = pacienteFacade.pesquisarSituacaoProntuario(
				firstResult, maxResults, paciente.getCodigo(), paciente
						.getNome(), filtroProntuario, paciente
						.getIndPacienteVip(), paciente.getIndPacProtegido(), consideraDigito);

		if (result == null) {
			result = new ArrayList<AipPacientes>();
		}

		return result;
	}
	
	public String obterProntuarioFormatado(Object valor) {
		return CoreUtil.formataProntuario(valor);
	}

	// ### GETs e SETs ###

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	/**
	 * @return the filtroProntuario
	 */
	public Integer getFiltroProntuario() {
		return filtroProntuario;
	}

	/**
	 * @param filtroProntuario the filtroProntuario to set
	 */
	public void setFiltroProntuario(Integer filtroProntuario) {
		this.filtroProntuario = filtroProntuario;
	}

	/**
	 * @return the consideraDigito
	 */
	public Boolean getConsideraDigito() {
		return consideraDigito;
	}

	/**
	 * @param consideraDigito the consideraDigito to set
	 */
	public void setConsideraDigito(Boolean consideraDigito) {
		this.consideraDigito = consideraDigito;
	}

	/**
	 * @return the exibeCheckbox
	 */
	public Boolean getExibeCheckbox() {
		return exibeCheckbox;
	}

	/**
	 * @param exibeCheckbox the exibeCheckbox to set
	 */
	public void setExibeCheckbox(Boolean exibeCheckbox) {
		this.exibeCheckbox = exibeCheckbox;
	}

	public DynamicDataModel<AipPacientes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipPacientes> dataModel) {
		this.dataModel = dataModel;
	}

	public AipPacientes getPacienteSelecionado() {
		return pacienteSelecionado;
	}

	public void setPacienteSelecionado(AipPacientes pacienteSelecionado) {
		this.pacienteSelecionado = pacienteSelecionado;
	}
	
	public Boolean verificarComputadorUbs(){
    	Boolean retorno = false;
    	try {
			AghMicrocomputador micro = administracaoFacade
					.obterAghMicroComputadorPorNomeOuIP(
							getEnderecoRedeHostRemoto(),
							DominioCaracteristicaMicrocomputador.PERFIL_UBS);
			if (micro != null){
				retorno = true;
			}
		} catch (UnknownHostException e) {
			LOG.error("Exception capturada: ", e);
		}
    	return retorno;
    }
	 
//	 public String obterBloqueioUbs(AipPacientes paciente){
//		 String retorno = "N\u00E3o";
//		 AipPacientesBloqueioUbs pacienteBloqueioUBS = cadastroPacienteFacade.obterUltimoIndPacienteBloqueioUbs(paciente.getCodigo());
//		 if (pacienteBloqueioUBS != null && (pacienteBloqueioUBS.getIndPacienteBloqueado() != null && pacienteBloqueioUBS.getIndPacienteBloqueado())){
//			 retorno = "Sim";
//		 }
//		 return retorno;
//	 }
}
