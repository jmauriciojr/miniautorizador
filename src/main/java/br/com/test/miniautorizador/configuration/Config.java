package br.com.test.miniautorizador.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.test.miniautorizador.commons.message.MessageBuilder;


@Configuration
public class Config {

	@Bean
	MessageBuilder getMessageBuilder(MessageSource messageSource) {
		return new MessageBuilder(messageSource);
	}

	@Bean
	ModelMapper modelMapper() {
		var mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return mapper;
	}
    @Bean
    MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource
				= new ReloadableResourceBundleMessageSource();

		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setUseCodeAsDefaultMessage(true);

		return messageSource;
	}

    @Bean
    LocalValidatorFactoryBean getValidator() {
		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(messageSource());
		return bean;
	}

    @Bean
    ObjectMapper objectMapper() {
		var object = new ObjectMapper();
		object.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return object;
	}
}
