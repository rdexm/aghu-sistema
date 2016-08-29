package br.gov.mec.aghu.transplante.action;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class PacientesListaTMOPaginatorController extends ActionController implements Serializable, ActionPaginator {
	
	private static final long serialVersionUID = 5036031608024552370L;
	
	@Inject @Paginator
	private DynamicDataModel<AipPacientes> dataModel;
	
	@Inject
	private IPacienteFacade pacienteFacade;
	
	private AipPacientes paciente;
	
	private AipPacientes selecionado;
	
	
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	private Boolean realizouPesquisaFonetica = false;
	private boolean exibirBotaoIncluir = false;
	
	
	// Filtros para pesquisa
	private Integer pacCodigo;
	
	@PostConstruct
	public void inicializar() {
		this.begin(conversation,true);
		limparCampos();
		
	}
		
	public void iniciar() {
		setExibirBotaoIncluir(exibirBotaoIncluir);
		if(pacCodigo!=null){
			pesquisarPacientePesquisaFonetica();
		}
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(Integer.valueOf((String) event.getNewValue()), event.getComponent().getId());	
			
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void pesquisarPacientePesquisaFonetica(){
		paciente = pacienteFacade.obterPaciente(this.pacCodigo);

	}
	
	public void limparCampos() {
		pacCodigo=null;
		exibirBotaoIncluir = false;
		this.selecionado=null;
		this.paciente = null;
		dataModel.setPesquisaAtiva(false);
		
	}
	
	public void pesquisar(){
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(true);
		exibirBotaoIncluir = true;
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}
	
	
	@Override
	public Long recuperarCount() {
		return this.pacienteFacade.consultarDadosPacientePorNomeOuProntuarioOuCodigoCount(paciente);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AipPacientes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.pacienteFacade.consultarDadosPacientePorNomeOuProntuarioOuCodigo(paciente, firstResult, maxResult, orderProperty, asc);
	}
	
	public String redirecionarPesquisaFonetica(){
		return PESQUISA_FONETICA;
	}


	public Boolean getRealizouPesquisaFonetica() {
		return realizouPesquisaFonetica;
	}

	public void setRealizouPesquisaFonetica(Boolean realizouPesquisaFonetica) {
		this.realizouPesquisaFonetica = realizouPesquisaFonetica;
	}

	public boolean isExibirBotaoIncluir() {
		return exibirBotaoIncluir;
	}

	public void setExibirBotaoIncluir(boolean exibirBotaoIncluir) {
		this.exibirBotaoIncluir = exibirBotaoIncluir;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
		
	}
	
	public DynamicDataModel<AipPacientes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AipPacientes> dataModel) {
		this.dataModel = dataModel;
	}
	
	public AipPacientes getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AipPacientes selecionado) {
		this.selecionado = selecionado;
	}
	
}
