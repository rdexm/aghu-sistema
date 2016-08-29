package br.gov.mec.aghu.paciente.prontuario.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class SolicitarProntuarioPaginatorController extends	ActionController implements ActionPaginator{

	private static final long serialVersionUID = 2589105746140921052L;
	private static final Log LOG = LogFactory.getLog(SolicitarProntuarioPaginatorController.class);
	private static final String REDIRECT_MANTER_SOLICITACAO_PRONTUARIO = "solicitacaoProntuario";
	private static final String REDIRECT_IMPRIMIR_SOLICITACAO_PRONTUARIO = "solicitacaoProntuarioPdf";
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	@Inject
	private SolicitarProntuarioController solicitarProntuarioController;
	
	@Inject
	private RelatorioSolicitacaoProntuarioController relatorioSolicitacaoProntuarioController;
	
	private AipSolicitacaoProntuarios solicitacaoProntuario = new AipSolicitacaoProntuarios();
	
	@Inject @Paginator
	private DynamicDataModel<AipSolicitacaoProntuarios> dataModel;
	/**
	 * Codigo do paciente, obtido via page parameter.
	 */
	private AipSolicitacaoProntuarios solicitacaoProntuarioSelecionada;
	
	private Boolean exibirBotaoNovo = false;

	@PostConstruct
	public void init(){
		this.begin(this.conversation);
	}
	
	public void pesquisar() {
		reiniciarPaginator();
		exibirBotaoNovo = true;
	}

	public void limparPesquisa() {
		this.reiniciarPaginator();
		exibirBotaoNovo =  false;
		this.dataModel.setPesquisaAtiva(false);
		this.solicitacaoProntuario = new AipSolicitacaoProntuarios();	
	}
	
	public String iniciarInclusao(){
		solicitarProntuarioController.iniciarInclusao();
		return REDIRECT_MANTER_SOLICITACAO_PRONTUARIO;
	}

	public String editar(){
		solicitarProntuarioController.iniciarEdicao(solicitacaoProntuarioSelecionada);
		return REDIRECT_MANTER_SOLICITACAO_PRONTUARIO;
	}
	
	public String imprimir(AipSolicitacaoProntuarios itemSolicitacaoProntuario){
		try {
			relatorioSolicitacaoProntuarioController.print(itemSolicitacaoProntuario, "pesquisa");
			return REDIRECT_IMPRIMIR_SOLICITACAO_PRONTUARIO;
		}
		catch (JRException | SystemException | IOException e) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
			return null;
		}
		
	}
	
	public void excluir() {
		reiniciarPaginator();
		
		AipSolicitacaoProntuarios solicitacaoProntuarios = solicitacaoProntuarioSelecionada;
		try {
			if (solicitacaoProntuarios != null) {
				this.cadastroPacienteFacade.removerSolicitacaoProntuario(solicitacaoProntuarios);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_SOLICITACAO_PRONTUARIO",
						solicitacaoProntuarios.getCodigo());
			} else {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ERRO_REMOCAO_SOLICITACAO_PRONTUARIO");
			}

			this.solicitacaoProntuarioSelecionada = null;
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
		}
	}
	
	// ### Paginação ###

	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return  pacienteFacade.pesquisarSolicitacaoProntuarioCount(solicitacaoProntuario.getSolicitante(),
				solicitacaoProntuario.getResponsavel(), solicitacaoProntuario.getObservacao());
	}

	@Override
	public List<AipSolicitacaoProntuarios> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		List<AipSolicitacaoProntuarios> result = pacienteFacade.pesquisarSolicitacaoProntuario(
				firstResult, maxResults, solicitacaoProntuario.getSolicitante(), solicitacaoProntuario.getResponsavel(),
				solicitacaoProntuario.getObservacao());

		if (result == null) {
			result = new ArrayList<AipSolicitacaoProntuarios>();
		}

		return result;
		
		
	}

	// ### GETs e SETs ###

	/**
	 * @return the solicitacaoProntuario
	 */
	public AipSolicitacaoProntuarios getSolicitacaoProntuario() {
		return solicitacaoProntuario;
	}

	/**
	 * @param solicitacaoProntuario the solicitacaoProntuario to set
	 */
	public void setSolicitacaoProntuario(
			AipSolicitacaoProntuarios solicitacaoProntuario) {
		this.solicitacaoProntuario = solicitacaoProntuario;
	}

	/**
	 * @return the exibirBotaoNovo
	 */
	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	/**
	 * @param exibirBotaoNovo the exibirBotaoNovo to set
	 */
	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
	
	

	public DynamicDataModel<AipSolicitacaoProntuarios> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipSolicitacaoProntuarios> dataModel) {
		this.dataModel = dataModel;
	}

	public AipSolicitacaoProntuarios getSolicitacaoProntuarioSelecionada() {
		return solicitacaoProntuarioSelecionada;
	}

	public void setSolicitacaoProntuarioSelecionada(
			AipSolicitacaoProntuarios solicitacaoProntuarioSelecionada) {
		this.solicitacaoProntuarioSelecionada = solicitacaoProntuarioSelecionada;
	}
	
	
	
	
}
