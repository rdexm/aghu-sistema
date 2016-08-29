package br.gov.mec.aghu.core.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

/**
 * Classe de teste de DateFormatUtil.<br>
 * 
 * Os testes estao escritos admitindo-se locale Locale.US(en_US), Locale.UK(en_GB) e Locale.default(pt_BR). 
 * Para outros locales teremos que ajustar as variaveis de dias da semana para os testes funcionarem corretamente.
 * 
 * @author rcorvalao
 *
 */
public class DateFormatUtilTest {

	private static final List<String> DIA_SEMANA_DOM = Arrays.asList("DOM", "SUN");
	private static final List<String> DIA_SEMANA_SEG = Arrays.asList("SEG", "MON");
	private static final List<String> DIA_SEMANA_TER = Arrays.asList("TER", "TUE");
	private static final List<String> DIA_SEMANA_QUA = Arrays.asList("QUA", "WED");
	private static final List<String> DIA_SEMANA_QUI = Arrays.asList("QUI", "THU");
	private static final List<String> DIA_SEMANA_SEX = Arrays.asList("SEX", "FRI");
	private static final List<String> DIA_SEMANA_SAB = Arrays.asList("SAB", "SAT");
	
	
	@Test
	public void testarDiaDaSemanaParametroNull() {
		
		String strDiaSemana = DateFormatUtil.diaDaSemana(null);
		
		Assert.assertTrue(strDiaSemana == null);
	}
	
	@Test
	public void testarDiaDaSemanaTerca() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 3, 26);
		
		String strDiaSemana = DateFormatUtil.diaDaSemana(data.getTime());
		
		Assert.assertTrue(strDiaSemana != null && DIA_SEMANA_TER.contains(strDiaSemana.toUpperCase()));
	}
	
	@Test
	public void testarDiaDaSemanaQuarta() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 6, 6);
		
		String strDiaSemana = DateFormatUtil.diaDaSemana(data.getTime());
		
		Assert.assertTrue(strDiaSemana != null && DIA_SEMANA_QUA.contains(strDiaSemana.toUpperCase()));
	}

	@Test
	public void testarDiaDaSemanaQuinta() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 6, 7);
		
		String strDiaSemana = DateFormatUtil.diaDaSemana(data.getTime());
		
		Assert.assertTrue(strDiaSemana != null && DIA_SEMANA_QUI.contains(strDiaSemana.toUpperCase()));
	}
	
	@Test
	public void testarDiaDaSemanaSexta() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 3, 29);
		
		String strDiaSemana = DateFormatUtil.diaDaSemana(data.getTime());
		
		Assert.assertTrue(strDiaSemana != null && DIA_SEMANA_SEX.contains(strDiaSemana.toUpperCase()));
	}

	@Test
	public void testarDiaDaSemanaSabado() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 6, 9);
		
		String strDiaSemana = DateFormatUtil.diaDaSemana(data.getTime());
		
		Assert.assertTrue(strDiaSemana != null && DIA_SEMANA_SAB.contains(strDiaSemana.toUpperCase()));
	}
	
	@Test
	public void testarDiaDaSemanaDomingo() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 6, 10);
		
		String strDiaSemana = DateFormatUtil.diaDaSemana(data.getTime());
		
		Assert.assertTrue(strDiaSemana != null && DIA_SEMANA_DOM.contains(strDiaSemana.toUpperCase()));
	}
	
	@Test
	public void testarDiaDaSemanaSegunda() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 6, 11);
		
		String strDiaSemana = DateFormatUtil.diaDaSemana(data.getTime());
		
		Assert.assertTrue(strDiaSemana != null && DIA_SEMANA_SEG.contains(strDiaSemana.toUpperCase()));
	}
	
	
	
	
	@Test
	public void testarFomataDiaMesAnoDataParametroNull() {
		
		String str = DateFormatUtil.fomataDiaMesAno(null);
		
		Assert.assertTrue(str == null);
	}
	
	@Test
	public void testarFomataDiaMesAnoDataQualquer() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 3, 26);
		
		String str = DateFormatUtil.fomataDiaMesAno(data.getTime());
		
		Assert.assertTrue("26/04/2011".equals(str));
	}
	
	@Test
	public void testarFomataDiaMesAnoDataPrimeiroDoAno() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 0, 1);
		
		String str = DateFormatUtil.fomataDiaMesAno(data.getTime());
		
		Assert.assertTrue("01/01/2011".equals(str));
	}
	
	@Test
	public void testarFomataDiaMesAnoDataUltimoDoAno() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 11, 31);
		
		String str = DateFormatUtil.fomataDiaMesAno(data.getTime());
		
		Assert.assertTrue("31/12/2011".equals(str));
	}
	
	@Test
	public void testarFomataDiaMesAnoDataUltimoDiaDeFevereiroNaoBissexto() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 1, 28);
		
		String str = DateFormatUtil.fomataDiaMesAno(data.getTime());
		
		Assert.assertTrue("28/02/2011".equals(str));
	}
	
	@Test
	public void testarFomataDiaMesAnoDataUltimoDiaDeFevereiroBissexto() {
		Calendar data = Calendar.getInstance();
		data.set(2012, 1, 29);
		
		String str = DateFormatUtil.fomataDiaMesAno(data.getTime());
		
		Assert.assertTrue("29/02/2012".equals(str));
	}
	
	
	
	
	
	
	
	
	@Test
	public void testarFormataHoraMinutoQualquer() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 4, 25, 14, 45, 55);
		
		String str = DateFormatUtil.formataHoraMinuto(data.getTime());
		
		Assert.assertTrue("14:45".equals(str));
	}
	
	@Test
	public void testarFormataHoraMinutoUltimoMinuto() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 4, 25, 23, 59, 59);
		
		String str = DateFormatUtil.formataHoraMinuto(data.getTime());
		
		Assert.assertTrue("23:59".equals(str));
	}
	
	@Test
	public void testarFormataHoraMinutoPrimeiroMinuto() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 4, 25, 0, 1, 59);
		
		String str = DateFormatUtil.formataHoraMinuto(data.getTime());
		
		Assert.assertTrue("00:01".equals(str));
	}
	
	@Test
	public void testarFormataHoraMinutoZerado() {
		Calendar data = Calendar.getInstance();
		data.set(2011, 4, 25, 0, 0, 59);
		
		String str = DateFormatUtil.formataHoraMinuto(data.getTime());
		
		Assert.assertTrue("00:00".equals(str));
	}
	
	
	
	
	public void testar() {
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DAY_OF_MONTH, -8);
		
		Date hoje = new Date();
		
		while (c1.getTime().before(hoje)) {
			String str = DateFormatUtil.diaDaSemana(c1.getTime());
			System.out.println("==> " + str + " data: " + DateFormatUtil.fomataDiaMesAno(c1.getTime()));
			c1.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		
		//
		System.out.println("Locale ENGLISH : " + Locale.ENGLISH);
		System.out.println("Locale UK      : " + Locale.UK);
		System.out.println("Locale US      : " + Locale.US);
		System.out.println("Locale default : " + Locale.getDefault());
	}
	
	
}
