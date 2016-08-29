package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCompSangProcCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoTurnosSalaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaHemoterapiaId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcCompSangProcCirg;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class EscalaPortalPlanejamentoCirurgiaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(EscalaPortalPlanejamentoCirurgiaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;

	@Inject
	private MbcCompSangProcCirgDAO mbcCompSangProcCirgDAO;

	@Inject
	private MbcAgendaHemoterapiaDAO mbcAgendaHemoterapiaDAO;


	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private EscalaPortalPlanejamentoCirurgiaON escalaPortalPlanejamentoCirurgiaON;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade iBlocoCirurgicoPortalPlanejamentoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2234693127560392381L;

	public void transferirAgendasEscalaParaPlanejamento(EscalaPortalPlanejamentoCirurgiasVO escala, Integer pucSerMatricula, Short pucSerVinCodigo) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (!escala.getIndGeradoSistema() && pucSerMatricula.equals(escala.getPucSerMatricula()) && pucSerVinCodigo.equals(escala.getPucSerVinCodigo())) {
			MbcAgendas agenda = getBlocoCirurgicoPortalPlanejamentoFacade().obterAgendaPorAgdSeq(escala.getAgdSeq());

			cancelaCirurgiasEletivas(agenda.getSeq());
			MbcSalaCirurgicaId salaId = new MbcSalaCirurgicaId(escala.getSciUnfSeq(), escala.getSciSeqp());
				
			//@ORADB p_volta_agenda
			agenda.setIndSituacao(DominioSituacaoAgendas.AG);
			agenda.setIndExclusao(Boolean.FALSE);
			agenda.setDthrPrevInicio(escala.getPrevInicio());
			agenda.setDthrPrevFim(escala.getPrevFim());
			agenda.setDtAgenda(escala.getDtAgenda());
			agenda.setSalaCirurgica(getMbcSalaCirurgicaDAO().obterPorChavePrimaria(salaId));
				
			limpaReservaHemoterapica(agenda, servidorLogado.getUsuario());
			agenda.setAgendasHemoterapias(null);
			
			getBlocoCirurgicoFacade().persistirAgenda(agenda, servidorLogado);
		}
	}
	
	/**
	 * @ORADB MBCC_VER_HORA_ESCALA
	 * @return Boolean
	 */
	public Boolean verificarHoraEscala(MbcAgendas agenda, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam, Short unfSeqParam, Date dtAgendaParam, Short sciSeqpCombo) {
		Date vHrFimTurno = null;
		Date vHrEscalaInicio = null;
		Date vHrEscalaFim = null;
		
		if(agenda.getDthrPrevInicio() == null) {
			vHrFimTurno = getMbcCaracteristicaSalaCirgDAO().buscarMaiorHorarioInicioFimTurno(pucSerMatriculaParam, pucSerVinCodigoParam,
					pucUnfSeqParam, pucFuncProfParam, dtAgendaParam, sciSeqpCombo, agenda, true, false, null);
			if(vHrFimTurno == null) {
				return false;
			} else {
				Calendar hrFimTurno = DateUtil.getCalendarBy(vHrFimTurno);
				Calendar dtAgendaTemp = DateUtil.getCalendarBy(dtAgendaParam);
				dtAgendaTemp.set(Calendar.HOUR_OF_DAY, hrFimTurno.get(Calendar.HOUR_OF_DAY));
				dtAgendaTemp.set(Calendar.MINUTE, hrFimTurno.get(Calendar.MINUTE));
				dtAgendaTemp.add(Calendar.MINUTE, -1);
				vHrEscalaInicio = dtAgendaTemp.getTime();
				vHrEscalaFim = vHrEscalaInicio;
			}
			
		} else {
			Calendar tempPrevInicio = DateUtil.getCalendarBy(agenda.getDthrPrevInicio());
			tempPrevInicio.add(Calendar.MINUTE, 1);
			vHrEscalaInicio = tempPrevInicio.getTime();
			vHrEscalaFim = agenda.getDthrPrevFim();
		}
		
		if(!verificarHoraTurnoValido(pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, espSeqParam, unfSeqParam,
				dtAgendaParam, sciSeqpCombo, vHrEscalaInicio)
				&& !verificarHoraTurnoValido(pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, espSeqParam,
						unfSeqParam, dtAgendaParam, sciSeqpCombo, vHrEscalaFim)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * @ORADB MBCC_VER_HORA_TURNO
	 * @param pucSerMatriculaParam
	 * @param pucSerVinCodigoParam
	 * @param pucUnfSeqParam
	 * @param pucFuncProfParam
	 * @param espSeqParam
	 * @param unfSeqParam
	 * @param dtAgendaParam
	 * @param sciSeqpCombo
	 * @param vHrEscala
	 * @return Boolean
	 */
	public Boolean verificarHoraTurnoValido(Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam, Short unfSeqParam, Date dtAgendaParam,
			Short sciSeqpCombo, Date vHrEscala) {
		MbcAgendas param = getEscalaPortalPlanejamentoCirurgiaON().montarParametroParaBuscarHorariosDisponiveis(
				pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam,
				pucFuncProfParam, espSeqParam, unfSeqParam, dtAgendaParam,
				sciSeqpCombo);
		
		List<PortalPlanejamentoTurnosSalaVO> listaVO = getBlocoCirurgicoFacade().buscarTurnosHorariosDisponiveis(param);
		
		Calendar hrZeradaCal = Calendar.getInstance();
		hrZeradaCal.set(Calendar.HOUR_OF_DAY, 0);
		hrZeradaCal.set(Calendar.MINUTE, 0);
		Integer hrZerada = getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(hrZeradaCal.getTime());
		
		Calendar ultimaHrDiaCal = Calendar.getInstance();
		ultimaHrDiaCal.set(Calendar.HOUR_OF_DAY, 23);
		ultimaHrDiaCal.set(Calendar.MINUTE, 59);
		Integer ultimaHrDia = getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(ultimaHrDiaCal.getTime());
		
		Integer pHora = getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(vHrEscala);
		
		for(PortalPlanejamentoTurnosSalaVO item : listaVO) {
			if((pHora >= getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(item.getHorarioInicial()) && pHora <= getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(item.getHorarioFinal()))
					|| (pHora == hrZerada && getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(item.getHorarioFinal()) == ultimaHrDia)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @ORADB MBCP_AGD_GERA_SANGUE
	 * @throws BaseException 
	 */
	public Boolean gerarSangue(Integer agdSeq) throws BaseException {
		Boolean incluiu = Boolean.FALSE;
		MbcAgendas agenda = getMbcAgendasDAO().obterPorChavePrimaria(agdSeq);
		if(DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())) {
			List<MbcCompSangProcCirg> listCompSangProcCirg = this.getMbcCompSangProcCirgDAO().buscarMbcCompSangProcCirg(
					agenda.getEspProcCirgs().getId().getPciSeq(), agenda.getEspProcCirgs().getId().getEspSeq());
			
			Boolean espNull = Boolean.FALSE;
			for(MbcCompSangProcCirg compSang : listCompSangProcCirg) {
				if(compSang != null && (compSang.getAghEspecialidades() == null && !espNull) || compSang.getAghEspecialidades() != null) {
					if(compSang.getAghEspecialidades() != null) {
						espNull = Boolean.TRUE;
					}
					incluiu = Boolean.TRUE;
					MbcAgendaHemoterapiaId id = new MbcAgendaHemoterapiaId(agdSeq, compSang.getAbsComponenteSanguineo().getCodigo());
					MbcAgendaHemoterapia agdHemo = new MbcAgendaHemoterapia();
					agdHemo.setId(id);
					agdHemo.setQtdeUnidade(compSang.getQtdeUnidade());
					agdHemo.setQtdeMl(compSang.getQtdeMl());
					agdHemo.setAbsComponenteSanguineo(compSang.getAbsComponenteSanguineo());
					agdHemo.setIndFiltrado(Boolean.FALSE);
					agdHemo.setIndIrradiado(Boolean.FALSE);
					agdHemo.setIndLavado(Boolean.FALSE);
					agdHemo.setMbcAgendas(agenda);
					agdHemo.setCriadoEm(new Date());
					agdHemo.setRapServidores(this.servidorLogadoFacade.obterServidorLogado());
					if(agenda.getAgendasHemoterapias()!=null){
						agenda.getAgendasHemoterapias().add(agdHemo);
					}else{
						Set<MbcAgendaHemoterapia> hemoterapias = new HashSet<MbcAgendaHemoterapia>();
						hemoterapias.add(agdHemo);
						agenda.setAgendasHemoterapias(hemoterapias);
					}
					
					getBlocoCirurgicoFacade().persistirAgendaHemoterapia(agdHemo);
				}
			}
		}
		
		return incluiu;
	}
	
	/**
	 * @ORADB p_cancela_cirgs
	 * @parameter MbcAgendas
	 * @parameter RapServidores
	 * @throws BaseException
	 */
	public void cancelaCirurgiasEletivas(Integer agdSeq) throws BaseException {
		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR);
		List<MbcCirurgias> eletivas = getMbcCirurgiasDAO().pesquisarCirurgiasEletivasParaAgenda(agdSeq);
		MbcMotivoCancelamento motivo = getBlocoCirurgicoFacade().obterMotivoCancelamentoPorChavePrimaria(Short.valueOf(parametro.getVlrNumerico().toString()));
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();	
		if(!eletivas.isEmpty() && motivo != null){
			for (MbcCirurgias cirurgia : eletivas) {
				cirurgia.setMotivoCancelamento(motivo);			
				getBlocoCirurgicoFacade().persistirCirurgia(cirurgia,servidorLogado);
			}	
		}
	}
		
	/**
	 * @ORADB p_limpa_reserva_sangue
	 * @parameter MbcAgendas
	 * @param nomeUsuario TODO
	 * @throws BaseException 
	 */
	private void limpaReservaHemoterapica(MbcAgendas agenda, String nomeUsuario) throws BaseException {
		if (agenda.getAgendasHemoterapias() != null && !agenda.getAgendasHemoterapias().isEmpty()) {
			for (MbcAgendaHemoterapia hemoterapia : agenda.getAgendasHemoterapias()) {
				MbcAgendaHemoterapia agdHemo = this.getMbcAgendaHemoterapiaDAO().obterOriginal(hemoterapia.getId());
				if(agdHemo != null) {
					getBlocoCirurgicoFacade().excluirAgendaHemoterapia(hemoterapia);
				}
			}
		}
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO() {
		return mbcSalaCirurgicaDAO; 
	}
	
	protected MbcCompSangProcCirgDAO getMbcCompSangProcCirgDAO(){
		return mbcCompSangProcCirgDAO;
	}
	
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	protected EscalaPortalPlanejamentoCirurgiaON getEscalaPortalPlanejamentoCirurgiaON() {
		return escalaPortalPlanejamentoCirurgiaON;
	}
	
	protected IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() {
		return this.iBlocoCirurgicoPortalPlanejamentoFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return this.iBlocoCirurgicoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return this.iParametroFacade;
	}
	
	protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO() {
		return mbcAgendaHemoterapiaDAO;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
}