package br.gov.mec.aghu.internacao.leitos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class SolicitarTransferenciaPacientePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3130682473491680274L;

	private static final String SOLICITA_TRANSFERENCIA_PACIENTE_CRUD = "solicitaTransferenciaPacienteCRUD";

	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@Inject
	private SolicitarTransferenciaPacienteController controller;

	private Integer internacaoSeq;
	private Integer prontuario;
	private String leitoID;
	private AinInternacao internacao;
	private String mensagem;
	private String nomePaciente;
	private boolean exibirBotaoNovo = false;
	private Integer seq;

	@Inject @Paginator
	private DynamicDataModel<AinSolicTransfPacientes> dataModel;
	private AinSolicTransfPacientes selection = new AinSolicTransfPacientes();
	private Integer solicitacaoSeq = null;
	private String cameFrom = "";

	
	@PostConstruct
	public void init() {
		begin(conversation);
	}	
	
	public void inicioPesquisa() {
	 

		if (this.prontuario != null) {
			pesquisar();
		} else if (this.internacaoSeq != null) {
			this.internacao = this.leitosInternacaoFacade.obterInternacaoPorId(this.internacaoSeq);
			if (this.internacao != null) {
				this.prontuario = this.internacao.getPaciente().getProntuario();
				this.nomePaciente = this.internacao.getPaciente().getNome();
				pesquisar();
			}
		}
	
	}

	public void pesquisar() {
		if (prontuario == null && StringUtils.isEmpty(leitoID)) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_INFORMAR_PRONTUARIO_LEITO");
			return;
		}

		dataModel.reiniciarPaginator();
		AipPacientes paciente = this.pacienteFacade.pesquisarPacientePorProntuario(this.prontuario);
		if (paciente != null) {
			this.nomePaciente = paciente.getNome();
		} else {
			this.nomePaciente = null;
		}

		if (this.prontuario != null) {
			this.internacao = this.leitosInternacaoFacade.obterInternacaoPorProntuario(this.prontuario);
		} else {
			this.internacao = this.leitosInternacaoFacade.obterInternacaoPorLeito(this.leitoID);
		}

		if (this.internacao != null) {
			this.mensagem = this.getMensagem(this.internacao);
			this.exibirBotaoNovo = false;
		} else {
			this.mensagem = null;
			this.internacao = null;
			this.exibirBotaoNovo = true;
		}
	}

	public void limparPesquisa() {
		this.prontuario = null;
		this.leitoID = null;
		this.mensagem = null;
		this.internacao = null;
		this.nomePaciente = null;
		this.exibirBotaoNovo = false;
		this.dataModel.setPesquisaAtiva(false);
	}

	public String getMensagem(AinInternacao internacao) {
		return this.leitosInternacaoFacade.mensagemSolicTransPaciente(internacao.getSeq());
	}

	@Override
	public Long recuperarCount() {
		return this.leitosInternacaoFacade.pesquisarSolicitacaoTransferenciaLeitoCount(this.prontuario, this.leitoID);
	}

	@Override
	public List<AinSolicTransfPacientes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AinSolicTransfPacientes> lista = new ArrayList<AinSolicTransfPacientes>(0);
		try {
			lista = this.leitosInternacaoFacade.pesquisarSolicitacaoTransferenciaLeito(firstResult, maxResult, orderProperty, asc, this.prontuario,
					this.leitoID);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return lista;
	}

	public String getUnidadeFuncionalInternacao(final AinInternacao internacao) {
		final AghUnidadesFuncionais unidade = this.leitosInternacaoFacade.unidadeFuncionalInternacao(internacao);
		return (unidade != null) ? unidade.getAndarAlaDescricao() : null;
	}
	

	public void cancelarSol() {
		getDataModel().reiniciarPaginator();

		AinSolicTransfPacientes solicitacaoTransfPac = this.leitosInternacaoFacade.obterSolicTransfPacientePorId(this.solicitacaoSeq);
		try {
			this.leitosInternacaoFacade.cancelarAinSolicTransfPacientes(solicitacaoTransfPac);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CANCELAMENTO_SOLIC_TRANSF_PAC", solicitacaoTransfPac.getSeq());
			setMensagem(getMensagem(solicitacaoTransfPac.getInternacao()));
		} catch (final BaseException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}

	public String cancelarPesquisaSolicitarTransferenciaPaciente() {
		this.limparPesquisa();
		return cameFrom;
	}

	public String solicitarTransferenciaPaciente(Integer seq) {
		controller.setSolicitacaoSeq(seq);
		controller.setProntuario(prontuario);
		controller.setLeitoID(leitoID);
		controller.inicio();
		return SOLICITA_TRANSFERENCIA_PACIENTE_CRUD;
	}

	public String iniciarInclusao() {
		controller.setInternacaoSeq(internacao.getSeq());
		controller.setProntuario(prontuario);
		controller.setLeitoID(leitoID);
		controller.setSolicitacaoSeq(null);
		controller.inicio();
		return SOLICITA_TRANSFERENCIA_PACIENTE_CRUD;
	}

	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AinInternacao getInternacao() {
		return this.internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public String getMensagem() {
		return this.mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public boolean isExibirBotaoNovo() {
		return this.exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public String getNomePaciente() {
		return this.nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getInternacaoSeq() {
		return this.internacaoSeq;
	}

	public void setInternacaoSeq(Integer internacaoSeq) {
		this.internacaoSeq = internacaoSeq;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public String getLeitoID() {
		return this.leitoID;
	}

	public DynamicDataModel<AinSolicTransfPacientes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinSolicTransfPacientes> dataModel) {
		this.dataModel = dataModel;
	}

	public AinSolicTransfPacientes getSelection() {
		return selection;
	}

	public void setSelection(AinSolicTransfPacientes selection) {
		this.selection = selection;
	}

	public Integer getSolicitacaoSeq() {
		return solicitacaoSeq;
	}

	public void setSolicitacaoSeq(Integer solicitacaoSeq) {
		this.solicitacaoSeq = solicitacaoSeq;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(final String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public SolicitarTransferenciaPacienteController getController() {
		return controller;
	}

	public void setController(SolicitarTransferenciaPacienteController controller) {
		this.controller = controller;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

}
