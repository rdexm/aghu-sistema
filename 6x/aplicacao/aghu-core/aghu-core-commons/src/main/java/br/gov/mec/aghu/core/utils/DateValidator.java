package br.gov.mec.aghu.core.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DateValidator {
	
	/**
	 * Valida se a data passada por parâmetro é maior que a data atual, se for
	 * maior retorna verdadeiro, caso contrário retorna falso.
	 * 
	 * Data passada por parâmetros deve ser diferente de null.
	 * 
	 * bsoliveira 21/09/2010
	 * 
	 * @param {Date} d
	 * @return {boolean} Se é maior ou não.
	 */
	public static boolean validaDataMaiorQueAtual(Date d) {
		boolean response = false;
		Date atual = new Date();

		if (d != null && d.compareTo(atual) > 0) {
			response = true;
		}

		return response;
	}

	/**
	 * Valida se a data d passada por parâmetro é maior que a data anotherd, se
	 * for maior retorna verdadeiro, caso contrário retorna falso.
	 * 
	 * Ambas as datas devem ser diferentes de null.
	 * 
	 * bsoliveira 28/09/2010
	 * 
	 * @param {Date} d
	 * @param {Date} anotherd
	 * @return {boolean} Se é maior ou não.
	 */
	public static boolean validaDataMaior(Date d, Date anotherd) {
		boolean response = false;

		if (d != null && anotherd != null && d.compareTo(anotherd) > 0) {
			response = true;
		}

		return response;
	}

	/**
	 * Valida se a data d passada por parâmetro é maior ou igual que a data
	 * anotherd, se for maior ou igual retorna verdadeiro, caso contrário
	 * retorna falso.
	 * 
	 * Ambas as datas devem ser diferentes de null.
	 * 
	 * bsoliveira 28/09/2010
	 * 
	 * @param {Date} d
	 * @param {Date} anotherd
	 * @return {boolean} Se é maior/igual ou não.
	 */
	public static boolean validaDataMaiorIgual(Date d, Date anotherd) {
		boolean response = false;

		if (d != null
				&& anotherd != null
				&& ((d.compareTo(anotherd) > 0) || (d.compareTo(anotherd) == 0))) {
			response = true;
		}

		return response;
	}

	/**
	 * Valida se a data d passada por parâmetro é maior ou igual que a data
	 * anotherd (APENAS COMPARA A DATA SEM A HORA), se for maior ou igual
	 * retorna verdadeiro, caso contrário retorna falso.
	 * 
	 * Ambas as datas devem ser diferentes de null.
	 * 
	 * bsoliveira 28/09/2010
	 * 
	 * @param {Date} d
	 * @param {Date} anotherd
	 * @return {boolean} Se é maior/igual ou não.
	 */
	public static boolean validaDataTruncadaMaiorIgual(Date d, Date anotherd) {
		boolean response = false;

		if (d != null && anotherd != null) {
			Calendar d1 = Calendar.getInstance();
			d1.setTime(d);
			DateUtil.zeraHorario(d1);
			d = d1.getTime();

			Calendar anotherd1 = Calendar.getInstance();
			anotherd1.setTime(anotherd);
			DateUtil.zeraHorario(anotherd1);
			anotherd = anotherd1.getTime();

			if (d.compareTo(anotherd) > 0 || d.compareTo(anotherd) == 0) {
				response = true;
			}
		}

		return response;
	}

	/**
	 * Valida se a data d passada por parâmetro é maior que a data anotherd
	 * (APENAS COMPARA A DATA SEM A HORA), se for maior retorna verdadeiro, caso
	 * contrário retorna falso.
	 * 
	 * Ambas as datas devem ser diferentes de null.
	 * 
	 * bsoliveira 19/10/2010
	 * 
	 * @param {Date} d
	 * @param {Date} anotherd
	 * @return {boolean} Se é maior ou não.
	 */
	public static boolean validaDataTruncadaMaior(Date d, Date anotherd) {
		boolean response = false;

		if (d != null && anotherd != null) {
			Calendar d1 = Calendar.getInstance();
			d1.setTime(d);
			DateUtil.zeraHorario(d1);
			d = d1.getTime();

			Calendar anotherd1 = Calendar.getInstance();
			anotherd1.setTime(anotherd);
			DateUtil.zeraHorario(anotherd1);
			anotherd = anotherd1.getTime();

			if (d.compareTo(anotherd) > 0) {
				response = true;
			}
		}

		return response;
	}

	/**
	 * Valida se a data passada por parâmetro é menor que a data atual, se for
	 * menor retorna verdadeiro, caso contrário retorna falso.
	 * 
	 * Data passada por parâmetros deve ser diferente de null.
	 * 
	 * bsoliveira 21/09/2010
	 * 
	 * @param {Date} d
	 * @return {boolean} Se é menor ou não.
	 */
	public static boolean validaDataMenorQueAtual(Date d) {
		boolean response = false;
		Date atual = new Date();

		if (d != null && d.compareTo(atual) < 0) {
			response = true;
		}

		return response;
	}

	/**
	 * Valida se a data d passada por parâmetro é menor que a data anotherd, se
	 * for menor retorna verdadeiro, caso contrário retorna falso.
	 * 
	 * Ambas as datas devem ser diferentes de null.
	 * 
	 * bsoliveira 28/09/2010
	 * 
	 * @param {Date} d
	 * @param {Date} anotherd
	 * @return {boolean} Se é menor ou não.
	 */
	public static boolean validaDataMenor(Date d, Date anotherd) {
		boolean response = false;

		if (d != null && anotherd != null && d.compareTo(anotherd) < 0) {
			response = true;
		}

		return response;
	}

	/**
	 * Valida se a data d passada por parâmetro é menor ou igual que a data
	 * anotherd, se for menor ou igual retorna verdadeiro, caso contrário
	 * retorna falso.
	 * 
	 * Ambas as datas devem ser diferentes de null.
	 * 
	 * @author rcorvalao
	 * 
	 * @param {Date} d
	 * @param {Date} anotherd
	 * @return {boolean} Se é menor/igual ou não.
	 */
	public static boolean validaDataMenorIgual(Date d, Date anotherd) {
		boolean response = false;

		if (d != null
				&& anotherd != null
				&& ((d.compareTo(anotherd) < 0) || (d.compareTo(anotherd) == 0))) {
			response = true;
		}

		return response;
	}

	/**
	 * Compara apenas Hora e Minuto das datas.<br>
	 * 
	 * Valida se a data dtHr1 passada por parâmetro é menor ou igual que a data
	 * dtHr2, se for menor ou igual retorna verdadeiro, caso contrário
	 * retorna falso.<br>
	 * 
	 * Ambas as datas devem ser diferentes de null.<br>
	 * 
	 * @param dtHr1
	 * @param dtHr2
	 * @return
	 */
	public static boolean validaHoraMenorIgual(Date dtHr1, Date dtHr2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(dtHr1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(dtHr2);
		
		// deixa apenas hora e minuto original
		c1.set(Calendar.YEAR, 2011);
		c1.set(Calendar.MONTH, 1);
		c1.set(Calendar.DAY_OF_MONTH, 1);
		c1.set(Calendar.SECOND, 0);
		c1.set(Calendar.MILLISECOND, 0);
		
		c2.set(Calendar.YEAR, 2011);
		c2.set(Calendar.MONTH, 1);
		c2.set(Calendar.DAY_OF_MONTH, 1);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		
		return validaDataMenorIgual(c1.getTime(), c2.getTime());
	}
	
	/**
	 * Compara apenas Hora e Minuto das datas.<br>
	 * 
	 * Valida se a data d passada por parâmetro é maior que a data anotherd, se
	 * for maior retorna verdadeiro, caso contrário retorna falso.<br>
	 * 
	 * Ambas as datas devem ser diferentes de null.<br>
	 * 
	 * @param dtHr1
	 * @param dtHr2
	 * @return
	 */
	public static boolean validaHoraMaior(Date dtHr1, Date dtHr2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(dtHr1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(dtHr2);
		
		// deixa apenas hora e minuto original
		c1.set(Calendar.YEAR, 2011);
		c1.set(Calendar.MONTH, 1);
		c1.set(Calendar.DAY_OF_MONTH, 1);
		c1.set(Calendar.SECOND, 0);
		c1.set(Calendar.MILLISECOND, 0);
		
		c2.set(Calendar.YEAR, 2011);
		c2.set(Calendar.MONTH, 1);
		c2.set(Calendar.DAY_OF_MONTH, 1);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		
		return validaDataMaior(c1.getTime(), c2.getTime());
	}

	/**
	 * Valida se duas datas estão no mesmo dia (ingora horário)
	 * 
	 * @param d1
	 * @param d2
	 * @return true ou false
	 */
	public static boolean validarMesmoDia(Date d1, Date d2) {
		Calendar d1C = Calendar.getInstance();
		d1C.setTime(d1);
		Calendar d2C = Calendar.getInstance();
		d2C.setTime(d2);

		for (int field : Arrays.asList(Calendar.YEAR, Calendar.MONTH,
				Calendar.DAY_OF_MONTH)) {
			if (d1C.get(field) != d2C.get(field)) {
				return false;
			}
		}

		return true;
	}
	
}
