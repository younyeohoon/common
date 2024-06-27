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
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {

	String message() default " 전화번호 형식이 아닙니다.";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
	
	String regexp() default "^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$";
	
}
