package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioVivoMorto;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatCadCidNascimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * Classe responsável por controlar a Pesquisa de CIDs por Nascimento
 * 
 * @author rafael.silvestre
 */
public class CadastroCidNascimentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8066419203064167113L;

	private static final String PAGE_CADASTRO_CID_NASCIMENTO = "faturamento-cadastroCidNascimentoCRUD";
	
	private static final String CADASTROS_BASICOS_FATURAMENTO = "manterCadastrosBasicosFaturamento";
	
	private static final String EXECUTAR = "executar";
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IPermissionService permissionService;

	@Inject 
	@Paginator
	private DynamicDataModel<FatCadCidNascimento> dataModel;
	
	private DominioVivoMorto vivo;
	
	private DominioVivoMorto morto;
	
	private String cid;
	
	private FatCadCidNascimento fatCadCidNascimentoSelecionado;
	
	@PostConstruct
	protected void inicializar() {

		begin(conversation);
	}
	
	public void iniciar() {
		
		if (super.isValidInitMethod()) {
			
			final Boolean permissao = this.permissionService
					.usuarioTemPermissao(this.obterLoginUsuarioLogado(),
							CADASTROS_BASICOS_FATURAMENTO, EXECUTAR);
		this.getDataModel().setUserEditPermission(permissao);
		}
	}
	
	@Override
	public List<FatCadCidNascimento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		return this.faturamentoApoioFacade.pesquisarFatCadCidNascimento(firstResult, maxResult, orderProperty, asc, 
				this.vivo, this.morto, this.cid);
	}

	@Override
	public Long recuperarCount() {
		
		return faturamentoApoioFacade.pesquisaCountFatCadCidNascimento(this.vivo, this.morto, this.cid);
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
		
		this.vivo = null;
		this.morto = null;
		this.cid = null;
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
	 * Ação do link Editar na tabela de registros
	 */
	public String editar() {
		
		return incluir();
	}
	
	/**
	 * Ação do botão Novo 
	 */
	public String incluir() {
		
		return PAGE_CADASTRO_CID_NASCIMENTO;
	}
	
	/**
	 * 
	 * GET's e SET's
	 * 
	 */
	public DynamicDataModel<FatCadCidNascimento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatCadCidNascimento> dataModel) {
		this.dataModel = dataModel;
	}

	public DominioVivoMorto getVivo() {
		return vivo;
	}

	public void setVivo(DominioVivoMorto vivo) {
		this.vivo = vivo;
	}

	public DominioVivoMorto getMorto() {
		return morto;
	}

	public void setMorto(DominioVivoMorto morto) {
		this.morto = morto;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public FatCadCidNascimento getFatCadCidNascimentoSelecionado() {
		return fatCadCidNascimentoSelecionado;
	}

	public void setFatCadCidNascimentoSelecionado(FatCadCidNascimento fatCadCidNascimentoSelecionado) {
		this.fatCadCidNascimentoSelecionado = fatCadCidNascimentoSelecionado;
	}
}
