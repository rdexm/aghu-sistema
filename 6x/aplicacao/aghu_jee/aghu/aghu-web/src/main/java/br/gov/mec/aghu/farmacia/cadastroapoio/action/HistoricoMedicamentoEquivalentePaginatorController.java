package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteJn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class HistoricoMedicamentoEquivalentePaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -6503866224869738678L;

	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	private AfaMedicamento medicamento;
	
	@Inject @Paginator
	private DynamicDataModel<AfaMedicamentoEquivalenteJn> dataModel;
	
	@PostConstruct
	public void init(){			
		begin(conversation);
	}
		
	@Override
	public List<AfaMedicamentoEquivalenteJn> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {	
		return this.farmaciaFacade.pesquisarMedicamentoEquivalenteJn(firstResult,
				maxResult, orderProperty, asc, medicamento);
		
	}
	
	@Override
	public Long recuperarCount() {		
		return this.farmaciaFacade.pesquisarMedicamentoEquivalenteJnCount(medicamento);
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public DynamicDataModel<AfaMedicamentoEquivalenteJn> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaMedicamentoEquivalenteJn> dataModel) {
		this.dataModel = dataModel;
	}	
	
}