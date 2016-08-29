package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaFormaDosagemJn;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class HistoricoFormaDosagemPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -2694665705028988928L;

	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	private AfaMedicamento medicamento;
	
	@Inject @Paginator
	private DynamicDataModel<AfaFormaDosagemJn> dataModel;
	
	@PostConstruct
	public void init(){			
		begin(conversation);
	}
		
	@Override
	public List<AfaFormaDosagemJn> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {	
		return this.farmaciaFacade.pesquisarFormaDosagemJn(firstResult,
				maxResult, orderProperty, asc, medicamento);
	}
	
	@Override
	public Long recuperarCount() {		
		return this.farmaciaFacade.pesquisarFormaDosagemJnCount(medicamento);
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public DynamicDataModel<AfaFormaDosagemJn> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AfaFormaDosagemJn> dataModel) {
		this.dataModel = dataModel;
	}	
	
}