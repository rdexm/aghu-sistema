package br.gov.mec.aghu.exames.contratualizacao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.exames.contratualizacao.business.IContratualizacaoFacade;
import br.gov.mec.aghu.model.AelArquivoIntegracao;
import br.gov.mec.aghu.model.AelArquivoSolicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;


public class PesquisaSolicitacoesArquivoPaginatorController extends ActionController implements ActionPaginator {

	private static final String TODOS = "TODOS";

	private static final long serialVersionUID = -1904739662290348902L;

	private static final String PESQUISAR_ARQUIVOS_PROCESSADOS = "exames-pesquisarArquivosProcessados";

	@EJB
	private IContratualizacaoFacade contratualizacaoFacade;

	private Integer arquivoSeq;
	private AelArquivoIntegracao aelArquivoIntegracao;
	private String nomePaciente;
	private Integer numeroSolicitacao;
	private boolean statusSucesso = true;
	private boolean statusErro = true;
	private String status = TODOS;
	private String tipo = TODOS;

	private boolean envioComAgendamento = true;
	private boolean envioSemAgendamento = true;
	private StatusSolicitacao statusSolicitacao = StatusSolicitacao.TODOS;
	private TipoEnvio tipoEnvio = TipoEnvio.TODOS;
	

	@Inject @Paginator
	private DynamicDataModel<AelArquivoSolicitacao> dataModel;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if (getArquivoSeq() != null) {
			// executa a pesquisa com o parametro do seq do arquivo
			aelArquivoIntegracao = this.contratualizacaoFacade.buscarArquivoIntegracaoPeloSeq(this.getArquivoSeq());
			if(aelArquivoIntegracao!=null){				
				dataModel.reiniciarPaginator();
			}
		}
	
	}

	public String voltar(){
		return PESQUISAR_ARQUIVOS_PROCESSADOS;
	}
	
	public void atualizarStatus(){
		if("SUCESSO".equals(status)){			
			statusSucesso = true;
			statusErro = false;
		}else if ("ERRO".equals(status)) {
			statusSucesso = false;
			statusErro = true;
		}else{
			statusSucesso = true;
			statusErro = true;
		}		
	}

	public void atualizarTipo(){
		if("COM_AGENDA".equals(tipo)){			
			envioComAgendamento = true;
			envioSemAgendamento = false;
		}else if ("SEM_AGENDA".equals(tipo)) {
			envioComAgendamento = false;
			envioSemAgendamento = true;
		}else{
			envioComAgendamento = true;
			envioSemAgendamento = true;
		}		
	}
	
	@Override
	public Long recuperarCount() {
		return contratualizacaoFacade.pesquisarSolicitacoesPorArquivoCount(aelArquivoIntegracao, nomePaciente, numeroSolicitacao,
																			statusSucesso, statusErro, envioComAgendamento,envioSemAgendamento);
	}

	@Override
	public List<AelArquivoSolicitacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,boolean asc) {
		return contratualizacaoFacade.pesquisarSolicitacoesPorArquivo(firstResult, maxResult, orderProperty, asc, aelArquivoIntegracao, nomePaciente, 
																	  numeroSolicitacao, statusSucesso, statusErro, envioComAgendamento, envioSemAgendamento);
	}
	
	public void pesquisar() {
		try {
			this.contratualizacaoFacade.validarFiltrosPesquisa(aelArquivoIntegracao, nomePaciente, numeroSolicitacao);
			dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}		
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		dataModel.limparPesquisa();

		statusSucesso = true;
		statusErro = true;
		status = TODOS;
		tipo = TODOS;

		envioComAgendamento = true;
		envioSemAgendamento = true;
		
		aelArquivoIntegracao = null;
		nomePaciente = null;
		numeroSolicitacao = null;
		
		arquivoSeq = null;
	}

	public List<AelArquivoIntegracao> pesquisarArquivosPeloNome(String param){
		return this.contratualizacaoFacade.pesquisarArquivoIntegracaoPeloNome(param);
	}
	

	public enum StatusSolicitacao {
		TODOS("Todos"), 
		SUCESSO("Processados com Sucesso"), 
		ERRO("Processados com Erro");

		private String nome;

		private StatusSolicitacao(String nome) {
			this.nome = nome;
		}

		@Override
		public String toString() {
			return nome;
		}
	}

	public enum TipoEnvio {
		TODOS("Todos"), 
		COM_AGENDAMENTO("Com Agendamento"), 
		SEM_AGENDAMENTO("Sem Agendamento");

		private String nome;

		private TipoEnvio(String nome) {
			this.nome = nome;
		}

		@Override
		public String toString() {
			return nome;
		}
	}

	public Integer getArquivoSeq() {
		return arquivoSeq;
	}

	public void setArquivoSeq(Integer arquivoSeq) {
		this.arquivoSeq = arquivoSeq;
	}

	public AelArquivoIntegracao getAelArquivoIntegracao() {
		return aelArquivoIntegracao;
	}

	public void setAelArquivoIntegracao(
			AelArquivoIntegracao aelArquivoIntegracao) {
		this.aelArquivoIntegracao = aelArquivoIntegracao;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public boolean isStatusSucesso() {
		return statusSucesso;
	}

	public void setStatusSucesso(boolean statusSucesso) {
		this.statusSucesso = statusSucesso;
	}

	public boolean isStatusErro() {
		return statusErro;
	}

	public void setStatusErro(boolean statusErro) {
		this.statusErro = statusErro;
	}

	public boolean isEnvioComAgendamento() {
		return envioComAgendamento;
	}

	public void setEnvioComAgendamento(boolean envioComAgendamento) {
		this.envioComAgendamento = envioComAgendamento;
	}

	public boolean isEnvioSemAgendamento() {
		return envioSemAgendamento;
	}

	public void setEnvioSemAgendamento(boolean envioSemAgendamento) {
		this.envioSemAgendamento = envioSemAgendamento;
	}

	public StatusSolicitacao getStatusSolicitacao() {
		return statusSolicitacao;
	}

	public void setStatusSolicitacao(StatusSolicitacao statusSolicitacao) {
		this.statusSolicitacao = statusSolicitacao;
	}

	public TipoEnvio getTipoEnvio() {
		return tipoEnvio;
	}

	public void setTipoEnvio(TipoEnvio tipoEnvio) {
		this.tipoEnvio = tipoEnvio;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public DynamicDataModel<AelArquivoSolicitacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelArquivoSolicitacao> dataModel) {
		this.dataModel = dataModel;
	}
}