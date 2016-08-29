package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioProgramacaoExecExames;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExecExamesMatAnalise;
import br.gov.mec.aghu.model.AelExecExamesMatAnaliseId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterEquipamentosExecutamExameController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -4400363911197712421L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	// Parâmetros da conversação
	private String exaSigla;
	private Integer manSeq;

	// Variaveis que representam os campos do XHTML
	private AelExamesMaterialAnalise exameMaterialAnalise;
	private AelExecExamesMatAnalise execExameMaterialAnalise;
	private DominioProgramacaoExecExames programacaoExecExames;
	private Integer ordem;
	private DominioSituacao situacao;
	List<AelExecExamesMatAnalise> listaEquipamentos = new LinkedList<AelExecExamesMatAnalise>();

	private Short equipamentoSeq;

	/**
	 * Parâmetros id para exclusao
	 */
	private AelExecExamesMatAnalise itemExclusao;

	private DominioProgramacaoExecExames programacao;

	/*
	 * Controla a edição dos itens
	 */
	private boolean emEdicao;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 


		if (this.exaSigla != null && manSeq != null && !this.emEdicao) {

			// Reseta todos os campos
			this.limpar();

			// Obtém o Exame Material de Análise
			this.exameMaterialAnalise = this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.exaSigla, this.manSeq);

			// Popula lista de Equipamentos
			this.listaEquipamentos = this.cadastrosApoioExamesFacade.pesquisarEquipamentosExecutamExamesPorExameMaterialAnalise(this.exameMaterialAnalise.getId());

		}

	
	}

	private void setValoresDefault() {
		execExameMaterialAnalise.setSituacao(DominioSituacao.A);
		this.programacaoExecExames = DominioProgramacaoExecExames.A;
	}

	/**
	 * Suggestion Box de Convênios
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<AelEquipamentos> pesquisarEquipamento(String objPesquisa) {
		final String equipamento = objPesquisa != null ? StringUtils.trim(objPesquisa) : null;
		List<AelEquipamentos> listEquipamentos = null;
		try {
			listEquipamentos = this.cadastrosApoioExamesFacade.pesquisarEquipamentosSeqDescricao(equipamento);
		} catch (ApplicationBusinessException e) {
			// TODO Auto-generated catch block
			apresentarExcecaoNegocio(e);
		}

		return listEquipamentos;
	}

	/**
	 * Valida campos obrigatórios
	 * 
	 * @return
	 */
	private boolean validarCamposObrigatorios() {

		if (this.execExameMaterialAnalise.getAelEquipamentos() == null) {
			this.apresentarMsgNegocio("sbEquipamento", Severity.ERROR, CAMPO_OBRIGATORIO, "Equipamento");
			return false;
		}
		if (this.programacaoExecExames == null) {
			this.apresentarMsgNegocio("programacao", Severity.ERROR, CAMPO_OBRIGATORIO, "Programação");
			return false;
		}
		if (this.execExameMaterialAnalise.getOrdem() == null) {
			this.apresentarMsgNegocio("ordem", Severity.ERROR, CAMPO_OBRIGATORIO, "Ordem");
			return false;
		}
		if (this.execExameMaterialAnalise.getSituacao() == null) {
			this.apresentarMsgNegocio("situacaoId", Severity.ERROR, CAMPO_OBRIGATORIO, "Situação");
			return false;
		}

		return true;
	}

	/**
	 * Grava Equipamento
	 * 
	 * @return
	 */
	public void gravar() {

		// Verifica preenchimento dos campos obrigatórios
		if (!validarCamposObrigatorios()) {
			return;
		}

		// Popula ID do Equipamento
		try {

			AelExecExamesMatAnaliseId id = new AelExecExamesMatAnaliseId();
			id.setEmaExaSigla(this.exameMaterialAnalise.getId().getExaSigla());
			id.setEmaManSeq(this.exameMaterialAnalise.getId().getManSeq());
			id.setEquSeq(execExameMaterialAnalise.getAelEquipamentos().getSeq());
			id.setProgramacao(programacaoExecExames);

			// this.execExameMaterialAnalise.setExamesMaterialAnalise(this.exameMaterialAnalise);
			this.execExameMaterialAnalise.setId(id);

			// Persiste Equipamento
			this.cadastrosApoioExamesFacade.inserirAelExecExamesMatAnalise(execExameMaterialAnalise);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_EQUIPAMENTO_EXEC_EXAMES", this.execExameMaterialAnalise.getAelEquipamentos().getDescricao());

			this.limpar();
			this.iniciar();

		} catch (BaseException e) {

			apresentarExcecaoNegocio(e);

		}

	}

	/**
	 * Edita o registro
	 * 
	 * @param AelEquipamentos
	 */
	public void editar(final AelExecExamesMatAnalise execExameMaterialAnalise) {

		this.limpar();
		this.execExameMaterialAnalise = execExameMaterialAnalise;
		this.equipamentoSeq = execExameMaterialAnalise.getAelEquipamentos().getSeq();
		this.emEdicao = true;

		this.iniciar();
	}

	public void limpar() {

		this.equipamentoSeq = null;
		this.execExameMaterialAnalise = new AelExecExamesMatAnalise();
		this.ordem = null;
		this.emEdicao = false;
		this.setValoresDefault();
		// this.executarRefreshLista();

	}

	/**
	 * Atualiza
	 * 
	 * @param execExameMaterialAnalise
	 */
	public void alterar() {
		try {
			this.cadastrosApoioExamesFacade.atualizarAelExecExamesMatAnalise(this.execExameMaterialAnalise);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_EQUIPAMENTO_EXEC_EXAMES", this.execExameMaterialAnalise.getAelEquipamentos().getDescricao());
			this.limpar();
			this.iniciar();
		} catch (BaseException e) {
			this.listaEquipamentos = this.cadastrosApoioExamesFacade.pesquisarEquipamentosExecutamExamesPorExameMaterialAnalise(this.exameMaterialAnalise.getId());
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Limpa os atributos
	 */
	public void cancelarEdicao() {
		// this.emEdicao = false;
		this.limpar();

	}

	/**
	 * Cancela o cadastro
	 */
	public String cancelar() {
		
		this.exaSigla = null;
		this.manSeq = null;
		this.exameMaterialAnalise = null;
		this.execExameMaterialAnalise = null;
		this.programacaoExecExames = null;
		this.ordem = null;
		this.situacao = null;
		this.listaEquipamentos = new LinkedList<AelExecExamesMatAnalise>();
		this.equipamentoSeq = null;
		this.itemExclusao = null;
		this.emEdicao = false;
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	/**
	 * Exclui um equipamento
	 */
	public void excluir() {
		try {

			if (this.itemExclusao != null) {

				String descricao = this.itemExclusao.getAelEquipamentos().getDescricao();

				this.cadastrosApoioExamesFacade.removerExecExamesMatAnalise(this.itemExclusao);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_EQUIPAMENTO_EXEC_EXAMES", descricao);
			}

			// Popula lista de Equipamentos
			this.listaEquipamentos = this.cadastrosApoioExamesFacade.pesquisarEquipamentosExecutamExamesPorExameMaterialAnalise(this.exameMaterialAnalise.getId());

		} catch (BaseException e) {

			apresentarExcecaoNegocio(e);

		} finally {
			this.itemExclusao = null;
		}

		// this.inicio();
	}

	public void cancelarExclusao() {
		this.itemExclusao = null;
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

	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}

	public void setExameMaterialAnalise(AelExamesMaterialAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}

	public AelExecExamesMatAnalise getExecExameMaterialAnalise() {
		return execExameMaterialAnalise;
	}

	public void setExecExameMaterialAnalise(AelExecExamesMatAnalise execExameMaterialAnalise) {
		this.execExameMaterialAnalise = execExameMaterialAnalise;
	}

	public DominioProgramacaoExecExames getProgramacaoExecExames() {
		return programacaoExecExames;
	}

	public void setProgramacaoExecExames(DominioProgramacaoExecExames programacaoExecExames) {
		this.programacaoExecExames = programacaoExecExames;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public List<AelExecExamesMatAnalise> getListaEquipamentos() {
		return listaEquipamentos;
	}

	public void setListaEquipamentos(List<AelExecExamesMatAnalise> listaEquipamentos) {
		this.listaEquipamentos = listaEquipamentos;
	}

	public Short getEquipamentoSeq() {
		return equipamentoSeq;
	}

	public void setEquipamentoSeq(Short equipamentoSeq) {
		this.equipamentoSeq = equipamentoSeq;
	}

	public DominioProgramacaoExecExames getProgramacao() {
		return programacao;
	}

	public void setProgramacao(DominioProgramacaoExecExames programacao) {
		this.programacao = programacao;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public AelExecExamesMatAnalise getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(AelExecExamesMatAnalise itemExclusao) {
		this.itemExclusao = itemExclusao;
	}
}