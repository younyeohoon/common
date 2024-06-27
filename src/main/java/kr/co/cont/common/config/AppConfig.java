package kr.co.cont.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

@Configuration
@PropertySources({
	@PropertySource("classpath:config/properties/appconfig.properties")
})
public class AppConfig {

	@Bean
	public ValidatorFactory validatorFactory() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		return factory;
	}
	
//	public ValidatorFactory validatorFactory2() {
//		javax.validation.Configuration<?> configuration = Validation.byDefaultProvider()
//				.providerResolver( null )
//				.configure();
//		ValidatorFactory factory = configuration.buildValidatorFactory();
//		
//		return factory;
//	}
//	
//	public ValidatorFactory validatorFactory3() {
//		ACMEConfiguration configuration = Validation.byProvider(ACMEProvider.class)
//				.providerResolver(new MyResolverStrategy()) // optionally set the provider resolver
//				.configure();
//		ValidatorFactory factory = configuration.buildValidatorFactory();
//		return factory
//	}

}
