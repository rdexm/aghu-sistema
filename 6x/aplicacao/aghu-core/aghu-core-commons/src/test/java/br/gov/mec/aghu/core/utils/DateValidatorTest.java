package br.gov.mec.aghu.core.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * Classe de teste para DateValidator.
 * 
 * @author rcorvalao
 *
 */
public class DateValidatorTest {

	/**
	 * Passa data igual a atual, deve retornar falso.
	 */
	@Test
	public void validaSeDataMaiorQueAtualTest001() {

		boolean actual = DateValidator.validaDataMaiorQueAtual(new Date());

		assertFalse(actual);

	}

	/**
	 * Passa data maior que a atual, deve retornar verdadeiro.
	 */
	@Test
	public void validaSeDataMaiorQueAtualTest002() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(10));

		boolean actual = DateValidator.validaDataMaiorQueAtual(calendarioTemp
				.getTime());

		assertTrue(actual);

	}

	/**
	 * Passa data menor que a atual, deve retornar falso.
	 */
	@Test
	public void validaSeDataMaiorQueAtualTest003() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(-10));

		boolean actual = DateValidator.validaDataMaiorQueAtual(calendarioTemp
				.getTime());

		assertFalse(actual);

	}

	/**
	 * Passa data igual a atual, deve retornar falso.
	 */
	//@Test
	public void validaSeDataMenorQueAtualTest001() {

		boolean actual = DateValidator.validaDataMenorQueAtual(new Date());

		assertFalse(actual);

	}

	/**
	 * Passa data maior que a atual, deve retornar falso.
	 */
	@Test
	public void validaSeDataMenorQueAtualTest002() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(10));

		boolean actual = DateValidator.validaDataMenorQueAtual(calendarioTemp
				.getTime());

		assertFalse(actual);

	}

	/**
	 * Passa data menor que a atual, deve retornar verdadeiro.
	 */
	@Test
	public void validaSeDataMenorQueAtualTest003() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(-10));

		boolean actual = DateValidator.validaDataMenorQueAtual(calendarioTemp
				.getTime());

		assertTrue(actual);

	}

	/**
	 * Passa data menor, deve retornar verdadeiro.
	 */
	@Test
	public void validaSeDataMenorTest001() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(10));

		boolean actual = DateValidator.validaDataMenor(new Date(), calendarioTemp
				.getTime());

		assertTrue(actual);

	}

	/**
	 * Passa data maior, deve retornar falso.
	 */
	@Test
	public void validaSeDataMenorTest002() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(-10));

		boolean actual = DateValidator.validaDataMenor(new Date(), calendarioTemp
				.getTime());

		assertFalse(actual);

	}

	/**
	 * Passa data nula, deve retornar falso.
	 */
	@Test
	public void validaSeDataMenorTest003() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(-10));

		boolean actual = DateValidator.validaDataMenor(null, calendarioTemp
				.getTime());

		assertFalse(actual);

	}

	/**
	 * Passa data nula, deve retornar falso.
	 */
	@Test
	public void validaSeDataMenorTest004() {

		boolean actual = DateValidator.validaDataMenor(new Date(), null);

		assertFalse(actual);

	}

	/**
	 * Passa ambas datas nulas, deve retornar falso.
	 */
	@Test
	public void validaSeDataMenorTest005() {

		boolean actual = DateValidator.validaDataMenor(null, null);

		assertFalse(actual);

	}

	/**
	 * Passa data menor, deve retornar falso.
	 */
	@Test
	public void validaSeDataMaiorTest001() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(10));

		boolean actual = DateValidator.validaDataMaior(new Date(), calendarioTemp
				.getTime());

		assertFalse(actual);

	}

	/**
	 * Passa data maior, deve retornar verdadeiro.
	 */
	@Test
	public void validaSeDataMaiorTest002() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(-10));

		boolean actual = DateValidator.validaDataMaior(new Date(), calendarioTemp
				.getTime());

		assertTrue(actual);

	}

	/**
	 * Passa data nula, deve retornar falso.
	 */
	@Test
	public void validaSeDataMaiorTest003() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(-10));

		boolean actual = DateValidator.validaDataMaior(null, calendarioTemp
				.getTime());

		assertFalse(actual);

	}

	/**
	 * Passa data nula, deve retornar falso.
	 */
	@Test
	public void validaSeDataMaiorTest004() {

		boolean actual = DateValidator.validaDataMaior(new Date(), null);

		assertFalse(actual);

	}

	/**
	 * Passa ambas datas nulas, deve retornar falso.
	 */
	@Test
	public void validaSeDataMaiorTest005() {

		boolean actual = DateValidator.validaDataMaior(null, null);

		assertFalse(actual);

	}

	/**
	 * Passa data menor, deve retornar falso.
	 */
	@Test
	public void validaSeDataMaiorIgualTest001() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(10));

		boolean actual = DateValidator.validaDataMaiorIgual(new Date(),
				calendarioTemp.getTime());

		assertFalse(actual);

	}

	/**
	 * Passa data maior, deve retornar verdadeiro.
	 */
	@Test
	public void validaSeDataMaiorIgualTest002() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(-10));

		boolean actual = DateValidator.validaDataMaiorIgual(new Date(),
				calendarioTemp.getTime());

		assertTrue(actual);

	}

	/**
	 * Passa data igual, deve retornar verdadeiro.
	 */
	@Test
	public void validaSeDataMaiorIgualTest003() {

		Date d = new Date();

		boolean actual = DateValidator.validaDataMaiorIgual(new Date(), d);

		assertTrue(actual);

	}

	/**
	 * Passa data nula, deve retornar falso.
	 */
	@Test
	public void validaSeDataMaiorIgualTest004() {

		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(-10));

		boolean actual = DateValidator.validaDataMaiorIgual(null, calendarioTemp
				.getTime());

		assertFalse(actual);

	}

	/**
	 * Passa data nula, deve retornar falso.
	 */
	@Test
	public void validaSeDataMaiorIgualTest005() {

		boolean actual = DateValidator.validaDataMaiorIgual(new Date(), null);

		assertFalse(actual);

	}

	/**
	 * Passa ambas datas nulas, deve retornar falso.
	 */
	@Test
	public void validaSeDataMaiorIgualTest006() {

		boolean actual = DateValidator.validaDataMaiorIgual(null, null);

		assertFalse(actual);

	}

	/**
	 * Passa duas datas nulas, deve retornar exception IllegalArgumentException
	 * rcorvalao 01/10/2010
	 */
	@Test(expected = IllegalArgumentException.class)
	public void validaDiferencaEmDiasTest001() {
		Float diff = DateUtil.diffInDays(null, null);
	}

	/**
	 * Passa a primeira data nula, deve retornar exception
	 * IllegalArgumentException rcorvalao 01/10/2010
	 */
	@Test(expected = IllegalArgumentException.class)
	public void validaDiferencaEmDiasTest002() {
		Float diff = DateUtil.diffInDays(null, new Date());
	}

	/**
	 * Passa a segunda data nula, deve retornar exception
	 * IllegalArgumentException rcorvalao 01/10/2010
	 */
	@Test(expected = IllegalArgumentException.class)
	public void validaDiferencaEmDiasTest003() {
		Float diff = DateUtil.diffInDays(new Date(), null);
	}

	/**
	 * Passa duas data iguais, deve retornar zero. rcorvalao 01/10/2010
	 */
	@Test
	public void validaDiferencaEmDiasTest004() {
		Date d1 = new Date();
		Float diff = DateUtil.diffInDays(d1, d1);
		assertTrue(diff.compareTo(0f) == 0);
	}

	/**
	 * Passa a primeira data menor que a segunda data, deve retornar valor
	 * positivo. rcorvalao 01/10/2010
	 */
	@Test
	public void validaDiferencaEmDiasTest005() {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.MONTH, 2);
		Float diff = DateUtil.diffInDays(c1.getTime(), c2.getTime());
		assertTrue(diff.compareTo(0f) > 0);
	}

	/**
	 * Passa a segunda data menor que a primeira data, deve retornar valor
	 * positivo. rcorvalao 01/10/2010
	 */
	@Test
	public void validaDiferencaEmDiasTest006() {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.MONTH, 2);
		Float diff = DateUtil.diffInDays(c2.getTime(), c1.getTime());
		assertTrue(diff.compareTo(0f) > 0);
	}

	/**
	 * Passa as datas com uma diferenca de dois dias, deve retornar valor 2.
	 * rcorvalao 01/10/2010
	 */
	@Test
	public void validaDiferencaEmDiasTest007() {
		Date d1 = new Date(2011, 8, 14, 14, 0, 0);
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d1);
		c2.add(Calendar.DAY_OF_MONTH, 2);
		
		Float diff = DateUtil.diffInDays(d1, c2.getTime());
		assertTrue(diff.compareTo(2f) == 0);
	}

	/**
	 * Passa um range de datas que tem o inicio do horario de verão, deve
	 * retornar valor 10. rcorvalao 01/10/2010
	 */
	@Test
	public void validaDiferencaEmDiasTest008() {
		Date d1 = new Date(2010, 10, 14, 14, 0, 0);

		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		c1.add(Calendar.DAY_OF_MONTH, 10);

		Float diff = DateUtil.diffInDays(d1, c1.getTime());
		assertTrue(diff.compareTo(10f) == 0);
	}

	/**
	 * Passa um range de datas que tem o fim do horario de verão, deve retornar
	 * um valor entre 14 e 15. rcorvalao 01/10/2010
	 */
	@Test
	public void validaDiferencaEmDiasTest009() {
		Date d1 = new Date(2011, 1, 18, 14, 0, 0);
		Date d2 = new Date(2011, 2, 4, 18, 0, 0);

		Float diff = DateUtil.diffInDays(d1, d2);
		assertTrue(diff.compareTo(14f) >= 0 && diff.compareTo(15f) <= 0);
	}
	
	/**
	 * Passa um datas no mesmo dia com diferenca de horas,
	 * deve retornar ...
	 *  
	 * rcorvalao 01/10/2010
	 */
	@Test
	public void validaDiferencaEmDiasTest010() {
		Date d1 = new Date(2011, 4, 14, 0, 0, 0);
		Date d2 = new Date(2011, 4, 14, 17, 0, 0);

		Float diff = DateUtil.diffInDays(d1, d2);
		assertTrue(diff.compareTo(0f) >= 0 && diff.compareTo(1f) <= 0);
	}
	
	
	/**
	 * Passa data igual a atual, deve retornar true.
	 */
	@Test
	public void validaDataTruncadaMaiorIgualTest001() {
		
		boolean actual = DateValidator.validaDataTruncadaMaiorIgual(new Date(), new Date());

		assertTrue(actual);
	}
	
	/**
	 * Passa data maior que a atual, deve retornar verdadeiro.
	 */
	@Test
	public void validaDataTruncadaMaiorIgualTest002() {
		
		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(new Date());
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(10));
		
		boolean actual = DateValidator.validaDataTruncadaMaiorIgual(calendarioTemp.getTime(), new Date());

		assertTrue(actual);
	}
	
	/**
	 * Passa data menor que a atual, deve retornar falso.
	 */
	@Test
	public void validaDataTruncadaMaiorIgualTest003() {
		
		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(new Date());
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(-10));
		
		boolean actual = DateValidator.validaDataTruncadaMaiorIgual(calendarioTemp.getTime(), new Date());

		assertFalse(actual);
	}

	
}
