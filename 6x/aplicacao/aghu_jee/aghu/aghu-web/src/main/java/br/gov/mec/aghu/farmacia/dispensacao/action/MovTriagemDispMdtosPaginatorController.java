package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class MovTriagemDispMdtosPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 210144827592842062L;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MovimentacaoTriagemDispensacaoMdtosController movimentacaoTriagemDispensacaoMdtosController;
	
	private MpmPrescricaoMedica prescricaoMedica;
	private Integer atdSeq;
	private Integer seq;
	private String urlBtVoltar;
	private Boolean exibirBotaoLimpar;
	private Integer matCodigo;
	private AfaMedicamento medicamento;
	private MpmItemPrescricaoMdto itemPrescricaoMdtoSelecionado;
	@Inject @Paginator
	private DynamicDataModel<AfaDispensacaoMdtos> dataModel;
	
	public void iniciarPagina() {
	 

		//Obtém a prescrição médica pelo id.
		this.prescricaoMedica = this.prescricaoMedicaFacade.obterPrescricaoPorId(atdSeq, seq);
		dataModel.reiniciarPaginator(); // exibir resultado na entrada da tela.
		if(matCodigo != null){
			medicamento = getFarmaciaFacade().obterMedicamento(matCodigo);
		}
		if(exibirBotaoLimpar==null){
			exibirBotaoLimpar=false;			
		}
	
	}
	
	/**
	 * Realiza a pesquisa de items da prescricao
	 */
	public void pesquisar(){
		dataModel.reiniciarPaginator();
	}	
	
	public void limparPesquisa(){
		this.atdSeq = null;
		this.seq = null;
		this.matCodigo = null;
		this.medicamento  = null;
		this.prescricaoMedica = null;
		dataModel.limparPesquisa();
	}
	
	@Override
	public List<MpmItemPrescricaoMdto> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.farmaciaDispensacaoFacade.recuperarListaTriagemMedicamentosPrescritos(firstResult,
			maxResult, orderProperty, asc, prescricaoMedica, medicamento);
	}

	@Override
	public Long recuperarCount() {
		return this.prescricaoMedicaFacade
				.pesquisarItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendenteCount(prescricaoMedica, medicamento);
	}
	
	public String editar(){
		movimentacaoTriagemDispensacaoMdtosController.setSeqPrescMed(seq);
		movimentacaoTriagemDispensacaoMdtosController.setPmdAtdSeq(itemPrescricaoMdtoSelecionado.getId().getPmdAtdSeq());
		movimentacaoTriagemDispensacaoMdtosController.setPmdSeq(itemPrescricaoMdtoSelecionado.getId().getPmdSeq());
		movimentacaoTriagemDispensacaoMdtosController.setMedMatCodigo(itemPrescricaoMdtoSelecionado.getId().getMedMatCodigo());
		movimentacaoTriagemDispensacaoMdtosController.setSeqp(itemPrescricaoMdtoSelecionado.getId().getSeqp());
		movimentacaoTriagemDispensacaoMdtosController.setUrlBtVoltar("movimentacaoTriagemDispensacaoMdtosList");
		movimentacaoTriagemDispensacaoMdtosController.setMatCodigo(matCodigo);
		return"editarTriagemDispensacaoMedicamentos";
	}
	
	public String voltar() {
		return urlBtVoltar;
	}
	
	//GETTERS E SETTERS
	
	public IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	public void setFarmaciaFacade(IFarmaciaFacade farmaciaFacade) {
		this.farmaciaFacade = farmaciaFacade;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public String getUrlBtVoltar() {
		return urlBtVoltar;
	}

	public void setUrlBtVoltar(String urlBtVoltar) {
		this.urlBtVoltar = urlBtVoltar;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public Boolean getExibirBotaoLimpar() {
		return exibirBotaoLimpar;
	}

	public void setExibirBotaoLimpar(Boolean exibirBotaoLimpar) {
		this.exibirBotaoLimpar = exibirBotaoLimpar;
	}

	public DynamicDataModel<AfaDispensacaoMdtos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaDispensacaoMdtos> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmItemPrescricaoMdto getItemPrescricaoMdtoSelecionado() {
		return itemPrescricaoMdtoSelecionado;
	}

	public void setItemPrescricaoMdtoSelecionado(
			MpmItemPrescricaoMdto itemPrescricaoMdtoSelecionado) {
		this.itemPrescricaoMdtoSelecionado = itemPrescricaoMdtoSelecionado;
	}
	
}