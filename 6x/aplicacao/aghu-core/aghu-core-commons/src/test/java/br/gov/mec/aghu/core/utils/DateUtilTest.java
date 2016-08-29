package br.gov.mec.aghu.core.utils;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * Classe de teste de DateUtil.
 * 
 * @author rcorvalao
 *
 */
public class DateUtilTest {

	
	/**
	 * Fornecendo uma data o método deve retorna a mesma instância que foi
	 * fornecida, e sem mofificação.
	 * 
	 * @author cvagheti
	 */
	@Test
	public void validaHojeSeNull() {
		Calendar instance = Calendar.getInstance();
		instance.set(2010, 10, 2);

		Date dt = instance.getTime();
		Date result = DateUtil.hojeSeNull(dt);

		// compara se é a mesma intância
		assertTrue(result == dt);
		// compara o mesmo momento no tempo com time em millesegundos
		assertTrue(result.getTime() == dt.getTime());

	}

	/**
	 * Fornecendo null o método deve retornar data/time corrente.
	 * 
	 * @author cvagheti
	 */
	@Test
	public void validaHojeSeNullPassandoNull() {
		Date hoje = DateUtil.hojeSeNull(null);

		assertTrue(hoje != null);

	}

		
	/**
	 * Passa data entre duas outras datas, deve retornar verdadeiro.
	 */
	@Test
	public void entreTest001() {
		
		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(new Date());
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(10));
		
		boolean actual = DateUtil.entre(new Date(), new Date(), calendarioTemp.getTime());

