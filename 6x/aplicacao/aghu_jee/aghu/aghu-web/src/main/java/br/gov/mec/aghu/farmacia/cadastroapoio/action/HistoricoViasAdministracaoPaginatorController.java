package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoJN;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class HistoricoViasAdministracaoPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 7246948939230648885L;

	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	private AfaMedicamento medicamento;
	
	@Inject @Paginator
	private DynamicDataModel<AfaViaAdministracaoMedicamentoJN> dataModel;
	
	@PostConstruct
	public void init(){			
		begin(conversation);
	}
		
	@Override
	public List<AfaViaAdministracaoMedicamentoJN> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {	
		return this.farmaciaFacade.pesquisarViasAdministracaoJn(firstResult,
				maxResult, orderProperty, asc, medicamento);
	}
	
	@Override
	public Long recuperarCount() {		
		return this.farmaciaFacade.pesquisarViasAdministracaoJnCount(medicamento);
	}	
	
	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public DynamicDataModel<AfaViaAdministracaoMedicamentoJN> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AfaViaAdministracaoMedicamentoJN> dataModel) {
		this.dataModel = dataModel;
	}

	
}