package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelPermissaoUnidSolicId;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterPermissoesUnidadesSolicitantesController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -4563726660051302983L;

	private static final String PAGE_EXAMES_MANTER_UNIDADES_EXECUTORAS_EXAMES_CRUD = "exames-manterUnidadesExecutorasExamesCRUD";

	private AelPermissaoUnidSolic aelPermissaoUnidSolic;
	private AelUnfExecutaExames aelUnfExecutaExames;

	private AghUnidadesFuncionais unidadeFuncionalSolicitante;

	private List<AelPermissaoUnidSolic> listaAelPermissaoUnidSolic;

	private String emaExaSigla;
	private Integer emaManSeq;
	private Short unfSeq;
	private Short unfSeqSolicitante;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Boolean editando = false;

	private AelPermissaoUnidSolic permissaoRemover;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 


		if (!this.editando && StringUtils.isNotBlank(this.emaExaSigla) && this.emaManSeq != null && this.unfSeq != null) {

			this.aelPermissaoUnidSolic = new AelPermissaoUnidSolic();

			aelUnfExecutaExames = this.examesFacade.obterAelUnidadeExecutoraExamesPorID(this.emaExaSigla, this.emaManSeq, this.unfSeq);
			listaAelPermissaoUnidSolic = this.examesFacade.buscaListaAelPermissoesUnidSolicPorEmaExaSiglaEmaManSeqUnfSeq(this.emaExaSigla, this.emaManSeq, this.unfSeq);

			setarCamposDefault();
		}

	
	}

	private void setarCamposDefault() {

		aelPermissaoUnidSolic.setIndPermiteProgramarExames(DominioSimNaoRotina.S);
		aelPermissaoUnidSolic.setIndPermiteColetarUrgente(Boolean.TRUE);
		this.unidadeFuncionalSolicitante = null;

	}

	public void confirmar() {

		boolean insert = false;

		try {

			if (aelPermissaoUnidSolic.getId() == null) {

				insert = true;
				aelPermissaoUnidSolic.setUnfExecutaExames(aelUnfExecutaExames);
				aelPermissaoUnidSolic.setUnfSolicitante(unidadeFuncionalSolicitante);

			}

			cadastrosApoioExamesFacade.persistirPermissaoUnidadeSolicitante(aelPermissaoUnidSolic);

			if (insert) {

				listaAelPermissaoUnidSolic.add(aelPermissaoUnidSolic);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_PERMISSAO_UNIDADE_SOLICITANTE");

			} else {

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_PERMISSAO_UNIDADE_SOLICITANTE");
				editando = false;

			}

			aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
			setarCamposDefault();

		} catch (BaseException e) {

			if (insert) {

				aelPermissaoUnidSolic.setUnfSolicitante(null);

			}
			aelPermissaoUnidSolic.setId(null);
			apresentarExcecaoNegocio(e);

		}

	}

	public void editar(AelPermissaoUnidSolic aelPermissaoUnidSolic) {

		editando = true;
		this.aelPermissaoUnidSolic = aelPermissaoUnidSolic;
		this.unidadeFuncionalSolicitante = aelPermissaoUnidSolic.getUnfSolicitante();

	}

	public void cancelarEdicao() {
		editando = false;
		aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
		aelPermissaoUnidSolic.setId(new AelPermissaoUnidSolicId());
		setarCamposDefault();
		this.iniciar();
	}

	public Boolean registroEditando(AelPermissaoUnidSolic aelPermissaoUnidSolicGrid) {
		
		if(aelPermissaoUnidSolicGrid != null){
			AelPermissaoUnidSolicId idGrid = aelPermissaoUnidSolicGrid.getId();
	
			if (aelPermissaoUnidSolic.getId() != null) {
	
				if (aelPermissaoUnidSolic.getUnfSolicitante() != null) {
	
					AelPermissaoUnidSolicId idEditando = aelPermissaoUnidSolic.getId();
	
					if (idGrid.getUnfSeq().equals(idEditando.getUnfSeq()) && editando) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void excluir() {

		try {

			cadastrosApoioExamesFacade.removerAelPermissaoUnidSolic(this.permissaoRemover.getId().getUfeEmaExaSigla(), this.permissaoRemover.getId().getUfeEmaManSeq(), this.permissaoRemover.getId()
					.getUfeUnfSeq(), this.permissaoRemover.getId().getUnfSeq());
			listaAelPermissaoUnidSolic = this.examesFacade.buscaListaAelPermissoesUnidSolicPorEmaExaSiglaEmaManSeqUnfSeq(this.permissaoRemover.getId().getUfeEmaExaSigla(), this.permissaoRemover
					.getId().getUfeEmaManSeq(), this.permissaoRemover.getId().getUfeUnfSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_PERMISSAO_UNIDADE_SOLICITANTE");

		} catch (BaseException e) {

			apresentarExcecaoNegocio(e);

		} finally {

			this.permissaoRemover = null;

		}

	}

	public void cancelarExclusao() {
		this.unfSeqSolicitante = null;
	}

	public String voltar() {

		this.aelUnfExecutaExames = null;
		this.unidadeFuncionalSolicitante = null;
		this.emaExaSigla = null;
		this.emaManSeq = null;
		this.unfSeq = null;
		this.editando = false;
		this.aelPermissaoUnidSolic = new AelPermissaoUnidSolic();
		this.aelPermissaoUnidSolic.setId(new AelPermissaoUnidSolicId());
		this.setarCamposDefault();
		this.unfSeqSolicitante = null;
		this.listaAelPermissaoUnidSolic = null;
		this.permissaoRemover = null;

		return PAGE_EXAMES_MANTER_UNIDADES_EXECUTORAS_EXAMES_CRUD;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalSolicitanteAvisada(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoEDescricao(param);
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

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	public AelUnfExecutaExames getAelUnfExecutaExames() {
		return aelUnfExecutaExames;
	}

	public void setAelUnfExecutaExames(AelUnfExecutaExames aelUnfExecutaExames) {
		this.aelUnfExecutaExames = aelUnfExecutaExames;
	}

	public AelPermissaoUnidSolic getAelPermissaoUnidSolic() {
		return aelPermissaoUnidSolic;
	}

	public void setAelPermissaoUnidSolic(AelPermissaoUnidSolic aelPermissaoUnidSolic) {
		this.aelPermissaoUnidSolic = aelPermissaoUnidSolic;
	}

	public List<AelPermissaoUnidSolic> getListaAelPermissaoUnidSolic() {
		return listaAelPermissaoUnidSolic;
	}

	public void setListaAelPermissaoUnidSolic(List<AelPermissaoUnidSolic> listaAelPermissaoUnidSolic) {
		this.listaAelPermissaoUnidSolic = listaAelPermissaoUnidSolic;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalSolicitante() {
		return unidadeFuncionalSolicitante;
	}

	public void setUnidadeFuncionalSolicitante(AghUnidadesFuncionais unidadeFuncionalSolicitante) {
		this.unidadeFuncionalSolicitante = unidadeFuncionalSolicitante;
	}

	public Short getUnfSeqSolicitante() {
		return unfSeqSolicitante;
	}

	public void setUnfSeqSolicitante(Short unfSeqSolicitante) {
		this.unfSeqSolicitante = unfSeqSolicitante;
	}

	public AelPermissaoUnidSolic getPermissaoRemover() {
		return permissaoRemover;
	}

	public void setPermissaoRemover(AelPermissaoUnidSolic permissaoRemover) {
		this.permissaoRemover = permissaoRemover;
	}

}