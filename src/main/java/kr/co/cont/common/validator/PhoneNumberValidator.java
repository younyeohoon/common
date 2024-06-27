package kr.co.cont.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
	
	private String regexp;
	
	@Override
	public void initialize(PhoneNumber constraintAnnotation) {
		this.regexp = constraintAnnotation.regexp();
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (log.isDebugEnabled()) log.debug("value {}, regexp = {}", value, regexp);
		return value.matches(regexp);
	}

	
}
