package br.gov.mec.aghu.business.scheduler;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public enum SchedulerRNExceptionCode implements BusinessExceptionCode {
	ERRO_PROCESSO_AGENDADO_INTERVALO_PROCESSAMENTO
	, ERRO_REAGENDAMENTO_TRIGGER_NAO_ENCONTRADA
	, ERRO_REAGENDAMENTO_CRON_EXPRESSION
	, ERRO_AO_REAGENDAR_TAREFA
	, MSG_REAGENTAMENTO_JOB_JAH_FINALIZADO
	, ERRO_LIMITE_MIN_INTERVALOS_ENTRE_AGENDAMENTO
	;
}
