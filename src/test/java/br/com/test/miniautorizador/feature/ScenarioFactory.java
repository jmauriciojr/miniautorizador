package br.com.test.miniautorizador.feature;

import java.math.BigDecimal;
import java.util.Optional;

import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.ResponseCardDTO;
import br.com.test.miniautorizador.domain.TransactionDTO;
import br.com.test.miniautorizador.domain.document.Card;

public class ScenarioFactory {

	public static final Optional<Card> NEW_CARD_OPTIONAL = Optional.of(newCard());
	public static final Card NEW_CARD = newCard();
	public static final CreateCardDTO NEW_CARD_DTO = createCardDTO();
	public static final ResponseCardDTO RESPONSE_CARD_DTO = responseCardDTO();
	public static final String CARD_NUMBER_1 = "1111222233334444";
	public static final Optional<Card> CARD_FOR_BALANCE_OPTIONAL = Optional.of(getCardForBalance());
	public static final Card CARD_FOR_BALANCE = getCardForBalance();
	public static final Optional<Card> NEW_CARD_TRANSACTION_OPTIONAL = Optional.of(newCardTransaction());
	public static final TransactionDTO TRANSACTION_DTO = mountTransactionDTO();
	

	public static Card newCard() {
		return Card.builder().number("1111222233334444").password("1111").balance(BigDecimal.valueOf(500)).build();
	}

	private static CreateCardDTO createCardDTO() {
		return CreateCardDTO.builder().number("1111222233334444").password("1111").build();
	}
	
	private static ResponseCardDTO responseCardDTO() {
		return ResponseCardDTO.builder().number("1111222233334444").password("1111").build();
	}

	public static Card getCardForBalance() {
		return Card.builder().number("1111222233337777").password("7777").balance(BigDecimal.valueOf(500)).build();
	}
	
	public static Card newCardTransaction() {
		return Card.builder().number("1111222233337777").password("7777").balance(BigDecimal.valueOf(500)).build();
	}
	
	public static TransactionDTO mountTransactionDTO() {
		return TransactionDTO.builder().number("1111222233337777").password("7777").value(BigDecimal.valueOf(150)).build();
	}

}
