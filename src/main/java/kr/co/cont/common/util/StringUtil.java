package kr.co.cont.common.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <b>기능</b> :
 * <p>
 * 문자열관련 유틸 클래스입니다.
 *
 * @author Administrator
 * @since 1.0
 */

public class StringUtil {

	/**
	 * 공백문자
	 */
	public final static char WHITE_SPACE = ' ';

	static String m_whiteSpace = " \t\n\r";

	static char m_citChar = '"';

	/**
	 * 문자열에서 Property형태의 값을 추출한다. Property 형태란 'key=value'형식으로 되어있는 것을 의미한다. 단,
	 * 여기에서는 하나의 문자열을 사용할 수 있게 하기 위해 각 Property의 구분자로 '::'를 사용한다. Example
	 *
	 * @param source       프로퍼티를 검색할 원본 문자열
	 * @param key          검색할 키 문자열
	 * @param defaultValue 해당 Key에 해당하는 값이 없을때 반환할 기본값
	 * @return 검색된 Property의 Value
	 *
	 *         <code>
	 * String source = "key1=value1::key2=value2::key3=value3";<br>
	 * String key = "key2";<br>
	 * String value = StringUtil.getParam(source,key,"Default Value");<br>
	 * </code> 위의 예제의 결과 값은 "value2" 이다.
	 */
	public static String getParam(String source, String key, String defaultValue) {
		if (source == null || key == null) {
			return defaultValue;
		}
		int i = source.indexOf(key + "=");
		if (i < 0) {
			return defaultValue;
		}
		int j = i + key.length() + 1;
		int k = source.indexOf("::", j);
		if (k < 0) {
			k = source.length();
		}
		try {
			return source.substring(j, k);
		} catch (Exception _ex) {
			return defaultValue;
		}
	}

	/**
	 * 문자열을 좌측 정렬한다. 이때 문자열뒤에 줄임표는 넣지 않는다.<br>
	 *
	 * @param source 원본 문자열
	 * @param length 정렬이 이루어질 길이
	 * @return 정렬이 이루어진 문자열
	 *
	 *         <code>
	 * String source = "ABCDEFG";<br>
	 * String result = StringUtil.alignLeft(source, 10);<br>
	 * </code> <code>result</code>는 <code>"ABCDEFG   "</code> 을 가지게 된다.
	 */
	public static String alignLeft(String source, int length) {

		return alignLeft(source, length, false);
	}

	/**
	 * 문자열을 좌측부터 원하는만큼 자른다.(원한다면 끝에 ...을 붙인다.)<br>
	 *
	 * @param source     원본 문자열
	 * @param length     정렬이 이루어질 길이
	 * @param isEllipsis 마지막에 줄임표("...")의 여부
	 * @return 정렬이 이루어진 문자열
	 *
	 *         <code>
	 * String source = "ABCDEFG";<br>
	 * String result = StringUtil.alignLeft(source, 5, true);<br>
	 * </code> <code>result</code>는 <code>"AB..."</code> 을 가지게 된다.
	 */
	public static String alignLeft(String source, int length, boolean isEllipsis) {

		if (source.length() <= length) {

			StringBuffer temp = new StringBuffer(source);
			for (int i = 0; i < (length - source.length()); i++) {
				temp.append(WHITE_SPACE);
			}
			return temp.toString();
		} else {
			if (isEllipsis) {

				StringBuffer temp = new StringBuffer(length);
				temp.append(source.substring(0, length - 3));
				temp.append("...");
				return temp.toString();
			} else {
				return source.substring(0, length);
			}
		}
	}

	/**
	 * 문자열을 우측 정렬한다. 이때 문자열뒤에 줄임표는 넣지 않는다.<br>
	 *
	 * @param source 원본 문자열
	 * @param length 정렬이 이루어질 길이
	 * @return 정렬이 이루어진 문자열
	 *
	 *         <code>
	 * String source = "ABCDEFG";<br>
	 * String result = StringUtil.alignRight(source, 10);<br>
	 * </code> <code>result</code>는 <code>"   ABCDEFG"</code> 을 가지게 된다.
	 */
	public static String alignRight(String source, int length) {

		return alignRight(source, length, false);
	}

