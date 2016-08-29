package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoJn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class HistoricoCadastroMedicamentoPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 7905320416160868170L;

	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	private AfaMedicamento medicamento;
	
	private String pageVoltar;
	
	@Inject @Paginator
	private DynamicDataModel<AfaMedicamentoJn> dataModel;
	
	@Inject
	private HistoricoCadastroMedicamentoController historicoCadastroMedicamentoController;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	public void inicio(){
	 

		this.dataModel.reiniciarPaginator();
	
	}
	@Override
	public List<AfaMedicamentoJn> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {	
		return this.farmaciaFacade.pesquisarHistoricoCadastroMedicamento(firstResult,
				maxResult, orderProperty, asc, medicamento);
	}
	
	@Override
	public Long recuperarCount() {		
		return this.farmaciaFacade.pesquisarHistoricoCadastroMedicamentoCount(medicamento);
	}	
	
	public String voltar(){
		return pageVoltar;
	}
	
	public String detalharHistoricoMedicamento(Integer seqJn){
		historicoCadastroMedicamentoController.setSeqJn(seqJn);
		historicoCadastroMedicamentoController.pesquisaHistoricoMedicamento();
		return "historicoCadastroMedicamento";
	}
	// Getters and Setters
	// ===================
	
	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}
	
	public String getPageVoltar() {
		return pageVoltar;
	}

	public void setPageVoltar(String pageVoltar) {
		this.pageVoltar = pageVoltar;
	}

	public DynamicDataModel<AfaMedicamentoJn> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaMedicamentoJn> dataModel) {
		this.dataModel = dataModel;
	}
}