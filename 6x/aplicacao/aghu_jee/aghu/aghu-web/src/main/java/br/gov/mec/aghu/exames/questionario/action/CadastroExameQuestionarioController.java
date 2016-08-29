package br.gov.mec.aghu.exames.questionario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioProgramacaoExecExames;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroExameQuestionarioController extends ActionController {

	private static final long serialVersionUID = 8789252292529947273L;

	private static final String MANTER_QUESTIONARIO_PESQUISA 	      = "manterQuestionarioPesquisa";
	private static final String MANTER_ASSOCIACAO_ORIGEM_QUESTIONARIO = "exames-manterAssociacaoOrigemQuestionario";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;

	// Parâmetros da conversação
	private String emaExaSigla;
	private Integer emaManSeq;
	private Integer qtnSeq;
	private String voltarPara; // O padrão é voltar para interface de pesquisa

	private AelExamesQuestionario examesQuestionario;
	private AelExamesQuestionario examesQuestionarioAhExcluir;
	private AelQuestionarios aelQuestionarios;
	private List<AelExamesQuestionario> listaExamesQuestionarios;
	private DominioProgramacaoExecExames programacaoExecExames;
	private VAelExameMatAnalise exame;
	private Boolean situacaoExameQuestionario;

	private boolean emEdicao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String inicio() {
	 

		if (this.qtnSeq != null) {
			// Reseta todos os campos
			this.limpar();

			aelQuestionarios = questionarioExamesFacade.obterQuestionarioPorId(qtnSeq);
			
			if(aelQuestionarios == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		}
		
		return null;
	
	}

	/**
	 * Suggestion Box Exames
	 */
	public List<VAelExameMatAnalise> pesquisarExames(String objPesquisa) {
		final String valPesquisa = objPesquisa != null ? StringUtils.trim((String) objPesquisa) : null;
		try {
			return this.returnSGWithCount(this.cadastrosApoioExamesFacade.buscarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(valPesquisa),contarExamesSuggestion(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public Long contarExamesSuggestion(String objPesquisa) {
		final String valPesquisa = objPesquisa != null ? StringUtils.trim((String) objPesquisa) : null;
		return this.cadastrosApoioExamesFacade.contarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(valPesquisa);
	}

	/**
	 * Grava Equipamento
	 */
	public void gravar() {
		boolean novo = this.examesQuestionario.getId() == null;
		try {
			if(!novo){
				AelExamesQuestionario aux = questionarioExamesFacade.obterAelExamesQuestionario(examesQuestionario.getId().getEmaExaSigla(), 
																								examesQuestionario.getId().getEmaManSeq(), 
																								examesQuestionario.getId().getQtnSeq());
				if(aux == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					inicio();
				}
			}
			
			AelExamesMaterialAnalise examesMaterialAnalise = cadastrosApoioExamesFacade.obterAelExamesMaterialAnaliseById(this.exame.getId());
			this.examesQuestionario.setExamesMaterialAnalise(examesMaterialAnalise);
			this.examesQuestionario.setSituacao(this.situacaoExameQuestionario ? DominioSituacao.A : DominioSituacao.I);
			this.examesQuestionario.setAelQuestionarios(this.questionarioExamesFacade.obterQuestionarioPorId(qtnSeq));
			
			this.questionarioExamesFacade.persistirAelExamesQuestionario(this.examesQuestionario);

			this.apresentarMsgNegocio(Severity.INFO,
					novo ? "MENSAGEM_SUCESSO_INSERIR_EXAME_QUESTIONARIO" : "MENSAGEM_SUCESSO_ALTERAR_EXAME_QUESTIONARIO",
							examesMaterialAnalise.getNomeUsualMaterial());

			this.inicio();
		} catch (final BaseException e) {
			if (novo) {
				this.examesQuestionario.setId(null);
			}
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String associarOrigens(final AelExamesQuestionario exameEditar) {
		this.emaExaSigla = exameEditar.getId().getEmaExaSigla();
		this.emaManSeq = exameEditar.getId().getEmaManSeq();
		this.qtnSeq = exameEditar.getId().getQtnSeq();
		return MANTER_ASSOCIACAO_ORIGEM_QUESTIONARIO;
	}
	
	public void editar(final AelExamesQuestionario exameEditar) {
		 
		this.limpar();
		this.examesQuestionario = exameEditar; 
		this.exame = this.cadastrosApoioExamesFacade.buscarVAelExameMatAnalisePorExameMaterialAnalise(this.examesQuestionario.getExamesMaterialAnalise());
		this.situacaoExameQuestionario = DominioSituacao.A.equals(this.examesQuestionario.getSituacao());
		this.emEdicao = true;
	}

	public void limpar() {
		this.examesQuestionario = new AelExamesQuestionario();
		this.emaExaSigla = null;
		this.emaManSeq = null;
		this.emEdicao = false;
		this.exame = null;
		this.situacaoExameQuestionario = true;
		this.executarRefreshLista();
	}

	
	public void executarRefreshLista() {
		this.listaExamesQuestionarios = this.questionarioExamesFacade.buscarAelExamesQuestionarioPorQuestionario(this.qtnSeq);
		if(this.listaExamesQuestionarios != null && !this.listaExamesQuestionarios.isEmpty()){
			final AelExamesQuestionario primeiro = this.listaExamesQuestionarios.get(0);
			this.emaExaSigla = primeiro.getId().getEmaExaSigla();
			this.emaManSeq = primeiro.getId().getEmaManSeq();
		}
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
		return MANTER_QUESTIONARIO_PESQUISA;
	}

	public void excluir() {
		try {
			if (examesQuestionarioAhExcluir != null) {
				final String descricao = examesQuestionarioAhExcluir.getExamesMaterialAnalise().getNomeUsualMaterial();
				this.questionarioExamesFacade.excluirAelExamesQuestionario(examesQuestionarioAhExcluir.getId());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_EXAME_QUESTIONARIO", descricao);
			}

			this.executarRefreshLista();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		this.setQtnSeq(null);
		return this.voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public DominioProgramacaoExecExames getProgramacaoExecExames() {
		return programacaoExecExames;
	}

	public void setProgramacaoExecExames(
			DominioProgramacaoExecExames programacaoExecExames) {
		this.programacaoExecExames = programacaoExecExames;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public void setExame(VAelExameMatAnalise exame) {
		this.exame = exame;
	}

	public VAelExameMatAnalise getExame() {
		return exame;
	}

	public void setSituacaoExameQuestionario(Boolean situacaoExameQuestionario) {
		this.situacaoExameQuestionario = situacaoExameQuestionario;
	}

	public Boolean getSituacaoExameQuestionario() {
		return situacaoExameQuestionario;
	}

	public void setExamesQuestionario(AelExamesQuestionario examesQuestionario) {
		this.examesQuestionario = examesQuestionario;
	}

	public AelExamesQuestionario getExamesQuestionario() {
		return examesQuestionario;
	}

	public void setQtnSeq(Integer qtnSeq) {
		this.qtnSeq = qtnSeq;
	}

	public Integer getQtnSeq() {
		return qtnSeq;
	}

	public void setListaExamesQuestionarios(
			List<AelExamesQuestionario> listaexamesQuestionarios) {
		this.listaExamesQuestionarios = listaexamesQuestionarios;
	}

	public List<AelExamesQuestionario> getListaExamesQuestionarios() {
		return listaExamesQuestionarios;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public AelQuestionarios getAelQuestionarios() {
		return aelQuestionarios;
	}

	public void setAelQuestionarios(AelQuestionarios aelQuestionarios) {
		this.aelQuestionarios = aelQuestionarios;
	}

	public AelExamesQuestionario getExamesQuestionarioAhExcluir() {
		return examesQuestionarioAhExcluir;
	}

	public void setExamesQuestionarioAhExcluir(
			AelExamesQuestionario examesQuestionarioAhExcluir) {
		this.examesQuestionarioAhExcluir = examesQuestionarioAhExcluir;
	}

}