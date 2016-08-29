package br.gov.mec.aghu.faturamento.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioOpcaoEncerramentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioTipoDadoParametro;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghParametros;

@Stateless
public class ProgramarEncerramentoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ProgramarEncerramentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ISchedulerFacade schedulerFacade;


	private enum ProgramarEncerramentoONExceptionCode implements BusinessExceptionCode {
		MSG_INFORME_DT_PREVISTA_FIM_PROXIMA_COMPETENCIA, MSG_NAO_INFORME_DT_PREVISTA_FIM_PROXIMA_COMPETENCIA, MSG_DT_FIM_PROXIMA_COMPETENCIA_MAIOR_ATUAL, MSG_HORARIO_INVALIDO_ENCERRAMENTO_PREVIA, MSG_DT_AGENDAMENTO_ANTERIOR_AGORA;
	}

	public void validarCamposProgramarEncerramento(final String nomeJob, final DominioOpcaoEncerramentoAmbulatorio opcao, final Date dtExecucao,
			final Date dtFimCompetencia, final Date dtFimProximaCompetencia, final Boolean previa) throws ApplicationBusinessException,
			ApplicationBusinessException {

		if (!previa && dtFimProximaCompetencia == null) {
			throw new ApplicationBusinessException(ProgramarEncerramentoONExceptionCode.MSG_INFORME_DT_PREVISTA_FIM_PROXIMA_COMPETENCIA);
		}
		if (previa && dtFimProximaCompetencia != null) {
			throw new ApplicationBusinessException(ProgramarEncerramentoONExceptionCode.MSG_NAO_INFORME_DT_PREVISTA_FIM_PROXIMA_COMPETENCIA);
		}
		if (!previa) {
			if (dtFimCompetencia.after(dtFimProximaCompetencia) || dtFimCompetencia.equals(dtFimProximaCompetencia)) {
				throw new ApplicationBusinessException(ProgramarEncerramentoONExceptionCode.MSG_DT_FIM_PROXIMA_COMPETENCIA_MAIOR_ATUAL);
			} else {
				atualizarParametro(opcao, dtFimProximaCompetencia);
			}
		}
		Calendar c = Calendar.getInstance();
		c.setTime(dtExecucao);

		Calendar agora = Calendar.getInstance();
		agora.add(Calendar.MINUTE, 1);
		agora.set(Calendar.SECOND, 0);
		agora.set(Calendar.MILLISECOND, 0);
		if (c.before(agora)) {
			throw new ApplicationBusinessException(ProgramarEncerramentoONExceptionCode.MSG_DT_AGENDAMENTO_ANTERIOR_AGORA);
		}

		ISchedulerFacade schedulerFacade = getSchedulerFacade();
		schedulerFacade.verificarNomeDuplicado(nomeJob);

		Integer hora = c.get(Calendar.HOUR_OF_DAY);
		Integer diaSemana = c.get(Calendar.DAY_OF_WEEK);

		AghFeriados feriado = getAghuFacade().obterFeriadoSemTurno(dtExecucao);

		IParametroFacade parametroFacade = getParametroFacade();

		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HORARIO_INICIAL_MANHA_ENCERRAMENTO);
		Integer horaInicialManha = parametro.getVlrNumerico().intValue();

		parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HORARIO_FINAL_MANHA_ENCERRAMENTO);
		Integer horaFinalManha = parametro.getVlrNumerico().intValue();

		parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HORARIO_INICIAL_TARDE_ENCERRAMENTO);
		Integer horaInicialTarde = parametro.getVlrNumerico().intValue();

		parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HORARIO_FINAL_TARDE_ENCERRAMENTO);
		Integer horaFinalTarde = parametro.getVlrNumerico().intValue();

		if (!diaSemana.equals(Calendar.SATURDAY) && !diaSemana.equals(Calendar.SUNDAY) && feriado == null
				&& ((hora >= horaInicialManha && hora < horaFinalManha) || (hora >= horaInicialTarde && hora < horaFinalTarde))) {
			// Horário inválido para execução de Encerramento/Prévia !!
			throw new ApplicationBusinessException(ProgramarEncerramentoONExceptionCode.MSG_HORARIO_INVALIDO_ENCERRAMENTO_PREVIA);
		}
		// validar se o job já não foi inserido
		parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TEMPO_PROCESSAMENTO_ENCERRAMENTO_AMBULATORIO);
		Integer hrProcessamento = parametro.getVlrNumerico().intValue();
		schedulerFacade.verificarIntervaloData(IFaturamentoFacade.NOME_JOB_ENCERRAMENTO_AMBULATORIO, dtExecucao, hrProcessamento);
	}
	
