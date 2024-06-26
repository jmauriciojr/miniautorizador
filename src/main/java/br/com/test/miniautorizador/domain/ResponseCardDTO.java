package br.com.test.miniautorizador.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ResponseCardDTO {

	@JsonProperty("numeroCartao")
    private String number;
	
	@JsonProperty("senha")
    private String password;
}
