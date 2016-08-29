package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioProgramacaoExecExames;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExameReflexo;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.VAelExameMatAnalise;

public class CadastroExameReflexoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -6017710391588902320L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	// Parâmetros da conversação
	private String exaSigla;
	private Integer manSeq;

	private AelExameReflexo exameReflexo;
	private List<AelExameReflexo> listaReflexos;
	private DominioProgramacaoExecExames programacaoExecExames;
	private VAelExameMatAnalise exame;
	private Boolean situacaoExameReflexo;

	/*
	 * Controla a edição dos itens
	 */
	private boolean emEdicao;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 

		if (this.exaSigla != null && manSeq != null) {
			// Reseta todos os campos
			this.limpar();

			// Obtém o Exame Material de Análise
			this.exameReflexo.setAelExamesMaterialAnalise(this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.exaSigla, this.manSeq));

		}
	
	}

	/**
	 * Suggestion Box Exames
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<VAelExameMatAnalise> pesquisarExames(String objPesquisa) {
		final String valPesquisa = objPesquisa != null ? StringUtils.trim((String) objPesquisa) : null;
		try {
			return this.returnSGWithCount(this.cadastrosApoioExamesFacade.buscarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(valPesquisa, "N"),contarExamesSuggestion(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public Long contarExamesSuggestion(String objPesquisa) {
		final String valPesquisa = objPesquisa != null ? StringUtils.trim((String) objPesquisa) : null;
		return this.cadastrosApoioExamesFacade.contarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(valPesquisa, "N");
	}

	public Long contarCampoLaudoSuggestion(String objPesquisa) {
		return this.cadastrosApoioExamesFacade.contarAelCampoLaudoPorVAelCampoLaudoExme(objPesquisa != null ? StringUtils.trim((String) objPesquisa) : null, this.exaSigla, this.manSeq);
	}

	public List<AelCampoLaudo> pesquisarCampoLaudo(String objPesquisa) {
		return this.returnSGWithCount(this.cadastrosApoioExamesFacade.buscarAelCampoLaudoPorVAelCampoLaudoExme(objPesquisa != null ? StringUtils.trim((String) objPesquisa) : null, this.exaSigla, this.manSeq, true),contarCampoLaudoSuggestion(objPesquisa));
	}

	public List<AelResultadoCodificado> pesquisarResultadoCodificado(final String objPesquisa) {
		if (this.getExameReflexo() != null && this.getExameReflexo().getAelCampoLaudo() != null) {
			return this.cadastrosApoioExamesFacade
					.buscarAelResultadoCodificado(objPesquisa != null ? StringUtils.trim((String) objPesquisa) : null, this.getExameReflexo().getAelCampoLaudo().getSeq());
		}
		return new ArrayList<AelResultadoCodificado>(0);
	}

	/**
	 * Grava Equipamento
	 * 
	 * @return
	 */
	public void gravar() {
		boolean novo = this.exameReflexo.getId() == null;
		try {
			this.exameReflexo.setAelExamesMaterialAnaliseReflexo(this.cadastrosApoioExamesFacade.obterAelExamesMaterialAnaliseById(this.exame.getId()));
			String nomeUsualMaterial = this.exameReflexo.getAelExamesMaterialAnaliseReflexo().getNomeUsualMaterial();
			this.exameReflexo.setSituacao(this.situacaoExameReflexo ? DominioSituacao.A : DominioSituacao.I);
			this.cadastrosApoioExamesFacade.persistirAelExameReflexo(this.exameReflexo);
			this.apresentarMsgNegocio(Severity.INFO, novo ? "MENSAGEM_SUCESSO_INSERIR_EXAME_REFLEXO" : "MENSAGEM_SUCESSO_ALTERAR_EXAME_REFLEXO", nomeUsualMaterial);

			this.iniciar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void editar(final AelExameReflexo exameReflexo) {
//		this.limpar();
		this.exameReflexo = exameReflexo;
		this.exame = this.cadastrosApoioExamesFacade.buscarVAelExameMatAnalisePorExameMaterialAnalise(this.exameReflexo.getAelExamesMaterialAnaliseReflexo());
		this.situacaoExameReflexo = DominioSituacao.A.equals(this.exameReflexo.getSituacao());
		this.emEdicao = true;
	}

	public void limpar() {
		this.exameReflexo = new AelExameReflexo();
		this.emEdicao = false;
		this.exame = null;
		this.situacaoExameReflexo = true;
		this.executarRefreshLista();
	}

	// limpa campo resultado codificado quando limpar a sugestion box de campo laudo
	public void posDeleteActionSuggestionBoxCampoLaudo() {
		this.exameReflexo.setAelResultadoCodificado(null);
	}

	public void executarRefreshLista() {
		this.listaReflexos = this.cadastrosApoioExamesFacade.buscarAelExamesReflexo(this.exaSigla, this.manSeq);
	}

	/**
	 * Limpa os atributos
	 */
	public void cancelarEdicao() {
		iniciar();
	}

	/**
	 * Cancela o cadastro
	 */
	public String cancelar() {

		this.exaSigla = null;
		this.manSeq = null;
		this.exameReflexo = null;
		this.listaReflexos = null;
		this.programacaoExecExames = null;
		this.exame = null;
		this.situacaoExameReflexo = null;
		this.emEdicao = false;
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	/**
	 * Exclui um equipamento
	 */
	public void excluir(AelExameReflexo itemExclusao) {
		try {
			if (itemExclusao != null) {
				// final String descricao =
				// aelExameReflexoExcluir.getAelExamesMaterialAnaliseReflexo().getAelExames().getDescricaoUsual();
				final String descricao = itemExclusao.getAelExamesMaterialAnaliseReflexo().getNomeUsualMaterial();

				this.cadastrosApoioExamesFacade.excluirAelExameReflexo(itemExclusao);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_EXAME_REFLEXO", descricao);
			}

			this.executarRefreshLista();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		} finally {
			itemExclusao = null;
		}

	}

	/*
	 * Getters e setters
	 */

	public String getExaSigla() {
		return exaSigla;
	}

	public void setExaSigla(String exaSigla) {
		this.exaSigla = exaSigla;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public DominioProgramacaoExecExames getProgramacaoExecExames() {
		return programacaoExecExames;
	}

	public void setProgramacaoExecExames(DominioProgramacaoExecExames programacaoExecExames) {
		this.programacaoExecExames = programacaoExecExames;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public void setExameReflexo(AelExameReflexo exameReflexo) {
		this.exameReflexo = exameReflexo;
	}

	public AelExameReflexo getExameReflexo() {
		return exameReflexo;
	}

	public void setListaReflexos(List<AelExameReflexo> listaReflexos) {
		this.listaReflexos = listaReflexos;
	}

	public List<AelExameReflexo> getListaReflexos() {
		return listaReflexos;
	}

	public void setExame(VAelExameMatAnalise exame) {
		this.exame = exame;
	}

	public VAelExameMatAnalise getExame() {
		return exame;
	}

	public void setSituacaoExameReflexo(Boolean situacaoExameReflexo) {
		this.situacaoExameReflexo = situacaoExameReflexo;
	}

	public Boolean getSituacaoExameReflexo() {
		return situacaoExameReflexo;
	}

}