	/**
	 * 문자열을 우측 정렬한다.(원한다면 끝에 ...을 붙인다.)<br>
	 *
	 * @param source     원본 문자열
	 * @param length     정렬이 이루어질 길이
	 * @param isEllipsis 마지막에 줄임표("...")의 여부
	 * @return 정렬이 이루어진 문자열
	 *
	 *         <code>
	 * String source = "ABCDEFG";<br>
	 * String result = StringUtil.alignRight(source, 5, true);<br>
	 * </code> <code>result</code>는 <code>"...AB"</code> 을 가지게 된다.
	 */
	public static String alignRight(String source, int length, boolean isEllipsis) {

		if (source.length() <= length) {

			StringBuffer temp = new StringBuffer(length);
			for (int i = 0; i < (length - source.length()); i++) {
				temp.append(WHITE_SPACE);
			}
			temp.append(source);
			return temp.toString();
		} else {
			if (isEllipsis) {

				StringBuffer temp = new StringBuffer(length);
				temp.append(source.substring(0, length - 3));
				temp.append("...");
				return temp.toString();
			} else {
				return source.substring(0, length);
			}
		}
	}

	/**
	 * 문자열을 중앙 정렬한다. 이때 문자열뒤에 줄임표는 넣지 않는다. 만약 공백이 홀수로 남는다면 오른쪽에 들어 간다.<br>
	 *
	 * @param source 원본 문자열
	 * @param length 정렬이 이루어질 길이
	 * @return 정렬이 이루어진 문자열
	 *
	 *         <code>
	 * String source = "ABCDEFG";<br>
	 * String result = StringUtil.alignCenter(source, 10);<br>
	 * </code> <code>result</code>는 <code>" ABCDEFG "</code> 을 가지게 된다.
	 */
	public static String alignCenter(String source, int length) {

		return alignCenter(source, length, false);
	}

	/**
	 * 문자열을 중앙 정렬한다. 만약 공백이 홀수로 남는다면 오른쪽에 들어 간다.<br>
	 *
	 * @param source     원본 문자열
	 * @param length     정렬이 이루어질 길이
	 * @param isEllipsis 마지막에 줄임표("...")의 여부
	 * @return 정렬이 이루어진 문자열
	 *
	 *         <code>
	 * String source = "ABCDEFG";<br>
	 * String result = StringUtil.alignCenter(source, 5,true);<br>
	 * </code> <code>result</code>는 <code>"AB..."</code> 을 가지게 된다.
	 */
	public static String alignCenter(String source, int length, boolean isEllipsis) {
		if (source.length() <= length) {

			StringBuffer temp = new StringBuffer(length);
			int leftMargin = (int) (length - source.length()) / 2;

			int rightMargin;
			if ((leftMargin * 2) == (length - source.length())) {
				rightMargin = leftMargin;
			} else {
				rightMargin = leftMargin + 1;
			}

			for (int i = 0; i < leftMargin; i++) {
				temp.append(WHITE_SPACE);
			}

			temp.append(source);

			for (int i = 0; i < rightMargin; i++) {
				temp.append(WHITE_SPACE);
			}

			return temp.toString();
		} else {
			if (isEllipsis) {

				StringBuffer temp = new StringBuffer(length);
				temp.append(source.substring(0, length - 3));
				temp.append("...");
				return temp.toString();
			} else {
				return source.substring(0, length);
			}
		}

	}

	/**
	 * 문자열의 제일 처음글자를 대문자화 한다.<br>
	 *
	 * @param s 원본 문자였
	 * @return 대문자화 된 문자열
	 *
	 *         <code>
	 * String source = "abcdefg";<br>
	 * String result = StringUtil.capitalize(source);<br>
	 * </code> <code>result</code>는 <code>"Abcdefg"</code> 을 가지게 된다.
	 */
	public static String capitalize(String s) {
		return !s.equals("") && s != null ? s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() : s;
	}

