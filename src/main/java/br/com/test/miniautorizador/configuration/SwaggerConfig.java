package br.com.test.miniautorizador.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig  {

	@Value("${springdoc.documentation.swagger.email}")
	private String email;
	
	@Value("${springdoc.documentation.swagger.url}")
	private String url;
	
	@Value("${springdoc.documentation.swagger.team}")
	private String team;

	@Value("${springdoc.documentation.swagger.api.title}")
	private String title;
	
	@Value("${springdoc.documentation.swagger.api.description}")
	private String description;
	
	@Value("${springdoc.documentation.swagger.api.version}")
	private String version;

	@Bean
	OpenAPI usersMicroserviceOpenAPI() {
		var contact = new Contact().name(team).email(email).url(url);
		
		return new OpenAPI().info(new Info()
				.title(title)
				.description(description)
				.contact(contact)
				.version(version));
	}

}