package kr.co.cont.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import kr.co.cont.common.constants.DateConstants;

public class DateUtil implements DateConstants {

	// -------------------------------------------------------------------------
	// 현재 날짜 및 시간
	// -------------------------------------------------------------------------

	/**
	 * 기본일자형식({@link DateConstants#DF_DATE})으로 된 현재날짜를 반환한다.
	 * 
	 * @return 현재날짜(yyyyMMdd)
	 */
	public static Date getCurrentDate() {
		return getCurrentDateTime(DF_DATE); // 시간값은 무시됨
	}

	/**
	 * 입력형식으로 된 현재날짜를 반환한다.
	 * 
	 * @param dateFormat 날짜형식
	 * @return 현재날짜
	 */
	public static Date getCurrentDate(String dateFormat) {
		return getCurrentDateTime(dateFormat);
	}

	/**
	 * 입력형식으로 된 현재날짜의 문자열을 반환한다.
	 * 
	 * <pre>
	 *예)
	 *    String dateTimeString = DateUtil.getCurrentDateString(DateUtil.DF_YYYYMMDDHHMMSS_DP);
	 * </pre>
	 * 
	 * @param dateFormat 날짜형식
	 * @return 현재날짜에 대한 문자열
	 */
	public static String getCurrentDateString(String dateFormat) {
		return format(Calendar.getInstance(), dateFormat, null, null);
	}

	/**
	 * 현재시각을 반환한다.
	 * 
	 * @return 현재일시
	 */
	public static Date getCurrentDateTime() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 기본일자형식({@link DateConstants#DF_BASE})으로 된 현재일시를 반환한다.
	 * 
	 * @return 현재일시(yyyyMMddHHmmss)
	 */
	public static Date getCurrentBaseDateTime() {
		return getCurrentDateTime(DF_BASE); // 밀리세컨드는 무시됨.
	}

	/**
	 * 입력형식으로 된 현재날짜 또는 일시를 반환한다.
	 * 
	 * @param dateFormat 날짜형식
	 * @return 현재날짜
	 */
	public static Date getCurrentDateTime(String dateFormat) {
		Date currentDate = Calendar.getInstance().getTime();
		try {
			return parse(currentDate, dateFormat, null);
		} catch (Exception e) {
			return currentDate;
		}
	}

	/**
	 * 입력받은 <code>java.util.Date</code>의 해당월 날짜를 1일로 설정하여 반환
	 * 
	 * @param date 설정할 날짜
	 * @return 입력된 날짜의 해당월 첫번째 날짜
	 */
	public static Date getFirstDateOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		return calendar.getTime();
	}

	/**
	 * 입력받은 <code>java.util.Date</code>의 해당월 마지막 날짜를 설정하여 반환
	 * 
	 * @param date 설정할 날짜
	 * @return 입력된 날짜의 해당월 마지막 날짜
	 */
	public static Date getLastDateOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

		return calendar.getTime();
	}

