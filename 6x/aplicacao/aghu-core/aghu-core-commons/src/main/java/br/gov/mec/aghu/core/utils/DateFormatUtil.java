package br.gov.mec.aghu.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Classe utilizada para ajudar nas formatações e conversoes de String e Date. Para
 * manipulacao de datas favor usar a classe DateUtil.
 * 
 * @author rcorvalao
 *
 */
public class DateFormatUtil {
	
	public final static String DATE_PATTERN_DDMMYYYY_HORA_MINUTO = "dd/MM/yyyy HH:mm";

	/**
	 * Utiliza o padrao EEE.<br>
	 * 
	 * Para um dia dia que cai na terca-feira, pode retornar: TER, TUE, etc...
	 * Conforme o locale default.<br>
	 * 
	 * @param d1
	 * @return
	 */
	public static String diaDaSemana(Date d1) {
		if (d1 == null) {
			return null;
		}
		LocalDateTime dt = new LocalDateTime(d1);
		String diaSemana = "";
		
		switch (dt.getDayOfWeek()) {
		case 0:
			diaSemana = "DOM";
			break;
		case 1:
			diaSemana = "SEG";	
			break;
		case 2:
			diaSemana = "TER";
			break;
		case 3:
			diaSemana = "QUA";
			break;
		case 4:
			diaSemana = "QUI";
			break;
		case 5:
			diaSemana = "SEX";
			break;
		case 6:
			diaSemana = "SAB";
			break;
		case 7:
			diaSemana = "DOM";
			break;
		default:
			break;
		}
		
		return diaSemana;
	}
	
	/**
	 * Utiliza o padrao dd/MM/yyyy.<br>
	 * 
	 * @return
	 */
	public static String fomataDiaMesAno(Date d1) {
		if (d1 == null) {
			return null;
		}
		LocalDateTime dt = new LocalDateTime(d1);
		DateTimeFormatter formatador = DateTimeFormat.forPattern("dd/MM/yyyy");
		
		return formatador.print(dt);
	}
	
	/**
	 *  Utiliza o padrao HH:mm.<br>
	 * 
	 * @param dthrProgramada
	 * @return
	 */
	public static String formataHoraMinuto(Date d1) {
		if (d1 == null) {
			return null;
		}
		LocalDateTime dt = new LocalDateTime(d1);
		DateTimeFormatter formatador = DateTimeFormat.forPattern("HH:mm");
		return formatador.print(dt);
	}
	
	
	/**
	 * Utiliza o padrão dd/MM/yy
	 * O ano tem apenas 2 digitos
	 * @param data
	 * @return
	 */
	public static String formataDiaMesAnoDoisDigitos(Date data) {
		if(data == null) {
			return null;
		}
		LocalDateTime dt = new LocalDateTime(data);
		DateTimeFormatter formatador = DateTimeFormat.forPattern("dd/MM/yy");
		return formatador.print(dt);
	}	
	
	
	/**
	 * Utiliza o padrão MM/yy
	 * O ano tem apenas 2 digitos
	 * @param data
	 * @return
	 */
	public static String formataMesAno(Date data) {
		if(data == null) {
			return null;
		}
		LocalDateTime dt = new LocalDateTime(data);
		DateTimeFormatter formatador = DateTimeFormat.forPattern("MM/yy");
		return formatador.print(dt);
	}
	
	
	/**
	 * Utiliza o padrão dd/MM
	 * @param data
	 * @return
	 */
	public static String formataDiaMes(Date data) {
		if(data == null) {
			return null;
		}
		LocalDateTime dt = new LocalDateTime(data);
		DateTimeFormatter formatador = DateTimeFormat.forPattern("dd/MM");
		return formatador.print(dt);
	}
	
	

	
	/**
	 * Utiliza o padrão dd_MM_yyyy
	 * @param data
	 * @return
	 */
	public static String formataDiaMesAnoParaNomeArquivo(Date data) {
		if(data == null) {
			return null;
		}
		LocalDateTime dt = new LocalDateTime(data);
		DateTimeFormatter formatador = DateTimeFormat.forPattern("dd_MM_yyyy");
		return formatador.print(dt);
	}
	
	/**
	 * Utiliza o padrão dd/MM/yyyy HH24:mm:si
	 * 
	 * @param date
	 * @return
	 */
	public static String formataTimeStamp(Date data) {
		if(data == null) {
			return null;
		}
		LocalDateTime dt = new LocalDateTime(data);
		DateTimeFormatter formatador = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
		return formatador.print(dt);
	}
	
	/**
	 * Retorna a data passada via paramêtro no formato
	 * dd/MM/yyyy HH:mm
	 * @return
	 */
	public static String obterDataFormatadaHoraMinutoSegundo(Date date){
		return obterDataFormatada(date, DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
	}
	
	/**
	 * Retorna a data atual no formato
	 * dd/MM/yyyy HH:mm
	 * @return
	 */
	public static String obterDataAtualHoraMinutoSegundo(){
		return obterDataFormatadaHoraMinutoSegundo(new Date());
	}
	
	/**
	 * Obter data formatada
	 * 
	 * @param data
	 * @param pattern 
	 * @return
	 */
	public static String obterDataFormatada(Date data, String pattern){
		if (data==null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat();
		if (pattern !=null){
			sdf.applyPattern(pattern);
		}
		return sdf.format(data);
	}
	
	/**
	 * Converte data em String.<br>
	 * 
	 * @param data
	 * @param pattern
	 * @return
	 */	
	public static String dataToString(Date data, String pattern){
		if (data==null){
			return "";
		}
		if (pattern==null){
			pattern="dd/MM/yyyy";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(data);
	}
	
	
}
