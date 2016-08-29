package br.gov.mec.aghu.business.scheduler;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.spi.JobFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.quartz.MECScheduler;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


@Singleton
@Startup
public class SchedulerInit {
	

	private static final Log LOG = LogFactory.getLog(SchedulerInit.class);
	
	/**
	 * Expressão que informa quando a tarefa deve ser executada. A execucao
	 * dessa job sera feita todo dia as 22:45.
	 */
	public static final String ALL_DAY_22_45_CRON = "0 45 22 * * ?";
	/**
	 * Expressão que informa quando a tarefa deve ser executada. A execucao
	 * dessa job sera feita todo Primeiro dia de cada mês as 6:00.
	 */
	public static final String ONCE_A_MONTH_CRON = "0 0 6 1 * ?";
	

	@SuppressWarnings("ucd")
	/**
	 * Expressão que informa quando a tarefa deve ser executada. A execucao
	 * dessa job sera feita a cada 5 minutos.
	 */
	public static final String EACH_FIVE_MINUTE_CRON = "0 0/5 * * * ?";
	
	/**
	 * Expressão que informa quando a tarefa deve ser executada. A execucao
	 * dessa job sera feita a cada 10 minutos.
	 */
	public static final String EACH_TEN_MINUTE_CRON = "0 0/10 * * * ?";
	
	/**
	 * Expressão que informa quando a tarefa deve ser executada. A execucao
	 * dessa job sera feita a cada 15 minutos.
	 */
	public static final String EACH_FIFTEEN_MINUTE_CRON = "0 0/15 * * * ?";
	
	/**
	 * Expressão que informa quando a tarefa deve ser executada. A execucao
	 * dessa job sera feita a cada 30 minutos.
	 */
	public static final String EACH_THIRTY_MINUTE_CRON = "0 0/30 * * * ?";
	
	public static final String EACH_ONE_HOUR_CRON = "0 0 0/1 * * ?";
	
	
	@SuppressWarnings("ucd")
	/**
	 * Expressão que informa quando a tarefa deve ser executada. A execucao
	 * dessa job sera feita a cada 1 minuto.
	 */	
	public static final String EACH_ONE_MINUTE_CRON = "0 0/1 * * * ?";
	/**
	 *  A execucao dessa job sera feita no ultimo dia de cada mes as 23:55.
	 */
	public static final String FECHAMENTO_MENSAL_ESTOQUE_CRON = "0 55 23 L * ? *";

	/**
	 *  A execucao dessa job sera feita todo Décimo Primeiroo dia de cada mês as 0:30 h.
	 */
	public static final String PROCESSAR_CUSTO_MENSAL_AUTOMATIZADO = "0 30 0 11 * ?";

	/**
	 * Trata-se de uma rotina que irá verificar se existe mensagem de comunicação do GESTam em lws_comunicacao.
	 * Será executada a cada X segundos
	 */
	public static final String PROCESSAR_GESTAO_AMOSTRA_CRON = "0/10 * * * * ?";

	/**
	 *  A execucao dessa job sera feita todos os dias as 22hs
	 */
	//public static final String PROCESSAR_EXAME_NAO_REALIZADO_AGH = "0 0 22 * * *";
	public static final String PROCESSAR_EXAME_NAO_REALIZADO_AGH = "0 0/5 * * * ?";
	
	public static final String EACH_FIVE_SECS_CRON = "0/5 * * * * ?";
	public static final String EACH_FIFTYTEEN_SECS_CRON = "0/15 * * * * ?";
	public static final String EACH_30_SECS_CRON = "0/30 * * * * ?";
	
	/**
	 * Expressão que informa quando a tarefa deve ser executada. A execucao
	 * dessa job sera feita todo dia as 08:00.
	 */
	public static final String ALL_DAY_08_CRON = "0 00 08 * * ?";
	
	/**
	 * Todos os dias às 07:00 horas.
	 */
	public static final String ENVIAR_EMAIL_PACIENTE_GMR = "0 0 7 * * ?";


	// s m h d M w
	// * * * * * * command to be executed
	// - - - - - -
	// | | | | | |
	// | | | | | +----- day of week (0 - 6) (Sunday=0)
	// | | | | +------- month (1 - 12)
	// | | | +--------- day of month (1 - 31)
	// | | +----------- hour (0 - 23)
	// | +------------- min (0 - 59)
	// +--------------- sec (0 - 59)

	// Examples:
	// Each day at 6:30 PM.
	// 30 18 * * *

	
	@EJB
	private AutomaticJobScheduler automaticJobScheduler;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private JobFactory cdiJobFactory;
	
	
	
