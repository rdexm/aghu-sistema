package br.gov.mec.aghu.exames.questionario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.model.AelQuestoesQuestionarioId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroQuestaoQuestionarioController extends ActionController implements ActionPaginator {


	private static final long	serialVersionUID	= -5116612435477990720L;

	private static final String MANTER_QUESTIONARIO_PESQUISA = "exames-manterQuestionarioPesquisa";

	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;

	@Inject @Paginator
	private DynamicDataModel<AelQuestoesQuestionario> dataModel;
	
	// Parâmetros da conversação
	private Integer qtnSeq;

	private AelQuestoesQuestionario questoesQuestionario;
	private Boolean situacaoQuestaoQuestionario;
	
	private boolean emEdicao;
	private boolean iniciouTela;
	private Integer seqExclusao;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String inicio() {
	 

		if (!iniciouTela) {
			iniciouTela = true;
			// Reseta todos os campos
			this.limpar();
			
			if(this.getQuestoesQuestionario().getAelQuestionarios() == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			this.executarRefreshLista();
		}
		
		return null;
	
	}

	@Override
	public List<AelQuestoesQuestionario> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.questionarioExamesFacade.buscarAelQuestoesQuestionarioPorQuestionario(this.qtnSeq, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return this.questionarioExamesFacade.contarBuscarAelQuestoesQuestionarioPorQuestionario(this.qtnSeq);
	}

	/**
	 * Suggestion Box Questão
	 */
	public List<AelQuestao> pesquisarQuestao(String objPesquisa) {
		return this.returnSGWithCount(this.questionarioExamesFacade.buscarAelQuestaoSuggestion( objPesquisa, 0, 100, null, false),contarQuestaoSuggestion(objPesquisa));
	}
	
	public Long contarQuestaoSuggestion(String objPesquisa) {
		return this.questionarioExamesFacade.contarAelQuestaoSuggestion(objPesquisa);
	}

	/**
	 * Suggestion Box Grupo
	 */
	public List<AelGrupoQuestao> pesquisarGrupo(String objPesquisa) {
		return this.returnSGWithCount(this.questionarioExamesFacade.buscarAelGrupoQuestaoSuggestion(objPesquisa, 0, 100, null, false),contarGrupoSuggestion(objPesquisa));
	}
	
	public Long contarGrupoSuggestion(String objPesquisa) {
		return this.questionarioExamesFacade.contarAelGrupoQuestaoSuggestion(objPesquisa);
	}
	
	/**
	 * Grava Equipamento
	 */
	public void gravar() {
		boolean novo = this.getQuestoesQuestionario().getId() == null;
		try {
			
			if(!novo){
				AelQuestoesQuestionario aux = questionarioExamesFacade.obterAelQuestoesQuestionario(getQuestoesQuestionario().getId());
				if(aux == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					this.limpar();
					this.executarRefreshLista();
					return;
				}
			}
			
			this.getQuestoesQuestionario().setSituacao(this.situacaoQuestaoQuestionario ? DominioSituacao.A : DominioSituacao.I);

			this.questionarioExamesFacade.persistirAelQuestoesQuestionario(getQuestoesQuestionario());
			this.apresentarMsgNegocio(Severity.INFO,
					novo ? "MENSAGEM_SUCESSO_INSERIR_QUESTAO_QUESTIONARIO" : "MENSAGEM_SUCESSO_ALTERAR_QUESTAO_QUESTIONARIO",
					this.getQuestoesQuestionario().getQuestao().getDescricao());

			this.limpar();
			this.executarRefreshLista();
		} catch (final BaseException e) {
			if (novo) {
				this.getQuestoesQuestionario().setId(null);
			}
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void editar(final AelQuestoesQuestionario questaoQuestionario) {
		this.limpar();
		this.setQuestoesQuestionario(questaoQuestionario); 
		this.situacaoQuestaoQuestionario = DominioSituacao.A.equals(this.getQuestoesQuestionario().getSituacao());
		this.emEdicao = true;
	}

	public void limpar() {
		this.setQuestoesQuestionario(new AelQuestoesQuestionario());
		this.getQuestoesQuestionario().setAelQuestionarios(this.questionarioExamesFacade.obterQuestionarioPorId(qtnSeq));
		this.emEdicao = false;
		this.situacaoQuestaoQuestionario = true;
	}

	public void executarRefreshLista() {
		dataModel.reiniciarPaginator();
	}

	/**
	 * Limpa os atributos
	 */
	public void cancelarEdicao() {
		this.limpar();
	}

	/**
	 * Cancela o cadastro
	 */
	public String cancelar() {
		iniciouTela = false;
		return MANTER_QUESTIONARIO_PESQUISA;
	}

	/**
	 * Exclui um equipamento
	 */
	public void excluir() {
		try {
			if (seqExclusao != null) {
				final AelQuestao questao =this.questionarioExamesFacade.obterAelQuestaoById(this.seqExclusao, null, null);
				final String descricao = questao.getDescricao();

				this.questionarioExamesFacade.excluirAelQuestoesQuestionario(new AelQuestoesQuestionarioId(this.seqExclusao, this.qtnSeq));
				this.executarRefreshLista();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_QUESTAO_QUESTIONARIO", descricao);
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public void setSituacaoQuestaoQuestionario(Boolean situacaoQuestaoQuestionario) {
		this.situacaoQuestaoQuestionario = situacaoQuestaoQuestionario;
	}

	public Boolean getSituacaoQuestaoQuestionario() {
		return situacaoQuestaoQuestionario;
	}

	public void setQtnSeq(Integer qtnSeq) {
		this.qtnSeq = qtnSeq;
	}

	public Integer getQtnSeq() {
		return qtnSeq;
	}

	public void setQuestoesQuestionario(AelQuestoesQuestionario questoesQuestionario) {
		this.questoesQuestionario = questoesQuestionario;
	}

	public AelQuestoesQuestionario getQuestoesQuestionario() {
		return questoesQuestionario;
	}

	public DynamicDataModel<AelQuestoesQuestionario> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelQuestoesQuestionario> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}

	public Integer getSeqExclusao() {
		return seqExclusao;
	}

	public void setSeqExclusao(Integer seqExclusao) {
		this.seqExclusao = seqExclusao;
	}
}