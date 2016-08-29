package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.checagemeletronica.business.IChecagemEletronicaFacade;
import br.gov.mec.aghu.model.EceJustificativaMdto;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class JustificativaMedicamentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8377124220277489320L;

	private static final String PAGE_JUSTIFICATIVA_MEDICAMENTO_CRUD = "justificativaMedicamentoCRUD";

	@EJB
	private IChecagemEletronicaFacade checagemEletronicaFacade;

	@Inject @Paginator
	private DynamicDataModel<EceJustificativaMdto> dataModel;
	private EceJustificativaMdto justificativaMdto;
	private EceJustificativaMdto justificativaMdtoSelecionado;

	private Boolean exibirBotaoIncluir;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		if (!dataModel.getPesquisaAtiva()) {
			justificativaMdto = new EceJustificativaMdto();
		}
	
	}

	public String iniciarInclusao() {
		return PAGE_JUSTIFICATIVA_MEDICAMENTO_CRUD;
	}

	public String editar() {
		return PAGE_JUSTIFICATIVA_MEDICAMENTO_CRUD;
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
		this.exibirBotaoIncluir = true;
	}

	public void limparPesquisa() {
		justificativaMdto = new EceJustificativaMdto();
		this.exibirBotaoIncluir = false;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	@Override
	public List<EceJustificativaMdto> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<EceJustificativaMdto> result = checagemEletronicaFacade.pesquisarJustificativasPorSeqDescricaoSituacao(firstResult,
				maxResult, orderProperty, asc, justificativaMdto.getSeq(), justificativaMdto.getDescricao());
		return result;
	}

	@Override
	public Long recuperarCount() {
		return checagemEletronicaFacade.pesquisarJustificativasPorSeqDescricaoSituacaoCount(justificativaMdto.getSeq(),
				justificativaMdto.getDescricao());
	}

	public void excluir() {
		dataModel.reiniciarPaginator();
		try {
			if (justificativaMdtoSelecionado != null) {
				this.checagemEletronicaFacade.removerJustificativaMdto(justificativaMdtoSelecionado.getSeq());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_JUSTIFICATIVA",
						justificativaMdto.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_REMOCAO_JUSTIFICATIVA_INVALIDA");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// ### Getters e Setters ###

	public EceJustificativaMdto getJustificativaMdto() {
		return justificativaMdto;
	}

	public Boolean getExibirBotaoIncluir() {
		return exibirBotaoIncluir;
	}

	public void setJustificativaMdto(EceJustificativaMdto justificativaMdto) {
		this.justificativaMdto = justificativaMdto;
	}

	public void setExibirBotaoIncluir(Boolean exibirBotaoIncluir) {
		this.exibirBotaoIncluir = exibirBotaoIncluir;
	}

	public DynamicDataModel<EceJustificativaMdto> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EceJustificativaMdto> dataModel) {
		this.dataModel = dataModel;
	}
	
	public EceJustificativaMdto getJustificativaMdtoSelecionado() {
		return justificativaMdtoSelecionado;
	}

	public void setJustificativaMdtoSelecionado(EceJustificativaMdto justificativaMdtoSelecionado) {
		this.justificativaMdtoSelecionado = justificativaMdtoSelecionado;
	}
}
