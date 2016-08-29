package br.gov.mec.aghu.internacao.leitos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class AtenderTransferenciaPacienteController extends ActionController {

	/**
	 * 
	 *///atenderTransferenciaPacienteList.xhtml
	private static final long serialVersionUID = -6270440994473195437L;

	private static final String PAGE_ATENDER_TRANSFERENCIA_PACIENTE_LIST = "atenderTransferenciaPacienteList";

	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Inject
	private AtenderTransferenciaPacientePaginatorController atenderTransferenciaPacientePaginatorController;

	// @Out(required = false) TODO: PASSAR POR PARAMETRO
	private AinSolicTransfPacientes solicitacao = null;

	private String leitoID = null;

	private AinLeitos leitoConcedido = null;

	private List<AinLeitos> listaLeitoPesq = null;

	@PostConstruct
	protected void init() {
		begin(conversation, true);
		this.listaLeitoPesq = null;
	}

	public void inicio() {
	 

		if (this.solicitacao != null && this.solicitacao.getLeito() != null) {
			this.leitoConcedido = this.solicitacao.getLeito();
		} else {
			this.leitoConcedido = null;
			this.leitoID = null;
		}
	
	}

	public String salvar() {
		atenderTransferenciaPacientePaginatorController.getDataModel().reiniciarPaginator();

		try {
			if (this.solicitacao != null && this.solicitacao.getIndSitSolicLeito() == DominioSituacaoSolicitacaoInternacao.P) {
				this.leitosInternacaoFacade.atenderSolicitacaoTransferencia(this.solicitacao, this.leitoConcedido);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATENDER_SOLICITACAO", this.solicitacao.getSeq());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_ATENDER_SOLICITACAO");
			}
			return PAGE_ATENDER_TRANSFERENCIA_PACIENTE_LIST;
		} catch (ApplicationBusinessException e) {
			// this.solicitacao = null;
			// this.solicitacao = this.leitosInternacaoFacade
			// .obterSolicitacaoPorSeq(this.solicitacaoSeq);
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String cancelar() {
		this.solicitacao = null;
		this.leitoConcedido = null;
		this.leitoID = null;
		return PAGE_ATENDER_TRANSFERENCIA_PACIENTE_LIST;
	}

	// LOV CLASSICA

	public void buscaLeito() {
		if (this.leitoID != null) {
			this.leitoConcedido = cadastrosBasicosInternacaoFacade.obterLeitoPorId(this.leitoID);
		} else {
			this.leitoConcedido = null;
		}
	}

	/**
	 * Retorna uma lista de Leitos.
	 * 
	 * @param param
	 * @return List<AinLeitos>
	 */
	public List<AinLeitos> pesquisaLeitoPaciente(String param) {
		this.listaLeitoPesq = this.leitosInternacaoFacade.pesquisarLeitosDisponiveis(param, this.solicitacao);
		return this.listaLeitoPesq;
	}

	// GETTERS & SETTERS

	public AinSolicTransfPacientes getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(AinSolicTransfPacientes solicitacao) {
		this.solicitacao = solicitacao;
	}

	public AinLeitos getLeitoConcedido() {
		return leitoConcedido;
	}

	public void setLeitoConcedido(AinLeitos leitoConcedido) {
		this.leitoConcedido = leitoConcedido;
	}

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public void setListaLeitoPesq(List<AinLeitos> listaLeitoPesq) {
		this.listaLeitoPesq = listaLeitoPesq;
	}

	public List<AinLeitos> getListaLeitoPesq() {
		return listaLeitoPesq;
	}

	public AtenderTransferenciaPacientePaginatorController getAtenderTransferenciaPacientePaginatorController() {
		return atenderTransferenciaPacientePaginatorController;
	}

	public void setAtenderTransferenciaPacientePaginatorController(AtenderTransferenciaPacientePaginatorController atenderTransferenciaPacientePaginatorController) {
		this.atenderTransferenciaPacientePaginatorController = atenderTransferenciaPacientePaginatorController;
	}
}