package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do listagem de finalidades de
 * movimentação.
 * 
 * @author david.laks
 */

public class FinalidadeMovimentacaoPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 6074203284564135316L;

	private static final String REDIRECT_MANTER_FINALIDADE_MOVIMENTACAO = "finalidadeMovimentacaoCRUD";
	
	private static final Log LOG = LogFactory.getLog(FinalidadeMovimentacaoPaginatorController.class);
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	/**
	 * Referencia para a tela de inserção/edição
	 */
	@Inject
	private FinalidadeMovimentacaoController finalidadeMovimentacaoController;

	/**
	 * Dados utilizados pelo componente serverDataTable
	 */
	@Inject @Paginator
	private DynamicDataModel<AipFinalidadesMovimentacao> dataModel;
	
	/**
	 * Finalidade que será editada/excluída
	 */
	private AipFinalidadesMovimentacao finalidadeMovimentacao;
	
	/**
	 * Atributo utilizado para controlar a exibicao do botao
	 * "incluir finalidade de movimentação"
	 */
	private boolean exibirBotaoIncluirFinalidadeMovimentacao;

	/**
	 * Atributo referente ao campo de filtro de código da finalidade de
	 * movimentação na tela de pesquisa.
	 */
	private Integer codigoPesquisaFinalidadeMovimentacao;

	/**
	 * Atributo referente ao campo de filtro de descrição da finalidade de
	 * movimentação na tela de pesquisa.
	 */
	private String descricaoPesquisaFinalidadeMovimentacao;

	/**
	 * Atributo referente ao campo de filtro de situação da nacionalidade na
	 * tela de pesquisa.
	 */
	private DominioSituacao situacaoPesquisaFinalidadeMovimentacao;

	@PostConstruct
	public void init(){
		LOG.info("Iniciando conversação");
		this.begin(this.conversation);
	}

	public void reiniciarPaginator() {
		this.dataModel.reiniciarPaginator();
		
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirFinalidadeMovimentacao = true;
	}

	public void limparPesquisa() {
		this.codigoPesquisaFinalidadeMovimentacao = null;
		this.descricaoPesquisaFinalidadeMovimentacao = null;
		this.situacaoPesquisaFinalidadeMovimentacao = null;
		this.exibirBotaoIncluirFinalidadeMovimentacao = false;
		this.dataModel.limparPesquisa();
	}

	public String iniciarInclusao(){
		finalidadeMovimentacaoController.iniciarInclusao();
		return REDIRECT_MANTER_FINALIDADE_MOVIMENTACAO;
	}
	
	public String editar(){
		finalidadeMovimentacaoController.iniciarEdicao(finalidadeMovimentacao);
		return REDIRECT_MANTER_FINALIDADE_MOVIMENTACAO;
	}

	/**
	 * Método chamado na tela de pesquisa de finalidades movimentacao quando o
	 * usuário clicar no botão exluir da grid com a lista de finalidades
	 * movimentacao
	 * 
	 * @param Código
	 *            da finalidade movimentacao a ser removida
	 */
	public void excluir() {
		try {
			this.reiniciarPaginator();

			// Obtem finalidades movimentacao e remove a mesma
			this.cadastrosBasicosPacienteFacade.excluirFinalidadeMovimentacao(this.finalidadeMovimentacao);

			// Exibr mensagem de exclusão com sucesso e fecha janela de
			// confirmação
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_FINALIDADE_MOVIMENTACAO");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosPacienteFacade.pesquisaCount(this.codigoPesquisaFinalidadeMovimentacao,
				this.descricaoPesquisaFinalidadeMovimentacao, this.situacaoPesquisaFinalidadeMovimentacao);
	}

	@Override
	public List<AipFinalidadesMovimentacao> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<AipFinalidadesMovimentacao> result = this.cadastrosBasicosPacienteFacade.pesquisa(firstResult, maxResults, AipFinalidadesMovimentacao.Fields.DESCRICAO.toString(),
				true, this.codigoPesquisaFinalidadeMovimentacao, this.descricaoPesquisaFinalidadeMovimentacao,
				this.situacaoPesquisaFinalidadeMovimentacao);
		if (result == null){
			result = new ArrayList<AipFinalidadesMovimentacao>();
		}
		
		return result;
	}

	// ### GETs e SETs ###
		
	public boolean isExibirBotaoIncluirFinalidadeMovimentacao() {
		return exibirBotaoIncluirFinalidadeMovimentacao;
	}

	public void setExibirBotaoIncluirFinalidadeMovimentacao(boolean exibirBotaoIncluirFinalidadeMovimentacao) {
		this.exibirBotaoIncluirFinalidadeMovimentacao = exibirBotaoIncluirFinalidadeMovimentacao;
	}

	public Integer getCodigoPesquisaFinalidadeMovimentacao() {
		return codigoPesquisaFinalidadeMovimentacao;
	}

	public void setCodigoPesquisaFinalidadeMovimentacao(Integer codigoPesquisaFinalidadeMovimentacao) {
		this.codigoPesquisaFinalidadeMovimentacao = codigoPesquisaFinalidadeMovimentacao;
	}

	public String getDescricaoPesquisaFinalidadeMovimentacao() {
		return descricaoPesquisaFinalidadeMovimentacao;
	}

	public void setDescricaoPesquisaFinalidadeMovimentacao(String descricaoPesquisaFinalidadeMovimentacao) {
		this.descricaoPesquisaFinalidadeMovimentacao = descricaoPesquisaFinalidadeMovimentacao;
	}

	public DominioSituacao getSituacaoPesquisaFinalidadeMovimentacao() {
		return situacaoPesquisaFinalidadeMovimentacao;
	}

	public void setSituacaoPesquisaFinalidadeMovimentacao(DominioSituacao situacaoPesquisaFinalidadeMovimentacao) {
		this.situacaoPesquisaFinalidadeMovimentacao = situacaoPesquisaFinalidadeMovimentacao;
	}

	public DynamicDataModel<AipFinalidadesMovimentacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipFinalidadesMovimentacao> dataModel) {
		this.dataModel = dataModel;
	}

	public AipFinalidadesMovimentacao getFinalidadeMovimentacao() {
		return finalidadeMovimentacao;
	}

	public void setFinalidadeMovimentacao(
			AipFinalidadesMovimentacao finalidadeMovimentacao) {
		this.finalidadeMovimentacao = finalidadeMovimentacao;
	}
}
