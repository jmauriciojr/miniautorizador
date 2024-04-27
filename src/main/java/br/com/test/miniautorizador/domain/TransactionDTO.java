package br.com.test.miniautorizador.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TransactionDTO {

	@JsonProperty("numeroCartao")
	@NotNull(message = "O número do cartão é obrigatório.")
    private String number;
	
	@JsonProperty("senhaCartao")
	@NotNull(message = "A senha do cartão é obrigatória.")
    private String password;
	
	@JsonProperty("valor")
	@NotNull(message = "O Valor da transação é obrigatóri0.")
    private Double value;
}
