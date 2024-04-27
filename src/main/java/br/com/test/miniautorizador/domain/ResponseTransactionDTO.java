package br.com.test.miniautorizador.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ResponseTransactionDTO {

    private String number;
    private Double balance;
}
