package br.gov.mec.aghu.faturamento.business;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

public class ManipulacaoDatasUtilTest {

	@Test
	public void testGetMonthDayAmountDate() {
		
		Calendar cal = new GregorianCalendar();
		Date date = null;
		int amount = 0;
		int result = 0;
		
		//setup
		cal.set(2000, Calendar.FEBRUARY, 1);
		date = cal.getTime();
		amount = 29;
		//assert
		result = ManipulacaoDatasUtil.getMonthDayAmount(date);
		Assert.assertEquals(date.toString(), amount, result);
		//setup
		cal.set(Calendar.YEAR, 1999);
		date = cal.getTime();
		amount = 28;
		//assert
		result = ManipulacaoDatasUtil.getMonthDayAmount(date);
		Assert.assertEquals(date.toString(), amount, result);
	}

	@Test
	public void testGetDayUntilMonthEndDate() {
		
		Calendar cal = new GregorianCalendar();
		Date date = null;
		int amount = 0;
		int result = 0;
		int day = 0;
		
		//setup
		day = 11;
		cal.set(2000, Calendar.FEBRUARY, day);
		date = cal.getTime();
		amount = 29 - day;
		//assert
		result = ManipulacaoDatasUtil.getDayUntilMonthEnd(date);
		Assert.assertEquals(date.toString(), amount, result);
		//setup
		cal.set(Calendar.YEAR, 1999);
		date = cal.getTime();
		amount = 28 - day;
		//assert
		result = ManipulacaoDatasUtil.getDayUntilMonthEnd(date);
		Assert.assertEquals(date.toString(), amount, result);
	}

	@Test
	public void testGetYearMonthDayDate() {
		
		Calendar cal = new GregorianCalendar();
		Date date = null;
		int[] yearMonthDay = null;
		int[] result = null;
		
		//setup
		yearMonthDay = new int[] {
				1999,
				02,
				13
		};
		cal.set(yearMonthDay[0], yearMonthDay[1] - 1, yearMonthDay[2]);
		date = cal.getTime();
		//assert
		result = ManipulacaoDatasUtil.getYearMonthDay(date);
		Assert.assertArrayEquals(date.toString(), yearMonthDay, result);
	}

	@Test
	public void testGetYearMonth() {
		
		Calendar cal = new GregorianCalendar();
		Date date = null;
		int[] yearMonth = null;
		int[] result = null;
		
		//setup
		yearMonth = new int[] {
				1999,
				02,
		};
		cal.set(yearMonth[0], yearMonth[1] - 1, 17);
		date = cal.getTime();
		//assert
		result = ManipulacaoDatasUtil.getYearMonth(date);
		Assert.assertArrayEquals(date.toString(), yearMonth, result);
	}

	@Test
	public void testGetNextYearMonth() {
		
		Calendar cal = new GregorianCalendar();
		Date date = null;
		Date result = null;
		int[] yearMonth = null;
		int[] yearMonthResult = null;
		
		//setup
		yearMonth = new int[] {
				2000,
				12,
		};
		cal.set(yearMonth[0], yearMonth[1] - 1, 17);
		date = cal.getTime();
		//assert
		result = ManipulacaoDatasUtil.getNextYearMonth(date);
		Assert.assertTrue(date.toString() + result.toString(), date.before(result));
		yearMonthResult = ManipulacaoDatasUtil.getYearMonth(result);
		Assert.assertTrue(Arrays.toString(yearMonthResult), yearMonthResult[0] == yearMonth[0] + 1);
		Assert.assertTrue(Arrays.toString(yearMonthResult), yearMonthResult[1] == 1);
	}

	@Test
	public void testGetPreviousYearMonth() {
		
		Calendar cal = new GregorianCalendar();
		Date date = null;
		Date result = null;
		int[] yearMonth = null;
		int[] yearMonthResult = null;
		
		//setup
		yearMonth = new int[] {
				2000,
				1,
		};
		cal.set(yearMonth[0], yearMonth[1] - 1, 17);
		date = cal.getTime();
		//assert
		result = ManipulacaoDatasUtil.getPreviousYearMonth(date);
		Assert.assertTrue(date.toString() + result.toString(), date.after(result));
		yearMonthResult = ManipulacaoDatasUtil.getYearMonth(result);
		Assert.assertTrue(Arrays.toString(yearMonthResult), yearMonthResult[0] == yearMonth[0] - 1);
		Assert.assertTrue(Arrays.toString(yearMonthResult), yearMonthResult[1] == 12);
	}

