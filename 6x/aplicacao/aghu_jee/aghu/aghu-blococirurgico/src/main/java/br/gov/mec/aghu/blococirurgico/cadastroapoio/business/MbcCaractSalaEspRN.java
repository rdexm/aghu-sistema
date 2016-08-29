package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcBloqSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoSalaEspEquipeDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcMvtoSalaEspEquipe;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MbcCaractSalaEspRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcCaractSalaEspRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcBloqSalaCirurgicaDAO mbcBloqSalaCirurgicaDAO;

	@Inject
	private MbcMvtoSalaEspEquipeDAO mbcMvtoSalaEspEquipeDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;


	@EJB
	private MbcMvtoSalaEspEquipeRN mbcMvtoSalaEspEquipeRN;

	
	/**
	 * @author fpalma
	 */
	private static final long serialVersionUID = -934700367550916820L;

	public enum MbcCaractSalaEspRNExceptionCode implements BusinessExceptionCode {
		MBC_00232, MBC_01324, MBC_00234, MBC_00235, MBC_01136, MBC_01322, MBC_01137, MBC_01138, MBC_01210, MBC_01296, MBC_01276, MBC_01135;
	}
	
	private static final int HORA_MIN_FINAL_DIA	= 1439;	// 23 * 60 + 59;
	
	
	public void inserir(MbcCaractSalaEsp mbcCaractSalaEsp) throws BaseException {
		preInserir(mbcCaractSalaEsp);
		this.getMbcCaractSalaEspDAO().persistir(mbcCaractSalaEsp);
		posInserir(mbcCaractSalaEsp);
	}
	
	/**
	 * @ORADB MBCT_CSE_ARI
	 * 
	 * @throws ApplicationBusinessException 
	 */
	private void posInserir(MbcCaractSalaEsp mbcCaractSalaEsp) throws BaseException {
		atualizarMovimentoCaractSalaEsp(mbcCaractSalaEsp, false);
	}

	/**
	 * @ORADB MBCT_CSE_BRI
	 * 
	 * 
	 * @throws ApplicationBusinessException 
	 */
	private void preInserir(MbcCaractSalaEsp mbcCaractSalaEsp) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			mbcCaractSalaEsp.setCriadoEm(new Date());
			mbcCaractSalaEsp.setRapServidores(servidorLogado);
			verificarEspecialidade(mbcCaractSalaEsp);
			verificarCaracteristica(mbcCaractSalaEsp);
			verificarColisaoHorario(mbcCaractSalaEsp);
			verificarHorarioTurno(mbcCaractSalaEsp);
			verificarPercentualReserva(mbcCaractSalaEsp);
	}
	
	public void atualizar(MbcCaractSalaEsp mbcCaractSalaEsp) throws BaseException {
		preAtualizar(mbcCaractSalaEsp);
		this.getMbcCaractSalaEspDAO().atualizar(mbcCaractSalaEsp);
		posAtualizar(mbcCaractSalaEsp);
	}
	
	/**
	 * @ORADB MBCT_CSE_BRU
	 * 
	 * @throws ApplicationBusinessException 
	 */
	private void preAtualizar(MbcCaractSalaEsp mbcCaractSalaEsp) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			verificarCaracteristica(mbcCaractSalaEsp);
			verificarPercentualReserva(mbcCaractSalaEsp);
			MbcCaractSalaEsp oldMbcCaractSalaEsp = getMbcCaractSalaEspDAO().obterOriginal(mbcCaractSalaEsp);
			//se a situação foi modificada chama mbck_cse_rn.rn_csep_ver_bloqueio
			if(DominioSituacao.I.equals(mbcCaractSalaEsp.getIndSituacao())
					&& !mbcCaractSalaEsp.getIndSituacao().equals(oldMbcCaractSalaEsp.getIndSituacao())) {
				verificarPossibilidadeInativar(oldMbcCaractSalaEsp);
			}
			mbcCaractSalaEsp.setRapServidores(servidorLogado);
	}
	
	/**
	 * @ORADB MBCT_CSE_ARU
	 * 
	 * @throws BaseException
	 */
	private void posAtualizar(MbcCaractSalaEsp mbcCaractSalaEsp) throws BaseException {
		MbcCaractSalaEsp mbcCaractSalaEspOriginal = getMbcCaractSalaEspDAO().obterOriginal(mbcCaractSalaEsp);
		verificarColisaoHorarioETurno(mbcCaractSalaEsp);
		
		if (CoreUtil.modificados(mbcCaractSalaEsp.getId(), mbcCaractSalaEspOriginal.getId())
				|| CoreUtil.modificados(mbcCaractSalaEsp.getMbcProfAtuaUnidCirgs(), mbcCaractSalaEspOriginal.getMbcProfAtuaUnidCirgs())
				|| CoreUtil.modificados(mbcCaractSalaEsp.getPercentualReserva(), mbcCaractSalaEspOriginal.getPercentualReserva())
				|| CoreUtil.modificados(mbcCaractSalaEsp.getHoraInicioEquipe(), mbcCaractSalaEspOriginal.getHoraInicioEquipe())
				|| CoreUtil.modificados(mbcCaractSalaEsp.getHoraFimEquipe(), mbcCaractSalaEspOriginal.getHoraFimEquipe())
				|| CoreUtil.modificados(mbcCaractSalaEsp.getIndSituacao(), mbcCaractSalaEspOriginal.getIndSituacao())) {
			atualizarMovimentoCaractSalaEsp(mbcCaractSalaEsp, true);
		}
	}
	
	/**
	 * @ORADB MBCP_ENFORCE_CSE_RULES
	 * 
	 * @throws ApplicationBusinessException
	 */
	protected void verificarColisaoHorarioETurno(MbcCaractSalaEsp mbcCaractSalaEsp) throws ApplicationBusinessException {
		verificarColisaoHorario(mbcCaractSalaEsp);
		verificarHorarioTurno(mbcCaractSalaEsp);
	}
	
	/**
	 * @ORADB rn_csep_ver_espec
	 * 
	 * @throws ApplicationBusinessException
	 */
	protected void verificarEspecialidade(MbcCaractSalaEsp mbcCaractSalaEsp) throws ApplicationBusinessException {
		if(mbcCaractSalaEsp.getAghEspecialidades() == null) {
			throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_00232);
		} else if(!DominioSituacao.A.equals(mbcCaractSalaEsp.getAghEspecialidades().getIndSituacao())) {
			throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_01324);
		}
	}
	
	/**
	 * @ORADB rn_csep_ver_caract
	 * 
	 * @throws ApplicationBusinessException
	 */
	protected void verificarCaracteristica(MbcCaractSalaEsp mbcCaractSalaEsp) throws ApplicationBusinessException {
		if(mbcCaractSalaEsp.getMbcCaracteristicaSalaCirg() == null) {
			throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_00234);
		} else if(!DominioSituacao.A.equals(mbcCaractSalaEsp.getMbcCaracteristicaSalaCirg().getSituacao())) {
			throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_00235);
		}
	}
	
	/**
	 * @ORADB mbck_cse_rn.rn_csep_ver_colis_hr
	 * 
	 * @throws ApplicationBusinessException
	 */
	protected void verificarColisaoHorario(final MbcCaractSalaEsp novo) throws ApplicationBusinessException {
		if(novo.getHoraInicioEquipe() != null && novo.getHoraFimEquipe() != null) {
			Calendar calHrIni = DateUtil.getCalendarBy(novo.getHoraInicioEquipe());
			Calendar calHrFim = DateUtil.getCalendarBy(novo.getHoraFimEquipe());
			final int novoHrIni = calHrIni.get(Calendar.HOUR_OF_DAY) * 60 + calHrIni.get(Calendar.MINUTE);
			final int novoHrFim = calHrFim.get(Calendar.HOUR_OF_DAY) * 60 + calHrFim.get(Calendar.MINUTE);
			
			final List<MbcCaractSalaEsp> caractSalaEsp = this.getMbcCaractSalaEspDAO().buscarOutrosHorariosCaractSala(
					novo.getMbcCaracteristicaSalaCirg(), novo.getId().getSeqp(), novo.getHoraInicioEquipe(), novo.getHoraFimEquipe());
			
			for (final MbcCaractSalaEsp item : caractSalaEsp) {
				calHrIni = DateUtil.getCalendarBy(item.getHoraInicioEquipe());
				calHrFim = DateUtil.getCalendarBy(item.getHoraFimEquipe());
				final int hrIni = calHrIni.get(Calendar.HOUR_OF_DAY) * 60 + calHrIni.get(Calendar.MINUTE);
				final int hrFim = calHrFim.get(Calendar.HOUR_OF_DAY) * 60 + calHrFim.get(Calendar.MINUTE);
				if (hrIni < hrFim) {
					if (!((novoHrIni < hrIni && novoHrFim <= hrIni) || (novoHrIni >= hrFim && novoHrFim > hrFim) || (novoHrIni >= hrFim && novoHrFim <= hrIni))) {
						throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_01136);
					}
				} else {
					if (!(novoHrIni < hrIni && novoHrIni >= hrFim && novoHrFim <= hrIni && novoHrFim > hrFim)) {
						throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_01322);
					}
				}
			}
		} else if((novo.getHoraInicioEquipe() != null && novo.getHoraFimEquipe() == null)
				|| (novo.getHoraInicioEquipe() == null && novo.getHoraFimEquipe() != null)) {
			throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_01135);
		}

	}
	
	/**
	 * @ORADB mbck_cse_rn.rn_csep_ver_hr_turno
	 * 
	 * @throws ApplicationBusinessException
	 */
	protected void verificarHorarioTurno(MbcCaractSalaEsp mbcCaractSalaEsp) throws ApplicationBusinessException {
		if(mbcCaractSalaEsp.getHoraInicioEquipe() != null && mbcCaractSalaEsp.getHoraFimEquipe() != null) {
			Calendar calHrIni = DateUtil.getCalendarBy(mbcCaractSalaEsp.getHoraInicioEquipe());
			Calendar calHrFim = DateUtil.getCalendarBy(mbcCaractSalaEsp.getHoraFimEquipe());
			final int novoHrIni = calHrIni.get(Calendar.HOUR_OF_DAY) * 60 + calHrIni.get(Calendar.MINUTE);
			final int novoHrFim = calHrFim.get(Calendar.HOUR_OF_DAY) * 60 + calHrFim.get(Calendar.MINUTE);
			
			MbcHorarioTurnoCirg horarioTurno = mbcCaractSalaEsp.getMbcCaracteristicaSalaCirg().getMbcHorarioTurnoCirg();
			
			if(horarioTurno == null) {
				throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_01137);
			}
			
			calHrIni = DateUtil.getCalendarBy(horarioTurno.getHorarioInicial());
			calHrFim = DateUtil.getCalendarBy(horarioTurno.getHorarioFinal());
			final int hrIniTurno = calHrIni.get(Calendar.HOUR_OF_DAY) * 60 + calHrIni.get(Calendar.MINUTE);
			final int hrFimTurno = calHrFim.get(Calendar.HOUR_OF_DAY) * 60 + calHrFim.get(Calendar.MINUTE);
			final int hora_fim_cse;
			final int hora_fim_param;
			if (hrFimTurno == 0) {
				hora_fim_cse = HORA_MIN_FINAL_DIA;
			} else {
				hora_fim_cse = hrFimTurno;
			}
			if (novoHrFim == 0) {
				hora_fim_param = HORA_MIN_FINAL_DIA;
			} else {
				hora_fim_param = novoHrFim;
			}
			
			if (hrIniTurno > novoHrIni || hrIniTurno > hora_fim_param || hora_fim_cse < novoHrIni || hora_fim_cse < hora_fim_param) {
				throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_01138);
			}
		}
	}
	
	/**
	 * @ORADB mbck_cse_rn.rn_csep_ver_perc
	 * 
	 * @throws ApplicationBusinessException
	 */
	protected void verificarPercentualReserva(MbcCaractSalaEsp mbcCaractSalaEsp) throws ApplicationBusinessException {
		if(mbcCaractSalaEsp.getMbcProfAtuaUnidCirgs() != null
				&& mbcCaractSalaEsp.getPercentualReserva() == null
				&& DominioSituacao.A.equals(mbcCaractSalaEsp.getIndSituacao())) {
			throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_01210);
		}
	}
	
	/**
	 * @ORADB mbck_cse_rn.rn_csep_ver_bloqueio
	 * 
	 * @throws ApplicationBusinessException
	 */
	protected void verificarPossibilidadeInativar(MbcCaractSalaEsp mbcCaractSalaEsp) throws ApplicationBusinessException {
		List<MbcBloqSalaCirurgica> listBloq = getMbcBloqSalaCirurgicaDAO().buscarBloqSalaPorCaractSalaEsp(
				mbcCaractSalaEsp.getMbcCaracteristicaSalaCirg().getMbcSalaCirurgica(), mbcCaractSalaEsp.getMbcProfAtuaUnidCirgs());
		if(listBloq != null && !listBloq.isEmpty()) {
			throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_01296);
		}
	}
	
	/**
	 * @ORADB mbck_cse_rn.rn_csep_atu_mvto
	 * 
	 * 
	 * @throws BaseException
	 */
	protected void atualizarMovimentoCaractSalaEsp(MbcCaractSalaEsp mbcCaractSalaEsp, boolean update) throws BaseException {
		MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipe = new MbcMvtoSalaEspEquipe();
		if (update) {
			MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipeUpdate = getMbcMvtoSalaEspEquipeDAO().obterUltimoMovimentoPorCaractSalaEsp(mbcCaractSalaEsp);
			if (mbcMvtoSalaEspEquipeUpdate == null) {
				throw new ApplicationBusinessException(MbcCaractSalaEspRNExceptionCode.MBC_01276);
			}
			setDataFimMvto(mbcMvtoSalaEspEquipeUpdate);
			getMbcMvtoSalaEspEquipeRN().persistirMbcMvtoSalaEspEquipe(mbcMvtoSalaEspEquipeUpdate);
			mbcMvtoSalaEspEquipe.setMbcMvtoSalaEspEquipe(mbcMvtoSalaEspEquipeUpdate);
		} else {
			mbcMvtoSalaEspEquipe.setMbcMvtoSalaEspEquipe(null);
		}
		populaMbcvtoSalaCirurgica(mbcMvtoSalaEspEquipe, mbcCaractSalaEsp);
		getMbcMvtoSalaEspEquipeRN().persistirMbcMvtoSalaEspEquipe(mbcMvtoSalaEspEquipe);
	}
	
	protected void setDataFimMvto(MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipe) {
		Date hoje = new Date();
		if (DateUtil.truncaData(mbcMvtoSalaEspEquipe.getDtInicioMvto()).compareTo(DateUtil.truncaData(hoje)) == 0) {
			mbcMvtoSalaEspEquipe.setDtFimMvto(mbcMvtoSalaEspEquipe.getDtInicioMvto());
		} else {
			mbcMvtoSalaEspEquipe.setDtFimMvto(DateUtil.adicionaDias(hoje, -1));
		}
	}
	
	private void populaMbcvtoSalaCirurgica(MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipe, MbcCaractSalaEsp mbcCaractSalaEsp) {
		mbcMvtoSalaEspEquipe.setDtInicioMvto(DateUtil.truncaData(new Date()));
		mbcMvtoSalaEspEquipe.setDtFimMvto(null);
		mbcMvtoSalaEspEquipe.setMbcCaractSalaEsp(mbcCaractSalaEsp);
		mbcMvtoSalaEspEquipe.setMbcProfAtuaUnidCirgs(mbcCaractSalaEsp.getMbcProfAtuaUnidCirgs());
		mbcMvtoSalaEspEquipe.setPercentualReserva(mbcCaractSalaEsp.getPercentualReserva());
		mbcMvtoSalaEspEquipe.setHoraInicioEquipe(mbcCaractSalaEsp.getHoraInicioEquipe());
		mbcMvtoSalaEspEquipe.setHoraFimEquipe(mbcCaractSalaEsp.getHoraFimEquipe());
		mbcMvtoSalaEspEquipe.setIndSituacao(mbcCaractSalaEsp.getIndSituacao());
	}
	
	protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
		return mbcCaractSalaEspDAO;
	}
	
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	protected MbcBloqSalaCirurgicaDAO getMbcBloqSalaCirurgicaDAO() {
		return mbcBloqSalaCirurgicaDAO;
	}
	
	protected MbcMvtoSalaEspEquipeDAO getMbcMvtoSalaEspEquipeDAO() {
		return mbcMvtoSalaEspEquipeDAO;
	}
	
	protected MbcMvtoSalaEspEquipeRN getMbcMvtoSalaEspEquipeRN() {
		return mbcMvtoSalaEspEquipeRN;
	}
	
}