	@PostConstruct
	public void start() {
		MECScheduler mecScheduler = MECScheduler.getInstance();
		mecScheduler.setJobFactory(cdiJobFactory);
		
		try {
			mecScheduler.start();
			
			
			/* http://sape.ebserh.gov.br/issues/74010
			 * Desativar o quartz na inicialização da aplicação. 

			*Causas*
			# Devido a vários problemas relatados de rotinas agendadas não  necessárias nos HU's. 
			# Também pelo fato do Quartz estar configuração de forma persistente no HU's não se faz necessário tentar agendar os processos toda a vez no start da aplicação.

			*Consequencias*
			# Os agendamentos devem ser feitos manualmente nos HU's, cada um com o seu conjunto de rotinas agendadas; Logo todas as equipes durante o processo de atualização para a versão 6.X deve garantir que o Quartz esta configurado corretamente e as rotinas agendadas corretamente para o HU em atualização.

			 */
			//agendarProcessosNegocio();
		} catch (BaseException e) {
			LOG.error("Erro no @PostConstruct SchedulerInit", e);
		}
	}


	@PreDestroy
	public void stop() {
		try {
			MECScheduler mecScheduler = MECScheduler.getInstance();
			mecScheduler.stop();
		} catch (Exception e) {
			LOG.error("Erro no @PreDestroy SchedulerInit", e);
		}
	}
	
