package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgJnDAO;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MbcHorarioTurnoCirgRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcHorarioTurnoCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcHorarioTurnoCirgJnDAO mbcHorarioTurnoCirgJnDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;


	private static final long	serialVersionUID	= 1779953909111759691L;
	private static final int	HORA_MIN_FINAL_DIA	= 1439;	// 23 * 60 + 59;

	protected enum MbcHorarioTurnoCirgRNExceptionCode implements BusinessExceptionCode {
		MBC_00202, MBC_01141;
	}

	public void persistirMbcHorarioTurnoCirg(final MbcHorarioTurnoCirg mbcHorarioTurnoCirg)
			throws ApplicationBusinessException {
		if (mbcHorarioTurnoCirg.getVersion() == null) {

			this.executarAntesInserir(mbcHorarioTurnoCirg);
			this.getMbcHorarioTurnoCirgDAO().persistir(mbcHorarioTurnoCirg);
		} else {
			final MbcHorarioTurnoCirg original = this.getMbcHorarioTurnoCirgDAO().obterOriginal(mbcHorarioTurnoCirg);
			this.executarAntesAtualizar(mbcHorarioTurnoCirg);
			this.getMbcHorarioTurnoCirgDAO().atualizar(mbcHorarioTurnoCirg);
			this.executarDepoisAtualizar(mbcHorarioTurnoCirg, original);
		}
	}

	/**
	 * @param obterLoginUsuarioLogado 
	 * @ORADB MBCT_HTC_ARU
	 */
	private void executarDepoisAtualizar(final MbcHorarioTurnoCirg mbcHorarioTurnoCirg, final MbcHorarioTurnoCirg original)
			throws ApplicationBusinessException {
		if (CoreUtil.modificados(mbcHorarioTurnoCirg.getId().getUnfSeq(), original.getId().getUnfSeq())
				|| CoreUtil.modificados(mbcHorarioTurnoCirg.getId().getTurno(), original.getId().getTurno())
				|| CoreUtil.modificados(mbcHorarioTurnoCirg.getHorarioFinal(), original.getHorarioFinal())
				|| CoreUtil.modificados(mbcHorarioTurnoCirg.getHorarioInicial(), original.getHorarioInicial())) {
			this.verificarColisaoHorario(mbcHorarioTurnoCirg);
			if (CoreUtil.modificados(mbcHorarioTurnoCirg.getCriadoEm(), original.getCriadoEm())
					|| CoreUtil.modificados(mbcHorarioTurnoCirg.getRapServidores(), original.getRapServidores())) {
				inserirMbcHorarioTurnoCirgJn(original,DominioOperacoesJournal.UPD);
			}
		}
	}

	private void inserirMbcHorarioTurnoCirgJn(MbcHorarioTurnoCirg original, DominioOperacoesJournal op) {
		MbcHorarioTurnoCirgJn jn = BaseJournalFactory.getBaseJournal(op,
				MbcHorarioTurnoCirgJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		jn.setAghUnidadesFuncionais(original.getAghUnidadesFuncionais());
		jn.setCriadoEm(original.getCriadoEm());
		jn.setHorarioFinal(original.getHorarioFinal());
		jn.setHorarioInicial(original.getHorarioInicial());
		jn.setRapServidores(original.getRapServidores());
		jn.setTurno(original.getId().getTurno());
		jn.setUnfSeq(original.getId().getUnfSeq());
		
		getMbcHorarioTurnoCirgJnDAO().persistir(jn);
	}

	/**
	 * @ORADB MBCT_HTC_BRU
	 */
	private void executarAntesAtualizar(final MbcHorarioTurnoCirg mbcHorarioTurnoCirg)
			throws ApplicationBusinessException {
		this.atualizarDadosMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
//		Conforme conversado com o Vacaro, CRIADO_EM não deve ser atualizado na BRU, somente na BRI. AGH estava incorreto.
//		Visto isso, será criada uma journal
		this.verificarHorarioEquipe(mbcHorarioTurnoCirg);
		mbcHorarioTurnoCirg.setRapServidores(servidorLogadoFacade.obterServidorLogado());
	}

	private void atualizarDadosMbcHorarioTurnoCirg(MbcHorarioTurnoCirg mbcHorarioTurnoCirg)
			throws ApplicationBusinessException {
		mbcHorarioTurnoCirg.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		mbcHorarioTurnoCirg.setCriadoEm(new Date());
	}

	/**
	 * @ORADB MBCT_HTC_BRI
	 */
	private void executarAntesInserir(MbcHorarioTurnoCirg mbcHorarioTurnoCirg) throws ApplicationBusinessException {
		this.verificarColisaoHorario(mbcHorarioTurnoCirg);
		this.atualizarDadosMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
	}

	/**
	 * @ORADB mbck_htc_rn.rn_htcp_ver_hr_equip
	 */
	protected void verificarHorarioEquipe(MbcHorarioTurnoCirg mbcHorarioTurnoCirg) throws ApplicationBusinessException {
		Calendar calHrIni = DateUtil.getCalendarBy(mbcHorarioTurnoCirg.getHorarioInicial());
		Calendar calHrFim = DateUtil.getCalendarBy(mbcHorarioTurnoCirg.getHorarioFinal());
		final int novoHrIni = calHrIni.get(Calendar.HOUR_OF_DAY) * 60 + calHrIni.get(Calendar.MINUTE);
		final int novoHrFim = calHrFim.get(Calendar.HOUR_OF_DAY) * 60 + calHrFim.get(Calendar.MINUTE);
		// FIXME: alterado pela criação da tabela MBC_TURNOS 
		final List<MbcCaractSalaEsp> salaEsps = this.getMbcCaractSalaEspDAO().pesquisarCaractSalaEspPorHorarioTurnoCirg(
				mbcHorarioTurnoCirg.getAghUnidadesFuncionais().getSeq(), mbcHorarioTurnoCirg.getMbcTurnos());//);
		for (final MbcCaractSalaEsp mbcCaractSalaEsp : salaEsps) {
			calHrIni = DateUtil.getCalendarBy(mbcCaractSalaEsp.getHoraInicioEquipe());
			calHrFim = DateUtil.getCalendarBy(mbcCaractSalaEsp.getHoraFimEquipe());
			final int hrIniEquipe = calHrIni.get(Calendar.HOUR_OF_DAY) * 60 + calHrIni.get(Calendar.MINUTE);
			final int hrFimEquipe = calHrFim.get(Calendar.HOUR_OF_DAY) * 60 + calHrFim.get(Calendar.MINUTE);
			final int hora_fim_cse;
			final int hora_fim_param;
			if (hrFimEquipe == 0 && novoHrFim != 0) {
				throw new ApplicationBusinessException(MbcHorarioTurnoCirgRNExceptionCode.MBC_01141);
			}
			if (hrFimEquipe == 0) {
				hora_fim_cse = HORA_MIN_FINAL_DIA;
			} else {
				hora_fim_cse = hrFimEquipe;
			}
			if (novoHrFim == 0) {
				hora_fim_param = HORA_MIN_FINAL_DIA;
			} else {
				hora_fim_param = novoHrFim;
			}

			if (hrIniEquipe < novoHrIni || hrIniEquipe > hora_fim_param || hora_fim_cse < novoHrIni || hora_fim_cse > hora_fim_param) {
				throw new ApplicationBusinessException(MbcHorarioTurnoCirgRNExceptionCode.MBC_01141);
			}
		}
	}

	/**
	 * @ORADB mbck_htc_rn.rn_htcp_ver_colis_hr
	 */
	protected void verificarColisaoHorario(final MbcHorarioTurnoCirg novo) throws ApplicationBusinessException {
		Calendar calHrIni = DateUtil.getCalendarBy(novo.getHorarioInicial());
		Calendar calHrFim = DateUtil.getCalendarBy(novo.getHorarioFinal());
		final int novoHrIni = calHrIni.get(Calendar.HOUR_OF_DAY) * 60 + calHrIni.get(Calendar.MINUTE);
		final int novoHrFim = calHrFim.get(Calendar.HOUR_OF_DAY) * 60 + calHrFim.get(Calendar.MINUTE);
		final List<MbcHorarioTurnoCirg> horarioTurnoCirgs = this.getMbcHorarioTurnoCirgDAO().buscarHorariosCirgOutrosTurnos(
				novo.getAghUnidadesFuncionais().getSeq(), novo.getId().getTurno());
		for (final MbcHorarioTurnoCirg horarioTurnoCirg : horarioTurnoCirgs) {
			calHrIni = DateUtil.getCalendarBy(horarioTurnoCirg.getHorarioInicial());
			calHrFim = DateUtil.getCalendarBy(horarioTurnoCirg.getHorarioFinal());
			final int hrIni = calHrIni.get(Calendar.HOUR_OF_DAY) * 60 + calHrIni.get(Calendar.MINUTE);
			final int hrFim = calHrFim.get(Calendar.HOUR_OF_DAY) * 60 + calHrFim.get(Calendar.MINUTE);
			if (hrIni < hrFim) {
				if (!((novoHrIni < hrIni && novoHrFim <= hrIni) || (novoHrIni >= hrFim && novoHrFim > hrFim) || (novoHrIni >= hrFim && novoHrFim <= hrIni))) {
					throw new ApplicationBusinessException(MbcHorarioTurnoCirgRNExceptionCode.MBC_00202);
				}
			} else {
				if (!(novoHrIni < hrIni && novoHrIni >= hrFim && novoHrFim <= hrIni && novoHrFim > hrFim)) {
					throw new ApplicationBusinessException(MbcHorarioTurnoCirgRNExceptionCode.MBC_00202);
				}
			}
		}

	}

	public void excluirMbcHorarioTurnoCirg(final MbcHorarioTurnoCirg mbcHorarioTurnoCirg) {
		this.getMbcHorarioTurnoCirgDAO().remover(mbcHorarioTurnoCirg);
		inserirMbcHorarioTurnoCirgJn(mbcHorarioTurnoCirg,DominioOperacoesJournal.DEL);
	}

	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO() {
		return mbcHorarioTurnoCirgDAO;
	}
	
	protected MbcHorarioTurnoCirgJnDAO getMbcHorarioTurnoCirgJnDAO() {
		return mbcHorarioTurnoCirgJnDAO;
	}

	protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
		return mbcCaractSalaEspDAO;
	}
}
