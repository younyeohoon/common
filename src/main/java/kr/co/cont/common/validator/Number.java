package kr.co.cont.common.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})  // 메소드와 필드에 사용
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumberValidator.class)
public @interface Number {

	String message() default " 숫자 형식이 아닙니다.";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
	
//	String regexp() default "^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$";
	
	int min() default 0;
	
	int max() default 999999999;
	
}
