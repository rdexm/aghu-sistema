package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosJn;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoJN;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class VisualizarHistLocalDispMdtoPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 2660467860492124507L;

	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	private AfaMedicamento medicamento;
	
	@Inject @Paginator
	private DynamicDataModel<AfaViaAdministracaoMedicamentoJN> dataModel;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}

	@Override
	public List<AfaLocalDispensacaoMdtosJn> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.farmaciaFacade.recuperarListaPaginadaLocalDispensacaoMdtosJn(this.getMedicamento(), firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return farmaciaFacade.pesquisarLocalDispensacaoMedicamentoCountJn(medicamento);
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
