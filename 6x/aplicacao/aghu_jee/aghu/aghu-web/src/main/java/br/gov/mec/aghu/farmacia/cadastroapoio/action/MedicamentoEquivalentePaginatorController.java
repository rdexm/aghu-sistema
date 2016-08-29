package br.gov.mec.aghu.farmacia.cadastroapoio.action;


import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalente;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class MedicamentoEquivalentePaginatorController extends AbstractCrudMedicamentoPaginatorController<AfaMedicamentoEquivalente> {

	private static final long serialVersionUID = 8728600057983180529L;

	@Inject
	private MedicamentoEquivalenteController medicamentoEquivalenteController;
	
	@Inject
	private HistoricoMedicamentoEquivalentePaginatorController historicoMedicamentoEquivalentePaginatorController;
	
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@Override
	public Long recuperarCount() {
		return this.farmaciaApoioFacade.recuperarCountMedicamentoEquivalente(this.getMedicamento());
	}

	@Override
	public List<AfaMedicamentoEquivalente> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.farmaciaApoioFacade.recuperarListaPaginadaMedicamentoEquivalente(this.getMedicamento(), firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public String paginaHist() {
		historicoMedicamentoEquivalentePaginatorController.setMedicamento(getMedicamento());
		historicoMedicamentoEquivalentePaginatorController.getDataModel().reiniciarPaginator();
		return "historicoMedicamentoEquivalenteList";
	}

	@Override
	protected AbstractCrudMedicamentoController getCrudController() {
		return medicamentoEquivalenteController;
	}

	@Override
	protected String paginaCrud() {
		return "medicamentoEquivalenteCrud";
	}
	
	public String cancelar(){
		if (getFromList()){
			return "medicamentoList";
		}else{
			return "medicamentoCRUD";
		}
		
	}
		
}
