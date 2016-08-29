package br.gov.mec.aghu.internacao.transferir.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.transferir.business.ITransferirPacienteFacade;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class TransferirPacientePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -9006927470833209923L;

	private static final String TRANSFERIR_PACIENTE_CRUD = "transferirPacienteCRUD";
	private static final String TRANSFERIR_PACIENTE_LIST = "transferirPacienteList";

	@EJB
	private ITransferirPacienteFacade transferirPacienteFacade;

	@Inject
	private TransferirPacienteController transferirPacienteController;

	private Integer prontuario;
	private String voltarPara;
	@Inject @Paginator
	private DynamicDataModel<AinInternacao> dataModel;
	private AinInternacao selection = new AinInternacao();

	@PostConstruct
	public void inicio() {
		begin(conversation);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		prontuario = null;
		dataModel.setPesquisaAtiva(false);
	}

	@Override
	public Long recuperarCount() {
		return transferirPacienteFacade.pesquisarInternacaoCount(prontuario);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AinInternacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return (prontuario != null) ? transferirPacienteFacade.pesquisarInternacao(firstResult, maxResult, orderProperty, asc, prontuario)
				: (new ArrayList<AinInternacao>());
	}

	public String transferirPacienteCRUD(Integer internacaoSeq) {
		transferirPacienteController.setInternacaoSeq(internacaoSeq);
		transferirPacienteController.setCameFrom(TRANSFERIR_PACIENTE_LIST);
		transferirPacienteController.inicio();
		return TRANSFERIR_PACIENTE_CRUD;
	}

	public String actionCancelar() {
		if (voltarPara != null) {
			
			if(voltarPara.equalsIgnoreCase("pesquisarDisponibilidadeQuarto")){
				return "internacao-pesquisarDisponibilidadeQuarto";
			}
			return voltarPara;
		} else {
			return "cancelar";
		}
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public DynamicDataModel<AinInternacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinInternacao> dataModel) {
		this.dataModel = dataModel;
	}

	public AinInternacao getSelection() {
		return selection;
	}

	public void setSelection(AinInternacao selection) {
		this.selection = selection;
	}

	public TransferirPacienteController getTransferirPacienteController() {
		return transferirPacienteController;
	}

	public void setTransferirPacienteController(TransferirPacienteController transferirPacienteController) {
		this.transferirPacienteController = transferirPacienteController;
	}

}
