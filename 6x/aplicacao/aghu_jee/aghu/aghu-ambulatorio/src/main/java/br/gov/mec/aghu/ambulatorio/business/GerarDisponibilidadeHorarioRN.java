package br.gov.mec.aghu.ambulatorio.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeSituacaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacHorarioGradeConsultaDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacSituacaoConsultasDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioOrigemConsulta;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeSituacao;
import br.gov.mec.aghu.model.AacHorarioGradeConsulta;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AghFeriados;

@Stateless
public class GerarDisponibilidadeHorarioRN extends BaseBusiness{

	@EJB
	private AmbulatorioConsultaRN ambulatorioConsultaRN;

	private static final Log LOG = LogFactory.getLog(GerarDisponibilidadeHorarioRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AacSituacaoConsultasDAO aacSituacaoConsultasDAO;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Inject
	private AacHorarioGradeConsultaDAO aacHorarioGradeConsultaDAO;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private AacGradeSituacaoDAO aacGradeSituacaoDAO;

	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;

	private static final long serialVersionUID = -8533302084692044852L;

	public enum GerarDisponibilidadeHorarioRNExceptionCode implements
	BusinessExceptionCode {

		AAC_00664, NAO_EXISTE_PERIODO_ATIVO;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	/**
	 * @ORADB: AACP_GERA_HRIO_CON_N
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 * @throws BaseException 
	 * @throws NumberFormatException 
	 */
	public int gerarHorarioConsulta(AacGradeAgendamenConsultas grade, Date dataInicio, Date dataFim, String nomeMicrocomputador)
		throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NumberFormatException, BaseException {
		AacGradeAgendamenConsultas gradeOld = aacGradeAgendamenConsultasDAO.obterOriginal(grade); 
		
		grade = aacGradeAgendamenConsultasDAO.obterPorChavePrimaria(grade.getSeq());
		
		AacGradeSituacao gradePeriodo = getAacGradeSituacaoDAO().obterGradePeriodoInformado(grade.getSeq(),dataInicio,dataFim);
		if(gradePeriodo==null) {
			GerarDisponibilidadeHorarioRNExceptionCode.NAO_EXISTE_PERIODO_ATIVO.throwException();
		}
		
		Integer qtdGerada=0;
		
		Calendar dataAtualCal = Calendar.getInstance();
		dataAtualCal.setTime(DateUtils.truncate(dataInicio, Calendar.DATE));
		
		Calendar dataBaseCal = Calendar.getInstance();
		dataBaseCal.setTime(DateUtils.truncate(dataInicio, Calendar.DATE));
		
		Calendar dataUltimaGeracao = Calendar.getInstance();
		
		Integer count = 0;
		
		while (!dataBaseCal.getTime().after(dataFim) && count < 7) {
			List<AacHorarioGradeConsulta> horarioGeradoList = getAacHorarioGradeConsultaDAO()
			.buscaHorarioGradeConsultaParaGeracao(dataInicio,grade.getSeq(), dataBaseCal);
			
			for(AacHorarioGradeConsulta horario : horarioGeradoList) {
				while (!dataAtualCal.getTime().after(dataFim)) {
					if (horario.getFormaAgendamento() == null) {
						GerarDisponibilidadeHorarioRNExceptionCode.AAC_00664.throwException();
					}

					getAmbulatorioConsultaRN().verificarFormaAgendamentoEspecialidade(
							horario.getFormaAgendamento(), grade,
							dataAtualCal.getTime());
					
					qtdGerada = this.criarHorarios(horario, dataAtualCal.getTime(), dataFim, qtdGerada, nomeMicrocomputador);
					
					dataAtualCal.add(Calendar.DATE, 7);
				}
				dataAtualCal.setTime(dataBaseCal.getTime());
				if (grade.getDtUltimaGeracao()!=null) {
					dataUltimaGeracao.setTime(grade.getDtUltimaGeracao());
				}
			}
			dataBaseCal.add(Calendar.DATE, 1);
			dataAtualCal.setTime(dataBaseCal.getTime());
			count++;
		}
		
		if (qtdGerada>0) {
			if (grade.getDtUltimaGeracao() ==null 				
					|| DateUtils.truncate(dataFim, Calendar.DATE).after(DateUtils.truncate(grade.getDtUltimaGeracao(), Calendar.DATE))) {
				//AacGradeAgendamenConsultas gradeOld = (AacGradeAgendamenConsultas) BeanUtils.cloneBean(grade);
				grade.setDtUltimaGeracao(DateUtils.truncate(dataFim, Calendar.DATE));
				getAmbulatorioFacade().salvarGradeAgendamentoConsulta(grade, gradeOld);

				if (grade.getDtUltimaGeracao() ==null){
					dataUltimaGeracao.setTime(DateUtils.truncate(dataFim, Calendar.DATE));
				}	
			}				
		}
//		GeraDisponiblidadeVO geraDisponibilidadeVO = new GeraDisponiblidadeVO();
//		geraDisponibilidadeVO.setDtUltimaGeracao(dataUltimaGeracao.getTime());
//		geraDisponibilidadeVO.setQtdGerada(qtdGerada);
		
		return qtdGerada;
	}
	
	
	private Integer criarHorarios(AacHorarioGradeConsulta horario,
			Date dataAtual, Date dataFim, Integer quantidadeGerada, String nomeMicrocomputador) throws IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException, NumberFormatException, BaseException {
		
		Calendar dthrAtualCal = Calendar.getInstance();
		dthrAtualCal.setTime(dataAtual);
		
		Calendar hrAuxCal = Calendar.getInstance();
		hrAuxCal.setTime(horario.getHoraInicio());
		dthrAtualCal.set(Calendar.HOUR_OF_DAY, hrAuxCal.get(Calendar.HOUR_OF_DAY));
		dthrAtualCal.set(Calendar.MINUTE, hrAuxCal.get(Calendar.MINUTE));
		dthrAtualCal.set(Calendar.SECOND, hrAuxCal.get(Calendar.SECOND));
		dthrAtualCal.set(Calendar.MILLISECOND, hrAuxCal.get(Calendar.MILLISECOND));
		
		Calendar dthrAnteriorCal = (Calendar)dthrAtualCal.clone();
		
		Calendar duracao = Calendar.getInstance();
		duracao.setTime(horario.getDuracao());
		
		if (horario.getHoraFim() != null){
			Calendar dthrFimCal = Calendar.getInstance();
			dthrFimCal.setTime(dataAtual);

			hrAuxCal.setTime(horario.getHoraFim());
			dthrFimCal.set(Calendar.HOUR_OF_DAY, hrAuxCal.get(Calendar.HOUR_OF_DAY));
			dthrFimCal.set(Calendar.MINUTE, hrAuxCal.get(Calendar.MINUTE));
			dthrFimCal.set(Calendar.SECOND, hrAuxCal.get(Calendar.SECOND));
			dthrFimCal.set(Calendar.MILLISECOND, hrAuxCal.get(Calendar.MILLISECOND));

			while(dthrAtualCal.before(dthrFimCal)){
				quantidadeGerada = this.criarConsulta(dthrAtualCal, dthrAnteriorCal,horario,quantidadeGerada, duracao, nomeMicrocomputador);
			}
			
		}else{ // POR HORARIOS.			
			for (Integer i = 1; i<=horario.getNumHorario(); i++){
				quantidadeGerada = this.criarConsulta(dthrAtualCal, dthrAnteriorCal,horario,quantidadeGerada, duracao, nomeMicrocomputador);
			}
		}
		
		if (quantidadeGerada>0){
			Calendar dataFimCal = Calendar.getInstance();
			dataFimCal.setTime(DateUtils.truncate(dataFim, Calendar.DATE));
			
			AacHorarioGradeConsulta horarioNew = horario;

			horario.setDtUltimaGeracao(dataFimCal.getTime());
			getAmbulatorioFacade().salvarHorarioGradeConsulta(horarioNew, null);
		}
		
		return quantidadeGerada;
	}
	
	public Integer criarConsulta(Calendar dthrAtualCal,
			Calendar dthrAnteriorCal, AacHorarioGradeConsulta horario,
			Integer quantidadeGerada, Calendar duracao, String nomeMicrocomputador) throws NumberFormatException, BaseException {

		Boolean indGeraFeriado = horario.getGradeAgendamentoConsulta().getEspecialidade().isIndicHoraDispFeriado();
		Boolean indGeraTurno = false;
		
		AghFeriados feriado = getAghuFacade().obterFeriado(dthrAtualCal.getTime());
		
		if (feriado!= null && feriado.getTurno()!=null){
			if(feriado.getTurno() == DominioTurno.T && (dthrAtualCal.get(Calendar.AM_PM) == Calendar.AM)){
				indGeraTurno = true;
			}else if (feriado.getTurno() == DominioTurno.M && (dthrAtualCal.get(Calendar.AM_PM) == Calendar.PM)){
				indGeraTurno = true;
			}
		}
		
		if (indGeraFeriado || feriado == null || indGeraTurno){

			AacSituacaoConsultas situacaoConsulta = getAacSituacaoConsultasDAO().obterSituacaoConsultaPeloId("G");
			
			AacConsultas consulta = new AacConsultas();
			consulta.setDtConsulta(dthrAtualCal.getTime());
			consulta.setDiaSemana(CoreUtil.retornaDiaSemana(dthrAtualCal.getTime()));
			consulta.setGradeAgendamenConsulta(horario.getGradeAgendamentoConsulta());
			consulta.setTpsTipo(horario.getTpsTipo());
			consulta.setCondicaoAtendimento(horario.getFormaAgendamento().getCondicaoAtendimento());
			consulta.setTipoAgendamento(horario.getFormaAgendamento().getTipoAgendamento());
			consulta.setPagador(horario.getFormaAgendamento().getPagador());
			consulta.setExcedeProgramacao(Boolean.FALSE);
			consulta.setOrigem(DominioOrigemConsulta.A);
			consulta.setSituacaoConsulta(situacaoConsulta);
			consulta.setProjetoPesquisa(horario.getGradeAgendamentoConsulta().getProjetoPesquisa());

			getAmbulatorioFacade().inserirConsulta(consulta, nomeMicrocomputador, false);

			quantidadeGerada++;
		}	
		dthrAnteriorCal = (Calendar) dthrAtualCal.clone();
		dthrAtualCal.add(Calendar.HOUR_OF_DAY, duracao.get(Calendar.HOUR_OF_DAY));
		dthrAtualCal.add(Calendar.MINUTE, duracao.get(Calendar.MINUTE));		
		
		return quantidadeGerada;
	}
	
	protected IAghuFacade getAghuFacade()  {
		return (IAghuFacade) aghuFacade;
	}
	
	
	protected AacSituacaoConsultasDAO getAacSituacaoConsultasDAO(){
		return aacSituacaoConsultasDAO;
	}
	
	protected AacHorarioGradeConsultaDAO getAacHorarioGradeConsultaDAO(){
		return aacHorarioGradeConsultaDAO;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}

	protected AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
		return ambulatorioConsultaRN;
	}
	
	private AacGradeSituacaoDAO getAacGradeSituacaoDAO() {
		return aacGradeSituacaoDAO;
	}

}
