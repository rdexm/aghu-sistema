package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe controle da tela Pesquisar Motivos de Saída de Pacientes
 * 
 * @author rafael.silvestre
 */
public class MotivoSaidaPacientePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5028205618635435897L;

	private static final String PAGE_CADASTRAR_MOTIVO_SAIDA_PACIENTE = "faturamento-motivoSaidaPacienteCRUD";
	
	private static final String PAGE_DETALHAR_MOTIVO_SAIDA_PACIENTE = "faturamento-situacaoSaidaPacienteList";
	
	private static final String MENSAGEM_SUCESSO_EDICAO = "MENSAGEM_SUCESSO_EDICAO_MOTIVO_SAIDA_PACIENTE";
	
	private static final String MENSAGEM_SUCESSO_EXCLUSAO = "MENSAGEM_SUCESSO_EXCLUSAO_MOTIVO_SAIDA_PACIENTE";
	
	private static final String CADASTROS_BASICOS_FATURAMENTO = "manterCadastrosBasicosFaturamento";
	
	private static final String EXECUTAR = "executar";
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IPermissionService permissionService;

	@Inject 
	@Paginator
	private DynamicDataModel<FatMotivoSaidaPaciente> dataModel;
	
	private FatMotivoSaidaPaciente entityFiltro = new FatMotivoSaidaPaciente();
	
	private FatMotivoSaidaPaciente entitySelecionado;
	
	@PostConstruct
	protected void inicializar() {

		begin(conversation);
	}
	
	public void iniciar() {
		
		if (super.isValidInitMethod()) {
		
			final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), CADASTROS_BASICOS_FATURAMENTO, EXECUTAR);
		this.getDataModel().setUserEditPermission(permissao);
		this.getDataModel().setUserRemovePermission(permissao);
		}
	}
	
	@Override
	public List<FatMotivoSaidaPaciente> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		return this.faturamentoApoioFacade.recuperarListaPaginadaMotivoSaidaPaciente(firstResult, maxResult, orderProperty, asc, this.entityFiltro);
	}

	@Override
	public Long recuperarCount() {
		
		return faturamentoApoioFacade.recuperarCountMotivoSaidaPaciente(this.entityFiltro);
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
		
		this.entityFiltro = new FatMotivoSaidaPaciente();
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
	 * Ação do link Editar
	 */
	public String editar() {
		
		return incluir();
	}
	
	/**
	 * Ação do link Ativar/Inativar
	 */
	public void ativarInativar() {
		
		if (this.entitySelecionado != null) {
			
			if (DominioSituacao.A.equals(this.entitySelecionado.getSituacao())) {
				
				this.entitySelecionado.setSituacao(DominioSituacao.I);
				
			} else {
				
				this.entitySelecionado.setSituacao(DominioSituacao.A);
			}
			
			try {
				
				this.faturamentoApoioFacade.gravarMotivoSaidaPaciente(this.entitySelecionado);
				
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EDICAO);
				
			} catch (ApplicationBusinessException e) {
				
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	/**
	 * Ação do botão Novo 
	 */
	public String incluir() {
		
		return PAGE_CADASTRAR_MOTIVO_SAIDA_PACIENTE;
	}
	
	/**
	 * Ação do link Detalhar 
	 */
	public String detalhar() {
		
		return PAGE_DETALHAR_MOTIVO_SAIDA_PACIENTE;
	}
	
	/**
	 * Ação do link Excluir
	 */
	public void excluir() {
		
		if (this.entitySelecionado != null && this.entitySelecionado.getSeq() != null) {
			
			try {
				
				this.faturamentoApoioFacade.removerMotivoSaidaPaciente(this.entitySelecionado.getSeq());
				
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EXCLUSAO);
				
			} catch (ApplicationBusinessException e) {
				
				apresentarExcecaoNegocio(e);
	}
		}
	}
	
	/**
	 * 
	 * GET's e SET's
	 * 
	 */
	public DynamicDataModel<FatMotivoSaidaPaciente> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatMotivoSaidaPaciente> dataModel) {
		this.dataModel = dataModel;
	}

	public FatMotivoSaidaPaciente getEntitySelecionado() {
		return entitySelecionado;
	}

	public void setEntitySelecionado(FatMotivoSaidaPaciente entitySelecionado) {
		this.entitySelecionado = entitySelecionado;
	}

	public FatMotivoSaidaPaciente getEntityFiltro() {
		return entityFiltro;
	}

	public void setEntityFiltro(FatMotivoSaidaPaciente entityFiltro) {
		this.entityFiltro = entityFiltro;
	}
}
