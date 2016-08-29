package br.gov.mec.aghu.paciente.prontuario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do listagem de prontuários para liberação.
 * 
 * @author tiago.felini
 */

public class LiberaProntuarioPaginatorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = 4966756087541210293L;
	private static final Log LOG = LogFactory.getLog(LiberaProntuarioPaginatorController.class);
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@Inject @Paginator
	private DynamicDataModel<AipPacientes> dataModel;

	private Integer codigoPesquisaPaciente;
	private String nomePesquisaPaciente;
	private Integer prontuarioPesquisaPaciente;

	private AipPacientes paciente;

	@PostConstruct
	public void init(){
		this.begin(this.conversation);
	}
	
	public void pesquisar() {
		if (codigoPesquisaPaciente != null || !StringUtils.isBlank(nomePesquisaPaciente) || prontuarioPesquisaPaciente!=null) {
			this.dataModel.reiniciarPaginator();
		}else{
			this.apresentarMsgNegocio(Severity.ERROR,"MESSAGEM_ERRO_FILTRO_PRONTUARIO");
			getDataModel().limparPesquisa();
		}
	}

	public void limparPesquisa() {
		this.codigoPesquisaPaciente = null;
		this.nomePesquisaPaciente = null;
		this.prontuarioPesquisaPaciente = null;
		this.dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de
	 * Liberação de prontuário.
	 */
	public void excluir() {
		reiniciarPaginator();
		try {
			
			if (paciente != null) {
				this.pacienteFacade.excluirProntuario(paciente, obterLoginUsuarioLogado());
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_PRONTUARIO");
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,
						"MENSAGEM_ERRO_REMOCAO_PRONTUARIO");
			}
			this.paciente = null;
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getClass().getName(),e);
			apresentarExcecaoNegocio(e);
			
		}
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return pacienteFacade.pesquisaProntuarioPacienteCount(
				this.codigoPesquisaPaciente, this.nomePesquisaPaciente,
				this.prontuarioPesquisaPaciente);
	}

	@Override
	public List<AipPacientes> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		return pacienteFacade.pesquisaProntuarioPaciente(firstResult,
				maxResults, this.codigoPesquisaPaciente,
				this.nomePesquisaPaciente,this.prontuarioPesquisaPaciente);
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}

	// ### GETs e SETs ###

	public Integer getCodigoPesquisaPaciente() {
		return codigoPesquisaPaciente;
	}

	public void setCodigoPesquisaPaciente(Integer codigoPesquisaPaciente) {
		this.codigoPesquisaPaciente = codigoPesquisaPaciente;
	}

	public String getNomePesquisaPaciente() {
		return nomePesquisaPaciente;
	}

	public void setNomePesquisaPaciente(String nomePesquisaPaciente) {
		this.nomePesquisaPaciente = nomePesquisaPaciente;
	}

	public Integer getProntuarioPesquisaPaciente() {
		return prontuarioPesquisaPaciente;
	}

	public void setProntuarioPesquisaPaciente(Integer prontuarioPesquisaPaciente) {
		this.prontuarioPesquisaPaciente = prontuarioPesquisaPaciente;
	}
	
	public DynamicDataModel<AipPacientes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipPacientes> dataModel) {
		this.dataModel = dataModel;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

}
