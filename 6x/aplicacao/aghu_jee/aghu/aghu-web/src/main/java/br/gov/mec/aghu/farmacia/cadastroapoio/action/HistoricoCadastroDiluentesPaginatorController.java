package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.CadastroDiluentesJnVO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;

import javax.annotation.PostConstruct;

import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class HistoricoCadastroDiluentesPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	@Inject @Paginator
	private DynamicDataModel<CadastroDiluentesJnVO> dataModel;

//	private static final Log LOG = LogFactory.getLog(HistoricoCadastroDiluentesPaginatorController.class);
	

	private static final long serialVersionUID = 6082107464862856320L;


	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	private AfaMedicamento medicamentoSelecionado;
	private Integer codigoMedicamento;
	
	
	public void iniciarPagina(){			
		this.dataModel.reiniciarPaginator();				
	}
		
	@Override
	public List<CadastroDiluentesJnVO> recuperarListaPaginada(Integer firstResult,	Integer maxResult, String orderProperty, boolean asc) {	
		List<CadastroDiluentesJnVO> listaHistoricoFormaDosagem = this.farmaciaFacade.pesquisarVinculoDiluentesJn(firstResult,maxResult, orderProperty, asc, medicamentoSelecionado.getMatCodigo());
		return listaHistoricoFormaDosagem;
	}
	
	@Override
	public Long recuperarCount() {		
		return this.farmaciaFacade.pesquisarVinculoDiluentesJnCount(medicamentoSelecionado.getMatCodigo());
	}	
	
	public String voltar(){
		return RetornoAcaoStrEnum.VOLTAR.toString();		
	}
	
	public static enum RetornoAcaoStrEnum {

		VOLTAR("farmacia-voltar");		

		private final String str;

		RetornoAcaoStrEnum(String str) {

			this.str = str;
		}

		@Override
		public String toString() {

			return this.str;
		}
	}

	public Integer getCodigoMedicamento() {
		return codigoMedicamento;
	}

	public void setCodigoMedicamento(Integer codigoMedicamento) {
		this.codigoMedicamento = codigoMedicamento;
	}

	public AfaMedicamento getMedicamentoSelecionado() {
		return medicamentoSelecionado;
	}

	public void setMedicamentoSelecionado(AfaMedicamento medicamentoSelecionado) {
		this.medicamentoSelecionado = medicamentoSelecionado;
	}

	public DynamicDataModel<CadastroDiluentesJnVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<CadastroDiluentesJnVO> dataModel) {
	 this.dataModel = dataModel;
	}
}