	/**
	 * 원본 문자열에서 target 문자열을 찾아 해당 boolean으로 치환한다.<br>
	 *
	 * @param s    원본 문자열
	 * @param s1   치환될 문자열
	 * @param flag 치환되어 들어갈 boolean
	 * @return 치환된 문자열
	 *
	 *         <code>
	 * String source = "Onwer is [B] statues.";<br>
	 * String result = StringUtil.replace(source, "[B]",true);<br>
	 * </code> <code>result</code>는 <code>"Onwer is true statues."</code> 을 가지게 된다.
	 */
	public static String replace(String s, String s1, boolean flag) {
		return replace(s, s1, String.valueOf(flag));
	}

	/**
	 * 원본 문자열에서 target 문자열을 찾아 해당 숫자로 치환한다.<br>
	 *
	 * @param s  원본 문자열
	 * @param s1 치환될 문자열
	 * @param i  치환되어 들어갈 숫자
	 * @return 치환된 문자열
	 *
	 *         <code>
	 * String source = "Onwer is [I] statues.";<br>
	 * String result = StringUtil.replace(source, "[I]",15);<br>
	 * </code> <code>result</code>는 <code>"Onwer is 15 statues."</code> 을 가지게 된다.
	 */
	public static String replace(String s, String s1, int i) {
		return replace(s, s1, String.valueOf(i));
	}

	/**
	 * 원본 문자열에서 target 문자열을 찾아 치환한다.<br>
	 *
	 * @param s  원본 문자열
	 * @param s1 치환될 문자열
	 * @param s2 치환되어 들어갈 문자열
	 * @return 치환된 문자열
	 *
	 *         <code>
	 * String source = "Onwer is [I] statues.";<br>
	 * String result = StringUtil.replace(source, "[I]","fool");<br>
	 * </code> <code>result</code>는 <code>"Onwer is fool statues."</code> 을 가지게 된다.
	 */
	public static String replace(String s, String s1, String s2) {
		StringBuffer stringbuffer = new StringBuffer(s.length());
		int j = 0;
		for (int i = s.indexOf(s1, j); i != -1; i = s.indexOf(s1, j)) {
			stringbuffer.append(s.substring(j, i));
			stringbuffer.append(s2);
			j = i + s1.length();
		}

		if (j < s.length()) {
			stringbuffer.append(s.substring(j));
		}
		return stringbuffer.toString();
	}
	
	/**
	 * 원본 문자열에서 target 문자열을 찾아 치환한다.<br>
	 * 
	 * <code>
	 * String source = "{@}, {@}, {@} is {@}.";<br>
	 * Object[] target= {1, 2, 3, "Number"};<br>
	 * String result = StringUtil.replace(source, "{@}", target);<br>
	 * </code> <code>result</code>는 <code>"1, 2, 3 is Number."</code> 을 가지게 된다.
	 * 
	 * @param s  원본 문자
	 * @param s1 치환될 문자
	 * @param arr 치환되어 들어갈 객체열
	 * @return 치환된 문자열
	 * 
	 */
	public static String replace(String s, String s1, Object[] arr) {
		return String.format(replace(s, s1, "%s"), arr);
	}

	/**
	 * 배열을 받아 연결될 문자열로 연결한다. 이때 각 엘레멘트 사이에 구분문자열을 추가한다.<br>
	 *
	 * @param aobj 문자열로 만들 배열
	 * @param s    각 엘레멘트의 구분 문자열
	 * @return 연결된 문자열
	 *
	 *         <code>
	 * String[] source = new String[] {"AAA","BBB","CCC"};<br>
	 * String result = StringUtil.join(source,"+");<br>
	 * </code> <code>result</code>는 <code>"AAABBBCCC"</code>를 가지게 된다.
	 */
	public static String join(Object aobj[], String s) {
		StringBuffer stringbuffer = new StringBuffer();
		int i = aobj.length;
		if (i > 0) {
			stringbuffer.append(aobj[0].toString());
		}
		for (int j = 1; j < i; j++) {
			stringbuffer.append(s);
			stringbuffer.append(aobj[j].toString());
		}

		return stringbuffer.toString();
	}

