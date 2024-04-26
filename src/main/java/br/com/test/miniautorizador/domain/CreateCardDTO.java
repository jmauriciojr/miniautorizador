package br.com.test.miniautorizador.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CreateCardDTO {

	@NotNull(message = "O número do cartão é obrigatório.")
    private String number;
	
	@NotNull(message = "A senha do cartão é obrigatória.")
    private String password;
}
