package br.gov.mec.aghu.faturamento.business;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ManipulacaoDatasUtil {
	
	private ManipulacaoDatasUtil() {
		
		super();
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	protected static Calendar getCalendar(Date date) {
		
		Calendar result = null;
		
		result = Calendar.getInstance();		
		result.setTime(date);
		
		return result;
	}
	
	/**
	 * 
	 * @param cal
	 * @return
	 */
	protected static int getMonthDayAmount(Calendar cal) {
		
		int result = 0;
		
		result = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		return result;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonthDayAmount(Date date) {
		
		int result = 0;
		Calendar cal = null;
		
		cal = getCalendar(date);
		result = getMonthDayAmount(cal);
		
		return result;
	}

	/**
	 * 
	 * @param cal
	 * @return
	 */
	protected static int getDayUntilMonthEnd(Calendar cal) {
		
		int result = 0;
		
		result = getMonthDayAmount(cal);
		result -= cal.get(Calendar.DAY_OF_MONTH);
		
		return result;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("ucd")
	public static int getDayUntilMonthEnd(Date date) {
		
		int result = 0;
		Calendar cal = null;

		cal = getCalendar(date);
		result = getDayUntilMonthEnd(cal);
				
		return result;
	}
	
	/**
	 * 
	 * @param cal
	 * @return
	 */
	protected static int[] getYearMonthDay(Calendar cal) {
		
		int[] result = new int[] {0, 0, 0};
		
		result[0] = cal.get(Calendar.YEAR);
		result[1] = cal.get(Calendar.MONTH);
		result[2] = cal.get(Calendar.DAY_OF_MONTH);
		
		return result;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static int[] getYearMonthDay(Date date) {
		
		int[] result = null;
		Calendar cal = null;

		cal = getCalendar(date);
		result = getYearMonthDay(cal);
		result[1]++;
		
		return result;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static int[] getYearMonth(Date date) {
		
		int[] result = null;
		int[] yearMonthDay = null;
	
		result = new int[] {0, 0};
		yearMonthDay = getYearMonthDay(date);
		result[0] = yearMonthDay[0];
		result[1] = yearMonthDay[1];
		
		return result;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("ucd")
	public static Date getNextYearMonth(Date date) {
		
		Date result = null;
		int[] yearMonth = null;

		yearMonth = getYearMonth(date);
		if (yearMonth[1] == 12) {
			yearMonth[0]++;
			yearMonth[1] = 1;
		} else {
			yearMonth[1]++;			
		}
		result = getDate(yearMonth[0], yearMonth[1], 1);
		
		return result;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("ucd")
	public static Date getPreviousYearMonth(Date date) {
		
		Date result = null;
		int[] yearMonth = null;

		yearMonth = getYearMonth(date);
		if (yearMonth[1] == 1) {
			yearMonth[0]--;
			yearMonth[1] = 12;
		} else {
			yearMonth[1]--;			
		}
		result = getDate(yearMonth[0], yearMonth[1], 1);
		
		return result;
	}
	
	/**
	 * 
	 * @param dateA
	 * @param dateB
	 * @return
	 */
	@SuppressWarnings("ucd")
	public static boolean isSameYearMonth(Date dateA, Date dateB) {
		
		boolean result = false;
		int[] yearMonthA = null;
		int[] yearMonthB = null;
		
		yearMonthA = getYearMonth(dateA);
		yearMonthB = getYearMonth(dateB);
		result = Arrays.equals(yearMonthA, yearMonthB);
		
		return result;
	}
	
	/**
	 * 
	 * @param dateA
	 * @param dateB
	 * @return
	 */
	@SuppressWarnings("ucd")
	public static boolean isSameYearMonthDay(Date dateA, Date dateB) {
		
		boolean result = false;
		int[] yearMonthDayA = null;
		int[] yearMonthDayB = null;
		
		yearMonthDayA = getYearMonthDay(dateA);
		yearMonthDayB = getYearMonthDay(dateB);
		result = Arrays.equals(yearMonthDayA, yearMonthDayB);
		
		return result;
	}
	
	/**
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date getDate(int year, int month, int day) {
		
		Date result = null;
		Calendar cal = null;
		
		cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		result = cal.getTime();
		
		return result;
	}
	
	/**
	 * Can be used to return the amount of days in a month
	 * @param year
	 * @param month
	 * @return 
	 */
	public static Date getLastDay(int year, int month) {
		
		Date result = null;
		Calendar cal = null;
		
		cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, getMonthDayAmount(cal));
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		result = cal.getTime();
		
		return result;
	}
	
	/**
	 * 
	 * @param dateA final date
	 * @param dateB initial date
	 * @return <code>A - B</code>
	 */
	public static int getDifferenceInDays(Date dateA, Date dateB) {
		
		int result = 0;
		long diffInSec = 0L;
		Calendar calA = null;
		Calendar calB = null;
		
		calA = getCalendar(dateA);
		calB = getCalendar(dateB);
		diffInSec = (calA.getTimeInMillis() - calB.getTimeInMillis()) / 1000;
		result = (int)(diffInSec / (3600L * 24L));
		
		return result;		
	}
	
	/**
	 * 
	 * @param dateA
	 * @param dateB
	 * @param dayOfWeek Use {@link Calendar#DAY_OF_WEEK}, like {@link Calendar#SATURDAY}
	 * @return
	 */
	public static int getWeekDayAmountBetween(Date dateA, Date dateB, int dayOfWeek) {
		
		int result = 0;
		Calendar calA = null;
		Calendar calB = null;
		
		calA = getCalendar(dateA);
		calB = getCalendar(dateB);
		/*
		 * There is a LOT of cleverer ways to do this. But this one is kind of fun.
		 */
		for (; calA.before(calB); calA.add(Calendar.DAY_OF_YEAR, 1)) {
			if (calA.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
				result++;
			}
		}
		if (calB.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
			result++;
		}
		
		return result;		
	}
}
