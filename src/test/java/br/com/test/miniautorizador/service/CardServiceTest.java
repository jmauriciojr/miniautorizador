package br.com.test.miniautorizador.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.test.miniautorizador.commons.message.MessageBuilder;
import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.TransactionDTO;
import br.com.test.miniautorizador.domain.document.Card;
import br.com.test.miniautorizador.feature.ScenarioFactory;
import br.com.test.miniautorizador.repository.CardRepository;
import javassist.NotFoundException;;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

	private CardService cardService;

	@Mock
	private CardRepository cardRepository;

	@Mock
	private CardAssembler cardAssembler;

	@Mock
	private MessageBuilder messageBuilder;

	@Test
	void givenUserDTO_whenCreate_thenReturnResponseCardDTO() throws NotFoundException {

		cardService = new CardServiceImpl(cardRepository, cardAssembler);

		BDDMockito.given(cardRepository.existsByNumber(ScenarioFactory.CARD_NUMBER_1)).willReturn(false);

		BDDMockito.given(cardAssembler.fromDTO(ArgumentMatchers.any(CreateCardDTO.class)))
				.willReturn(ScenarioFactory.NEW_CARD);

		BDDMockito.given(cardRepository.insert(Mockito.any(Card.class))).willReturn(ScenarioFactory.NEW_CARD);

		BDDMockito.given(cardAssembler.fromEntity(ArgumentMatchers.any(Card.class)))
				.willReturn(ScenarioFactory.RESPONSE_CARD_DTO);

		var card = cardService.create(ScenarioFactory.NEW_CARD_DTO);

		Assertions.assertEquals(card.getNumber(), "1111222233334444");
	}

	@Test
	void givenCardNumber_whenGetBalance_thenReturnDouble() throws NotFoundException {

		cardService = new CardServiceImpl(cardRepository, cardAssembler);

		BDDMockito.given(cardRepository.findByNumber(Mockito.any(String.class)))
				.willReturn(ScenarioFactory.CARD_FOR_BALANCE_OPTIONAL);

		var balance = cardService.getBalance("1111222233336666");

		Assertions.assertEquals(balance, 500d);
	}

	@Test
	void givenTransactionDTO_whenDoTransaction_thenReturnVoid() throws NotFoundException {

		var request = TransactionDTO.builder().number("1111222233337777").password("7777").value(200d).build();

		cardService = new CardServiceImpl(cardRepository, cardAssembler);

		BDDMockito.given(cardRepository.findByNumber(Mockito.any(String.class)))
				.willReturn(ScenarioFactory.CARD_FOR_BALANCE_OPTIONAL);

		cardService.doTransaction(request);

		BDDMockito.given(cardRepository.findByNumber(Mockito.any(String.class)))
				.willReturn(ScenarioFactory.NEW_CARD_TRANSACTION_OPTIONAL);

		var balance = cardService.getBalance("1111222233337777");

		Assertions.assertEquals(balance - request.getValue(), 300d);
	}

}