	protected void agendarProcessosNegocio() throws ApplicationBusinessException, BaseException {
		LOG.info(" # Agendando Tarefas por AghJobDetail ...");
		
		AghParametros aghParametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_USUARIO_AGENDADOR);
		String usuarioAgendador = aghParametro.getVlrTexto();
		RapServidores servidorAgendador = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(usuarioAgendador, new Date());
		String nomeMicrocomputador = null; // TODO Verificar se deve ser passado null mesmo.
		
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_PARAMETROSISTEMA, AghuParametrosEnum.SCHEDULERINIT_PARAMETROSISTEMA, servidorAgendador, " # Agendando execução de Parametro de Sistema ...", nomeMicrocomputador);		
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_FECHAMENTOESTOQUEMENSAL, AghuParametrosEnum.SCHEDULERINIT_FECHAMENTOESTOQUEMENSAL, servidorAgendador, " # Agendando Estoque Mensal...", nomeMicrocomputador);
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_PROCESSARGESTAOAMOSTRA, AghuParametrosEnum.SCHEDULERINIT_PROCESSARGESTAOAMOSTRA, servidorAgendador, " # Agendando Interfaceamento exames...", nomeMicrocomputador);
		
		// Carga de indicadores possui duas Tarefas e um param de ativacao apenas
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_CARGAINDICADORES_GERAR, AghuParametrosEnum.SCHEDULERINIT_CARGAINDICADORES, servidorAgendador, " # Agendando execução da Carga de Indicadores - gerar ...", nomeMicrocomputador);
		
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_VERIFICACAOESCALAPROFISSIONAL, AghuParametrosEnum.SCHEDULERINIT_VERIFICACAOESCALAPROFISSIONAL, servidorAgendador, " # Agendando execução da Verificação de Escala de Profissionais...", nomeMicrocomputador);
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_CARGAINDICADORES_GRAVAR, AghuParametrosEnum.SCHEDULERINIT_CARGAINDICADORES, servidorAgendador, " # Agendando execução da Carga de Indicadores - gravar ...", nomeMicrocomputador);
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_GERARDADOSSUMARIOPRESCRICAOMEDICA, AghuParametrosEnum.SCHEDULERINIT_GERARDADOSSUMARIOPRESCRICAOMEDICA, servidorAgendador, " # Agendando geração de dados para o relatório do sumário de prescricao médica...", nomeMicrocomputador); 
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_GERARDADOSSUMARIOPRESCRICAOENFERMAGEM, AghuParametrosEnum.SCHEDULERINIT_GERARDADOSSUMARIOPRESCRICAOENFERMAGEM, servidorAgendador, " # Agendando geração de dados para o relatório do sumário de prescricao de enfermagem...", nomeMicrocomputador); 
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_ENCERRAMENTOAUTOMATICOCONTASHOSPITALARES, AghuParametrosEnum.SCHEDULERINIT_ENCERRAMENTOAUTOMATICOCONTASHOSPITALARES, servidorAgendador, " # Agendando Encerramento Automatico Contas Hospitalares...", nomeMicrocomputador); 
		this.agendarTarefa(AutomaticJobEnum.SCHED_INIT_REM_PERFISACESSOSISTEMACOMPENDENCIASBLOQUEANTES, AghuParametrosEnum.SCHED_INIT_REM_PERFISACESSOSISTEMACOMPENDENCIASBLOQUEANTES, servidorAgendador," # Agendando rotina de remoção de perfis de acesso ao sistema com pendências bloqueantes...", nomeMicrocomputador); 
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_REMOVERLOGSAPLICACAO, AghuParametrosEnum.SCHEDULERINIT_REMOVERLOGSAPLICACAO, servidorAgendador," # Agendando rotina de remoção de logs da aplicação...", nomeMicrocomputador); 
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_EFETIVARREQUISICAOMATERIALAUTOMATICA, AghuParametrosEnum.SCHEDULERINIT_EFETIVARREQUISICAOMATERIALAUTOMATICA, servidorAgendador," # Agendando rotina para efetivacao automatica diaria de requisicao de materiais...", nomeMicrocomputador);
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_GERACAO_AUTOMATICA_SOL_COMPRAS_MAT_ESTOCAVEL, AghuParametrosEnum.SCHEDULERINIT_GERACAO_AUTOMATICA_SOL_COMPRAS_MAT_ESTOCAVEL, servidorAgendador," # Agendando rotina para geração automática de solicitação de compras de material estocável...", nomeMicrocomputador);
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_VALIDAPRONTUARIOSCHEDULER, AghuParametrosEnum.SCHEDULERINIT_VALIDAPRONTUARIOSCHEDULER, servidorAgendador," # Agendando rotina para validar e atualizar o número do prontuário...", nomeMicrocomputador);
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_PROCESSAREXAMENAOREALIZADOSCHEDULER, AghuParametrosEnum.SCHEDULERINIT_PROCESSAREXAMENAOREALIZADOSCHEDULER, servidorAgendador, " # Agendando rotina para validar e atualizar o número do prontuário...", nomeMicrocomputador);
		//this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_PROCESSARCUSTODIARIOAUTOMATIZADO, AghuParametrosEnum.SCHEDULERINIT_PROCESSARCUSTOMENSALAUTOMATIZADO, servidorAgendador," # Agendando rotina para processamento mensal dos custos por atividade...", nomeMicrocomputador);
		
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_PROCESSAR_OPME, AghuParametrosEnum.SCHEDULERINIT_PROCESSAR_OPME, servidorAgendador, "# Agendando Processar o acionamento de processo de autorizao do OPME ...", nomeMicrocomputador);
		
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_IMPORTACAOARQRETORNOPREGAOBBSCHEDULER, AghuParametrosEnum.SCHEDULERINIT_IMPORTA_PAC, servidorAgendador," # Agendando rotina para processamento automático do arquivo de retorno das Licitações do Pregão BB...", nomeMicrocomputador);
		
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_FREQUENCIA_ENVIO_PENDENCIA, AghuParametrosEnum.SCHEDULERINIT_NOTIFICACAO_PENDENCIA_TICKET, servidorAgendador, " #Agendando rotina para notificação de pendência de ticket...", nomeMicrocomputador);
		//48321
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_FREQUENCIA_ENVIO_PENDENCIA_SUMARIZADA, AghuParametrosEnum.SCHEDULERINIT_FREQUENCIA_ENVIO_PENDENCIA_SUMARIZADA, servidorAgendador, " # Usado no agendamento inicial do sistema para rodar a verificação de quando enviar pendência sumarizada para os usuários...", nomeMicrocomputador);
		
		this.agendarTarefa(AutomaticJobEnum.SCHEDULERINIT_NOTIF_GMR_AMBUL, AghuParametrosEnum.SCHEDULERINIT_NOTIF_GMR_AMBUL, servidorAgendador," # Agendando processo de notificação por e-mail de consultas do ambulatório de pacientes portadores de GMR...", nomeMicrocomputador);
	}
	
	
	protected void agendarTarefa(IAutomaticJobEnum paramScheduler, AghuParametrosEnum paramAtivacao, RapServidores servidorAgendador,
			String messagemLog, String nomeMicrocomputador) throws BaseException {
		LOG.info(messagemLog);

		if (!ehParaAtivarRotina(paramAtivacao)) {
			LOG.info(" # Tarefa: " + paramScheduler + " - DESABILITADA.");
			return;
		}
		
		paramScheduler.setServidor(servidorAgendador);
		
		automaticJobScheduler.agendarTarefa(paramScheduler, nomeMicrocomputador, servidorAgendador);
	}
	
	/**
	 * Verifica o valor para o parametro informado.<br>
	 * <b>Regra basica: as rotinas estao desabilitadas.</b><br>
	 * Entao:
	 * <li>Qualquer coisa diferente de S no valor do parametro retorna False.</li>
	 * <li>Parametro nao cadastrado tambem retorna False.</li>
	 * 
	 * @param enumIdRotina
	 * @return true apenas quando o parametro estiver definido e tiver valor S.
	 */
	private boolean ehParaAtivarRotina(AghuParametrosEnum enumIdRotina) {
		boolean isAtivarRotina = false;

		Boolean existeParametro = parametroFacade.verificarExisteAghParametro(enumIdRotina);
		if (existeParametro) {
			AghParametros param = parametroFacade.getAghParametro(enumIdRotina);
			if (param != null) {
				Object paramSimOuNao = param.getValor();
				if (paramSimOuNao != null) {
					isAtivarRotina = "S".equalsIgnoreCase(paramSimOuNao.toString()); 
				}
			}	
		}

		return isAtivarRotina;
	}

}
