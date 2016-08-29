package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoJn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class HistoricoSinonimoMedicamentoPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -1705221264956065340L;

	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	private AfaMedicamento medicamento;
	
	@Inject @Paginator
	private DynamicDataModel<AfaSinonimoMedicamentoJn> dataModel;
	
	@PostConstruct
	public void init(){			
		begin(conversation);
	}
	
	@Override
	public List<AfaSinonimoMedicamentoJn> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {	
		return this.farmaciaFacade.pesquisarSinonimoMedicamentoJn(firstResult,
				maxResult, orderProperty, asc, medicamento);
	}
	
	@Override
	public Long recuperarCount() {		
		return this.farmaciaFacade.pesquisarSinonimoMedicamentoJnCount(medicamento);
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public DynamicDataModel<AfaSinonimoMedicamentoJn> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaSinonimoMedicamentoJn> dataModel) {
		this.dataModel = dataModel;
	}	
}