//	public void agendarEncerramento(){
//		SchedulerFacade schedulerFacade = getSchedulerFacade();
//		final AghJobDetail jobDetail = schedulerFacade.persistirAghJobDetail(new AghJobDetail(triggerName, pessoa.getRapServidores()));
//		getLog().info("AghJobDetail inserido.");
//		
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(dtExecucao);
//		final String cron = "0 " + calendar.get(Calendar.MINUTE) 
//		+ " " + calendar.get(Calendar.HOUR_OF_DAY) + " " + calendar.get(Calendar.DAY_OF_MONTH) 
//		+ " " + (calendar.get(Calendar.MONTH)+1) + " ?" 
//		//+ " " + calendar.get(Calendar.YEAR)
//		;
//		
//		getLog().info("# IntervalCron: " + cron);
//		Boolean isPrevia = getPrevia();
//		Date dtFimCompetencia = getDtFimCompetencia();
//		QuartzTriggerHandle trigger = programarEncerramentoScheduler.agendar(dtExecucao, cron, usuarioLogado, triggerName, DominioOpcaoEncerramentoAmbulatorio.AMB, isPrevia, dtFimCompetencia);
//		jobDetail.setQuartzTriggerHandle(trigger);
//		
//		getLog().info("QuartzTriggerHandle: " + jobDetail.getQuartzTriggerHandle());
//		schedulerFacade.atualizarAghJobDetail(jobDetail);
//		this.getStatusMessages().addFromResourceBundle(Severity.INFO,
//				ProgramarEncerramentoAmbulatorioControllerExceptionCode.PROCEDIMENTO_AMBULATORIAL_AGENDADO_SUCESSO.toString());
//	}

	/**
	 * OBS.: Implementado parcialmente
	 * 
	 * @param pOpcao
	 * @param pData
	 * @throws ApplicationBusinessException
	 */
	protected void atualizarParametro(DominioOpcaoEncerramentoAmbulatorio pOpcao, Date pData) throws ApplicationBusinessException {
		/*
		 * if p_opcao in ('APAP','TODOS') THEN
		 * aghp_set_parametro('P_DT_PARA_ACO','FATF_ATU_ESCALA_ENC','S', p_data,
		 * v_vlr_numerico, 'APAP',v_msg); IF v_msg IS NOT NULL THEN
		 * qms$handle_ofg45_messages('E', TRUE, 'Erro na atualização de
		 * parâmetro APAP'); END IF; end if; if p_opcao in ('APEX','TODOS') THEN
		 * aghp_set_parametro('P_DT_PARA_APE','FATF_ATU_ESCALA_ENC','S', p_data,
		 * v_vlr_numerico, 'APEX',v_msg); IF v_msg IS NOT NULL THEN
		 * qms$handle_ofg45_messages('E', TRUE, 'Erro na atualização de
		 * parâmetro APEX'); END IF; end if; if p_opcao in ('APAF','TODOS') THEN
		 * aghp_set_parametro('P_DT_PARA_APF','FATF_ATU_ESCALA_ENC','S', p_data,
		 * v_vlr_numerico, 'APAF',v_msg); IF v_msg IS NOT NULL THEN
		 * qms$handle_ofg45_messages('E', TRUE, 'Erro na atualização de
		 * parâmetro APAF'); END IF; end if; if p_opcao in ('APAR','TODOS') THEN
		 * aghp_set_parametro('P_DT_PARA_APR','FATF_ATU_ESCALA_ENC','S', p_data,
		 * v_vlr_numerico, 'APAR',v_msg); IF v_msg IS NOT NULL THEN
		 * qms$handle_ofg45_messages('E', TRUE, 'Erro na atualização de
		 * parâmetro APAR'); END IF; end if; if p_opcao in ('APAT','TODOS') THEN
		 * aghp_set_parametro('P_DT_PARA_APT','FATF_ATU_ESCALA_ENC','S', p_data,
		 * v_vlr_numerico, 'APAT',v_msg); IF v_msg IS NOT NULL THEN
		 * qms$handle_ofg45_messages('E', TRUE, 'Erro na atualização de
		 * parâmetro APAT'); END IF; end if; -- Milena 15/12/07 -- separando a
		 * Nefro if p_opcao in ('APAN','TODOS') THEN
		 * aghp_set_parametro('P_DT_PARA_APN','FATF_ATU_ESCALA_ENC','S', p_data,
		 * v_vlr_numerico, 'APAN',v_msg); IF v_msg IS NOT NULL THEN
		 * qms$handle_ofg45_messages('E', TRUE, 'Erro na atualização de
		 * parâmetro APAN'); END IF; end if; -- if p_opcao in ('APAC','TODOS')
		 * THEN aghp_set_parametro('P_DT_PARA_APAC','FATF_ATU_ESCALA_ENC','S',
		 * p_data, v_vlr_numerico, 'APAC',v_msg); IF v_msg IS NOT NULL THEN
		 * qms$handle_ofg45_messages('E', TRUE, 'Erro na atualização de
		 * parâmetro APAC'); END IF; end if;
		 */

		IParametroFacade parametroFacade = getParametroFacade();
		if (pOpcao.equals(DominioOpcaoEncerramentoAmbulatorio.AMB) || pOpcao.equals(DominioOpcaoEncerramentoAmbulatorio.TODOS)) {
			AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DT_PARA_AMB);

			parametro.setVlrData(pData);
			parametro.setTipoDado(DominioTipoDadoParametro.D);
			parametroFacade.setAghpParametro(parametro);
		}

		/*
		 * if p_opcao in ('SISCOLO','TODOS') THEN
		 * aghp_set_parametro('P_DT_PARA_SIS','FATF_ATU_ESCALA_ENC','S', p_data,
		 * v_vlr_numerico, 'SIS',v_msg); IF v_msg IS NOT NULL THEN
		 * qms$handle_ofg45_messages('E', TRUE, 'Erro na atualização de
		 * parâmetro SISCOLO'); END IF; end if; -- MARINA 21/09/2009 if p_opcao
		 * in ('SISMAMA','TODOS') THEN
		 * aghp_set_parametro('P_DT_PARA_MAMA','FATF_ATU_ESCALA_ENC','S',
		 * p_data, v_vlr_numerico, 'MAMA',v_msg); IF v_msg IS NOT NULL THEN
		 * qms$handle_ofg45_messages('E', TRUE, 'Erro na atualização de
		 * parâmetro SISMAMA'); END IF; end if;
		 */
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ISchedulerFacade getSchedulerFacade() {
		return schedulerFacade;
	}
}
