package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ConsultarMedicamentosPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -865928807307630431L;
	
	private static final String PAGE_CONSULTAR_GRUPOS_MEDICAMENTO= "consultarGruposMedicamento";
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@Inject
	private ConsultarGruposMedicamentoPaginatorController consultarGruposMedicamentoPaginatorController;
	
	private AfaMedicamento medicamento = new AfaMedicamento();
	private AfaMedicamento medicamentoSelecionado;
	@Inject @Paginator
	private DynamicDataModel<ScoMaterial> dataModel;

	@PostConstruct
	public void init(){
		begin(conversation);
	}
	
	public DynamicDataModel<ScoMaterial> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoMaterial> dataModel) {
		this.dataModel = dataModel;
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public String consultarMedicamento() {
		//consultarGruposMedicamentoPaginatorController.setMatCodigo(medicamentoSelecionado.getCodigo());
		consultarGruposMedicamentoPaginatorController.setMedicamento(medicamentoSelecionado);
		return PAGE_CONSULTAR_GRUPOS_MEDICAMENTO;
	}
	
	public void limparPesquisa() {
		this.setMedicamento(new AfaMedicamento());
		dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return this.farmaciaApoioFacade.consultarMedicamentoCount(this.getMedicamento());
	}

	@Override
	public List<AfaMedicamento> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
				
		return this.farmaciaApoioFacade.consultarMedicamento(firstResult,
				maxResult, AfaMedicamento.Fields.DESCRICAO.toString(), true, this.getMedicamento());
	}
	
	public AfaMedicamento getMedicamento() { 
		return medicamento; 
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public AfaMedicamento getMedicamentoSelecionado() {
		return medicamentoSelecionado;
	}

	public void setMedicamentoSelecionado(AfaMedicamento medicamentoSelecionado) {
		this.medicamentoSelecionado = medicamentoSelecionado;
	}
}