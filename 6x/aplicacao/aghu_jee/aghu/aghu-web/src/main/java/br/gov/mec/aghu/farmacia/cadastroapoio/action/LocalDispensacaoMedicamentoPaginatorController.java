package br.gov.mec.aghu.farmacia.cadastroapoio.action;


import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;


@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class LocalDispensacaoMedicamentoPaginatorController extends AbstractCrudMedicamentoPaginatorController<AfaLocalDispensacaoMdtos> {
	
	private static final long serialVersionUID = 4395735768963683272L;

	@Inject
	private VisualizarHistLocalDispMdtoPaginatorController visualizarHistLocalDispMdtoPaginatorController;
	
	@Inject
	private LocalDispensacaoMedicamentoController localDispensacaoMedicamentoController;
	
	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@Override
	public Long recuperarCount() {
		return farmaciaFacade.pesquisarLocalDispensacaoMedicamentoCount(getMedicamento());
	}

	@Override
	public List<AfaLocalDispensacaoMdtos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return farmaciaFacade.recuperarListaPaginadaLocalDispensacaoMdtos(getMedicamento(), firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public String paginaHist() {
		visualizarHistLocalDispMdtoPaginatorController.setMedicamento(getMedicamento());
		visualizarHistLocalDispMdtoPaginatorController.getDataModel().reiniciarPaginator();
		return "visualizaHistLocalDispMdtoList";
	}

	@Override
	protected AbstractCrudMedicamentoController getCrudController() {
		return localDispensacaoMedicamentoController;
	}

	@Override
	protected String paginaCrud() {
		return "localDispensacaoMedicamentoCRUD";
	}
	
	//Utilizado para verificar se o botão Selecionar todas unidades deve ser apresentado
	public Boolean exibeBotaoGravar() {
		
		Long qtdRegistros = farmaciaApoioFacade.recuperarCountAghUnidadesFuncionaisDisponiveis(getMedicamento());
		Boolean retorno = true;
		
		if (qtdRegistros != null && qtdRegistros <= 0){
			retorno = false;
		}		
		return retorno;
	}
	
	public void excluir(){
		getCrudController().setEntidade(getEntidadeSelecionada());
		getCrudController().excluir();
	}
	
	public String cancelar(){
		if (getFromList()){
			return "medicamentoList";
		}else{
			return "medicamentoCRUD";
		}
		
	}
	
	public void selecionarTodasUnidadesFuncionaisParaMedicamento(){
		localDispensacaoMedicamentoController.setMedicamento(getMedicamento());
		localDispensacaoMedicamentoController.selecionarTodasUnidadesFuncionaisParaMedicamento();
	}
}
