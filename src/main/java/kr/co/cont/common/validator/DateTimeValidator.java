package kr.co.cont.common.validator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateTimeValidator implements ConstraintValidator<DateTime, String> {

	private String pattern;
	
	@Override
	public void initialize(DateTime constraintAnnotation) {
		this.pattern = constraintAnnotation.pattern();
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.length() == 0)
			return true;
		try {
			if ("HHmmss".equals(this.pattern.replaceAll("[^a-zA-Z]", ""))) {
				LocalTime.from(LocalTime.parse(value, DateTimeFormatter.ofPattern(this.pattern)));
			} else {
				LocalDate.from(LocalDate.parse(value, DateTimeFormatter.ofPattern(this.pattern)));
			}
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
