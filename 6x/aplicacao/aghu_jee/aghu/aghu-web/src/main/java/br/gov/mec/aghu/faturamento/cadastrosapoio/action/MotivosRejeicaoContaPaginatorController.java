package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

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
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatMotivoRejeicaoConta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MotivosRejeicaoContaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -7124873009503527428L;
	
	private static final String PAGE_MOTIVOS_REJEICAO_CONTA_CRUD = "faturamento-motivosRejeicaoContaCRUD";
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject 
	@Paginator
	private DynamicDataModel<FatMotivoRejeicaoConta> dataModel;
	
	private FatMotivoRejeicaoConta filtro = new FatMotivoRejeicaoConta();
	
	private FatMotivoRejeicaoConta entitySelecionado;
	
	@PostConstruct
	protected void inicializar() {

		begin(conversation);
	}
	
	/**
	 * Valida se o usuario tem permissao para acessar as funcionalidades da pagina
	 */
	public void iniciar() {
		
		if (super.isValidInitMethod()) {
	 
			final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterCadastrosBasicosFaturamento", "executar");
		this.getDataModel().setUserEditPermission(permissao);
		}
	}
	
	/**
	 * Ação do botão Pesquisar
	 */
	public void pesquisar() {
		
		this.dataModel.reiniciarPaginator();
	}
	
	/**
	 * Ação do botão Limpar
	 */
	public void limpar() {
		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();

		while (componentes.hasNext()) {
			
			limparValoresSubmetidos(componentes.next());
		}
		
		this.filtro = new FatMotivoRejeicaoConta();
		this.dataModel.limparPesquisa();
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
	
	/**
	 * Trunca texto da Grid caso o mesmo ultrapasse o tamanho indicado.
	 * 
	 * @param item {@link String}
	 * @param tamanhoMaximo {@link Integer}
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {

		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
			
		return item;
	}

	/**
	 * Ação do link Editar na tabela de registros
	 */
	public String editar() {
		
		return incluir();
	}

	/**
	 * Ação do link Ativar/Desativar
	 */
	public void ativarDesativar() {
		
		if (this.entitySelecionado != null) {
			
			if (DominioSituacao.A.equals(this.entitySelecionado.getSituacao())) {
				
				this.entitySelecionado.setSituacao(DominioSituacao.I);
				
			} else {
				
				this.entitySelecionado.setSituacao(DominioSituacao.A);
			}
			
			try {
				
				this.faturamentoApoioFacade.gravarMotivoRejeicaoConta(this.entitySelecionado);
				
			} catch (ApplicationBusinessException e) {
				
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	/**
	 * Ação do botão Novo 
	 */
	public String incluir() {
		
		return PAGE_MOTIVOS_REJEICAO_CONTA_CRUD;
	}

	@Override
	public List<FatMotivoRejeicaoConta> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		return this.faturamentoApoioFacade.recuperarListaPaginadaMotivosRejeicaoConta(firstResult, maxResult, orderProperty, asc, filtro);
	}

	@Override
	public Long recuperarCount() {
		
		return this.faturamentoApoioFacade.recuperarCountMotivosRejeicaoConta(filtro);
	}

	/**
	 *
	 * GET's and SET's
	 * 
	 */
	public DynamicDataModel<FatMotivoRejeicaoConta> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatMotivoRejeicaoConta> dataModel) {
		this.dataModel = dataModel;
	}

	public FatMotivoRejeicaoConta getFiltro() {
		return filtro;
	}

	public void setFiltro(FatMotivoRejeicaoConta filtro) {
		this.filtro = filtro;
	}

	public FatMotivoRejeicaoConta getEntitySelecionado() {
		return entitySelecionado;
	}

	public void setEntitySelecionado(FatMotivoRejeicaoConta entitySelecionado) {
		this.entitySelecionado = entitySelecionado;
	}
}
