package br.gov.mec.aghu.exames.questionario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaQuestionarioController extends ActionController {

	private static final long serialVersionUID = 5118710971998752265L;

	private static final String MANTER_QUESTIONARIO_CRUD = "manterQuestionarioCRUD";

	private static final String EXAME_QUESTIONARIO = "exames-manterExameQuestionario";

	private static final String QUESTOES_QUESTIONARIO 		 = "exames-manterQuestoesQuestionario";
	private static final String MANTER_QUESTIONARIO_PESQUISA = "manterQuestionarioPesquisa";

	private static final String ASSOCIACAO_REQUISITANTE_QUESTIONARIO = "exames-manterAssociacaoRequisitanteQuestionario";


	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;
	
	@EJB
	private IQuestionarioExamesFacade questionarioFacade;
	
	// Parâmetros do filtro de pesquisa
	private Integer seq;
	private String descricao;
	private Byte nroVias;
	private DominioSituacao situacao;
	private DominioSituacao[] listaSituacao = {DominioSituacao.A, DominioSituacao.I};
	
	// Identificador utilizado para exclusao de um item selecionado na listagem da consulta
	private Integer idItemExclusao;
	private Integer seqSelecionado;

	private List<AelQuestionarios> listaQuestionarios;
	
	// Flag para indicar se já foi feita uma pesquisa
	private Boolean pesquisaEfetuada;
	
	// Controla exibicao do "botao Novo"
	private boolean exibirBotaoNovo;
	

	private AelQuestionarios questionario;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciarEdicao() {
	 

		if (questionario != null && questionario.getSeq() != null) {
			questionario = questionarioExamesFacade.obterQuestionarioPorChavePrimaria(questionario.getSeq());

			if(questionario == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelarEdicao();
			}
			
		} else {
			questionario = new AelQuestionarios();
			questionario.setSituacao(DominioSituacao.A);
		}
		
		return null;
	
	} 
	
	public void iniciarPesquisa() {
	 

		this.pesquisaEfetuada = false;
	
	}

	public void limparPesquisa() {
		this.seq = null;
		this.descricao = null;
		this.nroVias = null;
		this.situacao = null;
		this.pesquisaEfetuada = false;
		this.listaQuestionarios = null;
		this.exibirBotaoNovo = false;
		this.seqSelecionado = null;
	}
	
	public String inserir(){
		return MANTER_QUESTIONARIO_CRUD;
	}
	
	public String editar(){
		return MANTER_QUESTIONARIO_CRUD;
	}
	
	public String exameQuestionario(){
		return EXAME_QUESTIONARIO;
	}
	
	public String questoesQuestionario(){
		return QUESTOES_QUESTIONARIO;
	}
	
	public String associacaoRequisitanteQuestionario(){
		return ASSOCIACAO_REQUISITANTE_QUESTIONARIO;
	}
	
	public void pesquisar(){
		this.listaQuestionarios = this.questionarioFacade.pesquisarQuestionarios(seq, descricao, nroVias, situacao);
		if(this.listaQuestionarios != null && this.listaQuestionarios.size() > 0) {
			this.seqSelecionado = this.listaQuestionarios.get(0).getSeq();
		}
		this.exibirBotaoNovo = true;
		this.pesquisaEfetuada = true;
	}
	
	public void excluir(){
		try {
			this.questionarioFacade.excluirQuestionario(idItemExclusao);
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_QUESTIONARIO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		pesquisar();
		this.idItemExclusao = null;
	}

	public String gravarEdicao() {
		try {
			
			if(questionario.getSeq() == null) {
				questionarioExamesFacade.persistirQuestionario(questionario);
			} else {
				questionarioExamesFacade.atualizarQuestionario(questionario);
			}
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_SALVAR_QUESTIONARIO");
			pesquisar();
			return cancelarEdicao();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelarEdicao() {
		this.questionario = null;
		return MANTER_QUESTIONARIO_PESQUISA;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Byte getNroVias() {
		return nroVias;
	}

	public void setNroVias(Byte nroVias) {
		this.nroVias = nroVias;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public List<AelQuestionarios> getListaQuestionarios() {
		return listaQuestionarios;
	}

	public void setListaQuestionarios(List<AelQuestionarios> listaQuestionarios) {
		this.listaQuestionarios = listaQuestionarios;
	}

	public Boolean getPesquisaEfetuada() {
		return pesquisaEfetuada;
	}

	public void setPesquisaEfetuada(Boolean pesquisaEfetuada) {
		this.pesquisaEfetuada = pesquisaEfetuada;
	}

	public Integer getIdItemExclusao() {
		return idItemExclusao;
	}

	public void setIdItemExclusao(Integer idItemExclusao) {
		this.idItemExclusao = idItemExclusao;
	}

	public DominioSituacao[] getListaSituacao() {
		return listaSituacao;
	}

	public void setListaSituacao(DominioSituacao[] listaSituacao) {
		this.listaSituacao = listaSituacao;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Integer getSeqSelecionado() {
		return seqSelecionado;
	}

	public void setSeqSelecionado(Integer seqSelecionado) {
		this.seqSelecionado = seqSelecionado;
	}

	public AelQuestionarios getQuestionario() {
		return questionario;
	}

	public void setQuestionario(AelQuestionarios questionario) {
		this.questionario = questionario;
	}
}