	/**
	 * 문자열을 지정된 Token Seperator로 Tokenize한다.(문자열 배열을 리턴)<br>
	 *
	 * @param s  원본 문지열
	 * @param s1 Token Seperators
	 * @return 토큰들의 배열
	 *
	 *         <code>
	 * String source = "Text token\tis A Good\nAnd bad.";<br>
	 * String[] result = StringUtil.split(source, " \t\n");<br>
	 * </code> <code>result</code>는
	 *         <code>"Text","token","is","A","Good","And","bad."</code> 를 가지게 된다.
	 */
	public static String[] split(String s, String s1) {
		StringTokenizer stringtokenizer = new StringTokenizer(s, s1);
		int i = stringtokenizer.countTokens();
		String as[] = new String[i];
		for (int j = 0; j < i; j++) {
			as[j] = stringtokenizer.nextToken();
		}

		return as;
	}

	/**
	 * 원본 문자열을 일반적인 공백문자(' ','\n','\t','\r')로 토큰화 한다.
	 *
	 * @param s 원본문자열
	 * @return 토큰의 배열
	 *
	 *         <code>
	 * String source = "Text token\tis A Good\nAnd\rbad.";<br>
	 * String[] result = StringUtil.splitWords(source);<br>
	 * </code> <code>result</code>는
	 *         <code>"Text","token","is","A","Good","And","bad."</code> 를 가지게 된다.
	 */
	public static String[] splitWords(String s) {
		return splitWords(s, m_whiteSpace);
	}

	/**
	 * 원본 문자열을 일반적인 공백문자(' ','\n','\t','\r')로 토큰화 한다.<br>
	 * 겹따옴표('"') 안의 내용은 하나의 토큰으로 판단하여 문자열을 토큰화 한다.
	 *
	 * @param s  원본 문자열
	 * @param s1 Token Seperators
	 * @return Description of the Returned Value
	 *
	 *         <code>
	 * String source = "Text token\tis A \"Good day\"\nAnd\r\"bad day.\"";<br>
	 * String[] result = StringUtil.splitWords(source);<br>
	 * </code> <code>result</code>는
	 *         <code>"Text","token","is","A","Good day","And","bad day."</code> 를
	 *         가지게 된다.
	 */
	public static String[] splitWords(String s, String s1) {
		boolean flag = false;
		StringBuffer stringbuffer = null;
		Vector<StringBuffer> vector = new Vector<>();
		for (int i = 0; i < s.length();) {
			char c = s.charAt(i);
			if (!flag && s1.indexOf(c) != -1) {
				if (stringbuffer != null) {
					vector.addElement(stringbuffer);
					stringbuffer = null;
				}
				for (; i < s.length() && s1.indexOf(s.charAt(i)) != -1; i++) {
					;
				}
			} else {
				if (c == m_citChar) {
					if (flag) {
						flag = false;
					} else {
						flag = true;
					}
				} else {
					if (stringbuffer == null) {
						stringbuffer = new StringBuffer();
					}
					stringbuffer.append(c);
				}
				i++;
			}
		}

		if (stringbuffer != null) {
			vector.addElement(stringbuffer);
		}
		String as[] = new String[vector.size()];
		for (int j = 0; j < vector.size(); j++) {
			as[j] = new String((StringBuffer) vector.elementAt(j));
		}

		return as;
	}