	@Test
	public void testIsSameYearMonth() {
		
		Calendar calA = new GregorianCalendar();
		Calendar calB = new GregorianCalendar();
		Date dateA = null;
		Date dateB = null;
		
		//setup
		calA.set(2000, Calendar.MARCH, 17);
		calB.set(2000, Calendar.MARCH, 23);
		dateA = calA.getTime();
		dateB = calB.getTime();
		//assert
		Assert.assertTrue(dateA.toString() + dateB.toString(), ManipulacaoDatasUtil.isSameYearMonth(dateA, dateB));
		calB.set(2000, Calendar.FEBRUARY, 17);
		dateB = calB.getTime();
		Assert.assertFalse(dateA.toString() + dateB.toString(), ManipulacaoDatasUtil.isSameYearMonth(dateA, dateB));
	}

	@Test
	public void testIsSameYearMonthDay() {
		
		Calendar calA = new GregorianCalendar();
		Calendar calB = new GregorianCalendar();
		Date dateA = null;
		Date dateB = null;
		
		//setup
		calA.set(2000, Calendar.MARCH, 17);
		calB.set(2000, Calendar.MARCH, 23);
		dateA = calA.getTime();
		dateB = calB.getTime();
		//assert
		Assert.assertFalse(dateA.toString() + dateB.toString(), ManipulacaoDatasUtil.isSameYearMonthDay(dateA, dateB));
		calB.set(2000, Calendar.MARCH, 17);
		dateB = calB.getTime();
		Assert.assertTrue(dateA.toString() + dateB.toString(), ManipulacaoDatasUtil.isSameYearMonthDay(dateA, dateB));
	}

	@Test
	public void testGetDate() {
		
		Calendar cal = new GregorianCalendar();
		Date date = null;
		Date result = null;
		int[] yearMonthDay = null;
		
		//setup
		yearMonthDay = new int[] {
				2000,
				1,
				17
		};
		cal.set(yearMonthDay[0], yearMonthDay[1] - 1, yearMonthDay[2], 0, 0, 0);
		date = cal.getTime();
		//assert
		result = ManipulacaoDatasUtil.getDate(yearMonthDay[0], yearMonthDay[1], yearMonthDay[2]);
		Assert.assertTrue(date.toString() + result.toString(), ManipulacaoDatasUtil.getDifferenceInDays(date, result) == 0);
	}

	@Test
	public void testGetDifferenceInDays() {
		
		Calendar calA = new GregorianCalendar();
		Calendar calB = new GregorianCalendar();
		Date dateA = null;
		Date dateB = null;
		int amount = 0;
		int result = 0;
		
		//setup
		calA.set(2000, Calendar.FEBRUARY, 19);
		calB.set(2000, Calendar.MARCH, 10);
		dateA = calA.getTime();
		dateB = calB.getTime();
		amount = 20;
		//assert
		result = ManipulacaoDatasUtil.getDifferenceInDays(dateB, dateA);
		Assert.assertEquals(dateA.toString() + dateB.toString(), amount, result);
	}
	
	@Test
	public void testGetLastDay() {

		Calendar cal = new GregorianCalendar();
		Date date = null;
		Date result = null;
		int[] yearMonthDay = null;
		
		//setup
		yearMonthDay = new int[] {
				2000,
				1,
				31
		};
		cal.set(yearMonthDay[0], yearMonthDay[1] - 1, yearMonthDay[2], 23, 59, 59);
		cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		date = cal.getTime();
		//assert
		result = ManipulacaoDatasUtil.getLastDay(yearMonthDay[0], yearMonthDay[1]);
		Assert.assertEquals(date.toString() + result.toString(), 0, date.compareTo(result));
	}
	
	@Test
	public void testGetWeekDayAmountBetween() {
		
		Calendar calA = new GregorianCalendar();
		Calendar calB = new GregorianCalendar();
		Date dateA = null;
		Date dateB = null;
		int amount = 0;
		int result = 0;
		
		//setup
		calA.set(1999, Calendar.DECEMBER, 1);
		calB.set(2000, Calendar.JANUARY, 31);
		dateA = calA.getTime();
		dateB = calB.getTime();
		amount = 9;
		//assert
		result = ManipulacaoDatasUtil.getWeekDayAmountBetween(dateA, dateB, Calendar.SATURDAY);
		Assert.assertEquals(dateA.toString() + dateB.toString(), amount, result);		
		//assert
		result = ManipulacaoDatasUtil.getWeekDayAmountBetween(dateA, dateB, Calendar.WEDNESDAY);
		Assert.assertEquals(dateA.toString() + dateB.toString(), amount, result);		
		//assert		
		result = ManipulacaoDatasUtil.getWeekDayAmountBetween(dateA, dateB, Calendar.TUESDAY);
		Assert.assertEquals(dateA.toString() + dateB.toString(), amount - 1, result);		
	}

}
