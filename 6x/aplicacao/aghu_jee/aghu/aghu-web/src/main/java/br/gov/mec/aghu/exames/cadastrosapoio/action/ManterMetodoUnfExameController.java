package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelMetodo;
import br.gov.mec.aghu.model.AelMetodoUnfExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author lalegre
 * 
 */

public class ManterMetodoUnfExameController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 947355802486110625L;

	private static final String PAGE_EXAMES_MANTER_UNIDADES_EXECUTORAS_EXAMES_CRUD = "exames-manterUnidadesExecutorasExamesCRUD";

	/**
	 * Unidade Executora de Exames Relacionada
	 */
	private AelUnfExecutaExames aelUnfExecutaExames;

	/**
	 * AelMetodoUnfExame Cadastrado
	 */
	private AelMetodoUnfExame metodoUnfExame;

	/**
	 * Lista de Metodos
	 */
	private List<AelMetodoUnfExame> listaMetodoUnfExames;

	/**
	 * Sigla Exames
	 */
	private String emaExaSigla;

	/**
	 * Material de Analise
	 */
	private Integer emaManSeq;

	/**
	 * Unidade Executora
	 */
	private Short unfSeq;

	/**
	 * mtdSeq AelMetodoUnfExame
	 */
	private Short mtdSeq;

	/**
	 * Seqp AelMetodoUnfExame
	 */
	private Short seqp;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 


		limpar();

		if (StringUtils.isNotBlank(this.emaExaSigla) && this.emaManSeq != null && this.unfSeq != null) {

			aelUnfExecutaExames = this.examesFacade.obterAelUnidadeExecutoraExamesPorID(this.emaExaSigla, this.emaManSeq, this.unfSeq);
			atualizaLista();

		}

	
	}

	/**
	 * Atualiza a lista de métodos
	 */
	private void atualizaLista() {

		listaMetodoUnfExames = this.examesFacade.obterAelMetodoUnfExamePorUnfExecutaExames(aelUnfExecutaExames);

	}

	/**
	 * Lista Metodos SuggestionBox
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<AelMetodo> obterMetodo(String objPesquisa) {

		return this.examesFacade.obterMetodosPorSerDescricao(objPesquisa);

	}

	/**
	 * Botao Gravar
	 */
	public void gravar() {

		if (metodoUnfExame.getMetodo() == null && metodoUnfExame.getDthrInicio() == null) {

			this.apresentarMsgNegocio("metodo", Severity.ERROR, CAMPO_OBRIGATORIO, "Método");
			this.apresentarMsgNegocio("dataInicio", Severity.ERROR, CAMPO_OBRIGATORIO, "Data Início");
			return;

		} else if (metodoUnfExame.getDthrInicio() == null) {

			this.apresentarMsgNegocio("dataInicio", Severity.ERROR, CAMPO_OBRIGATORIO, "Data Início");
			return;

		} else if (metodoUnfExame.getMetodo() == null) {

			this.apresentarMsgNegocio("metodo", Severity.ERROR, CAMPO_OBRIGATORIO, "Método");
			return;

		}

		try {

			metodoUnfExame.setUnfExecutaExames(aelUnfExecutaExames);
			cadastrosApoioExamesFacade.inserirMetodoUnfExame(metodoUnfExame);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_METODO_REALIZACAO_EXAMES");

			atualizaLista();
			limpar();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Botao Editar
	 * 
	 * @param metodoUnfExameSelecionado
	 */
	public void editar(AelMetodoUnfExame metodoUnfExameSelecionado) {

		this.metodoUnfExame = new AelMetodoUnfExame();
		this.metodoUnfExame.setId(metodoUnfExameSelecionado.getId());
		this.metodoUnfExame.setMetodo(metodoUnfExameSelecionado.getMetodo());
		this.metodoUnfExame.setDthrFim(metodoUnfExameSelecionado.getDthrFim());
		this.metodoUnfExame.setDthrInicio(metodoUnfExameSelecionado.getDthrInicio());
		this.metodoUnfExame.setServidor(metodoUnfExameSelecionado.getServidor());
		this.metodoUnfExame.setSituacao(metodoUnfExameSelecionado.getSituacao());
		this.metodoUnfExame.setUnfExecutaExames(metodoUnfExameSelecionado.getUnfExecutaExames());
		this.metodoUnfExame.setVersion(metodoUnfExameSelecionado.getVersion());
		this.metodoUnfExame.setCriadoEm(metodoUnfExameSelecionado.getCriadoEm());

		this.seqp = metodoUnfExameSelecionado.getId().getSeqp();
		this.mtdSeq = metodoUnfExameSelecionado.getId().getMtdSeq();

	}

	/**
	 * Confirma edição
	 */
	public void confirmar() {

		try {

			this.cadastrosApoioExamesFacade.atualizarMetodoUnfExame(metodoUnfExame);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_METODO_REALIZACAO_EXAMES");

			atualizaLista();
			limpar();

		} catch (ApplicationBusinessException e) {

			apresentarExcecaoNegocio(e);

		}

	}

	/**
	 * Cancela Edição
	 */
	public void limpar() {

		metodoUnfExame = new AelMetodoUnfExame();
		metodoUnfExame.setSituacao(DominioSituacao.A);
		this.seqp = null;
		this.mtdSeq = null;

	}

	/**
	 * Botao Voltar
	 * 
	 * @return
	 */
	public String voltar() {
		this.aelUnfExecutaExames = null;
		this.metodoUnfExame = null;
		this.listaMetodoUnfExames = null;
		this.emaExaSigla = null;
		this.emaManSeq = null;
		this.unfSeq = null;
		this.mtdSeq = null;
		this.seqp = null;
		return PAGE_EXAMES_MANTER_UNIDADES_EXECUTORAS_EXAMES_CRUD;
	}

	// GETS AND SETS

	public AelUnfExecutaExames getAelUnfExecutaExames() {
		return aelUnfExecutaExames;
	}

	public void setAelUnfExecutaExames(AelUnfExecutaExames aelUnfExecutaExames) {
		this.aelUnfExecutaExames = aelUnfExecutaExames;
	}

	public AelMetodoUnfExame getMetodoUnfExame() {
		return metodoUnfExame;
	}

	public void setMetodoUnfExame(AelMetodoUnfExame metodoUnfExame) {
		this.metodoUnfExame = metodoUnfExame;
	}

	public List<AelMetodoUnfExame> getListaMetodoUnfExames() {
		return listaMetodoUnfExames;
	}

	public void setListaMetodoUnfExames(List<AelMetodoUnfExame> listaMetodoUnfExames) {
		this.listaMetodoUnfExames = listaMetodoUnfExames;
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

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Short getMtdSeq() {
		return mtdSeq;
	}

	public void setMtdSeq(Short mtdSeq) {
		this.mtdSeq = mtdSeq;
	}

}
