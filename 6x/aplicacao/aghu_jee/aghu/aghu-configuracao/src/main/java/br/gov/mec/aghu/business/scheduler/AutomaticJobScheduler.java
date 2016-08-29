package br.gov.mec.aghu.business.scheduler;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Trigger;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.business.scheduler.auto.CustoDiarioAutomatizadoScheduler;
import br.gov.mec.aghu.business.scheduler.auto.DadosSumarioPrescricaoEnfermagemScheduler;
import br.gov.mec.aghu.business.scheduler.auto.DadosSumarioPrescricaoMedicaScheduler;
import br.gov.mec.aghu.business.scheduler.auto.EncerramentoAutomaticoContaHospitalarScheduler;
import br.gov.mec.aghu.business.scheduler.auto.EnviaEmailNotificacaoAceitePendenteScheduler;
import br.gov.mec.aghu.business.scheduler.auto.EnvioEmailPacienteGMRScheduler;
import br.gov.mec.aghu.business.scheduler.auto.EscalaProfissionalScheduler;
import br.gov.mec.aghu.business.scheduler.auto.ExameInternetStatusScheduler;
import br.gov.mec.aghu.business.scheduler.auto.FechamentoEstoqueMensalScheduler;
import br.gov.mec.aghu.business.scheduler.auto.GestaoAmostrasScheduler;
import br.gov.mec.aghu.business.scheduler.auto.ImportacaoArqRetornoPregaoBBScheduler;
import br.gov.mec.aghu.business.scheduler.auto.IndicadoresHospitalaresScheduler;
import br.gov.mec.aghu.business.scheduler.auto.IndicadoresResumidosScheduler;
import br.gov.mec.aghu.business.scheduler.auto.LogsAplicacaoScheduler;
import br.gov.mec.aghu.business.scheduler.auto.MensageiroServiceScheduler;
import br.gov.mec.aghu.business.scheduler.auto.MonitoramentoProcessoAutorizacaoOpmeScheduler;
import br.gov.mec.aghu.business.scheduler.auto.ParametroSistemaScheduler;
import br.gov.mec.aghu.business.scheduler.auto.PedidosPapelariaScheduler;
import br.gov.mec.aghu.business.scheduler.auto.PerfisAcessoSistemaComPendenciasBloqueantesScheduler;
import br.gov.mec.aghu.business.scheduler.auto.ProcessarExameInternetScheduler;
import br.gov.mec.aghu.business.scheduler.auto.RequisicaoMaterialAutomaticaScheduler;
import br.gov.mec.aghu.business.scheduler.auto.ServicoRepeticaoNotificacaoScheduler;
import br.gov.mec.aghu.business.scheduler.auto.SolicitacaoCompraAlmoxarifadoScheduler;
import br.gov.mec.aghu.business.scheduler.auto.ValidacaoProntuarioScheduler;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.quartz.MECScheduler;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;

@Stateless
@SuppressWarnings("PMD.CyclomaticComplexity")
public class AutomaticJobScheduler {
	
	private static final Log LOG = LogFactory.getLog(AutomaticJobScheduler.class);
	
	
	@EJB
	private ISchedulerFacade schedulerFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private SolicitacaoCompraAlmoxarifadoScheduler gerarSolicitacaoCompraAlmoxarifadoScheduler;
	
	@Inject
	private ExameInternetStatusScheduler exameInternetStatusScheduler;
	
	@Inject
	private ProcessarExameInternetScheduler processarExameInternetScheduler;
	
	@Inject
	private ParametroSistemaScheduler parametroSistemaScheduler;
	
	@Inject
	private MensageiroServiceScheduler mensageiroServiceScheduler;
	
	@Inject
	private GestaoAmostrasScheduler processarGestaoAmostrasScheduler;
	
	@Inject
	private FechamentoEstoqueMensalScheduler fechamentoEstoqueMensalScheduler;
	
	@Inject
	private CustoDiarioAutomatizadoScheduler processarCustoDiarioAutomatizadoScheduler;
	
	@Inject
	private EscalaProfissionalScheduler aEscalaProfissionalScheduler;
	
	@Inject
	private IndicadoresHospitalaresScheduler aIndicadoresHospitalaresScheduler;
	
	@Inject
	private IndicadoresResumidosScheduler aIndicadoresResumidosScheduler;
	
