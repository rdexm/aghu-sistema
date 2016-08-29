package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelExamesProva;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author lalegre
 * 
 */

public class ManterProvaExameController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -6801221820466058465L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	/**
	 * Sigla do Exame
	 */
	private String sigla;

	/**
	 * Seq material de análise
	 */
	private Integer manSeq;

	/**
	 * Exame Material de Análise
	 */
	private AelExamesMaterialAnalise examesMaterialAnalise;

	/**
	 * Exame Material de Análise Prova
	 */
	private AelExamesMaterialAnalise examesMaterialAnaliseEhProva;

	/**
	 * Exame Prova
	 */
	private AelExamesProva examesProva;

	/**
	 * Lista de Exames Prova
	 */
	private List<AelExamesProva> listaExamesProva;

	/**
	 * View Suggestion Box
	 */
	private VAelExameMatAnalise vAelExameMatAnalise;

	/**
	 * Sigla da prova em edição
	 */
	private String siglaProva;

	/**
	 * Material da prova em edição
	 */
	private Integer manSeqProva;

	/**
	 * Exclusão
	 */
	private AelExamesProva examesProvaExclusao;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 


		
		if (StringUtils.isNotBlank(this.sigla) && this.manSeq != null) {

			this.examesMaterialAnalise = this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.sigla, this.manSeq);

		}

		this.examesProva = new AelExamesProva();
		this.examesProva.setIndConsiste(true);
		this.examesProva.setIndSituacao(DominioSituacao.A);
		atualizarLista(sigla, manSeq);
	

		//this.//setIgnoreInitPageConfig(true);

	
	}

	/**
	 * Confirma operação
	 */
	public void confirmar() {

		try {

			if (this.examesProva.getId() == null) {

				this.examesMaterialAnaliseEhProva = this.examesFacade.buscarAelExamesMaterialAnalisePorId(vAelExameMatAnalise.getId().getExaSigla(), vAelExameMatAnalise.getId().getManSeq());

				this.examesProva.setExamesMaterialAnalise(examesMaterialAnalise);
				this.examesProva.setExamesMaterialAnaliseEhProva(examesMaterialAnaliseEhProva);
				this.cadastrosApoioExamesFacade.inserirExamesProva(examesProva);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_PROVA_EXAME");

			} else {

				this.cadastrosApoioExamesFacade.atualizarExamesProva(examesProva);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_PROVA_EXAME");

			}

		} catch (BaseException e) {

			apresentarExcecaoNegocio(e);

		}

		limpar();

	}

	/**
	 * Edita o registro
	 * 
	 * @param oldExamesProva
	 */
	public void editar(AelExamesProva oldExamesProva) {

		this.vAelExameMatAnalise = new VAelExameMatAnalise();
		AelExamesMaterialAnaliseId id = new AelExamesMaterialAnaliseId();
		id.setExaSigla(oldExamesProva.getId().getEmaExaSiglaEhProva());
		id.setManSeq(oldExamesProva.getId().getEmaManSeqEhProva());
		this.vAelExameMatAnalise.setId(id);
		this.vAelExameMatAnalise.setNomeUsualMaterial(oldExamesProva.getExamesMaterialAnaliseEhProva().getAelExames().getDescricao());

		this.siglaProva = oldExamesProva.getId().getEmaExaSiglaEhProva();
		this.manSeqProva = oldExamesProva.getId().getEmaManSeqEhProva();
		this.examesProva.setExamesMaterialAnalise(oldExamesProva.getExamesMaterialAnalise());
		this.examesProva.setExamesMaterialAnaliseEhProva(oldExamesProva.getExamesMaterialAnaliseEhProva());
		this.examesProva.setId(oldExamesProva.getId());
		this.examesProva.setIndConsiste(oldExamesProva.getIndConsiste());
		this.examesProva.setIndSituacao(oldExamesProva.getIndSituacao());
		this.examesProva.setServidor(oldExamesProva.getServidor());
		this.examesProva.setVersion(oldExamesProva.getVersion());

	}

	/**
	 * Exclui o registro
	 */
	public void excluir(AelExamesProva examesProvaExclusao) {

		try {
			this.cadastrosApoioExamesFacade.removerExamesProva(examesProvaExclusao);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_PROVA_EXAME");
			atualizarLista(sigla, manSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.examesProvaExclusao = null;
		}
	}

	/**
	 * Cancela o cadastro
	 */
	public String cancelar() {
		this.sigla = null;
		this.manSeq = null;
		this.examesMaterialAnalise = null;
		this.examesMaterialAnaliseEhProva = null;
		this.examesProva = null;
		this.listaExamesProva = null;
		this.vAelExameMatAnalise = null;
		this.siglaProva = null;
		this.manSeqProva = null;
		this.examesProvaExclusao = null;
		//this.
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	public void cancelarExclusao() {
		this.examesProvaExclusao = null;
	}

	/**
	 * Limpa os atributos
	 */
	public void limpar() {

		this.siglaProva = null;
		this.manSeqProva = null;
		this.vAelExameMatAnalise = null;
		this.examesProva = new AelExamesProva();
		this.examesProva.setIndConsiste(true);
		this.examesProva.setIndSituacao(DominioSituacao.A);
		atualizarLista(this.sigla, this.manSeq);

	}

	/**
	 * Consulta suggestionbox
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<VAelExameMatAnalise> obterExameMaterialAnalise(String objPesquisa) {
		String siglaDescViewExaMatAnalise = (String) objPesquisa;
		try {

			if (StringUtils.isBlank(siglaDescViewExaMatAnalise)) {
				apresentarMsgNegocio(Severity.ERROR, "ERROR_TAMANHO_FILTRO_MINIMO"); // Deve ser informado mais que três caracteres pra realizar a pesquisa.
				return new LinkedList<VAelExameMatAnalise>();
			}

			return cadastrosApoioExamesFacade.obterExameMaterialAnalise(siglaDescViewExaMatAnalise);

		} catch (ApplicationBusinessException e) {

			super.apresentarExcecaoNegocio(e);
			return new LinkedList<VAelExameMatAnalise>();

		}

	}

	/**
	 * Atualiza a lista de recomendações de exames
	 * 
	 * @param sigla
	 * @param manSeq
	 */
	private void atualizarLista(String sigla, Integer manSeq) {

		this.listaExamesProva = this.examesFacade.obterAelExamesProva(sigla, manSeq);

	}

	// GETS AND SETS

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public AelExamesMaterialAnalise getExamesMaterialAnalise() {
		return examesMaterialAnalise;
	}

	public void setExamesMaterialAnalise(AelExamesMaterialAnalise examesMaterialAnalise) {
		this.examesMaterialAnalise = examesMaterialAnalise;
	}

	public AelExamesProva getExamesProva() {
		return examesProva;
	}

	public void setExamesProva(AelExamesProva examesProva) {
		this.examesProva = examesProva;
	}

	public VAelExameMatAnalise getvAelExameMatAnalise() {
		return vAelExameMatAnalise;
	}

	public void setvAelExameMatAnalise(VAelExameMatAnalise vAelExameMatAnalise) {
		this.vAelExameMatAnalise = vAelExameMatAnalise;
	}

	public AelExamesMaterialAnalise getExamesMaterialAnaliseEhProva() {
		return examesMaterialAnaliseEhProva;
	}

	public void setExamesMaterialAnaliseEhProva(AelExamesMaterialAnalise examesMaterialAnaliseEhProva) {
		this.examesMaterialAnaliseEhProva = examesMaterialAnaliseEhProva;
	}

	public List<AelExamesProva> getListaExamesProva() {
		return listaExamesProva;
	}

	public void setListaExamesProva(List<AelExamesProva> listaExamesProva) {
		this.listaExamesProva = listaExamesProva;
	}

	public String getSiglaProva() {
		return siglaProva;
	}

	public void setSiglaProva(String siglaProva) {
		this.siglaProva = siglaProva;
	}

	public Integer getManSeqProva() {
		return manSeqProva;
	}

	public void setManSeqProva(Integer manSeqProva) {
		this.manSeqProva = manSeqProva;
	}

	public AelExamesProva getExamesProvaExclusao() {
		return examesProvaExclusao;
	}

	public void setExamesProvaExclusao(AelExamesProva examesProvaExclusao) {
		this.examesProvaExclusao = examesProvaExclusao;
	}

}