package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.CadastroDiluentesVO;
import br.gov.mec.aghu.model.AfaMedicamento;

public class CadastroDiluentesPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4275122780834112272L;
	
	private AfaMedicamento medicamentoSelecionado;
	private Boolean fromList;
	
	@Inject @Paginator
	private DynamicDataModel<CadastroDiluentesVO> dataModel;
	
	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void iniciarPagina() {
		dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return farmaciaFacade.pesquisarDiluentesCount(this.medicamentoSelecionado);
	}

	@Override
	public List<CadastroDiluentesVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
	List<CadastroDiluentesVO> result = this.farmaciaFacade.recuperarListaPaginadaDiluentes(firstResult, maxResult, orderProperty, asc, this.medicamentoSelecionado);
		
		if (result == null) {
			result = new ArrayList<CadastroDiluentesVO>();
		}

		return result;
	}
	
	public DominioSituacao[] obterValoresDominioSituacao() {
		return DominioSituacao.values();
	}

	public AfaMedicamento getMedicamentoSelecionado() {
		return medicamentoSelecionado;
	}

	public void setMedicamentoSelecionado(AfaMedicamento medicamentoSelecionado) {
		this.medicamentoSelecionado = medicamentoSelecionado;
	}

	public DynamicDataModel<CadastroDiluentesVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<CadastroDiluentesVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getFromList() {
		return fromList;
	}

	public void setFromList(Boolean fromList) {
		this.fromList = fromList;
	}
	
	public String visualizarHistorico(){
		return RetornoAcaoStrEnum.VISUALIZAR_HISTORICO.toString();		
	}
	
	public String iniciarInclusao() {
		return RetornoAcaoStrEnum.DILUENTE_CRUD.toString();
	}
	
	public String cancelarPesquisa() {
		return RetornoAcaoStrEnum.CANCELADO.toString();
	}
	
	public static enum RetornoAcaoStrEnum {

		VISUALIZAR_HISTORICO("farmacia-visualizarHistorico"),
		CANCELADO("farmacia-cancelar"),
		DILUENTE_CRUD("farmacia-incluirDiluente");		

		private final String str;

		RetornoAcaoStrEnum(String str) {

			this.str = str;
		}

		@Override
		public String toString() {

			return this.str;
		}

	}
	

}
