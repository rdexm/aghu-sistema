package br.gov.mec.aghu.core.utils;

import java.util.Calendar;
import java.util.Date;

public class DateMaker {
	
	
	/**
	 * Obtém uma data passando ano, mes, dia.<br>
	 * 
	 * @param ano
	 * @param mes - de 0 a 11.
	 * @param dia
	 * @return data
	 */
	public static Date obterData(int ano, int mes, int dia) {
		return obterData(ano, mes, dia, 0, 0);
	}
	
	
	/**
	 * Obtém uma data passando ano, mes, dia, hora e minuto.<br>
	 * 
	 * @param ano
	 * @param mes - de 0 a 11.
	 * @param dia
	 * @param hora
	 * @param minuto
	 * @return data
	 */
	public static Date obterData(int ano, int mes, int dia, int hora, int minuto) {
		Calendar c = Calendar.getInstance();
		c.clear();
		
		c.set(Calendar.YEAR, ano);
		c.set(Calendar.MONTH, mes);
		c.set(Calendar.DAY_OF_MONTH, dia);
		c.set(Calendar.HOUR_OF_DAY, hora);
		c.set(Calendar.MINUTE, minuto);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTime();
	}
	

}
