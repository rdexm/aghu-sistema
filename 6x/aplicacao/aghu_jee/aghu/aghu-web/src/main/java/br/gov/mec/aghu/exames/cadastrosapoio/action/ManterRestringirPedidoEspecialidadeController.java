package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExamesEspecialidade;
import br.gov.mec.aghu.model.AelExamesEspecialidadeId;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterRestringirPedidoEspecialidadeController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -6836296840136319206L;

	private static final String PAGE_EXAMES_MANTER_UNIDADES_EXECUTORAS_EXAMES_CRUD = "exames-manterUnidadesExecutorasExamesCRUD";

	private AelUnfExecutaExames aelUnfExecutaExames;
	private AghEspecialidades especialidade = null; // Especialidade

	private List<AelExamesEspecialidade> listaAelExamesEspecialidade;

	private String emaExaSigla;
	private Integer emaManSeq;
	private Short unfSeq;

	private Short espSeq; // Seq utilizado na exclusão

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 


		if (StringUtils.isNotBlank(this.emaExaSigla) && this.emaManSeq != null && this.unfSeq != null) {
			aelUnfExecutaExames = this.examesFacade.obterAelUnidadeExecutoraExamesPorID(this.emaExaSigla, this.emaManSeq, this.unfSeq);
			atualizaLista();
		}
	
	}

	public void confirmar() {
		try {
			AelExamesEspecialidadeId exaEspId = new AelExamesEspecialidadeId();
			exaEspId.setEspSeq(especialidade.getSeq());
			exaEspId.setUfeEmaExaSigla(this.emaExaSigla);
			exaEspId.setUfeEmaManSeq(this.emaManSeq);
			exaEspId.setUfeUnfSeq(this.unfSeq);

			AelExamesEspecialidade exameEspecialidade = new AelExamesEspecialidade();
			exameEspecialidade.setAghEspecialidades(especialidade);
			exameEspecialidade.setId(exaEspId);

			cadastrosApoioExamesFacade.persistirExameEspecialidade(exameEspecialidade);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_PERMISSAO_ESPECIALIDADE");

			atualizaLista();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void atualizaLista() {
		especialidade = null;
		listaAelExamesEspecialidade = this.examesFacade.buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeq(this.emaExaSigla, this.emaManSeq, this.unfSeq);
	}

	public void excluir() {
		try {
			cadastrosApoioExamesFacade.removerExameEspecialidade(this.emaExaSigla, this.emaManSeq, this.unfSeq, this.espSeq);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_PERMISSAO_ESPECIALIDADE");
			atualizaLista();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.espSeq = null;
		}
	}

	public String voltar() {

		this.aelUnfExecutaExames = null;
		this.especialidade = null;
		this.listaAelExamesEspecialidade = null;
		this.emaExaSigla = null;
		this.emaManSeq = null;
		this.unfSeq = null;
		this.espSeq = null;

		return PAGE_EXAMES_MANTER_UNIDADES_EXECUTORAS_EXAMES_CRUD;
	}

	// Metódo para Suggestion Box de Especialidade
	public List<AghEspecialidades> obterEspecialidade(String objPesquisa) {
		// return this.prescricaoMedicaFacade.listarEspecialidadesAtivas(objPesquisa);
		return this.prescricaoMedicaFacade.listarEspecialidadesAtivas(objPesquisa);
	}

	public void cancelarExclusao() {
		this.espSeq = null;
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

	public AelUnfExecutaExames getAelUnfExecutaExames() {
		return aelUnfExecutaExames;
	}

	public void setAelUnfExecutaExames(AelUnfExecutaExames aelUnfExecutaExames) {
		this.aelUnfExecutaExames = aelUnfExecutaExames;
	}

	public List<AelExamesEspecialidade> getListaAelExamesEspecialidade() {
		return listaAelExamesEspecialidade;
	}

	public void setListaAelExamesEspecialidade(List<AelExamesEspecialidade> listaAelExamesEspecialidade) {
		this.listaAelExamesEspecialidade = listaAelExamesEspecialidade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
}