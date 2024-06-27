package kr.co.cont.common.biz.base.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;

import kr.co.cont.common.constants.BaseConstants;
import kr.co.cont.common.constants.ErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseMap extends LinkedHashMap<Object, Object> {

	private static final long serialVersionUID = -1608711493048602865L;
	
	public BaseMap(Map<? extends Object, ? extends Object> m) {
		if (m != null) this.putAll(m);
	}
	
	@SuppressWarnings("unchecked")
	public BaseMap(BaseData baseData) {
		if (baseData != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			this.putAll(objectMapper.convertValue(baseData, Map.class));
		}
	}
	
	/**
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public <T> T convert(Class<T> clazz) {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.convertValue(this, clazz);
	}

	@Override
	public Object put(Object key, Object value) {
		String __key = (String) key;
		
		if (__key.indexOf("_") != -1) __key = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, __key);
		else if (__key.equals(__key.toUpperCase())) __key = __key.toLowerCase();
		return super.put(__key, value);
	}
	
	/**
	 * Map의 값을 복사한다.
	 */
	@Override
	public void putAll(Map<? extends Object, ? extends Object> m) {
		m.forEach((k, v) -> {
			this.put(k, v);
		});
	}
	
	/**
	 * key에 해당하는 값을 설정한다.
	 * key에 해당하는 값이 존재하지 않은 경우 default 값을 설정한다.
	 * 
	 * @param key
	 * @param defaultObj
	 */
	public void setDefault(String key, Object defaultObj) {
		Object obj = get(key);
		if(obj == null || "".equals(obj)) {
			put(key, defaultObj);
		}
	}
	
	/**
	 * key에 해당하는 값을 문자로 반환 한다.
	 * key에 해당하는 값이 존재하지 않으면 "" 값을 반환한다.
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return getString(key, "");
	}
	
	/**
	 * key에 해당하는 값을 문자로 반환 한다.
	 * key에 해당하는 값이 존재하지 않으면 디폴트 값을 반환한다.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getString(String key, String defaultValue) {
		Object obj = super.get(key);
		if (obj == null || "".equals(obj)) {
			return defaultValue;
		} else {
			return String.valueOf(obj);
		}
	}
	
	/**
	 * key에 해당하는 값을 int 타입으로 반환한다.
	 * key에 해당하는 값이 존재하지 않거나 타입이 int 아니면 0을 반환한다.
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}
	
	/**
	 * key에 해당하는 값을 int 타입으로 반환한다.
	 * key에 해당하는 값이 존재하지 않거나 타입이 int 아니면 디폴트 값을 반환한다.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getInt(String key, int defaultValue) {
		Object obj = super.get(key);
		if (obj == null || "".equals(obj)) {
			return defaultValue;
		} else if(obj instanceof Integer) {
			return (int) obj;
		} else {
			try {
				return Integer.parseInt(obj.toString());
			} catch (Exception e) {
				return defaultValue;
			}
		}
	}
	
	/**
	 * key에 해당하는 값을 long 타입으로 반환한다.
	 * key에 해당하는 값이 존재하지 않거나 타입이 long 아니면 0을 반환한다.
	 * 
	 * @param key
	 * @return
	 */
	public long getLong(String key) {
		return getInt(key, 0);
	}
	
	/**
	 * key에 해당하는 값을 long 타입으로 반환한다.
	 * key에 해당하는 값이 존재하지 않거나 타입이 long 아니면 디폴트 값을 반환한다.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getLong(String key, long defaultValue) {
		Object obj = super.get(key);
		if (obj == null || "".equals(obj)) {
			return defaultValue;
		} else if(obj instanceof Long) {
			return (long) obj;
		} else {
			try {
				return Long.parseLong(obj.toString());
			} catch (Exception e) {
				return defaultValue;
			}
		}
	}
	
	/**
	 * key에 해당하는 값이 "true" 또는 "Y" 인 경우에 true 를 반환한다.
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		String val = this.getString(key);
		if (BaseConstants.USE_YES.equals(val)) {
			return true;
		} else {
			return Boolean.parseBoolean(val);
		}
	}
	
	/**
	 * key에 해당하는 값을 BaseMap 타입으로 반환한다.
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public BaseMap getBaseMap(String key) {
		Object obj = super.get(key);
		
		if (obj instanceof BaseMap) {
			return (BaseMap) obj;
		} else if (obj instanceof Map) {
			return new BaseMap((Map<? extends Object, ? extends Object>) obj);
		} else {
			return new BaseMap();
		}
		
	}
	
	/**
	 * key에 해당하는 값을 List 타입으로 반환한다.
	 * key에 해당하는 값의 타입이 List 가 아닌 경우 빈 List를 반환한다.
	 * @param <T>
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key) {
		Object obj = super.get(key);
		
		if (obj instanceof List) {
			return (List<T>) obj;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * ErrorCode를 반환한다.
	 * ErrorCode 값이 존재하지 않는 경우 null를 반환한다.
	 * 
	 * @param key
	 * @return
	 */
	public ErrorCode getErrorCode() {
		Object obj = super.get("ERROR_CODE");
		
		if (obj == null) {
			return null;
		} else {
			return (ErrorCode) obj;
		}
	}
	
	/**
	 * ErrorCode를 설정한다.
	 * 
	 * @param errorCode
	 */
	public void setErrorCode(ErrorCode errorCode, String... errorMessage) {
		super.put("ERROR_CODE", errorCode);
		if (errorMessage != null) {
			super.put("ERROR_MESSAGE", errorMessage);
		}
	}
	
	/**
	 * ErrorCode에 해당하는 메시지를 반환한다.
	 * 메시지 값이 존재하지 않는 경우 null를 반환한다.
	 * 
	 * @param key
	 * @return
	 */
	public Object[] getErrorMessage() {
		Object obj = super.get("ERROR_MESSAGE");
		
		if (obj == null) {
			return null;
		} else {
			return (Object[]) obj;
		}
	}
	
}