		assertTrue(actual);
	}
	
	/**
	 * Passa data entre duas outras datas, deve retornar verdadeiro.
	 */
	@Test
	public void entreTruncadoTest001() {
		
		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(new Date());
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(10));
		
		boolean actual = DateUtil.entreTruncado(new Date(), new Date(), calendarioTemp.getTime());

		assertTrue(actual);
	}
	
	/**
	 * 
	 */
	@Test
	public void validarIsFinalSemana001() {
		Calendar d = Calendar.getInstance();
		for (int i = 0; i < 8; i++) {
			if (d.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| d.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				break;
			}
			d.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		
		boolean isFinalSemana = DateUtil.isFinalSemana(d.getTime());
		
		Assert.assertTrue(isFinalSemana);
	}
	
	@Test
	public void validarIsFinalSemana002() {
		Calendar d = Calendar.getInstance();
		for (int i = 0; i < 8; i++) {
			if (d.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& d.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				break;
			}
			d.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		boolean isFinalSemana = DateUtil.isFinalSemana(d.getTime());
		
		Assert.assertFalse(isFinalSemana);
	}
	
	//@Test
	public void validarGetHorasDataAtual() {
		Integer d = DateUtil.getHoras(new Date());
		Assert.assertTrue(d>=1 && d<=23);
	}

	@Test
	public void validarGetHorasData23horas() {
		Calendar instance = Calendar.getInstance();
		instance.set(2010, 10, 2, 23, 59, 59);
		
		Integer d = DateUtil.getHoras(instance.getTime());
		
		Assert.assertTrue(d == 23);
	}

	@Test
	public void validarGetHorasDataUmaHoras() {
		Calendar instance = Calendar.getInstance();
		instance.set(2010, 10, 2, 1, 59, 59);
		
		Integer d = DateUtil.getHoras(instance.getTime());
		
		Assert.assertTrue(d == 1);
	}
	
	@Test
	public void validarGetHorasData12Horas() {
		Calendar instance = Calendar.getInstance();
		instance.set(2010, 10, 2, 12, 59, 59);
		
		Integer d = DateUtil.getHoras(instance.getTime());
		
		Assert.assertTrue(d == 12);
	}
	
	@Test
	public void validarGetHorasData13Horas() {
		Calendar instance = Calendar.getInstance();
		instance.set(2010, 10, 2, 13, 59, 59);
		
		Integer d = DateUtil.getHoras(instance.getTime());
		
		Assert.assertTrue(d == 13);
	}
	
	
	@Test
	public void validarGetMinutosData59Minutos() {
		Calendar instance = Calendar.getInstance();
		instance.set(2010, 10, 2, 13, 59, 59);
		
		Double d = DateUtil.getMinutos(instance.getTime());
		
		Assert.assertTrue(d == 59);
	}
	
	@Test
	public void validarObterDataComUltimoDiaMesAnterior_MesFevereiro2011() {
		final Date fev2011 = DateMaker.obterData(2011, 01, 01); 
		
		final Date fev = DateUtil.obterDataComUltimoDiaMesAnterior(fev2011);

		final Date anteriorFev2011 = DateMaker.obterData(2011, 00, 31); 

		Assert.assertTrue(anteriorFev2011.equals(fev));
	}

	@Test
	public void validarObterDataComUltimoDiaMesAnterior_MesMarco2011() {
		final Date data = DateMaker.obterData(2011, 02, 06); 
		
		final Date dataCalculada = DateUtil.obterDataComUltimoDiaMesAnterior(data);

		final Date dataEsperada = DateMaker.obterData(2011, 01, 28); 

		Assert.assertTrue(dataEsperada.equals(dataCalculada));
	}
	
	
	@Test
	public void validarObterDataComUltimoDiaMesAnterior_MesMarco2000() {
		final Date data = DateMaker.obterData(2000, 02, 06); 
		
		final Date dataCalculada = DateUtil.obterDataComUltimoDiaMesAnterior(data);

		final Date dataEsperada = DateMaker.obterData(2000, 01, 29); 

		Assert.assertTrue(dataEsperada.equals(dataCalculada));
	}
	
	@Test
	public void validarObterDataComUltimoDiaMesAnterior_MesDezembro2012() {
		final Date data = DateMaker.obterData(2012, 11, 06); 
		
		final Date dataCalculada = DateUtil.obterDataComUltimoDiaMesAnterior(data);

		final Date dataEsperada = DateMaker.obterData(2012, 10, 30); 

		Assert.assertTrue(dataEsperada.equals(dataCalculada));
	}
	
	@Test
	public void validarObterDataComUltimoDiaMesAnterior_MesJaneiro2012() {
		final Date data = DateMaker.obterData(2012, 00, 06); 
		
		final Date dataCalculada = DateUtil.obterDataComUltimoDiaMesAnterior(data);

		final Date dataEsperada = DateMaker.obterData(2011, 11, 31); 

		Assert.assertTrue(dataEsperada.equals(dataCalculada));
	}
	
	@Test 
	public void validarObterUltimoDiaDoMes_DataNula() {
		
		final Date dataCalculada = DateUtil.obterUltimoDiaDoMes(null);
		
		Assert.assertTrue(dataCalculada != null); 
	}
	
	@Test 
	public void validarObterUltimoDiaDoMes_DataNovembro() { 
		final Date data = DateMaker.obterData(2011, 10, 1);
				
		final Date dataCalculada = DateUtil.obterUltimoDiaDoMes(data);
		
		final Date dataEsperada = DateMaker.obterData(2011, 10, 30);
		Assert.assertTrue(dataEsperada.equals(DateUtil.obterDataComHoraInical(dataCalculada))); 
	}
	
	@Test 
	public void validarObterUltimoDiaDoMes_DataDezembro() {
		final Date data = DateMaker.obterData(2011, 11, 1);
				
		final Date dataCalculada = DateUtil.obterUltimoDiaDoMes(data);
		
		final Date dataEsperada = DateMaker.obterData(2011, 11, 31);
		Assert.assertTrue(dataEsperada.equals(DateUtil.obterDataComHoraInical(dataCalculada))); 
	}

	/**
	 * 
	 */
	@Test
	public void validarObterQtdHorasEntreDuasDatas001() {
		Date d1 = new Date(2011, 4, 14, 0, 0, 0);
		Date d2 = new Date(2011, 4, 14, 17, 0, 0);
		
		Integer diff = DateUtil.obterQtdHorasEntreDuasDatas(d1, d2);
		assertTrue(diff == 17);
	}
	
	/**
	 * 
	 */
	@Test
	public void validarObterQtdHorasEntreDuasDatas003() {
		Date d1 = new Date(2011, 4, 13, 0, 0, 0);
		Date d2 = new Date(2011, 4, 14, 12, 0, 0);
		
		Integer diff = DateUtil.obterQtdHorasEntreDuasDatas(d1, d2);
		assertTrue(diff == 36);
	}
	/**
	 * 
	 */
	@Test
	public void validarObterQtdHorasEntreDuasDatas002() {
		Date d1 = new Date(2011, 4, 14, 16, 0, 0);
		Date d2 = new Date(2011, 4, 14, 17, 0, 0);
		
		Integer diff = DateUtil.obterQtdHorasEntreDuasDatas(d1, d2);
		assertTrue(diff == 1);
	}
	
	/**
	 * 
	 */
	@Test
	public void validarObterQtdHorasEntreDuasDatas004() {
		Date d1 = new Date(2011, 4, 14, 16, 0, 0);
		Date d2 = new Date(2011, 4, 15, 17, 0, 0);
		
		Integer diff = DateUtil.obterQtdHorasEntreDuasDatas(d1, d2);
		assertTrue(diff == 25);
	}
	
	/**
	 * 
	 */
	@Test
	public void validarObterQtdHorasEntreDuasDatas005() {
		Date d1 = new Date(2011, 4, 14, 16, 0, 0);
		Date d2 = new Date(2011, 4, 15, 17, 0, 0);
		
		Integer diff = DateUtil.obterQtdHorasEntreDuasDatas(d2, d1);
		assertTrue(diff == -25);
	}
	
	/**
	 * Diferenca em dias zero, uma hora de diferenca.
	 */
	@Test
	public void validarObterQtdDiasEntreDuasDatas001() {
		Date d1 = new Date(2011, 4, 14, 16, 0, 0);
		Date d2 = new Date(2011, 4, 14, 17, 0, 0);
		
		Integer diff = DateUtil.obterQtdDiasEntreDuasDatas(d1, d2);
		assertTrue(diff == 0);
	}
	
	/**
	 * Diferenca em dias um.
	 */
	@Test
	public void validarObterQtdDiasEntreDuasDatas002() {
		Date d1 = new Date(2011, 4, 14, 16, 0, 0);
		Date d2 = new Date(2011, 4, 15, 16, 0, 0);
		
		Integer diff = DateUtil.obterQtdDiasEntreDuasDatas(d1, d2);
		assertTrue(diff == 1);
	}
	
	/**
	 * Diferenca em dias zero. 23 horas de 0 - 23.
	 */
	@Test
	public void validarObterQtdDiasEntreDuasDatas003() {
		Date d1 = new Date(2011, 4, 14, 0, 0, 0);
		Date d2 = new Date(2011, 4, 14, 23, 0, 0);
		
		Integer diff = DateUtil.obterQtdDiasEntreDuasDatas(d1, d2);
		assertTrue(diff == 0);
	}
	
	/**
	 * Diferenca em dias zero. 23 horas de 14 - 13.
	 */
	@Test
	public void validarObterQtdDiasEntreDuasDatas004() {
		Date d1 = new Date(2011, 4, 14, 14, 0, 0);
		Date d2 = new Date(2011, 4, 15, 13, 0, 0);
		
		Integer diff = DateUtil.obterQtdDiasEntreDuasDatas(d1, d2);
		assertTrue(diff == 0);
	}
	
	/**
	 * Diferenca em dias um. 0 hora dia 14 e 15.
	 */
	@Test
	public void validarObterQtdDiasEntreDuasDatas005() {
		Date d1 = new Date(2011, 4, 14, 0, 0, 0);
		Date d2 = new Date(2011, 4, 15, 0, 0, 0);
		
		Integer diff = DateUtil.obterQtdDiasEntreDuasDatas(d1, d2);
		assertTrue(diff == 1);
	}
	
	/**
	 * Diferenca em dias um. mais que 24 horas - de 26 horas.
	 */
	@Test
	public void validarObterQtdDiasEntreDuasDatas006() {
		Date d1 = new Date(2011, 4, 14, 0, 0, 0);
		Date d2 = new Date(2011, 4, 15, 2, 0, 0);
		
		Integer diff = DateUtil.obterQtdDiasEntreDuasDatas(d1, d2);
		assertTrue(diff == 1);
	}
	
	/**
	 * Diferenca em dias um. mais que 24 horas quase 2 dias - 47 horas.
	 */
	@Test
	public void validarObterQtdDiasEntreDuasDatas007() {
		Date d1 = new Date(2011, 4, 14, 0, 0, 0);
		Date d2 = new Date(2011, 4, 15, 23, 0, 0);
		
		Integer diff = DateUtil.obterQtdDiasEntreDuasDatas(d1, d2);
		assertTrue(diff == 1);
	}
	

	
}
