package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemanaFeriado;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ManterHorarioColetaExceptionCode;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.ManterHorarioRotinaColetaVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelHorarioRotinaColetas;
import br.gov.mec.aghu.model.AelHorarioRotinaColetasId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
public class ManterHorarioRotinaColetaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3288102063913966552L;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private ManterHorarioRotinaColetaVO filtro = new ManterHorarioRotinaColetaVO();

	private List<AghUnidadesFuncionais> unidadesColetaPesquisa;
	private List<AelHorarioRotinaColetas> horariosRotinas;

	private AghUnidadesFuncionais unidadeColetora;

	/* Atributos para cadastro */
	private AghUnidadesFuncionais unidadeSolicCadastro;
	private DominioSituacao situacaoHorarioCadastro = DominioSituacao.A;
	private Date horarioCadastro;
	private DominioDiaSemanaFeriado diaSemanaCadastro = null;
	private boolean habilitaBotao = false;

	/* Edição de horário */
	private AelHorarioRotinaColetasId horarioId;

	/* Exclusão */
	private Short unfSeqExc;
	private Short unfSeqSolicitanteExc;
	private String diaExc;
	private Long horarioExc;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 

		limparCamposCadastro();
		this.filtro.setUnidadeColeta(solicitacaoExameFacade.buscarAghUnidadesFuncionaisPorParametro(AghuParametrosEnum.P_COD_UNIDADE_COLETA_DEFAULT.toString()));
	
	}

	public void pesquisar() {

		Short unidadeColeta = null;
		Short unidadeSolicitante = null;
		String dia = null;

		if (filtro.getUnidadeColeta() != null) {
			unidadesColetaPesquisa = aghuFacade.listarAghUnidadesFuncionaisAtivasColetaveis(filtro.getUnidadeColeta().getSeq());
			unidadeColeta = filtro.getUnidadeColeta().getSeq();
			unidadeColetora = filtro.getUnidadeColeta();

		} else {
			unidadesColetaPesquisa = aghuFacade.listarAghUnidadesFuncionaisAtivasColetaveis(null);
			if (unidadesColetaPesquisa != null && unidadesColetaPesquisa.size() > 0) {
				unidadeColeta = unidadesColetaPesquisa.get(0).getSeq();
				unidadeColetora = unidadesColetaPesquisa.get(0);
			}
		}

		if (filtro.getUnidadeSolic() != null && filtro.getUnidadeSolic().getSeq() != null) {
			unidadeSolicitante = filtro.getUnidadeSolic().getSeq();
		}

		if (filtro.getDiaSemana() != null) {
			dia = filtro.getDiaSemana().toString();
		}

		limparCamposCadastro();

		horariosRotinas = cadastrosApoioExamesFacade.obterAelHorarioRotinaColetasPorParametros(unidadeColeta, unidadeSolicitante, filtro.getHorario(), dia, filtro.getSituacaoHorario());
		// atualizaLista();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void confirmar() {

		try {

			testaHorarioAMPM();

			AelHorarioRotinaColetas horarioRotina = new AelHorarioRotinaColetas();
			AelHorarioRotinaColetasId horarioId = getHorarioId();
			boolean isInclusao = (horarioId == null || horarioId.getUnfSeq() == null);

			if (isInclusao) {
				horarioId = new AelHorarioRotinaColetasId();
				horarioId.setUnfSeq(this.unidadeColetora.getSeq());

			} else {
				AelHorarioRotinaColetas horarioRotinaAux = cadastrosApoioExamesFacade.obterAelHorarioRotinaColetaPorId(horarioId);
				horarioId = horarioRotinaAux.getId();

				horarioRotina.setIdAux(new AelHorarioRotinaColetasId(horarioRotinaAux.getId().getUnfSeq(), horarioRotinaAux.getId().getUnfSeqSolicitante(), horarioRotinaAux.getId().getDia(),
						horarioRotinaAux.getId().getHorario()));
				horarioRotina.setCriadoEm(horarioRotinaAux.getCriadoEm());
				horarioRotina.setServidor(horarioRotinaAux.getServidor());
			}

			horarioRotina.setId(horarioId);

			horarioRotina.getId().setHorario(this.horarioCadastro);
			horarioRotina.getId().setDia(this.diaSemanaCadastro);
			horarioRotina.getId().setUnfSeqSolicitante(this.unidadeSolicCadastro);

			horarioRotina.setIndSituacao(this.situacaoHorarioCadastro);

			// Determina o tipo de mensagem de confirmação
			if (isInclusao) {
				this.cadastrosApoioExamesFacade.persistirHorarioRotina(horarioRotina);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_HORARIO_ROTINA");

			} else {
				this.cadastrosApoioExamesFacade.atualizarHorarioRotina(horarioRotina);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_HORARIO_ROTINA");
			}

			limparCamposCadastro();

			horariosRotinas = cadastrosApoioExamesFacade.obterAelHorarioRotinaColetasPorParametros(this.getUnidadeColetora().getSeq(), (this.filtro.getUnidadeSolic() != null) ? this.filtro
					.getUnidadeSolic().getSeq() : null, (this.filtro.getHorario() != null) ? this.filtro.getHorario() : null, (this.filtro.getDiaSemana() != null) ? this.filtro.getDiaSemana()
					.toString() : null, (this.filtro.getSituacaoHorario() != null) ? this.filtro.getSituacaoHorario() : null);
			// atualizaLista();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void limparCamposCadastro() {
		setHorarioId(null);
		setUnidadeSolicCadastro(null);
		setSituacaoHorarioCadastro(DominioSituacao.A);
		setHorarioCadastro(null);
		setHabilitaBotao(false);
	}

	public void limparCamposPesquisa() {
		this.filtro = new ManterHorarioRotinaColetaVO();
		setUnidadesColetaPesquisa(null);
		setHorariosRotinas(null);
		setUnidadeColetora(null);

		limparCamposCadastro();
	}

	public void editarHorario(AelHorarioRotinaColetas item) {
		setUnidadeSolicCadastro(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(item.getId().getUnfSeqSolicitante().getSeq()));

		AelHorarioRotinaColetas horario = cadastrosApoioExamesFacade.obterAelHorarioRotinaColetaPorId(item.getId());

		setHorarioId(horario.getId());

		setSituacaoHorarioCadastro(horario.getIndSituacao());
		setHorarioCadastro(horarioId.getHorario());
		setDiaSemanaCadastro(horarioId.getDia());
		habilitaBotaoGravar();
	}

	public void habilitaBotaoGravar() {
		setHabilitaBotao(true);
	}

	public void desabilitaBotaoGravar() {
		setHabilitaBotao(false);
	}

	public void selecionaUnidadeColetora() {
		//setUnidadeColetora(unidadeColetora);

		Short unidadeSolicitante = null;
		String dia = null;

		if (filtro.getUnidadeSolic() != null && filtro.getUnidadeSolic().getSeq() != null) {
			unidadeSolicitante = filtro.getUnidadeSolic().getSeq();
		}

		if (filtro.getDiaSemana() != null) {
			dia = filtro.getDiaSemana().toString();
		}

		limparCamposCadastro();

		horariosRotinas = cadastrosApoioExamesFacade.obterAelHorarioRotinaColetasPorParametros(unidadeColetora.getSeq(), unidadeSolicitante, filtro.getHorario(), dia, filtro.getSituacaoHorario());
	}

	public boolean isUnidadeColetoraSelecionada(Short seq) {
		return this.unidadeColetora != null && seq != null && this.unidadeColetora.getSeq().equals(seq);
	}

	public boolean isHorarioSelecionado(AelHorarioRotinaColetasId rotinaId) {
		return this.horarioId != null && rotinaId != null && this.horarioId.getUnfSeq().equals(rotinaId.getUnfSeq())
				&& this.horarioId.getUnfSeqSolicitante().getSeq().equals(rotinaId.getUnfSeqSolicitante().getSeq()) && this.horarioId.getDia().equals(rotinaId.getDia())
				&& this.horarioId.getHorario().equals(rotinaId.getHorario());
	}

	/**
	 * Excluir
	 */
	public void excluir(AelHorarioRotinaColetasId horarioRotinaColetaId) {

		try {

//			AelHorarioRotinaColetasId id = new AelHorarioRotinaColetasId();
//			id.setDia(DominioDiaSemanaFeriado.getInstance(getDiaExc()));
//			id.setHorario(new Date(getHorarioExc()));
//			id.setUnfSeq(getUnfSeqExc());
//			id.setUnfSeqSolicitante(this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(getUnfSeqSolicitanteExc()));

			this.cadastrosApoioExamesFacade.excluirHorarioRotina(horarioRotinaColetaId);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_HORARIO_ROTINA");

			limparCamposCadastro();

			horariosRotinas = cadastrosApoioExamesFacade.obterAelHorarioRotinaColetasPorParametros(this.getUnidadeColetora().getSeq(), (this.filtro.getUnidadeSolic() != null) ? this.filtro
					.getUnidadeSolic().getSeq() : null, (this.filtro.getHorario() != null) ? this.filtro.getHorario() : null, (this.filtro.getDiaSemana() != null) ? this.filtro.getDiaSemana()
					.toString() : null, (this.filtro.getSituacaoHorario() != null) ? this.filtro.getSituacaoHorario() : null);

		} catch (BaseException e) {

			apresentarExcecaoNegocio(e);

		} finally {
			this.cancelarExclusao();
		}

	}

	public void cancelarExclusao() {
		setUnfSeqExc(null);
		setUnfSeqSolicitanteExc(null);
		setDiaExc(null);
		setHorarioExc(null);
	}

	private void testaHorarioAMPM() throws ApplicationBusinessException {
		Calendar calendar = Calendar.getInstance(new Locale("pt", "BR"));
		calendar.setTime(this.horarioCadastro);

		if (this.diaSemanaCadastro.equals(DominioDiaSemanaFeriado.FERM) && calendar.get(Calendar.AM_PM) != Calendar.AM) {
			throw new ApplicationBusinessException(ManterHorarioColetaExceptionCode.MSG_ROTINA_COLETA_MANHA);
		} else if (this.diaSemanaCadastro.equals(DominioDiaSemanaFeriado.FERT) && calendar.get(Calendar.AM_PM) != Calendar.PM) {
			throw new ApplicationBusinessException(ManterHorarioColetaExceptionCode.MSG_ROTINA_COLETA_TARDE);
		}
	}

	// Metodo para Suggestion Box de Unidade de Coleta (Unidade Funcional)
	public List<AghUnidadesFuncionais> obterUnidadeColeta(String parametro) {
		return this.aghuFacade.listarAghUnidadesFuncionaisAtivasColetaveis(parametro);
	}

	// Metodo para Suggestion Box Unidade Solicitante
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalSolicitanteAvisada(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoEDescricao(param);
	}

	/*
	 * Getters and Setters abaixo...
	 */

	public ManterHorarioRotinaColetaVO getFiltro() {
		return filtro;
	}

	public void setFiltro(ManterHorarioRotinaColetaVO filtro) {
		this.filtro = filtro;
	}

	public List<AghUnidadesFuncionais> getUnidadesColetaPesquisa() {
		return unidadesColetaPesquisa;
	}

	public void setUnidadesColetaPesquisa(List<AghUnidadesFuncionais> unidadesColetaPesquisa) {
		this.unidadesColetaPesquisa = unidadesColetaPesquisa;
	}

	public AghUnidadesFuncionais getUnidadeColetora() {
		return unidadeColetora;
	}

	public void setUnidadeColetora(AghUnidadesFuncionais unidadeColetora) {
		this.unidadeColetora = unidadeColetora;
	}

	public List<AelHorarioRotinaColetas> getHorariosRotinas() {
		return horariosRotinas;
	}

	public void setHorariosRotinas(List<AelHorarioRotinaColetas> horariosRotinas) {
		this.horariosRotinas = horariosRotinas;
	}

	public AghUnidadesFuncionais getUnidadeSolicCadastro() {
		return unidadeSolicCadastro;
	}

	public void setUnidadeSolicCadastro(AghUnidadesFuncionais unidadeSolicCadastro) {
		this.unidadeSolicCadastro = unidadeSolicCadastro;
	}

	public DominioSituacao getSituacaoHorarioCadastro() {
		return situacaoHorarioCadastro;
	}

	public void setSituacaoHorarioCadastro(DominioSituacao situacaoHorarioCadastro) {
		this.situacaoHorarioCadastro = situacaoHorarioCadastro;
	}

	public Date getHorarioCadastro() {
		return horarioCadastro;
	}

	public void setHorarioCadastro(Date horarioCadastro) {
		this.horarioCadastro = horarioCadastro;
	}

	public DominioDiaSemanaFeriado getDiaSemanaCadastro() {
		return diaSemanaCadastro;
	}

	public void setDiaSemanaCadastro(DominioDiaSemanaFeriado diaSemanaCadastro) {
		this.diaSemanaCadastro = diaSemanaCadastro;
	}

	public AelHorarioRotinaColetasId getHorarioId() {
		return horarioId;
	}

	public void setHorarioId(AelHorarioRotinaColetasId horarioId) {
		this.horarioId = horarioId;
	}

	public boolean isHabilitaBotao() {
		return habilitaBotao;
	}

	public void setHabilitaBotao(boolean habilitaBotao) {
		this.habilitaBotao = habilitaBotao;
	}

	public Short getUnfSeqExc() {
		return unfSeqExc;
	}

	public void setUnfSeqExc(Short unfSeqExc) {
		this.unfSeqExc = unfSeqExc;
	}

	public Short getUnfSeqSolicitanteExc() {
		return unfSeqSolicitanteExc;
	}

	public void setUnfSeqSolicitanteExc(Short unfSeqSolicitanteExc) {
		this.unfSeqSolicitanteExc = unfSeqSolicitanteExc;
	}

	public String getDiaExc() {
		return diaExc;
	}

	public void setDiaExc(String diaExc) {
		this.diaExc = diaExc;
	}

	public Long getHorarioExc() {
		return horarioExc;
	}

	public void setHorarioExc(Long horarioExc) {
		this.horarioExc = horarioExc;
	}
}