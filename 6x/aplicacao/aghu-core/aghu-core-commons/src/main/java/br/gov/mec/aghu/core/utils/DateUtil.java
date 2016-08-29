package br.gov.mec.aghu.core.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Classe utilizada para ajudar nas manipulação e calculos com datas. Para
 * formatação de datas favor usar outra classe.
 * 
 * @author rcorvalao
 *
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class DateUtil {
	
	
	/**
	 * Retorna a idade do paciente em anos
	 * 
	 * @param dataNascimento
	 * @return
	 */
	public static Integer getIdade(Date dataNascimento) {
		if (dataNascimento == null) {
			return null;
		}
		Period period = new Period(dataNascimento.getTime(), Calendar
				.getInstance().getTimeInMillis(), PeriodType.years());
	
		return period.getYears();
	}
	
	/**
	 * Retorna a idade do paciente em meses
	 * 
	 * @param dataNascimento
	 * @return
	 */
	public static Integer getIdadeMeses(Date dataNascimento) {
		if (dataNascimento == null) {
			return null;
		}

		Period period = new Period(dataNascimento.getTime(), Calendar
				.getInstance().getTimeInMillis(), PeriodType.months());
		return period.getMonths();
	}
	
	/**
	 * Retorna a idade do paciente em dias
	 * 
	 * @param dataNascimento
	 * @return
	 */
	public static Integer getIdadeDias(Date dataNascimento) {
		if (dataNascimento == null) {
			return null;
		}

		Period period = new Period(dataNascimento.getTime(), Calendar
				.getInstance().getTimeInMillis(), PeriodType.days());
		return period.getDays();		
	}
	
	
	/**
	 * Faz o trunc da data. Desconsiderando dia, mês e ano.<br>
	 * Seta o dia, mes e ano para a data atual.
	 * 
	 * @param date
	 * @return 
	 */
	public static Date truncaHorario(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		
		Calendar dateAux = Calendar.getInstance();
		
		c1.set(Calendar.DAY_OF_MONTH, dateAux.get(Calendar.DAY_OF_MONTH));
		c1.set(Calendar.MONTH, dateAux.get(Calendar.MONTH));
		c1.set(Calendar.YEAR, dateAux.get(Calendar.YEAR));
		
		return c1.getTime();
	}
	
	/**
	 * Seta o horário para zero (hora, minuto, segundo e milisegundo)
	 * @param calendar
	 */
	public static void zeraHorario(Calendar calendar) {
		if(calendar != null){
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
	}
	
	/**
	 * Seta a hora da data informada como parametro para a primeira hora do dia.
	 * Caso o parametro seja nulo, retorna a data atual na sua hora inicial.
	 * 
	 * rcorvalao 01/09/2010
	 * 
	 * @param d
	 * @return uma data com a hora inicial do dia.
	 */
	public static Date obterDataComHoraInical(Date d) {
		if (d == null) {
			d = new Date();
		}
		Date returnValue;

		LocalDate dataTemp = new LocalDate(d);
		DateTime dt = dataTemp.toDateTimeAtStartOfDay();
		returnValue = dt.toDate();

		// caso seja o dia da troca do Horario de Verao
		if (dt.getHourOfDay() > 0) {
			// utiliza a ultima hora do dia anterior
			dt = dt.plusDays(-1);
			returnValue = DateUtil.obterDataComHoraFinal(dt.toDate());
		}

		return returnValue;
	}

	/**
	 * Seta a hora da data informada como parametro para o ultima hora do dia.
	 * Caso o parametro seja nulo, retorna a data atual na sua hora final.
	 * 
	 * rcorvalao 01/09/2010
	 * 
	 * @param d
	 * @return uma data com a hora final do dia.
	 */
	public static Date obterDataComHoraFinal(Date d) {
		if (d == null) {
			d = new Date();
		}
		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);

		calendarioTemp.set(Calendar.HOUR_OF_DAY, 23);
		calendarioTemp.set(Calendar.MINUTE, 59);
		calendarioTemp.set(Calendar.SECOND, 59);
		calendarioTemp.set(Calendar.MILLISECOND, 999);

		return calendarioTemp.getTime();
	}
	
	/**
	 * Soma a quantidade de minutos desejada, para data informada. 
	 * Caso o parametro seja nulo, retorna a data atual 
	 * adicionando a quantidade de horas informados.
	 * 
	 * @param d
	 * @param qtHoras
	 * @return
	 */
	public static Date adicionaMinutos(Date d, int qtdMinutos) {
		if (d == null) {
			d = new Date();
		}

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.MINUTE, qtdMinutos);

		return calendarioTemp.getTime();
	}
	
	/**
	 * Soma a quantidade (qtd) desejada, para data informada. 
	 * Caso o parametro data (d) seja nulo, utiliza a data atual. 
	 * Adicionando a quantidade informados no Field second da data.
	 * 
	 * @param d
	 * @param qtd
	 * @return
	 */
	public static Date adicionaSegundos(Date d, int qtd) {
		if (d == null) {
			d = new Date();
		}
		
		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.SECOND, qtd);

		return calendarioTemp.getTime();
	}

	/**
	 * Soma a quantidade de horas desejada, para data informada. Caso o parametro
	 * seja nulo, retorna a data atual adicionando a quantidade de horas
	 * informados.
	 * 
	 * @param d
	 * @param qtHoras
	 * @return
	 */
	public static Date adicionaHoras(Date d, int qtHoras) {
		if (d == null) {
			d = new Date();
		}

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.HOUR_OF_DAY, qtHoras);

		return calendarioTemp.getTime();
	}
	
	/**
	 * Soma a quantidade de dias desejada, para data informada. Caso o parametro
	 * seja nulo, retorna a data atual adicionando a quantidade de dias
	 * informados.
	 * 
	 * @param d
	 * @param qtDias
	 */
	public static Date adicionaDias(Date d, final Integer qtDias) {
		if (d == null) {
			d = new Date();
		}

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, qtDias);

		return calendarioTemp.getTime();
	}
	
	/**
	 * Soma a quantidade de meses desejadas, para data informada. Caso o parametro
	 * seja nulo, retorna a data atual adicionando a quantidade de meses
	 * informados.
	 * 
	 * @param d
	 * @param qtDias
	 */
	public static Date adicionaMeses(Date d, final Integer qtMeses) {
		if (d == null) {
			d = new Date();
		}

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.MONTH, qtMeses);

		return calendarioTemp.getTime();
	}

	public static Date adicionaDiasFracao(Date d, final Float fracaoDias) {
		if (d == null) {
			d = new Date();
		}

		int segundos = Math.round(fracaoDias * 24 * 60 * 60);
	

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.SECOND, segundos);

		return calendarioTemp.getTime();
	}
	
	/**
     * Representa a function LAST_DAY do oracle. 
     * Irá retornar o último dia do mês. 
     * 
     * @param date data representando o mês a ser processado. Caso null instancia uma nova data. 
     * 
     * @return Último dia do mês 
     */
	public static Date obterUltimoDiaDoMes(final Date date) {
		final Calendar calendar = Calendar.getInstance(); 
		
		if (date != null) {
		   calendar.setTime(date);		     
		} else { 
		   calendar.setTime(new Date()); 
		} 
		
		int lastDate = calendar.getActualMaximum(Calendar.DATE); 
		calendar.set(Calendar.DATE, lastDate); 
		
		return calendar.getTime(); 
	}
	


	/**
	 * Retorna a data atual se o parâmetro fornecido for null.
	 * 
	 * @author cvagheti 30/09/2010
	 * 
	 * @param dt
	 * @return a mesma instância fornecida ou data atual se null
	 */
	public static Date hojeSeNull(Date dt) {
		return dt == null ? new Date() : dt;
	}

	/**
	 * Faz uma subtracao entre as datas. Considerando a ordem dos parametros.<br>
	 * <b>Assim retornar um numero INTEIRO, posito ou negativo.</b><br>
	 * Uma subtração é representada por: <b>A - B = C</b> , 0nde: A = minuendo,
	 * B = subtraendo e C = diferença ou resto.<br>
	 * 
	 * dtInicio - dtFim = result 
	 * 
	 * @param dtInicio
	 * @param dtFim
	 * @return a diferenca inteira entre dtInicio e dtFim.
	 */
	public static Integer diffInDaysInteger(Date dtInicio, Date dtFim) {
		if (dtInicio == null || dtFim == null) {
			throw new IllegalArgumentException("Invalid Parameter!!!");
		}

		int returnValue = 0;
		if (!dtInicio.equals(dtFim)) {
			Calendar cInicial = Calendar.getInstance();
			Calendar cFinal = Calendar.getInstance();

			cInicial.setTime(dtInicio);
			cFinal.setTime(dtFim);

			float diferenca = cInicial.getTimeInMillis()
					- cFinal.getTimeInMillis();

			// Quantidade de milissegundos em um dia
			int tempoDia = 1000 * 60 * 60 * 24;

			Float diff = diferenca / tempoDia;
			returnValue = diff.intValue();
		}

		return returnValue;
	}

	/**
	 * Faz uma subtracao entre as datas. Considerando a maior das duas data como minuendo.<br>
	 * <b>Assim sempre retornar um numero maior igual a zero.</b><br>
	 * Uma subtração é representada por: <b>A - B = C</b> , 0nde: A = minuendo,
	 * B = subtraendo e C = diferença ou resto.<br>
	 * 
	 * @param dtInicio
	 * @param dtFim
	 * @return a diferenca fracionario entre dtInicio e dtFim.
	 */
	public static Float diffInDays(Date dtInicio, Date dtFim) {
		if (dtInicio == null || dtFim == null) {
			throw new IllegalArgumentException("Invalid Parameter!!!");
		}

		float returnValue = 0;
		if (!dtInicio.equals(dtFim)) {
			Calendar cInicial = Calendar.getInstance();
			Calendar cFinal = Calendar.getInstance();

			cInicial.setTime(dtInicio);
			cFinal.setTime(dtFim);
			if (dtInicio.after(dtFim)) {
				cInicial.setTime(dtFim);
				cFinal.setTime(dtInicio);
			}

			float diferenca = cFinal.getTimeInMillis()
					- cInicial.getTimeInMillis();

			// Quantidade de milissegundos em um dia
			int tempoDia = 1000 * 60 * 60 * 24;

			returnValue = diferenca / tempoDia;
		}

		return Float.valueOf(returnValue);
	}
	
	/**
	 * Faz uma subtracao entre as datas retornando o n. meses. 
	 * cqsilva 10/06/2011
	 * 
	 * @param dtInicio
	 * @param dtFim
	 * @return a diferenca em int .
	 */
	public static int difMeses(Date dt1, Date dt2){
		if (dt1==null || dt2==null){
			return 0;
		}
		Calendar data1 = Calendar.getInstance();data1.setTime(dt1);
		Calendar data2 = Calendar.getInstance();data2.setTime(dt2);
		
		return data1.get(Calendar.MONTH) - data2.get(Calendar.MONTH);
		
	}
	
	/**
	 * Retorna um novo objeto Date compondo o dia de um e a hora de outro
	 * 
	 * @param dia
	 * @param hora
	 * @return dia e hora
	 */
	public static Date comporDiaHora(Date dia, Date hora) {
		Calendar diaC = Calendar.getInstance();
		diaC.setTime(dia);

		Calendar horaC = Calendar.getInstance();
		horaC.setTime(hora);

		for (int field : Arrays.asList(Calendar.HOUR_OF_DAY, Calendar.MINUTE,
				Calendar.SECOND, Calendar.MILLISECOND)) {
			diaC.set(field, horaC.get(field));
		}

		return diaC.getTime();
	}

	

	public static Boolean entre(Date dataComparacao, Date dataInicial,
			Date dataFinal) {
		return (dataInicial.getTime() <= dataComparacao.getTime() && dataFinal
				.getTime() >= dataComparacao.getTime());

	}

	/**
	 * Método para calcular diferença em dias entre duas datas. A lógica
	 * utilizada é igual a subtração de datas no Oracle (calcula o numero de
	 * dias de diferença, arredondando para cima se a hora for maior que 12hs e
	 * para baixo caso seja menor que 12hs).
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static int calcularDiasEntreDatas(Date dataInicial, Date dataFinal) {
		int resultado = 0;

		if (dataInicial != null && dataFinal != null) {
			BigDecimal diferencaExata = calcularDiasEntreDatasComPrecisao(dataInicial, dataFinal);
			resultado = (int) Math.round(diferencaExata.doubleValue());
		}
		return resultado;
	}

	/**
	 * Método para calcular diferença em dias entre duas datas com precisão de
	 * casas decimais.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static BigDecimal calcularDiasEntreDatasComPrecisao(Date dataInicial, Date dataFinal) {
		BigDecimal resultado = BigDecimal.valueOf(0);

		if (dataInicial != null && dataFinal != null) {
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(dataInicial);
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(dataFinal);

			Long diferenca = cal2.getTimeInMillis() - cal1.getTimeInMillis();
			BigDecimal diferencaExata = BigDecimal.valueOf(diferenca)
					.divide(BigDecimal.valueOf(1000), 2,
							BigDecimal.ROUND_HALF_UP)
					.divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP)
					.divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP)
					.divide(BigDecimal.valueOf(24), 2, BigDecimal.ROUND_HALF_UP);
			resultado = diferencaExata;
		}
		return resultado;
	}

	



	/**
	 * Método para calcular diferença em dias entre duas datas com precisão de
	 * casas decimais.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @param escalaPrecisaoQuociente
	 * @return
	 */
	public static BigDecimal calcularDiasEntreDatasComPrecisao(
			Date dataInicial, Date dataFinal, Integer escalaPrecisaoQuociente) {
		BigDecimal resultado = BigDecimal.valueOf(0);

		if (dataInicial != null && dataFinal != null) {
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(dataInicial);
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(dataFinal);

			Long diferenca = cal2.getTimeInMillis() - cal1.getTimeInMillis();
			BigDecimal diferencaExata = BigDecimal
					.valueOf(diferenca)
					.divide(BigDecimal.valueOf(1000), escalaPrecisaoQuociente,
							BigDecimal.ROUND_HALF_UP)
					.divide(BigDecimal.valueOf(60), escalaPrecisaoQuociente, BigDecimal.ROUND_HALF_UP)
					.divide(BigDecimal.valueOf(60), escalaPrecisaoQuociente, BigDecimal.ROUND_HALF_UP)
					.divide(BigDecimal.valueOf(24), escalaPrecisaoQuociente, BigDecimal.ROUND_HALF_UP);
			resultado = diferencaExata;
		}
		return resultado;
	}	
	
	
	/**
	 * Se data {dataComparacao} estiver entre as datas {dataInicial} e
	 * {dataFinal} retorna verdadeiro, senão retorna falso.
	 * 
	 * Atentar que a comparação é feita com <= e >=.
	 * 
	 * bsoliveira - 11/11/2010
	 * 
	 * @param {Date} dataComparacao
	 * @param {Date} dataInicial
	 * @param {Date} dataFinal
	 * @return Boolean
	 */
	public static Boolean entreTruncado(Date dataComparacao, Date dataInicial,
			Date dataFinal) {
		dataComparacao = truncaData(dataComparacao);
		dataInicial = truncaData(dataInicial);
		dataFinal = truncaData(dataFinal);

		return (dataInicial.getTime() <= dataComparacao.getTime() && dataFinal
				.getTime() >= dataComparacao.getTime());
	}
	
	/**
	 * Truca data, ou seja, zera a hora de uma data Retorna data com a hora
	 * zerada.
	 * 
	 * bsoliveira 02/11/2010
	 * 
	 * @param {Date} d
	 * @return {Date} Data com hora zerada.
	 */
	public static Date truncaData(Date d) {
		Date response = null;

		if (d != null) {

			Calendar d1 = Calendar.getInstance();
			d1.setTime(d);
			zeraHorario(d1);
			response = d1.getTime();
		}

		return response;
	}
	
	public static Date truncaDataFim(Date d) {
		Date response = null;

		if (d != null) {
			Calendar d1 = Calendar.getInstance();
			d1.setTime(d);
			d1.set(Calendar.HOUR, 23);
			d1.set(Calendar.HOUR_OF_DAY, 23);
			d1.set(Calendar.MINUTE, 59);
			d1.set(Calendar.SECOND, 59);
			d1.set(Calendar.MILLISECOND, 0);
			response = d1.getTime();
		}

		return response;
	}
	

	/**
	 * Retorna a quatidade de dia do mês.
	 * 
	 * @param date
	 * @return
	 */
	public static Integer obterQtdeDiasMes(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	

	
	/**
	 * Método para comparar duas datas evitando nullPointerException
	 * 
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static Boolean isDatasIguais(Date data1, Date data2) {
		if(data1 == null && data2 == null){
			return true;
		}
		Boolean retorno = false;
		if (data1 != null && data2 != null) {
			retorno = data1.compareTo(data2) == 0;
		}
		return retorno;
	}

	public static boolean isFinalSemana(Date dtTemp) {
		LocalDateTime dt = new LocalDateTime(dtTemp);
		
		return (
			dt.dayOfWeek().get() == DateTimeConstants.SUNDAY
			|| dt.dayOfWeek().get() == DateTimeConstants.SATURDAY
		);
	}
	
	
	
	
	
	/**
	 * Retorna a hora da data d, no formato HH24.
	 * @param d
	 * @return
	 */
	public static Integer getHoras(Date d) {
		LocalDateTime local = new LocalDateTime(d.getTime());
		
		DateTimeFormatter formatador = DateTimeFormat.forPattern("HH");
		
		String hora = formatador.print(local);
		
		return Integer.valueOf(hora);
	}
	
	/**
	 * Retorna os minutos da data d, no formato mm.
	 * @param d
	 * @return
	 */
	public static Double getMinutos(Date d) {
		LocalDateTime local = new LocalDateTime(d.getTime());
		
		DateTimeFormatter formatador = DateTimeFormat.forPattern("HH:mm");
		
		String horas = formatador.print(local);
		String minutos = horas.split(":")[1];
		
		return Double.valueOf(minutos);
	}
	
	/**
	 * Retorna uma instancia de calendar com o a data passada via param
	 * caso o param seja nulo é considerado a data atual
	 * @param date
	 * @return
	 */
	public static Calendar getCalendarBy(Date date){
		if(date==null) {
			date =new Date();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	/** 
     * Retorna uma data representando o último dia do mês anterior.<br>
     *  
     * @param date - data atual
     * @return - data representando o último dia do mês anterior. 
     */ 
    public static Date obterDataComUltimoDiaMesAnterior(Date date) { 
        if (date == null) {
        	date = new Date(); 
        } 
        
        Calendar calendarioTemp = Calendar.getInstance(); 
        calendarioTemp.setTime(date); 
        calendarioTemp.add(Calendar.MONTH, -1); 
        calendarioTemp.set(Calendar.DAY_OF_MONTH, calendarioTemp.getActualMaximum(Calendar.DAY_OF_MONTH)); 
        
        return calendarioTemp.getTime(); 
    }
    
	/**
	 * Obtem a quantidade de dias entre duas datas.<br>
	 * 
	 * <b>dataInicial < dataFinal</b>, para diferenca ser positiva. Caso contrario a diferenca serah negativa.<br>
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static Integer obterQtdDiasEntreDuasDatas(Date dataInicial, Date dataFinal) {
		DateTime dtInicial = new DateTime(dataInicial);
		DateTime dtFinal = new DateTime(dataFinal);
		Days dias = Days.daysBetween(dtInicial, dtFinal);
		return dias.getDays();
	}

	
	/**
	 * Obtem a quantidade de horas entre duas datas.<br>
	 * 
	 * <b>dataInicial < dataFinal</b>, para diferenca ser positiva. Caso contrario a diferenca serah negativa.<br>
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static Integer obterQtdHorasEntreDuasDatas(Date dataInicial, Date dataFinal) {
		DateTime dtInicial = new DateTime(dataInicial);
		DateTime dtFinal = new DateTime(dataFinal);
		
		Hours horas = Hours.hoursBetween(dtInicial, dtFinal);
		
		return horas.getHours();
	}
	
	/**
	 * Obtem a quantidade de dias entre duas datas
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static Integer obterQtdDiasEntreDuasDatasTruncadas(Date dataInicial, Date dataFinal) {
		
		Calendar d1 = Calendar.getInstance();
		d1.setTime(dataInicial);
		DateUtil.zeraHorario(d1);
		dataInicial = d1.getTime();

		Calendar anotherd1 = Calendar.getInstance();
		anotherd1.setTime(dataFinal);
		DateUtil.zeraHorario(anotherd1);
		dataFinal = anotherd1.getTime();
		
		DateTime dtInicial = new DateTime(dataInicial);
		DateTime dtFinal = new DateTime(dataFinal);
		Days dias = Days.daysBetween(dtInicial, dtFinal);
		
		return dias.getDays();
		
	}

	
	/**
	 * Obtem a quantidade de meses entre duas datas
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static Integer obterQtdMesesEntreDuasDatas(Date dataInicial,
			Date dataFinal) {
		DateTime dtInicial = new DateTime(dataInicial);
		DateTime dtFinal = new DateTime(dataFinal);
		Months meses = Months.monthsBetween(dtInicial, dtFinal);
		return meses.getMonths();
	}

	
	/**
	 * Obtem a quantidade de anos entre duas datas
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static Integer obterQtdAnosEntreDuasDatas(Date dataInicial,
			Date dataFinal) {
		DateTime dtInicial = new DateTime(dataInicial);
		DateTime dtFinal = new DateTime(dataFinal);
		Years anos = Years.yearsBetween(dtInicial, dtFinal);
		return anos.getYears();
	}

	
	/**
	 * Retorna a diferença entre data corrente a data passada por parâmetro,
	 * que representa uma data de nascimento geralmente, sob o formato de uma
	 * String por extenso como, por exemplo, '5 anos 1 mês 2 dias'.
	 * 
	 * ORADB MPMC_IDA_ANO_MES_DIA
	 * 
	 * OBSERVAÇÃO: Sempre que possível usar o método:
	 * @see br.gov.mec.aghu.model.AipPacientes#getIdadeAnoMesFormat()
	 * @param data menor ou igual a data corrente
	 * @return Diferença entre datas por extenso
	 */
	public static String obterIdadeFormatadaAnoMesDia(Date data) {
		if (data == null) {
			throw new IllegalArgumentException("Data não informada.");
		}		
		
		if (data.after(new Date())) {
			throw new IllegalArgumentException("Data informada é maior que a data atual do sistema.");
		}
		
		Integer vAnos = obterQtdAnosEntreDuasDatas(data,new Date() );
		Integer vMeses = obterQtdMesesEntreDuasDatas(data,new Date()) % 12;
		Integer vDias = (((obterQtdMesesEntreDuasDatas(data,new Date()) % 1)* 1000000) * ((Calendar.getInstance()).getActualMaximum(Calendar.DAY_OF_MONTH)/ 1000000));
		StringBuffer retorno = new StringBuffer(20);
		/* TODO criar teste unitarios pra este metodo e garantir a funcionalidade.
		if(vAnos == 0 && vMeses == 0){
			if(vDias > 1){
				retorno.append(vDias).append(" dias ");
			}else if(vDias == 1){
				retorno.append(vDias).append(" dia ");
			}
			return retorno.toString();
		}
		
		if(vAnos == 0){
			if(vMeses > 1){
				retorno.append(vMeses).append(" meses ");
			}else if(vDias == 1){
				retorno.append(vMeses).append(" mês ");
			}
			
			if(vDias > 1){
				retorno.append(vDias).append(" dias ");
			}else if(vDias == 1){
				retorno.append(vDias).append(" dia ");
			}
			return retorno.toString();
		}
		*/
		if(vAnos > 1){
			retorno.append(vAnos).append(" anos ");
		}else if(vAnos == 1){
			retorno.append(vAnos).append(" ano ");
		}
		if(vMeses > 1){
			retorno.append(vMeses).append(" meses ");
		}else if(vMeses == 1){
			retorno.append(vMeses).append(" mês ");
		}
		
		if(vDias > 1){
			retorno.append(vDias).append(" dias ");
		}else if(vDias == 1){
			retorno.append(vDias).append(" dia ");
		}
		
		return retorno.toString();
	}
	
	
	/**
	 * Obter data com 23:59:59 e ultimo dia do mês.
	 * 
	 * @param mesCompetencia
	 * @return
	 */
	public static Date obterDataFimCompetencia(Date mesCompetencia) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(mesCompetencia);
		cal.set(Calendar.DAY_OF_MONTH, cal
				.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

		return cal.getTime();
	}
	
	/**
	 * Obter data com 00:00:00 e primeiro dia do mês.
	 * 
	 * @param mesCompetencia
	 * @return
	 */
	public static Date obterDataInicioCompetencia(Date mesCompetencia) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(mesCompetencia);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 00);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 00);

		return cal.getTime();
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
			zeraHorario(d1);
			d = d1.getTime();

			Calendar anotherd1 = Calendar.getInstance();
			anotherd1.setTime(anotherd);
			zeraHorario(anotherd1);
			anotherd = anotherd1.getTime();

			if (d.compareTo(anotherd) > 0) {

				response = true;

			}

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
	 * Retorna a data passada via paramêtro no formato
	 * dd/MM/yyyy HH:mm    
	 * @return
	 */
	public static String obterDataFormatadaHoraMinutoSegundo(Date date){
		return obterDataFormatada(date, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
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
	
	public static String calculaDiferencaTempo(Date dstart, Date dfinal) {
		long diff = dfinal.getTime() - dstart.getTime();
		long difMiliseconds = diff % 1000;
		long diffSeconds = diff / 1000 % 60;
		if (diffSeconds==0){
			return difMiliseconds + "ms";
		}
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		StringBuffer res = new StringBuffer();
		String format = "%1$02d";
		if (diffDays > 0){
			res.append(String.format(format, diffDays)).append("D ");
		}		
		res.append(String.format(format, diffHours)).append(':');
		res.append(String.format(format, diffMinutes)).append(':');
		res.append(String.format(format, diffSeconds)).append(':');
		res.append(String.format(format, difMiliseconds));
		return res.toString();
	}
	
	
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
	
	/**
	 * Obtém a quantidade de minutos entre duas datas.<br>
	 * 
	 * <b>dataInicial < dataFinal</b>, para diferenca ser positiva. Caso contrario a diferenca serah negativa.<br>
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static Integer obterQtdMinutosEntreDuasDatas(Date dataInicial, Date dataFinal) {
		DateTime dtInicial = new DateTime(dataInicial);
		DateTime dtFinal = new DateTime(dataFinal);
		
		Minutes minutos = Minutes.minutesBetween(dtInicial, dtFinal);
		
		return minutos.getMinutes();
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
			zeraHorario(d1);
			d = d1.getTime();

			Calendar anotherd1 = Calendar.getInstance();
			anotherd1.setTime(anotherd);
			zeraHorario(anotherd1);
			anotherd = anotherd1.getTime();

			if (d.compareTo(anotherd) > 0 || d.compareTo(anotherd) == 0) {

				response = true;

			}

		}

		return response;

	}	

}