package br.com.test.miniautorizador.service;

import java.math.BigDecimal;

import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.ResponseCardDTO;
import br.com.test.miniautorizador.domain.TransactionDTO;

public interface CardService {

	ResponseCardDTO create(CreateCardDTO cardDTO) ;

	BigDecimal getBalance(String cardNumber);

	void doTransaction(TransactionDTO transactionDTO);
}