	/**
	 * 배열을 Vector로 만든다.<br>
	 *
	 * @param array 원본 배열
	 * @return 배열과 같은 내용을 가지는 Vector
	 *
	 */
	public static Vector<Object> toVector(Object[] array) {
		if (array == null) {
			return null;
		}

		Vector<Object> vec = new Vector<>(array.length);

		for (int i = 0; i < array.length; i++) {
			vec.add(i, array[i]);
		}
		return vec;
	}

	/**
	 * 문자열의 배열을 소팅한다.
	 *
	 * @param array 원본 배열
	 * @return 배열과 같은 내용을 가지는 Vector
	 */
	public static String[] sortStringArray(String[] source) {

		Arrays.sort(source);

		return source;
	}

	/**
	 * 문자열의 Enemration을 소팅된 배열로 반환한다.
	 *
	 * @param source 원본 열거형
	 * @return 열거형의 값을 가진 배열
	 */
	public static String[] sortStringArray(Enumeration<Object> source) {
		Vector<Object> buf = new Vector<>();
		while (source.hasMoreElements()) {
			buf.add(source.nextElement());
		}
		String[] buf2 = new String[buf.size()];

		for (int i = 0; i < buf.size(); i++) {

			Object obj = buf.get(i);
			if (obj instanceof String) {
				buf2[i] = (String) obj;
			} else {
				throw new IllegalArgumentException("Not String Array");
			}
		}
		Arrays.sort(buf2);
		return buf2;
	}

	/**
	 * 문자열이 입력한 길이보다 남는 공백에 좌측정렬후 원하는 문자를 삽입힌다.
	 *
	 * @param source 원본 문자열
	 * @param length 정렬이 이루어질 길이
	 * @param ch     공백에 삽입할 원하는 문자
	 * @return 결과 문자열
	 *
	 *         <code>
	 * String source = "ABC"<br>
	 * String result = StringUtil.insertLeftChar(source, 5, '*');<br>
	 * </code> <code>result</code>는 <code>"ABC**"</code> 을 가지게 된다.
	 */
	public static String insertLeftChar(String source, int length, char ch) {

		StringBuffer temp = new StringBuffer(length);

		if (source.length() <= length) {

			for (int i = 0; i < (length - source.length()); i++) {
				temp.append(ch);
			}
			temp.append(source);
		}
		return temp.toString();
	}
	
	/**
	 * 문자열을 거꾸로 뒤집어서 출력합니다.
	 * 
	 * @param source 원본 문자열
	 * @return 결과 문자열
	 */
	public static String reverse(String source) {
		StringBuffer sb = new StringBuffer(source);
		return sb.reverse().toString();
	}

	/**
	 * 포멧에 맞는 날짜의 역순에 자리수만큰 외쪽에 0 을 추가한 일련번호를 합하여 생성된 값을 될려준다. 
	 * 
	 * @param dateFormat 날짜 포멧
	 * @param seqNo 일련번호
	 * @param size 0을 왼쪽 패딩할 자리수
	 * @return
	 */
	public static String makeIdByDate(String dateFormat, int seqNo, int size ) {
		String currentDate = DateUtil.getCurrentDateString(dateFormat);
		return StringUtil.reverse(currentDate) + StringUtils.leftPad(String.valueOf(seqNo), size, "0");
	}

	/**
	 * Json 형태의 문자열을 반환한다.
	 * 
	 * @param arg
	 * @return
	 */
	public static String toJsonString(Object arg) {
		try {
			return (new ObjectMapper()).writeValueAsString(arg);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(json, Map.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * 램덤 숫자를 생성하여 문자열로 반환한다.
	 * 
	 * @param length 문자열 길이
	 * @return
	 */
	public static String random(int length) {
		String num = "";
		
		try {
			SecureRandom ran = SecureRandom.getInstanceStrong();
			num = String.valueOf(ran.nextInt((int) Math.pow(10, length)));
		} catch (NoSuchAlgorithmException e) {
			num = "000";
		}
		return StringUtils.leftPad(num, length, "0");
	}
	
}