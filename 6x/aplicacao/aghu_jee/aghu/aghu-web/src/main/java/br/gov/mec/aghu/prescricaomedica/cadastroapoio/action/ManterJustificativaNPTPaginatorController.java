package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmJustificativaNpt;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ManterJustificativaNPTPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6806503339380073715L;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject
	private ManterJustificativaNPTController manterJustificativaNPTController;

	private DominioSituacao situacao;
	private Short codigo;
	private String descricao;
	
	private List<MpmJustificativaNpt> lista;

	private MpmJustificativaNpt itemSelecionado;
	
	private boolean pesquisaAtiva;
	
	@Inject
	@Paginator
	private DynamicDataModel<MpmJustificativaNpt> dataModel;
	
	private final String PAGE_MANTER_JUSTIFICATIVA_NPT_CRUD = "prescricaomedica-manterJustificativaNPTCRUD";
	
	private static final String MANTER_JUSTIFICATIVA_NPT_LIST = "manterJustificativaNPTList";
	
	private static final String EXECUTAR = "executar";
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void iniciar() {
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), MANTER_JUSTIFICATIVA_NPT_LIST,	EXECUTAR);
		this.getDataModel().setUserEditPermission(permissao);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		pesquisaAtiva = true;
	}
	
	public void limparPesquisa() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
		lista = new ArrayList<MpmJustificativaNpt>();
		this.setPesquisaAtiva(false);
	}
	
	
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

	public String novaJustificativa(){
		manterJustificativaNPTController.setJustificativa(new MpmJustificativaNpt());
		manterJustificativaNPTController.setSituacao(Boolean.TRUE);
		manterJustificativaNPTController.setEdicao(Boolean.FALSE);
		return PAGE_MANTER_JUSTIFICATIVA_NPT_CRUD;
	}
	
	public String editar() {
		manterJustificativaNPTController.setEdicao(Boolean.TRUE);
		manterJustificativaNPTController.setJustificativa(itemSelecionado);
		
		if(itemSelecionado.getSituacao().equals(DominioSituacao.A) ){
			manterJustificativaNPTController.setSituacao(Boolean.TRUE);
		}else{
			manterJustificativaNPTController.setSituacao(Boolean.FALSE);
		}
		return PAGE_MANTER_JUSTIFICATIVA_NPT_CRUD;
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public DynamicDataModel<MpmJustificativaNpt> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmJustificativaNpt> dataModel) {
		this.dataModel = dataModel;
	}

	public List<MpmJustificativaNpt> getLista() {
		return lista;
	}

	public void setLista(List<MpmJustificativaNpt> lista) {
		this.lista = lista;
	}
	
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public MpmJustificativaNpt getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MpmJustificativaNpt itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	@Override
	public Long recuperarCount() {
		return prescricaoMedicaFacade.listarJustificativaNPTCount(this.codigo, this.descricao, this.situacao);
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public List<MpmJustificativaNpt> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.prescricaoMedicaFacade.listarJustificativasNPT(firstResult, maxResult, orderProperty,
				true, this.codigo, this.descricao, this.situacao);
	}
}