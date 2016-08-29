package br.gov.mec.aghu.core.quartz;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.TriggerBuilder;

import br.gov.mec.aghu.core.utils.DateUtil;

public class QuartzUtils {
	
	public static boolean isValidCronExpression(String cronExpression) {
		return CronExpression.isValidExpression(cronExpression);
	}
	
	/**
	 * As execucoes devem ser maiores que o periodo definido em <b>diffMinAceitavel</b>.<br>
	 * Se ocorrer algum erro na conversao da Cron, returna FALSE.<br>
	 * <b>diffMinAceitavel</b> valor em horas.
	 * 
	 * @param cronExpression
	 * @param diffMinAceitavel
	 * @return FALSE quando pelo menos um dos periodos entre as execucoes ficarem menor que <b>diffMinAceitavel</b>,
	 * caso contrario retorna TRUE.
	 */
	public static boolean isValidoPeriodoEntreExecucoes(String cronExpression, Integer diffMinAceitavel) {
		boolean returnValue = false;
		
		if (isValidCronExpression(cronExpression)) {
			
			CronTrigger trgCron =
					TriggerBuilder.newTrigger()
					.withIdentity("name", "group")
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
					.build();
			
			
			List<Date> dataReferenteUmCiclo = proximasExecucoes(trgCron);
			Date nextFireTime1, nextFireTime2;
			for (int index = 0; index <= (dataReferenteUmCiclo.size()-2); index++) {
				nextFireTime1 = dataReferenteUmCiclo.get(index);
				nextFireTime2 = dataReferenteUmCiclo.get(index+1);
				Integer diff = DateUtil.obterQtdHorasEntreDuasDatas(nextFireTime1, nextFireTime2);
				if (diff < diffMinAceitavel) {
					returnValue = false;
					break;
				}
			}
		}
		
		return returnValue;
	}
	
	/**
	 * Calcula as datas em que a Cron informada tentarah disparar o Job associado a ela.<br>
	 * Para um periodo de um ano.<br>
	 * 
	 * @param trgCron
	 * @return uma lista de datas.
	 */
	private static List<Date> proximasExecucoes(final CronTrigger trgCron) {
		Date dataInicial = new Date();
		// Adiciona um ano.
		Date dataFinal = DateUtil.adicionaDias(dataInicial, 366);
		
		List<Date> list = new LinkedList<Date>();
		Date nextFireTime = dataInicial;
		do {
			nextFireTime = trgCron.getFireTimeAfter(nextFireTime);
			list.add(nextFireTime);
		} while (nextFireTime.before(dataFinal));
		
		return list;
	}
	
	/**
	 * Transforma a data em um cron expression que represente a data.<br>
	 * Formato: 0 mi HH dd MM ? YYYY<br>
	 * 
	 * @param date
	 * @return
	 */
	public static String dataAsCronExpression(Date date, boolean includeYear) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		StringBuilder cron = new StringBuilder("0 "); 
		cron.append(calendar.get(Calendar.MINUTE));
		cron.append(" " + calendar.get(Calendar.HOUR_OF_DAY) + " ");
		cron.append(calendar.get(Calendar.DAY_OF_MONTH)); 
		cron.append(" " + (calendar.get(Calendar.MONTH)+1) + " ?"); 
		if (includeYear){
			cron.append(" " + calendar.get(Calendar.YEAR));
		}	
		return cron.toString();
	}
	
	public static String dataAsCronExpression(Date date) {
		return dataAsCronExpression(date, true);
	}
	
	
	public static String getJobDetailName(JobDetail job) {
		String name = null;
		
		if (job != null && job.getKey() != null) {
			name = job.getKey().getName();
		}
		
		return name;
	}
	
	public static String getJobDetailGroup(JobDetail job) {
		String name = null;
		
		if (job != null && job.getKey() != null) {
			name = job.getKey().getGroup();
		}
		
		return name;
	}


}
