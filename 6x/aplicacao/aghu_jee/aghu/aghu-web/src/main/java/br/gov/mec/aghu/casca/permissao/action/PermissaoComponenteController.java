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
import br.gov.mec.aghu.casca.model.PermissoesComponentes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PermissaoComponenteController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = 1151156366637807817L;

	private final String PAGE_PESQUISAR_PERMISSAO = "pesquisarPermissoes";
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")	
	private ICascaFacade cascaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<PermissoesComponentes> dataModel;	

	private Permissao permissao;
	private Componente componente;
	private PermissoesComponentes permissaoComponente;
	private Modulo modulo;
	private Metodo action;
	private String actionNome;
	private String target;
	
//	private List<Metodo> listaMetodos; 
//	private List<Metodo> metodosComponentePermissao;

	@PostConstruct	
	public void init() {
		limparPesquisa();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}	
	
	
	public void limparPesquisa(){
		actionNome="";
		target="";
		componente=null;
		action=null;
		modulo=null;
		pesquisar();
	}	
	
	
	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarComponentesPermissaoCount(getPermissao().getId(), target, actionNome);
	}

	
	@Override
	public List<PermissoesComponentes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarComponentesPermissao(getPermissao().getId(), target, actionNome, firstResult, maxResult, orderProperty, asc);
	}
	
	
	public String cancelar() {
		limparPesquisa();
		return PAGE_PESQUISAR_PERMISSAO;
	}	
	
	public List<Componente> pesquisarComponentePorNome(String nome) {
		return cascaFacade.pesquisarComponentePorNome(nome);
	}
	
	public List<Metodo> pesquisarActionPorNome(String nome) {
		if (getComponente()==null) {
			apresentarMsgNegocio(Severity.ERROR, "CASCA_MENSAGEM_TARGET_NAO_INFORMADO");
			return null;
		}else{
			return cascaFacade.pesquisarActionPorNome(nome, getComponente());
		}
	}
	

	public void excluir() {
		try {
			cascaFacade.excluirComponentePermissao(permissaoComponente.getId());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}


	public void incluir() {
		if (getAction()==null) {
			apresentarMsgNegocio(Severity.ERROR,"CASCA_MENSAGEM_TARGET_NAO_INFORMADO");
			return;
		}	
		try {
			cascaFacade.associarPermissaoComponenteMetodos(getPermissao().getId(), componente.getId(), action);
			apresentarMsgNegocio(Severity.INFO, "CASCA_MENSAGEM_SUCESSO_CRIACAO_PERMISSAO");
			
			setComponente(null);
			setAction(null);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public void setDataModel(DynamicDataModel<PermissoesComponentes> dataModel) {
		this.dataModel = dataModel;
	}		


	public Modulo getModulo() {
		return modulo;
	}


	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}


	public String getTarget() {
		return target;
	}


	public void setTarget(String target) {
		this.target = target;
	}


	public DynamicDataModel<PermissoesComponentes> getDataModel() {
		return dataModel;
	}


	public Componente getComponente() {
		return componente;
	}

	public void setComponente(Componente componente) {
		this.componente = componente;
	}

	public PermissoesComponentes getPermissaoComponente() {
		return permissaoComponente;
	}

	public Metodo getAction() {
		return action;
	}

	public void setAction(Metodo action) {
		this.action = action;
	}

	public String getActionNome() {
		return actionNome;
	}

	public void setActionNome(String actionNome) {
		this.actionNome = actionNome;
	}


	public void setPermissao(Permissao permissao) {
		this.permissao = permissao;
	}


	public Permissao getPermissao() {
		return permissao;
	}

	public void setPermissaoComponente(PermissoesComponentes permissaoComponente) {
		this.permissaoComponente = permissaoComponente;
	}
}