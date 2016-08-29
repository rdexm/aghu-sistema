package br.gov.mec.aghu.casca.componente.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ComponenteController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5625909260763853971L;
	private final String PAGE_DETALHAR_COMPONENTE = "manterMetodo";

	@EJB @SuppressWarnings("cdi-ambiguous-dependency")	
	private ICascaFacade cascaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<Componente> dataModel;	

	private String nome;

	@PostConstruct
	public void init() {
		begin(conversation, true);		
		nome=null;
	}

	public String detalhar() {
		return PAGE_DETALHAR_COMPONENTE;
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}	
	
	public void limparPesquisa() {
		nome=null;
		this.dataModel.limparPesquisa();
	}	
	
	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarComponentesCount(nome);
	}

	@Override
	public List<Componente> recuperarListaPaginada(Integer firstResult,Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarComponentes(nome, firstResult, maxResult, orderProperty, asc);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DynamicDataModel<Componente> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Componente> dataModel) {
		this.dataModel = dataModel;
	}	

}
