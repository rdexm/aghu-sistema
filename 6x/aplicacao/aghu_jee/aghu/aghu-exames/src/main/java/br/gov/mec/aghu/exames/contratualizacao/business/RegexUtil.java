package br.gov.mec.aghu.exames.contratualizacao.business;

import java.util.Calendar;
import java.util.Date;

import br.gov.mec.aghu.core.utils.DateUtil;

public class RegexUtil {
	
	/* Valida a data no formato ddmmyyyy
	 * (			#start of group #1
			 0?[1-9]		#  01-09 or 1-9
			 |                  	#  ..or
			 [12][0-9]		#  10-19 or 20-29
			 |			#  ..or
			 3[01]			#  30, 31
			) 			#end of group #1
			   (			#    start of group #2
			    0?[1-9]		#	01-09 or 1-9
			    |			#	..or
			    1[012]		#	10,11,12
			    )			#    end of group #2
			      (			#	  start of group #3
			       (19|20)\\d\\d	#	    19[0-9][0-9] or 20[0-9][0-9]
		       )		#	  end of group #3*/
	public static final String DATE_PATTERN = "(0?[1-9]|[12][0-9]|3[01])(0?[1-9]|1[012])((19|20)\\d\\d)";	

	/*Valida a hora no formato  hhmmss
	 * (				#start of group #1
			 [01]?[0-9]			#  start with 0-9,1-9,00-09,10-19
			 |				#  or
			 2[0-3]				#  start with 20-23
			)				#end of group #1
			  [0-5][0-9]			#      follw by 0..5 and 0..9, which means 00 to 59*/
	public static final String HOUR_PATTERN_HHMMSS = "([01]?[0-9]|2[0-3])[0-5][0-9][0-5][0-9]"; 

	/*Valida a hora no formato  hhmm
	 * (				#start of group #1
			 [01]?[0-9]			#  start with 0-9,1-9,00-09,10-19
			 |				#  or
			 2[0-3]				#  start with 20-23
			)				#end of group #1*/
	public static final String HOUR_PATTERN_HHMM = "([01]?[0-9]|2[0-3])[0-5][0-9]";

	/**
	 * Primeiro testa o padrão da data, conforme os regex contidos nesta classe.
	 * Depois Valida se o dia não é maior do que um dia possível em um mês.
	 * @param date
	 * @return
	 */
	public static Boolean validarDias(String date) { 
		if (date.length() > 7) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, Integer.valueOf(date.substring(2,4)) - 1);
			cal.set(Calendar.YEAR, Integer.valueOf(date.substring(4,8)));

			Date ultimoDia = DateUtil.obterUltimoDiaDoMes(cal.getTime());
			cal.setTime(ultimoDia);

			return !(Integer.parseInt(date.substring(0,2)) > cal.get(Calendar.DAY_OF_MONTH));
		}
		return false;
	}

}