	@Inject
	private DadosSumarioPrescricaoMedicaScheduler aDadosSumarioPrescricaoMedicaScheduler;
	
	@Inject
	private EncerramentoAutomaticoContaHospitalarScheduler aEncerramentoAutomaticoContaHospitalarScheduler;
	
	@Inject
	private PerfisAcessoSistemaComPendenciasBloqueantesScheduler aPerfisAcessoSistemaComPendenciasBloqueantesScheduler;
	
	@Inject
	private LogsAplicacaoScheduler aLogsAplicacaoScheduler;
	
	@Inject
	private DadosSumarioPrescricaoEnfermagemScheduler aDadosSumarioPrescricaoEnfermagemScheduler;
	
	@Inject
	private RequisicaoMaterialAutomaticaScheduler aRequisicaoMaterialAutomaticaScheduler;
	
	@Inject
	private ValidacaoProntuarioScheduler aValidacaoProntuarioScheduler;
	
	@Inject
	private PedidosPapelariaScheduler aPedidosPapelariaScheduler;
	
	@Inject
	private MonitoramentoProcessoAutorizacaoOpmeScheduler monitoramentoProcessoAutorizacaoOpmeScheduler;

	@Inject
	private ImportacaoArqRetornoPregaoBBScheduler importacaoArqRetornoPregaoBBScheduler;

	@Inject
	private EnvioEmailPacienteGMRScheduler envioEmailPacienteGMRScheduler;
	
	
	@Inject
	private ServicoRepeticaoNotificacaoScheduler repeticaoNotificacaoScheduler;
	
	@Inject
	private EnviaEmailNotificacaoAceitePendenteScheduler enviaEmailNotificacaoAceitePendenteScheduler;
	
	
	
	public void agendarTarefa(IAutomaticJobEnum idTarefa, String nomeMicrocomputador, final RapServidores servidorAgendador) throws BaseException {
		if (idTarefa == null) {
			throw new IllegalArgumentException("Deve ser informado um Job para agendamento!!!");
		}
		try {
			String triggerName = idTarefa.getTriggerName();
			AghJobDetail job = schedulerFacade.obterAghJobDetailPorNome(triggerName);
			
			if (job == null) {
				Trigger trigger = this.doAgendarTarefa(idTarefa, nomeMicrocomputador, servidorAgendador);
				
				AghJobDetail jobDetail = new AghJobDetail(triggerName, idTarefa.getServidor(), trigger);
				
				schedulerFacade.persistirAghJobDetail(jobDetail);
				LOG.info(" # Tarefa: " + triggerName + " agendada com sucesso!!!");
			} else {
				LOG.info(" # Tarefa: " + triggerName + " JAH estava agendada, vou somente criar a Trigger...!!!");
				
				Trigger trigger = this.doAgendarTarefa(idTarefa, nomeMicrocomputador, servidorAgendador);
				LOG.info(" # Trigger: " + trigger.getDescription());
			}
			
		} catch (Throwable tex) { //NOPMD
			LOG.error(tex.getMessage(), tex);
			BaseException negocioEx = new ApplicationBusinessException(
					AutomaticJobSchedulerExceptionCode.ERRO_AO_AGENDAR_TAREFA
					, (tex.getCause() != null) ? tex.getCause().getMessage() : tex.getMessage()
			);
			negocioEx.initCause(tex);
			throw negocioEx;
		}		
	}
	
