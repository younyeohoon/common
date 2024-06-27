package kr.co.cont.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumberValidator implements ConstraintValidator<Number, Integer> {
	
	private int min;
	private int max;
	
	@Override
	public void initialize(Number constraintAnnotation) {
		this.min = constraintAnnotation.min();
		this.max = constraintAnnotation.max();
	}
	
	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		if (log.isDebugEnabled()) log.debug("value {}, min = {}, max = {}", value, min, max);
		
		int len = String.valueOf(value).length();
		
		return (len >= min && len <= max);
	}
	
}
