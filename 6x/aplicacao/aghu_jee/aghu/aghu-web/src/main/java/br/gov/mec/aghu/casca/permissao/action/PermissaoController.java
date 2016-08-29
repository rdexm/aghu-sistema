package br.gov.mec.aghu.casca.permissao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PermissaoController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = 1151156366637807817L;

	private final String PAGE_CADASTRAR_PERMISSAO = "cadastrarPermissao";
	private final String PAGE_PESQUISAR_PERMISSAO = "pesquisarPermissoes";
	private final String PAGE_MANTER_PERMISSAO_COMPONENTE = "manterPermissaoComponente";
															 
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")	
	private ICascaFacade cascaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<Permissao> dataModel;
	
	private Permissao permissaoFiltro;
	private Permissao permissao;
	private Modulo modulo;
	private Componente componente;
	private Metodo action;
	private String voltarPara;
	
	@PostConstruct	
	public void init() {
		begin(conversation, true);	
		
		permissaoFiltro=new Permissao();
		permissao = new Permissao();
	}

	public String iniciarEdicao(){
	 
		if(permissao != null && permissao.getId() != null){
			try {
				permissao = cascaFacade.obterPermissao(permissao.getId());
				
				if(permissao == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
		} else {
			permissao = new Permissao();
		}
		
		return null;
	
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}	
	
	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarPermissoesCount(permissaoFiltro.getNome(), permissaoFiltro.getDescricao(), modulo);
	}

	@Override
	public List<Permissao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarPermissoes(permissaoFiltro.getNome(), permissaoFiltro.getDescricao(), modulo, firstResult, maxResult, orderProperty, asc);
	}	
	
	
	public String novo() {
		permissao = new Permissao();
		
		return PAGE_CADASTRAR_PERMISSAO;
	}
	
	
	public String editar() {
		return PAGE_CADASTRAR_PERMISSAO;
	}	
	
	
	
	public void excluir() {
		try {
			cascaFacade.excluirPermissao(permissao);
			apresentarMsgNegocio("CASCA_MENSAGEM_SUCESSO_EXCLUSAO_PERMISSAO");
			
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	
	public String cancelar() {
		permissao = null;
		return PAGE_PESQUISAR_PERMISSAO;
	}	

	public String voltar(){
		return voltarPara;
	}
	
	public Long listarModulosCount(String objPesquisa) {
		return this.cascaFacade.listarModulosCount((String) objPesquisa);
	}

	public List<Modulo> autocompleteModulos(String objPesquisa) {
		return this.returnSGWithCount(this.cascaFacade.listarModulos((String) objPesquisa),listarModulosCount(objPesquisa));
	}

	
	public void limparPesquisa() {
		permissaoFiltro = new Permissao();
		modulo=null;
		this.dataModel.limparPesquisa();
	}
	
	
	public String manterPermissaoComponente() {
		return PAGE_MANTER_PERMISSAO_COMPONENTE;
	}	
	
	
	public String salvar() {
		try {
			boolean novo = permissao.getId() == null;
			cascaFacade.salvarPermissao(permissao);
			
			if (novo) {
				apresentarMsgNegocio("CASCA_MENSAGEM_SUCESSO_CRIACAO_PERMISSAO");			
			} else {
				apresentarMsgNegocio("CASCA_MENSAGEM_SUCESSO_EDICAO_PERMISSAO");
			}			
			
			permissao = new Permissao();
			modulo = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return PAGE_PESQUISAR_PERMISSAO;
	}
	
	

	public void setDataModel(DynamicDataModel<Permissao> dataModel) {
		this.dataModel = dataModel;
	}		


	public DynamicDataModel<Permissao> getDataModel() {
		return dataModel;
	}


	public Permissao getPermissao() {
		return permissao;
	}


	public void setPermissao(Permissao permissao) {
		this.permissao = permissao;
	}


	public Modulo getModulo() {
		return modulo;
	}


	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}


	public Componente getComponente() {
		return componente;
	}


	public void setComponente(Componente componente) {
		this.componente = componente;
	}


	public Metodo getAction() {
		return action;
	}


	public void setAction(Metodo action) {
		this.action = action;
	}

	public Permissao getPermissaoFiltro() {
		return permissaoFiltro;
	}

	public void setPermissaoFiltro(Permissao permissaoFiltro) {
		this.permissaoFiltro = permissaoFiltro;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
}