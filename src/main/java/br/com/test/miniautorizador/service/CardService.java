package br.com.test.miniautorizador.service;

import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.ResponseCardDTO;
import br.com.test.miniautorizador.domain.TransactionDTO;

public interface CardService {

	ResponseCardDTO create(CreateCardDTO cardDTO) ;

	Double getBalance(String cardNumber);

	void doTransaction(TransactionDTO transactionDTO);
}
