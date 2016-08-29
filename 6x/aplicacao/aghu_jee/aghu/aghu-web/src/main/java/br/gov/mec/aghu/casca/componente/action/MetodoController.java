package br.gov.mec.aghu.casca.componente.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class MetodoController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = -1021578343429440159L;
	private final String PAGE_PESQUISAR_COMPONENTE = "pesquisarComponentes";	

	@EJB @SuppressWarnings("cdi-ambiguous-dependency")		
	private ICascaFacade cascaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<Metodo> dataModel;	

	private Componente componente;

	private Date data;
	
	@PostConstruct	
	public void init() {
		begin(conversation, true);
		dataModel.reiniciarPaginator();
	}	
	
	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarMetodosComponenteCount(componente.getId());
	}

	@Override
	public List<Metodo> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarMetodosComponente(componente.getId(),firstResult, maxResult, orderProperty, asc);
	}
	

	public String cancelar() {
		return PAGE_PESQUISAR_COMPONENTE;
	}	

	public Componente getComponente() {
		return componente;
	}

	public void setComponente(Componente componente) {
		this.componente = componente;
	}

	public DynamicDataModel<Metodo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Metodo> dataModel) {
		this.dataModel = dataModel;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

}