	/**
	 * 입력받은 <code>java.util.Date</code>를 1월 1일로 설정하여 반환
	 * 
	 * @param date 설정할 날짜
	 * @return 입력일자의 1월 1일 날짜
	 */
	public static Date getFirstDateOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);

		return calendar.getTime();
	}

	/**
	 * 입력받은 <code>java.util.Date</code>를 12월 31일로 설정하여 반환
	 * 
	 * @param date 설정할 날짜
	 * @return 입력일자의 12월 31일 날짜
	 */
	public static Date getLastDateOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);

		return calendar.getTime();
	}

	// -------------------------------------------------------------------------
	// 문자열 --> 날짜 변경
	// -------------------------------------------------------------------------

	/**
	 * 기본일자형식({@link DateConstants#DF_DATE})의 날짜문자열을 날짜로 변환하여 반환한다.
	 * <p>
	 * 내부에서 {@link #toDate(String, String) toDate(dateString, DF_DATE)} 를 호출한다.
	 * 
	 * @param dateString 날짜문자열
	 * @return 날짜(yyyyMMddHHmmss)
	 */
	public static Date toDate(String dateString) {
		return toDate(dateString, DF_DATE);
	}

	/**
	 * format형태인 String데이타를 java.util.Date로 변환
	 * <p>
	 * 다음과 같이 지정형식보다 많은 데이터값이 들어오면 오류가 발생한다. 단, 지정 형식과 다른 데이터가 입력되면 널을 반환한다.
	 * 
	 * <pre>
	 * <code>
	 * DateUtil.toDate("20091223124040", "yyyyMMddHHmm"); // [Sat Dec 26 07:20:00 KST 2009]
	 * DateUtil.toDate("20091223124040", "yyyyMMdd"); // [Wed May 21 00:00:00 KST 65321]
	 * DateUtil.toDate("2009", "yyyy"); // [Fri Jan 01 00:00:00 KST 2009]
	 * DateUtil.toDate("200912", "yyyy"); // [Fri Jan 01 00:00:00 KST 200912]
	 * DateUtil.toDate("20091223", "yyyy-MM-dd"); // [null]
	 * </code>
	 * </pre>
	 * 
	 * <b><strong><font color="red"> ※ 위의 결과에서 볼수 있듯이 지정형식과 같으면서 데이터가 많을 경우 원하는 결과값과
	 * 다른 결과를 반환하므로 사용시 주의해서 사용하도록 한다. </font></strong></b>
	 *
	 * @param dateString 변경대상이 되는 일자문자열
	 * @param dateFormat 날짜형식
	 * @return java.util.Date
	 */
	public static Date toDate(String dateString, String dateFormat) {
		if (dateString == null || dateString.length() == 0)
			return null;

		try {
			return parse(dateString, dateFormat, null);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 변경전날짜형식의 날짜문자열을 변경후날짜형식의 날짜로 변환하여 반환한다.
	 * <p>
	 * 변경전날짜형식과 변경후날짜형식이 널이면 기본날짜형식({@link DateConstants#DF_DATE})을 적용한다.
	 *
	 * @param dateString       날짜문자열
	 * @param beforeDateFormat 변경전날짜형식
	 * @param afterDateFormat  변경후날짜형식
	 * @return 변경후날짜형식으로 변환된 날짜. 만약 입력된 날짜문자열이 널이거나 형식이 유효하지 않을 경우 널을 반환한다.
	 */
	public static Date toDate(String dateString, String beforeDateFormat, String afterDateFormat) {
		if (dateString == null || dateString.length() == 0)
			return null;
		// 변경전날짜형식과 변경후날짜형식이 널이면 기본날짜형식을 적용하고 형식이 같을 경우 날짜를 변경하지 않고 반환한다.
		if (beforeDateFormat == null)
			beforeDateFormat = DF_DATE;
		if (afterDateFormat == null)
			afterDateFormat = DF_DATE;

		try {
			Date date = parse(dateString, beforeDateFormat, null);
			if (beforeDateFormat == afterDateFormat || beforeDateFormat.equals(afterDateFormat)) {
				return date;
			}

			// 패턴변경후 날짜변환
			return DateUtil.parse(date, afterDateFormat, null);// 패턴변경
		} catch (Exception e) {
			return null;
		}
	}

	// -------------------------------------------------------------------------
	// 날짜 --> 문자열 변경
	// -------------------------------------------------------------------------

	/**
	 * 기본일자형식({@link DateConstants#DF_DATE})으로 된 현재날짜를 문자열로 반환한다.
	 * 
	 * @return 현재날짜(yyyyMMdd)
	 */
	public static String getCurrentDateString() {
		Date currentDate = getCurrentDateTime();
		return toDateString(currentDate, DF_DATE);
	}

	/**
	 * 기본형식({@link DateConstants#DF_BASE})으로 된 현재날짜를 문자열로 반환한다.
	 * 
	 * @return 현재날짜(yyyyMMddHHmmss)
	 */
	public static String getCurrentBaseDateString() {
		Date currentDate = getCurrentDateTime();
		return toDateString(currentDate, DF_BASE);
	}

	/**
	 * <code>java.util.Date</code>인 데이타를 날짜형식에 맞게 <code>java.lang.String</code>으로 변환
	 * 
	 * @param date       변경대상이 되는 일자
	 * @param dateFormat 날짜형식
	 * @return 입력된 날짜형식으로 변환된 날짜문자열
	 */
	public static String toDateString(Date date, String dateFormat) {
		if (date == null)
			return null;
		return DateUtil.format(date, dateFormat, null, null);
	}

	/**
	 * 입력받은 <code>java.util.Date</code>의 해당월 날짜를 1일로 설정하여 데이타를 날짜형식에 맞게
	 * <code>java.lang.String</code>으로 변환
	 * 
	 * @param date       설정할 날짜
	 * @param dateFormat 날짜형식
	 * @return 입력된 날짜형식으로 변환된 해당월 첫번째 날짜
	 */
	public static String getFirstDateStringOfMonth(Date date, String dateFormat) {
		return toDateString(getFirstDateOfMonth(date), dateFormat);
	}

	/**
	 * 입력받은 <code>java.util.Date</code>의 해당월 마지막 날짜를 설정하여 데이터를 날짜 형식에 맞게
	 * <code>java.lang.String</code>으로 변환
	 * 
	 * @param date       설정할 날짜
	 * @param dateFormat 날짜형식
	 * @return 입력된 날짜형식으로 변환된 해당월 마지막 날짜
	 */
	public static String getLastDateStringOfMonth(Date date, String dateFormat) {
		return toDateString(getLastDateOfMonth(date), dateFormat);
	}

	/**
	 * 입력받은 <code>java.util.Date</code>를 1월 1일로 설정하여 데이터를 날짜 형식에 맞게
	 * <code>java.lang.String</code>으로 변환
	 * 
	 * @param date       설정할 날짜
	 * @param dateFormat 날짜형식
	 * @return 입력된 날짜형식으로 변환된 입력일자의 1월 1일 날짜
	 */
	public static String getFirstDateStringOfYear(Date date, String dateFormat) {
		return toDateString(getFirstDateOfYear(date), dateFormat);
	}

	/**
	 * 입력받은 <code>java.util.Date</code>를 12월 31일로 설정하여 데이터를 날짜 형식에 맞게
	 * <code>java.lang.String</code>으로 변환
	 * 
	 * @param date       설정할 날짜
	 * @param dateFormat 날짜형식
	 * @return 입력된 날짜형식으로 변환된 입력일자의 12월 31일 날짜
	 */
	public static String getLastDateStringOfYear(Date date, String dateFormat) {
		return toDateString(getLastDateOfYear(date), dateFormat);
	}

	// -------------------------------------------------------------------------
	// 문자열 --> 문자열 변경
	// -------------------------------------------------------------------------

	/**
	 * 날짜형식변경
	 * <p>
	 * 변경전날짜형식과 변경후날짜형식이 널이면 기본날짜형식({@link DateConstants#DF_DATE})을 적용하고 형식이 같을 경우
	 * 날짜를 변경하지 않고 반환한다.
	 *
	 * @param dateString       변경대상이 되는 일자
	 * @param beforeDateFormat 변경전날짜형식. 널이 입력되면 {@link DateConstants#DF_DATE}을 적용한다.
	 * @param afterDateFormat  변경후날짜형식. 널이 입력되면 {@link DateConstants#DF_DATE}을 적용한다.
	 * @return 변경후날짜형식으로 변환된 날짜문자열. 만약 입력된 날짜문자열이 널이거나 형식이 유효하지 않을 경우 널을 반환한다.
	 */
	public static String convertDateFormat(String dateString, String beforeDateFormat, String afterDateFormat) {
		if (dateString == null)
			return null;
		// 변경전날짜형식과 변경후날짜형식이 널이면 기본날짜형식을 적용하고 형식이 같을 경우 날짜를 변경하지 않고 반환한다.
		if (beforeDateFormat == null)
			beforeDateFormat = DF_DATE;
		if (afterDateFormat == null)
			afterDateFormat = DF_DATE;
		if (beforeDateFormat == afterDateFormat || beforeDateFormat.equals(afterDateFormat))
			return dateString;

		try {
			Date date = DateUtil.parse(dateString, beforeDateFormat, null);
			return DateUtil.format(date, afterDateFormat, null, null)  ; // 패턴변경
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 기준일자가 해당월의 말일자인지 여부를 반환한다.
	 * 
	 * @param basisDate 기준일자
	 */
	public static boolean isLastDay(Date basisDate) {
		Calendar basisCal = Calendar.getInstance();
		basisCal.setTime(basisDate);
		return isLastDay(basisCal);
	}

	/**
	 * 기준일자가 해당월의 말일자인지 여부를 반환한다.
	 * 
	 * @param basisCal 기준달력
	 */
	public static boolean isLastDay(Calendar basisCal) {
		return (basisCal.get(Calendar.DATE) == basisCal.getActualMaximum(Calendar.DATE));
	}

	/**
	 * 해당일의 말일자 구하기
	 * 
	 * <pre>
	 * setLastDay("20100119", "yyyyMMdd") => "20100131" setLastDay("20100205",
	 * "yyyy-MM-dd") => "2010-02-28"
	 * 
	 * @param dateString 기준일자 (yyyyMMdd)s
	 * @param dateFormat 반환될 날짜형식
	 * @return
	 */
	public static String setLastDay(String dateString, String dateFormat) {
		Date date = toDate(dateString, DF_DATE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

		return toDateString(new Date(calendar.getTimeInMillis()), dateFormat);
	}

	/**
	 * 일자의 요일을 반환
	 * 
	 * <pre>
	 * "20100117(일요일) ==> 1 "20080123(토요일) ==> 7
	 * 
	 * @param date 일자
	 * @return
	 */
	public static int getDayOfWeek(Date date) {
		int result = 0;
		Calendar calendar = Calendar.getInstance(Locale.KOREAN);
		calendar.setTime(date);
		result = calendar.get(Calendar.DAY_OF_WEEK);
		return result;
	}

	/**
	 * 입력받은 날짜에서 +/- N일의 날짜를 구한다.
	 * 
	 * @param date 기준 일자
	 * @param day  +/- N일
	 * @return 결과 일자
	 */
	public static String changeDateWithDayLevel(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + day);
		return DateUtil.toDateString(cal.getTime(), DateUtil.DF_DATE);
	}

	/**
	 * 입력한 일자(A)가 오늘일자(B) 기준 이전일자인지 여부 (오늘일자 포함 안함) (A < B)
	 *
	 * @param date 일자
	 * @return 여부
	 */
	public static boolean isBeforeDate(Date date) {
		String currentDate = getCurrentDateString();
		String compareDate = toDateString(date, DF_DATE);
		int compare = currentDate.compareTo(compareDate);
		if (compare > 0)
			return true;
		return false;
	}

	/**
	 * 입력한 일자(A)가 오늘일자(B) 기준 이전일자인지 여부 (오늘일자 포함 안함)(A < B)
	 *
	 * @param date 일자
	 * @return 여부
	 */
	public static boolean isBeforeDate(String compareDate) {
		String currentDate = getCurrentDateString();
		int compare = currentDate.compareTo(compareDate);
		if (compare > 0)
			return true;
		return false;
	}

	/**
	 * 첫번째 입력한 일자(A)가 두번째 입력한 비교일자(B) 기준 이전 일자인지 여부(A <= B)
	 * 
	 * @param date        일자
	 * @param compareDate 비교일자
	 * @return 여부
	 */
	public static boolean isBeforeDate(String date, String compareDate) {
		int compare = compareDate.compareTo(date);
		if (compare >= 0)
			return true;
		return false;
	}

	/**
	 * 입력한 일자(A)가 오늘일자(B) 기준 이후 일자인지 여부 (오늘일자 포함 안함) (A > B)
	 *
	 * @param date 일자
	 * @return 여부
	 */
	public static boolean isAfterDate(Date date) {
		String currentDate = getCurrentDateString();
		String compareDate = toDateString(date, DF_DATE);
		int compare = currentDate.compareTo(compareDate);
		if (compare < 0)
			return true;
		return false;
	}

	/**
	 * 입력한 일자(A)가 오늘일자(B) 기준 이후 일자인지 여부 (오늘일자 포함 안함) (A > B)
	 *
	 * @param date 일자
	 * @return 여부
	 */
	public static boolean isAfterDate(String date) {
		String currentDate = getCurrentDateString();
		int compare = currentDate.compareTo(date);
		if (compare < 0)
			return true;
		return false;
	}
	
	/**
	 * 날짜 데이터 형식 유효성 검사
	 * @param date 			검증할 데이터
	 * @param dateFormat	검증할 데이터 형식
	 * */
	public static boolean checkDate(String date, String dateFormat) {
		try {
			SimpleDateFormat dateFormatParser = new SimpleDateFormat(dateFormat); // 검증할 날짜 포맷 설정
			dateFormatParser.setLenient(false); // false일경우 처리시 입력한 값이 잘못된 형식일 시 오류가 발생
			dateFormatParser.parse(date); // 대상 값 포맷에 적용되는지 확인
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private static String format(Date date, String format, TimeZone timeZone, Locale locale) {
		return DateFormatUtils.format(date, format, timeZone, locale);
	}
	
	private static String format(Calendar calendar, String format, TimeZone timeZone, Locale locale) {
		return DateFormatUtils.format(calendar, format, timeZone, locale);
	}
	
	private static Date parse(String dateString, String dateFormat, Locale locale ) throws ParseException {
		return DateUtils.parseDate(dateString, locale, dateFormat);
	}
	
	private static Date parse(Date date, String dateFormat, Locale locale ) throws ParseException {
		String dateString = DateUtil.format(date, dateFormat, null, null);
		return DateUtils.parseDate(dateString, locale, dateFormat);
	}
}