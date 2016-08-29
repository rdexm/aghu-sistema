package br.gov.mec.aghu.exames.questionario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioOrigemQuestionario;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.model.AelExQuestionarioOrigens;
import br.gov.mec.aghu.model.AelExQuestionarioOrigensId;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.VAelExameMatAnalise;

public class AssociaOrigemQuestionarioController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -374090823391361635L;

	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;

	private AelExQuestionarioOrigens questionarioOrigem;
	private AelExamesQuestionario exameQuestionario;
	private VAelExameMatAnalise exame;
	private DominioOrigemQuestionario origemQuestionario;
	private Integer nroVias;

	// parametros recebidos da estoria #2233
	private Integer qtnSeq;
	private String emaExaSigla;
	private Integer emaManSeq;

	private String origem;

	private List<AelExQuestionarioOrigens> listaQuestionarioOrigens;

	private boolean modoEdicao;
	private boolean colorir;
	private boolean exibePainelCancelaAlteracao;

	/**
	 * Redirecionamento para estoria 2233 - Associar exames ao questionário
	 * 
	 */
	private static final String PAGE_EXAMES_MANTER_EXAME_QUESTIONARIO = "exames-manterExameQuestionario";
	private static final String PAGE_EXAMES_MANTEM_INFO_COMPLEMENTARES_VINCULADAS_EXAME = "exames-mantemInfoComplementaresVinculadasExame";

	/**
	 * Mensagens das operações realizadas
	 */
	private enum AssociarOrigemQuestionarioControllerMessages {
		MENSAGEM_SUCESSO_ALTERAR_ASSOCIACAO_ORIGEM_QUESTIONARIO, MENSAGEM_SUCESSO_EXCLUIR_ASSOCIACAO_ORIGEM_QUESTIONARIO, MENSAGEM_SUCESSO_GRAVAR_ASSOCIACAO_ORIGEM_QUESTIONARIO;
	}

	/**
	 * Executado quando a tela é acessada
	 */
	public void iniciar() {
	 

		setNroVias(null);
		setOrigemQuestionario(null);
		setModoEdicao(Boolean.FALSE);
		setColorir(Boolean.FALSE);
		setExibePainelCancelaAlteracao(Boolean.FALSE);
		carregarDadosIniciais();
		pesquisar();
	
	}

	/**
	 * Carrega os dados referentes a questionario e exame de acordo com os parâmetros recebidos da estoria #2233
	 */
	private void carregarDadosIniciais() {
		questionarioOrigem = new AelExQuestionarioOrigens();
		questionarioOrigem.setId(new AelExQuestionarioOrigensId());
		// if (qtnSeq != null && emaExaSigla != null && emaManSeq != null) {
		if (qtnSeq != null && emaExaSigla != null && emaManSeq != null) {
			questionarioOrigem.getId().setEqeQtnSeq(qtnSeq);
			questionarioOrigem.getId().setEqeEmaExaSigla(emaExaSigla);
			questionarioOrigem.getId().setEqeEmaManSeq(emaManSeq);
			exameQuestionario = questionarioExamesFacade.obterAelExamesQuestionario(emaExaSigla, emaManSeq, qtnSeq);
			questionarioOrigem.setAelExamesQuestionario(exameQuestionario);
			exame = questionarioExamesFacade.buscarVAelExameMatAnalisePelaSiglaESeq(emaExaSigla, emaManSeq);
		}
	}

	/**
	 * Habilita o modo edição e carrega os valores a serem alterados
	 */
	public void editar(AelExQuestionarioOrigens obj) {
		this.nroVias = obj.getNroVias();
		this.origemQuestionario = obj.getId().getOrigemQuestionario();
		this.questionarioOrigem = obj;
		setModoEdicao(Boolean.TRUE);
		setColorir(Boolean.TRUE);
	}

	/**
	 * Exclui o registro selecionado e atualiza a grid de pesquisa
	 */
	public void excluir(AelExQuestionarioOrigens obj)  {
		try {
			this.origemQuestionario = obj.getId().getOrigemQuestionario();
			this.questionarioOrigem = obj;
			questionarioExamesFacade.removerOrigem(questionarioOrigem.getId());
			this.apresentarMsgNegocio(Severity.INFO, AssociarOrigemQuestionarioControllerMessages.MENSAGEM_SUCESSO_EXCLUIR_ASSOCIACAO_ORIGEM_QUESTIONARIO.toString());
			setNroVias(null);
			setOrigemQuestionario(null);
			pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.questionarioOrigem = null;
		}
	}

	/**
	 * Insere novo ou atualiza registro selecionado e atualiza a grid de pesquisa
	 */
	public void gravar() {
		try {
			String msgConfirmacao = "";
			if (modoEdicao) {
				msgConfirmacao = AssociarOrigemQuestionarioControllerMessages.MENSAGEM_SUCESSO_ALTERAR_ASSOCIACAO_ORIGEM_QUESTIONARIO.toString();
			} else {
				carregarDadosIniciais();
				msgConfirmacao = AssociarOrigemQuestionarioControllerMessages.MENSAGEM_SUCESSO_GRAVAR_ASSOCIACAO_ORIGEM_QUESTIONARIO.toString();
			}

			questionarioOrigem.setNroVias(nroVias);
			questionarioOrigem.getId().setOrigemQuestionario(origemQuestionario);
			questionarioExamesFacade.gravarOrigem(questionarioOrigem, modoEdicao);

			if (StringUtils.isNotBlank(msgConfirmacao)) {
				this.apresentarMsgNegocio(Severity.INFO, msgConfirmacao);
			}
			limpar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limpar() {
		setNroVias(null);
		setOrigemQuestionario(null);
		setModoEdicao(Boolean.FALSE);
		setColorir(Boolean.FALSE);
		setExibePainelCancelaAlteracao(Boolean.FALSE);
		pesquisar();
	}

	/**
	 * Retorna para a estoria #2233
	 * 
	 * @return
	 */
	public String voltar() {
		setNroVias(null);
		setOrigemQuestionario(null);
		setColorir(Boolean.FALSE);
		setModoEdicao(Boolean.FALSE);
		setExibePainelCancelaAlteracao(Boolean.FALSE);
		if (StringUtils.isNotBlank(origem)) {
			if (StringUtils.equals(origem, PAGE_EXAMES_MANTER_EXAME_QUESTIONARIO)) {
				this.limparParametros();
				return PAGE_EXAMES_MANTER_EXAME_QUESTIONARIO;
			}
			if (StringUtils.equals(origem, PAGE_EXAMES_MANTEM_INFO_COMPLEMENTARES_VINCULADAS_EXAME)) {
				this.limparParametros();
				return PAGE_EXAMES_MANTEM_INFO_COMPLEMENTARES_VINCULADAS_EXAME;
			}
		}
		return null;
	}

	private void limparParametros() {
		this.questionarioOrigem = null;
		this.exameQuestionario = null;
		this.exame = null;
		this.origemQuestionario = null;
		this.nroVias = null;
		// this.qtnSeq = null;
		// this.emaExaSigla = null;
		// this.emaManSeq = null;
		this.origem = null;
		this.listaQuestionarioOrigens = null;
		this.modoEdicao = false;
		this.colorir = false;
		this.exibePainelCancelaAlteracao = false;
	}

	/**
	 * Retorna a lista de AelExQuestionarioOrigens
	 */
	public void pesquisar() {
		this.listaQuestionarioOrigens = questionarioExamesFacade.pesquisarAelExQuestionarioOrigens(this.exameQuestionario.getId().getEmaExaSigla(), this.exameQuestionario.getId().getEmaManSeq(),
				this.exameQuestionario.getId().getQtnSeq());
	}

	/**
	 * Verifica a linha selecionada
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isOrigemSelecionada(AelExQuestionarioOrigens obj) {
		return questionarioOrigem != null && questionarioOrigem.equals(obj) && colorir;
	}

	/**
	 * Retorna a descrição do exame seguindo o padrão abaixo
	 * 
	 * @return SIGLA - NOME_USUAL_MATERIAL
	 */
	public String getDescricaoExame() {
		if (exame != null) {
			return exame.getId().getExaSigla().concat(" - ").concat(exame.getNomeUsualMaterial());
		}
		return "";
	}

	/**
	 * Itens do combo origem
	 * 
	 * @return
	 */
	public DominioOrigemQuestionario[] getOrigemItens() {
		return DominioOrigemQuestionario.values();
	}

	public AelExQuestionarioOrigens getQuestionarioOrigem() {
		return questionarioOrigem;
	}

	public void setQuestionarioOrigem(AelExQuestionarioOrigens questionarioOrigem) {
		this.questionarioOrigem = questionarioOrigem;
	}

	public AelExamesQuestionario getExameQuestionario() {
		return exameQuestionario;
	}

	public void setExameQuestionario(AelExamesQuestionario exameQuestionario) {
		this.exameQuestionario = exameQuestionario;
	}

	public VAelExameMatAnalise getExame() {
		return exame;
	}

	public void setExame(VAelExameMatAnalise exame) {
		this.exame = exame;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public DominioOrigemQuestionario getOrigemQuestionario() {
		return origemQuestionario;
	}

	public void setOrigemQuestionario(DominioOrigemQuestionario origemQuestionario) {
		this.origemQuestionario = origemQuestionario;
	}

	public boolean isColorir() {
		return colorir;
	}

	public void setColorir(boolean colorir) {
		this.colorir = colorir;
	}

	public boolean isExibePainelCancelaAlteracao() {
		return exibePainelCancelaAlteracao;
	}

	public void setExibePainelCancelaAlteracao(boolean exibePainelCancelaAlteracao) {
		this.exibePainelCancelaAlteracao = exibePainelCancelaAlteracao;
	}

	public Integer getNroVias() {
		return nroVias;
	}

	public void setNroVias(Integer nroVias) {
		this.nroVias = nroVias;
	}

	public List<AelExQuestionarioOrigens> getListaQuestionarioOrigens() {
		return listaQuestionarioOrigens;
	}

	public void setListaQuestionarioOrigens(List<AelExQuestionarioOrigens> listaQuestionarioOrigens) {
		this.listaQuestionarioOrigens = listaQuestionarioOrigens;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	public Integer getQtnSeq() {
		return qtnSeq;
	}

	public void setQtnSeq(Integer qtnSeq) {
		this.qtnSeq = qtnSeq;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

}