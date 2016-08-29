package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterFormasParaAgendamentoPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<AacFormaAgendamento> dataModel;

	private static final long serialVersionUID = 2062421884200857887L;

	private final String PAGE_FORMAS_AGENDAMENTO_CRUD = "manterFormasParaAgendamentoCRUD";

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private AacPagador pagador;

	private AacTipoAgendamento tipoAgendamento;

	private AacCondicaoAtendimento condicaoAtendimento;

	private boolean exibirBotaoIncluirFormaParaAgendamento = false;

	private Short caaSeq;

	private Short tagSeq;

	private Short pgdSeq;
	
	private AacFormaAgendamento formaSelecionada;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	@Override
	public Long recuperarCount() {
		return this.ambulatorioFacade.listarFormasAgendamentosCount(this.pagador, this.tipoAgendamento, this.condicaoAtendimento);
	}

	@Override
	public List<AacFormaAgendamento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.ambulatorioFacade.listarFormasAgendamentos(firstResult, maxResult, orderProperty, asc, this.pagador,
				this.tipoAgendamento, this.condicaoAtendimento);
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.pagador = null;
		this.tipoAgendamento = null;
		this.condicaoAtendimento = null;
		this.exibirBotaoIncluirFormaParaAgendamento = false;
		this.dataModel.limparPesquisa();
	}

	public void excluirForma() {
		this.dataModel.reiniciarPaginator();

		try {
			this.ambulatorioFacade.removerFormaAgendamento(formaSelecionada.getId());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_FORMA_AGENDAMENTO");

			this.caaSeq = null;
			this.tagSeq = null;
			this.pgdSeq = null;
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String editarForma() {
		return PAGE_FORMAS_AGENDAMENTO_CRUD;
	}
	
	public String incluirForma() {
		return PAGE_FORMAS_AGENDAMENTO_CRUD;
	}
	
	public Long pesquisarPagadoresCount(String filtro) {
		return this.ambulatorioFacade.pesquisarPagadoresCount((String) filtro);
	}

	public List<AacPagador> pesquisarPagadores(String filtro) {
		return  this.returnSGWithCount(this.ambulatorioFacade.pesquisarPagadores((String) filtro),pesquisarPagadoresCount(filtro));
	}

	public Long pesquisarTiposAgendamentoCount(String filtro) {
		return this.ambulatorioFacade.pesquisarTiposAgendamentoCount((String) filtro);
	}

	public List<AacTipoAgendamento> pesquisarTiposAgendamento(String filtro) {
		return  this.returnSGWithCount(this.ambulatorioFacade.pesquisarTiposAgendamento((String) filtro),pesquisarTiposAgendamentoCount(filtro));
	}

	public Long pesquisarCondicoesAtendimentoCount(String filtro) {
		return this.ambulatorioFacade.pesquisarCondicoesAtendimentoCount((String) filtro);
	}

	public List<AacCondicaoAtendimento> pesquisarCondicoesAtendimento(String filtro) {
		return  this.returnSGWithCount(this.ambulatorioFacade.pesquisarCondicoesAtendimento((String) filtro),pesquisarCondicoesAtendimentoCount(filtro));
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirFormaParaAgendamento = true;
	}

	public AacPagador getPagador() {
		return pagador;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}

	public AacTipoAgendamento getTipoAgendamento() {
		return tipoAgendamento;
	}

	public void setTipoAgendamento(AacTipoAgendamento tipoAgendamento) {
		this.tipoAgendamento = tipoAgendamento;
	}

	public AacCondicaoAtendimento getCondicaoAtendimento() {
		return condicaoAtendimento;
	}

	public void setCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) {
		this.condicaoAtendimento = condicaoAtendimento;
	}

	public boolean isExibirBotaoIncluirFormaParaAgendamento() {
		return exibirBotaoIncluirFormaParaAgendamento;
	}

	public void setExibirBotaoIncluirFormaParaAgendamento(boolean exibirBotaoIncluirFormaParaAgendamento) {
		this.exibirBotaoIncluirFormaParaAgendamento = exibirBotaoIncluirFormaParaAgendamento;
	}

	public Short getCaaSeq() {
		return caaSeq;
	}

	public void setCaaSeq(Short caaSeq) {
		this.caaSeq = caaSeq;
	}

	public Short getTagSeq() {
		return tagSeq;
	}

	public void setTagSeq(Short tagSeq) {
		this.tagSeq = tagSeq;
	}

	public Short getPgdSeq() {
		return pgdSeq;
	}

	public void setPgdSeq(Short pgdSeq) {
		this.pgdSeq = pgdSeq;
	}

	public DynamicDataModel<AacFormaAgendamento> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AacFormaAgendamento> dataModel) {
	 this.dataModel = dataModel;
	}

	public AacFormaAgendamento getFormaSelecionada() {
		return formaSelecionada;
	}

	public void setFormaSelecionada(AacFormaAgendamento formaSelecionada) {
		this.formaSelecionada = formaSelecionada;
	}
}
