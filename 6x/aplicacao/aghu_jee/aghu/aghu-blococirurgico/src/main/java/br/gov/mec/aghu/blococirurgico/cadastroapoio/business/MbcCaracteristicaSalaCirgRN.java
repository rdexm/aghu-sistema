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
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoCaractSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTurnosDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcMvtoCaractSalaCirg;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author fpalma
 * 
 */
@Stateless
public class MbcCaracteristicaSalaCirgRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcCaracteristicaSalaCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMvtoCaractSalaCirgDAO mbcMvtoCaractSalaCirgDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;

	@Inject
	private MbcTurnosDAO mbcTurnosDAO;


	@EJB
	private MbcMvtoCaractSalaCirgRN mbcMvtoCaractSalaCirgRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1660648832327444424L;

	public enum MbcCaracteristicaSalaCirgRNExceptionCode implements BusinessExceptionCode {
		MBC_00204, MBC_00405, MBC_00407, MBC_01139, MBC_01140, MBC_01267, MBC_00415, MBC_00416
	}
	
	private static final int HORA_MIN_FINAL_DIA	= 1439;	// 23 * 60 + 59;
	
	public void inserir(MbcCaracteristicaSalaCirg newCaractSalaCirg) throws BaseException {
		executarAntesDeInserir(newCaractSalaCirg);
		getMbcCaracteristicaSalaCirgDAO().persistir(newCaractSalaCirg);
		executarDepoisDeInserir(newCaractSalaCirg);
	}
	
	public void atualizar(MbcCaracteristicaSalaCirg newCaractSalaCirg) throws BaseException {
		executarAntesDeUpdate(newCaractSalaCirg);
		getMbcCaracteristicaSalaCirgDAO().merge(newCaractSalaCirg);
		posAtualizar(newCaractSalaCirg);
	}
	
	/**
	 * Trigger
	 * 
	 * @ORADB: MBCT_CAS_BRI  
	 * 
	 * 
	 * @param newCaractSalaCirg
	 * @throws ApplicationBusinessException
	 */
	protected void executarAntesDeInserir(MbcCaracteristicaSalaCirg newCaractSalaCirg) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(servidorLogado == null) {
			throw new ApplicationBusinessException(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_00204);
		}
		newCaractSalaCirg.setCriadoEm(new Date());
		newCaractSalaCirg.setRapServidoresByMbcCasSerFk1(servidorLogado);
		validarSalaDeUnidadeCirurgicaAtiva(newCaractSalaCirg);
	}
	
	/**
	 * @ORADB: rn_casp_ver_sl_cirg
	 * 
	 * @param newCaractSalaCirg
	 * @throws ApplicationBusinessException
	 */
	private void validarSalaDeUnidadeCirurgicaAtiva(MbcCaracteristicaSalaCirg newCaractSalaCirg) throws ApplicationBusinessException {
		if(newCaractSalaCirg.getMbcSalaCirurgica() != null && !DominioSituacao.A.equals(newCaractSalaCirg.getMbcSalaCirurgica().getSituacao())) {
			throw new ApplicationBusinessException(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_00405);
		}
	}
	
	/**
	 * Trigger
	 * 
	 * @ORADB: MBCT_CAS_BRU
	 * 
	 * @param newCaractSalaCirg
	 * @param oldCaractSalaCirg
	 * @throws ApplicationBusinessException
	 */
	protected void executarAntesDeUpdate(MbcCaracteristicaSalaCirg newCaractSalaCirg) throws ApplicationBusinessException {
        RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(servidorLogado == null) {
			throw new ApplicationBusinessException(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_00204);
		}
		validarCaractSalas(newCaractSalaCirg);
		verificarHorarioEquipe(newCaractSalaCirg);
		newCaractSalaCirg.setRapServidoresByMbcCasSerFk2(servidorLogado);
	}
	
	
	/**
	 * @ORADB: rn_casp_ver_urgencia
	 * 
	 * @param newCaractSalaCirg
	 * @throws ApplicationBusinessException
	 */
	protected void validarCaractSalas(MbcCaracteristicaSalaCirg newCaractSalaCirg) throws ApplicationBusinessException {
		//#Correção de bug
//		Buscar da base, pois se nao recarregou o objeto vem lista null no caso de inserir-colocar equipe-editar um mapeamento
		MbcTurnos turno = getMbcTurnosDAO().obterOriginal(newCaractSalaCirg.getMbcHorarioTurnoCirg().getId().getTurno());
		newCaractSalaCirg.setMbcTurnos(turno);
		newCaractSalaCirg.getMbcHorarioTurnoCirg().setMbcTurnos(turno);
		List<MbcCaractSalaEsp> listaMbcCaractsalaEspes = getMbcCaractSalaEspDAO().pesquisarCaractSalaEspPorUnidadeSalaTurnoDiaSemana(newCaractSalaCirg.getMbcSalaCirurgica().getUnidadeFuncional().getSeq(), newCaractSalaCirg.getMbcSalaCirurgica().getId().getSeqp(), newCaractSalaCirg.getMbcTurnos(), newCaractSalaCirg.getDiaSemana());
		if(newCaractSalaCirg.getIndUrgencia() && listaMbcCaractsalaEspes != null
				&& !listaMbcCaractsalaEspes.isEmpty()) {
			for(MbcCaractSalaEsp caract : listaMbcCaractsalaEspes) {
				if(DominioSituacao.A.equals(caract.getIndSituacao())) {
					throw new ApplicationBusinessException(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_00407);
				}
			}
		}
	}
	
	
	
	/**
	 * @ORADB: rn_casp_ver_hr_equip
	 * 
	 * @param newCaractSalaCirg
	 * @throws ApplicationBusinessException
	 */
	protected void verificarHorarioEquipe(MbcCaracteristicaSalaCirg newCaractSalaCirg) throws ApplicationBusinessException {
		
		if(newCaractSalaCirg.getMbcHorarioTurnoCirg() == null) {
			throw new ApplicationBusinessException(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01139);
		}
		
		Calendar calHrIni = DateUtil.getCalendarBy(newCaractSalaCirg.getMbcHorarioTurnoCirg().getHorarioInicial());
		Calendar calHrFim = DateUtil.getCalendarBy(newCaractSalaCirg.getMbcHorarioTurnoCirg().getHorarioFinal());
		final int novoHrIni = calHrIni.get(Calendar.HOUR_OF_DAY) * 60 + calHrIni.get(Calendar.MINUTE);
		final int novoHrFim = calHrFim.get(Calendar.HOUR_OF_DAY) * 60 + calHrFim.get(Calendar.MINUTE);
		
		
		final List<MbcCaractSalaEsp> salaEsps = this.getMbcCaractSalaEspDAO().pesquisarCaractSalaEspPorCaractSalaCirg(
				newCaractSalaCirg.getSeq());
		
		for (final MbcCaractSalaEsp mbcCaractSalaEsp : salaEsps) {
			calHrIni = DateUtil.getCalendarBy(mbcCaractSalaEsp.getHoraInicioEquipe());
			calHrFim = DateUtil.getCalendarBy(mbcCaractSalaEsp.getHoraFimEquipe());
			final int hrIniEquipe = calHrIni.get(Calendar.HOUR_OF_DAY) * 60 + calHrIni.get(Calendar.MINUTE);
			final int hrFimEquipe = calHrFim.get(Calendar.HOUR_OF_DAY) * 60 + calHrFim.get(Calendar.MINUTE);
			final int hora_fim_cse;
			final int hora_fim_param;
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
				throw new ApplicationBusinessException(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01140);
			}
		}
	}
	
	/**
	 * Trigger
	 * 
	 * @ORADB: MBCT_CAS_ARI
	 * 
	 * @param newCaractSalaCirg
	 * @throws BaseException 
	 */
	protected void executarDepoisDeInserir(MbcCaracteristicaSalaCirg caractSalaCirg) throws BaseException {
		atualizarHistoricoMovimentacoes(caractSalaCirg, false);
	}
	
	/**
	 * @ORADB MBCT_CAS_ARU
	 * @throws BaseException
	 */
	protected void posAtualizar(MbcCaracteristicaSalaCirg caractSalaCirg) throws BaseException {
		MbcCaracteristicaSalaCirg caractSalaCirgOriginal = getMbcCaracteristicaSalaCirgDAO().obterOriginal(caractSalaCirg.getSeq());
		
		if (CoreUtil.modificados(caractSalaCirg.getSeq(),caractSalaCirgOriginal.getSeq())
				|| CoreUtil.modificados(caractSalaCirg.getMbcSalaCirurgica(), caractSalaCirgOriginal.getMbcSalaCirurgica())
				|| CoreUtil.modificados(caractSalaCirg.getMbcHorarioTurnoCirg(), caractSalaCirgOriginal.getMbcHorarioTurnoCirg())
				|| CoreUtil.modificados(caractSalaCirg.getDiaSemana(), caractSalaCirgOriginal.getDiaSemana())
				|| CoreUtil.modificados(caractSalaCirg.getCirurgiaParticular(), caractSalaCirgOriginal.getCirurgiaParticular())
				|| CoreUtil.modificados(caractSalaCirg.getSituacao(), caractSalaCirgOriginal.getSituacao())
				|| CoreUtil.modificados(caractSalaCirg.getIndUrgencia(), caractSalaCirgOriginal.getIndUrgencia())
				|| CoreUtil.modificados(caractSalaCirg.getIndDisponivel(), caractSalaCirgOriginal.getIndDisponivel())) {
			atualizarHistoricoMovimentacoes(caractSalaCirg, true);
		}
		
	}
	
	/**
	 * @ORADB rn_casp_atu_mvto
	 * 
	 * @throws BaseException
	 */
	protected void atualizarHistoricoMovimentacoes(MbcCaracteristicaSalaCirg caractSalaCirg, boolean update) throws BaseException {
		MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirurg = new MbcMvtoCaractSalaCirg();
		if (update) {
			MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirgUpdate = getMbcMvtoCaractSalaCirgDAO().pesquisarUltimoMovimentoDaCaractSalaCirg(caractSalaCirg.getSeq());
			if (mbcMvtoCaractSalaCirgUpdate == null) {
				throw new ApplicationBusinessException(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01267);
			}
			setDataFimMvto(mbcMvtoCaractSalaCirgUpdate);
			getMbcMvtoCaractSalaCirgRN().persistirMbcMvtoCaractSalaCirg(mbcMvtoCaractSalaCirgUpdate);
			mbcMvtoCaractSalaCirurg.setMbcMvtoCaractSalaCirg(mbcMvtoCaractSalaCirgUpdate);
		} else {
			mbcMvtoCaractSalaCirurg.setMbcMvtoCaractSalaCirg(null);
		}
		populaMbcCaracteristicaSalaCirg(mbcMvtoCaractSalaCirurg, caractSalaCirg);
		getMbcMvtoCaractSalaCirgRN().persistirMbcMvtoCaractSalaCirg(mbcMvtoCaractSalaCirurg);
	}
	
	private void populaMbcCaracteristicaSalaCirg(MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirurg, MbcCaracteristicaSalaCirg caractSalaCirg) {
		mbcMvtoCaractSalaCirurg.setDtInicioMvto(DateUtil.truncaData(new Date()));
		mbcMvtoCaractSalaCirurg.setDtFimMvto(null);
		mbcMvtoCaractSalaCirurg.setMbcCaracteristicaSalaCirg(caractSalaCirg);
		mbcMvtoCaractSalaCirurg.setMbcSalaCirurgica(caractSalaCirg.getMbcSalaCirurgica());
		mbcMvtoCaractSalaCirurg.setMbcHorarioTurnoCirg(caractSalaCirg.getMbcHorarioTurnoCirg());
		mbcMvtoCaractSalaCirurg.setDiaSemana(caractSalaCirg.getDiaSemana());
		mbcMvtoCaractSalaCirurg.setCirurgiaParticular(caractSalaCirg.getCirurgiaParticular());
		mbcMvtoCaractSalaCirurg.setSituacao(caractSalaCirg.getSituacao());
		mbcMvtoCaractSalaCirurg.setIndUrgencia(caractSalaCirg.getIndUrgencia());
		mbcMvtoCaractSalaCirurg.setIndDisponivel(caractSalaCirg.getIndDisponivel());
	}
	
	private void setDataFimMvto(MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirg) {
		Date hoje = new Date();
		if (DateUtil.truncaData(mbcMvtoCaractSalaCirg.getDtInicioMvto()).compareTo(DateUtil.truncaData(hoje)) == 0) {
			mbcMvtoCaractSalaCirg.setDtFimMvto(mbcMvtoCaractSalaCirg.getDtInicioMvto());
		} else {
			mbcMvtoCaractSalaCirg.setDtFimMvto(DateUtil.adicionaDias(hoje, -1));
		}
	}
	
	protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
		return mbcCaractSalaEspDAO;
	}
	
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	protected MbcMvtoCaractSalaCirgDAO getMbcMvtoCaractSalaCirgDAO() {
		return mbcMvtoCaractSalaCirgDAO;
	}
	
	protected MbcTurnosDAO getMbcTurnosDAO() {
		return mbcTurnosDAO;
	}
	
	protected MbcMvtoCaractSalaCirgRN getMbcMvtoCaractSalaCirgRN() {
		return mbcMvtoCaractSalaCirgRN;
	}
	
	public List<MbcCaracteristicaSalaCirg> pesquisarSalaDiaSemanaTurno(Object objSalaDiaSemanaTurno, AghUnidadesFuncionais unidadeFuncional) {
		return getMbcCaracteristicaSalaCirgDAO().pesquisarSalaDiaSemanaTurno(objSalaDiaSemanaTurno, unidadeFuncional);
	}

}
