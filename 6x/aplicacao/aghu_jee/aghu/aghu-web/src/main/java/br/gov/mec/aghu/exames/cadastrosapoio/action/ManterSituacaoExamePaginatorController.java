package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterSituacaoExamePaginatorController extends ActionController implements ActionPaginator {
	
	private static final String PAGE_EDITAR = "manterSituacaoExameCRUD";

	@PostConstruct
	protected void inicializar() {
		LOG.info("");
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AelSitItemSolicitacoes> dataModel;

	private static final Log LOG = LogFactory.getLog(ManterSituacaoExamePaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3407865326491993808L;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	//@EJB
	//private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	//Campos filtro
	private String codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	
	//Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;
	
	
	public String editar() {
		return PAGE_EDITAR;
	}
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		//Limpa filtro
		codigo = null;
		descricao = null;
		indSituacao = null;
		exibirBotaoNovo = false;
		
		//Apaga resultados da exibição
		//setAtivo(false);
		dataModel.limparPesquisa();
	}
	
	public String iniciarInclusao() {
		return PAGE_EDITAR;
	}
	
	@Override
	public List<AelSitItemSolicitacoes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		//Cria objeto com os parâmetros para busca
		AelSitItemSolicitacoes elemento = new AelSitItemSolicitacoes(codigo, descricao, indSituacao);
		
		return examesFacade.pesquisarSituacaoExame(firstResult, maxResult, AelSitItemSolicitacoes.Fields.CODIGO.toString(), true, elemento);
	}
	
	@Override
	public Long recuperarCount() {
		//Cria objeto com os parâmetros para busca
		AelSitItemSolicitacoes elemento = new AelSitItemSolicitacoes(codigo, descricao, indSituacao);
		
		return examesFacade.pesquisarSituacaoExameCount(elemento);
	}
	
	public void setAtivo(AelSitItemSolicitacoes situacao, boolean ativar) {
		try {
			situacao = cadastrosApoioExamesFacade.ativarDesativarSituacaoExame(situacao, ativar);
			
			//Apresenta as mensagens de acordo
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_SITUACAO_EXAME", situacao.getDescricao());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	//------------------------------------------------------
	//Getters e setters
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	} 


	public DynamicDataModel<AelSitItemSolicitacoes> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelSitItemSolicitacoes> dataModel) {
	 this.dataModel = dataModel;
	}
}