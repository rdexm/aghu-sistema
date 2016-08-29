package br.gov.mec.aghu.transplante.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MtxOrigens;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class OrigemPacientePaginatorController extends
		ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8141207457021389090L;

	private static final String PAGE_ORIGENS_PACIENTES_CRUD = "origensPacientesCRUD";
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<MtxOrigens> dataModel;
	
	private MtxOrigens mtxOrigensFiltro = new MtxOrigens();
	private MtxOrigens mtxOrigensExcluir = new MtxOrigens();
	private MtxOrigens mtxOrigensSelect;
	private String descricao;
	private DominioSituacao situacao;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar(){
		mtxOrigensFiltro.setSituacao(situacao);
		mtxOrigensFiltro.setDescricao(descricao);
		dataModel.setPesquisaAtiva(true);
		dataModel.reiniciarPaginator();
	}

	public String editar(){
		return PAGE_ORIGENS_PACIENTES_CRUD;
	}
	
	public String incluir(){
		return PAGE_ORIGENS_PACIENTES_CRUD;
	}
	
	public void excluir(){
		try {
			this.transplanteFacade.validarExclusaoOrigemPaciente(mtxOrigensExcluir);
			this.transplanteFacade.excluirOrigemPaciente(mtxOrigensExcluir);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ORIGEM_PACIENTE");
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	/**
	 * @info Se for chamada pela funcao de edicao, nao executa pesquisa
	 * @param pesquisar
	 */
	public void limparPesquisa() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		mtxOrigensExcluir = new MtxOrigens();
		descricao = null;
		situacao = null;
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		pesquisar();
	}
	
	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * @param object {@link Object}
	 */
	private void limparValoresSubmetidos(Object object) {
		
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}

	@Override
	public Long recuperarCount() {
		return transplanteFacade.pesquisarOrigemPacienteCount(mtxOrigensFiltro, false);
	}

	@Override
	public  List<MtxOrigens> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return transplanteFacade.pesquisarOrigemPaciente(mtxOrigensFiltro, firstResult, maxResults, orderProperty, asc);
	}

	public DynamicDataModel<MtxOrigens> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MtxOrigens> dataModel) {
		this.dataModel = dataModel;
	}

	public MtxOrigens getMtxOrigensFiltro() {
		return mtxOrigensFiltro;
	}

	public void setMtxOrigensFiltro(MtxOrigens mtxOrigensFiltro) {
		this.mtxOrigensFiltro = mtxOrigensFiltro;
	}

	public MtxOrigens getMtxOrigensExcluir() {
		return mtxOrigensExcluir;
	}

	public void setMtxOrigensExcluir(MtxOrigens mtxOrigensExcluir) {
		this.mtxOrigensExcluir = mtxOrigensExcluir;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public MtxOrigens getMtxOrigensSelect() {
		return mtxOrigensSelect;
	}

	public void setMtxOrigensSelect(MtxOrigens mtxOrigensSelect) {
		this.mtxOrigensSelect = mtxOrigensSelect;
	}
}