	@SuppressWarnings("PMD.CyclomaticComplexity")
	private Trigger doAgendarTarefa(IAutomaticJobEnum idTarefa, String nomeMicrocomputador, final RapServidores servidorAgendador) throws BaseException {
		Trigger trigger = null;
				
		AghParametros parametro;
		String[] hm;
		String cron = null;
		
		switch (idTarefa.toString()) {
		case "SCHEDULERINIT_PARAMETROSISTEMA": //AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ALL_DAY_22_45_CRON);
			trigger = parametroSistemaScheduler.agendar(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), "Teste", true, servidorAgendador);
			break;
		case "SCHEDULERINIT_PROCESSARGESTAOAMOSTRA"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.PROCESSAR_GESTAO_AMOSTRA_CRON);
			trigger = processarGestaoAmostrasScheduler.agendarGestaoAmostra(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), "Teste", true, nomeMicrocomputador, servidorAgendador);
			break;
		case "SCHEDULERINIT_FECHAMENTOESTOQUEMENSAL"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.FECHAMENTO_MENSAL_ESTOQUE_CRON); // Último dia de cada mês as 23:55
			trigger = fechamentoEstoqueMensalScheduler.gerarFechamentoEstoqueMensal(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_PROCESSARCUSTODIARIOAUTOMATIZADO"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.PROCESSAR_CUSTO_MENSAL_AUTOMATIZADO); 														
			trigger = processarCustoDiarioAutomatizadoScheduler.processarCustoDiarioAutomatizado(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), "Teste", true, servidorAgendador);
			break;

			
		case "SCHEDULERINIT_VERIFICACAOESCALAPROFISSIONAL"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ALL_DAY_22_45_CRON);
			trigger = this.aEscalaProfissionalScheduler.verificar(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador, nomeMicrocomputador, new Date());
			break;
		case "SCHEDULERINIT_CARGAINDICADORES_GERAR"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ONCE_A_MONTH_CRON);
			trigger = this.aIndicadoresHospitalaresScheduler.gerar(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_CARGAINDICADORES_GRAVAR"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ONCE_A_MONTH_CRON);
			//
			trigger = this.aIndicadoresResumidosScheduler.gravar(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_GERARDADOSSUMARIOPRESCRICAOMEDICA"://AutomaticJobEnum
			parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_GERA_SUMARIO_ALTA_EXECUCAO);
			hm = parametro.getVlrTexto().split(":");
			//Seta a hora e o minuto para as execuções diarias
			cron = "0 " + hm[1] + " " + hm[0] + " ? * *";
			cron = this.calculateCronExpression(idTarefa, cron);
			//
			trigger = this.aDadosSumarioPrescricaoMedicaScheduler.agendarGerar(cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_ENCERRAMENTOAUTOMATICOCONTASHOSPITALARES"://AutomaticJobEnum
			parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_HORA_ENCERRAMENTO_AUTOMATICO_CTH);
			hm = parametro.getVlrTexto().split(":");
			// Seta a hora e o minuto para as execuções diarias
			cron = "0 " + hm[1] + " " + hm[0] + " ? * *";
			cron = this.calculateCronExpression(idTarefa, cron);
			//
			trigger = this.aEncerramentoAutomaticoContaHospitalarScheduler.executar(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador, nomeMicrocomputador);
			break;
		case "SCHED_INIT_REM_PERFISACESSOSISTEMACOMPENDENCIASBLOQUEANTES"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ALL_DAY_22_45_CRON);
			//
			trigger = this.aPerfisAcessoSistemaComPendenciasBloqueantesScheduler.remover(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_REMOVERLOGSAPLICACAO"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ALL_DAY_22_45_CRON);
			//
			trigger = this.aLogsAplicacaoScheduler.remover(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		
		case "SCHEDULERINIT_GERARDADOSSUMARIOPRESCRICAOENFERMAGEM"://AutomaticJobEnum
			parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_GERA_SUMARIO_ALTA_EXECUCAO);
			hm = parametro.getVlrTexto().split(":");
			//Seta a hora e o minuto para as execuções diarias
			cron = "0 " + hm[1] + " " + hm[0] + " ? * *";
			cron = this.calculateCronExpression(idTarefa, cron);
			//
			trigger = this.aDadosSumarioPrescricaoEnfermagemScheduler.agendarGerar(cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		
		case "SCHEDULERINIT_EFETIVARREQUISICAOMATERIALAUTOMATICA"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ALL_DAY_22_45_CRON);
			//
			trigger = this.aRequisicaoMaterialAutomaticaScheduler.efetivar(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador, nomeMicrocomputador);
			break;
		case "SCHEDULERINIT_GERACAO_AUTOMATICA_SOL_COMPRAS_MAT_ESTOCAVEL"://AutomaticJobEnum
			try {
				parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HORA_AGENDA_SC);

				if (parametro != null && parametro.getVlrData() != null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(parametro.getVlrData());
					
					Integer hora = calendar.get(Calendar.HOUR_OF_DAY);
					Integer minuto = calendar.get(Calendar.MINUTE);
					
					if (hora != null && minuto != null) {
						cron = this.calculateCronExpression(idTarefa, "0 "+minuto.intValue()+" "+hora.intValue()+" * * ? *");
					}
				}
			} catch (ApplicationBusinessException e) {
				LOG.debug("Ocorreu um erro ao carregar o parâmetro P_HORA_AGENDA_SC.");
				cron = null;
			}
			
			if (cron != null) {
				trigger = this.gerarSolicitacaoCompraAlmoxarifadoScheduler.agendar(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), "Geracao Automatica das SCs para Material Estocavel", true, servidorAgendador);
			}
			
			break;
		case "SCHEDULERINIT_PROCESSAREXAMENAOREALIZADOSCHEDULER"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.PROCESSAR_EXAME_NAO_REALIZADO_AGH);
			trigger = exameInternetStatusScheduler.enviarExamesNaoRealizadosFila(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_VALIDAPRONTUARIOSCHEDULER"://AutomaticJobEnum
			try {
				parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_TEMPO_VERIFICACAO_PRONTUARIO);

				if (parametro != null) {
					Integer minutos = parametro.getVlrNumerico().intValue();
					if (minutos != null) {
						cron = this.calculateCronExpression(idTarefa, "0 0/" + minutos +" * * * ?");
					}
				}
			} catch (ApplicationBusinessException e) {
				LOG.debug("Ocorreu um erro ao carregar o parâmetro P_AGHU_TEMPO_VERIFICACAO_PRONTUARIO.");
				cron = null;
			}
			
			if (cron == null) {
				cron = this.calculateCronExpression(idTarefa, SchedulerInit.EACH_TEN_MINUTE_CRON);
			}
			
			//
			trigger = aValidacaoProntuarioScheduler.validar(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_PROCESSAR_PEDIDOS_PAPELARIA"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ALL_DAY_22_45_CRON);
			//
			trigger = aPedidosPapelariaScheduler.processar(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		
		case "SCHEDULERINIT_IMPORTACAOARQRETORNOPREGAOBBSCHEDULER"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.EACH_FIFTEEN_MINUTE_CRON);
			trigger = importacaoArqRetornoPregaoBBScheduler.importarArqPregaoBBRetorno(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_PROCESSAR_OPME"://AutomaticJobEnum
			trigger = agendaProcessoOPME(idTarefa, servidorAgendador);
			break;
		case "SCHEDULERINIT_PROCESSAR_EXAME_INTERNET_SCHEDULER"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.EACH_30_SECS_CRON);
			trigger = processarExameInternetScheduler.processarExameInternet(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_NOTIF_GMR_AMBUL"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ENVIAR_EMAIL_PACIENTE_GMR);
			trigger = envioEmailPacienteGMRScheduler.enviarEmailPacienteGMR(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_FREQUENCIA_ENVIO_PENDENCIA"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.EACH_ONE_HOUR_CRON);
			trigger = this.repeticaoNotificacaoScheduler.notificarPendenciaTicket(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_FREQUENCIA_ENVIO_PENDENCIA_SUMARIZADA"://AutomaticJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ALL_DAY_08_CRON);
			trigger = this.enviaEmailNotificacaoAceitePendenteScheduler.enviaEmailNotificacaoAceitePendenteRetorno(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		case "SCHEDULERINIT_WHATSAPP_INTERNACOES_EXCEDENTES"://NotificacaoJobEnum
			cron = this.calculateCronExpression(idTarefa, SchedulerInit.ALL_DAY_22_45_CRON);
			trigger = mensageiroServiceScheduler.enviarWhatsappDeInternacoesExcedentes(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidorAgendador);
			break;
		default:
			throw new ApplicationBusinessException(MECScheduler.AghuSchedulerExceptionCode.ERRO_AO_AGENDAR_TAREFA);
		}
		
		return trigger;
	}
	
	private Trigger agendaProcessoOPME(IAutomaticJobEnum idTarefa, final RapServidores servidor) throws ApplicationBusinessException {
		String cron = this.calculateCronExpression(idTarefa, SchedulerInit.ALL_DAY_22_45_CRON);
		return this.monitoramentoProcessoAutorizacaoOpmeScheduler.processarAcionamentoProcessoAutorizacaoOPME(Calendar.getInstance().getTime(), cron, idTarefa.getTriggerName(), servidor);
	}
	
	private String calculateCronExpression(IAutomaticJobEnum tarefa, String cronDefault) {
		String cron = cronDefault;
		if (tarefa.getCron() != null) {
			cron = tarefa.getCron(); 
		}
		return cron;
	}

}
