package br.com.test.miniautorizador.service;

import static org.mockito.Mockito.reset;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.test.miniautorizador.commons.constants.Constants;
import br.com.test.miniautorizador.commons.message.MessageBuilder;
import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.TransactionDTO;
import br.com.test.miniautorizador.domain.document.Card;
import br.com.test.miniautorizador.exception.CardAlreadyExistsException;
import br.com.test.miniautorizador.exception.CardNotFoundException;
import br.com.test.miniautorizador.exception.TransactionException;
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
	void givenCardDTO_whenCreate_thenReturnResponseCardDTO() throws NotFoundException {

		cardService = new CardServiceImpl(cardRepository, cardAssembler);

		BDDMockito.given(cardRepository.findByNumber(ScenarioFactory.CARD_NUMBER_1)).willReturn(Optional.ofNullable(null));

		BDDMockito.given(cardAssembler.fromDTO(ArgumentMatchers.any(CreateCardDTO.class)))
				.willReturn(ScenarioFactory.NEW_CARD);

		BDDMockito.given(cardRepository.insert(Mockito.any(Card.class))).willReturn(ScenarioFactory.NEW_CARD);

		BDDMockito.given(cardAssembler.fromEntity(ArgumentMatchers.any(Card.class)))
				.willReturn(ScenarioFactory.RESPONSE_CARD_DTO);

		var card = cardService.create(ScenarioFactory.NEW_CARD_DTO);

		Assertions.assertThat(card.getNumber()).isEqualTo("1111222233334444");
        reset(cardRepository);
	}
	
	@Test
	void givenCardDTO_whenCreate_thenReturnResponseCardAlreadyExist() throws NotFoundException {

		cardService = new CardServiceImpl(cardRepository, cardAssembler);

		BDDMockito.given(cardRepository.findByNumber(ScenarioFactory.CARD_NUMBER_1))
				.willReturn(Optional.of(ScenarioFactory.NEW_CARD));

		CardAlreadyExistsException thrown = org.junit.jupiter.api.Assertions.assertThrows(
				CardAlreadyExistsException.class,
		           () -> cardService.create(ScenarioFactory.NEW_CARD_DTO));

		Assertions.assertThat(thrown.getMessage()).isEqualTo("Cartão já existe");
        reset(cardRepository);
	}

	@Test
	void givenCardNumber_whenGetBalance_thenReturnDouble() throws NotFoundException {

		cardService = new CardServiceImpl(cardRepository, cardAssembler);

		BDDMockito.given(cardRepository.findByNumber(Mockito.any(String.class)))
				.willReturn(ScenarioFactory.CARD_FOR_BALANCE_OPTIONAL);

		var balance = cardService.getBalance("1111222233336666");

		Assertions.assertThat(balance).isEqualTo(500d);
		reset(cardRepository);
	}
	
	@Test
	void givenCardNumber_whenGetBalance_thenCardNotFound() throws NotFoundException {

		cardService = new CardServiceImpl(cardRepository, cardAssembler);

		BDDMockito.given(cardRepository.findByNumber(Mockito.any(String.class)))
		.willReturn(Optional.ofNullable(null));

		CardNotFoundException thrown = org.junit.jupiter.api.Assertions.assertThrows(
				CardNotFoundException.class,
           () -> cardService.getBalance("1111222233336666"));

		Assertions.assertThat(thrown.getMessage()).isEqualTo("Cartão não existe");
		reset(cardRepository);
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

		Assertions.assertThat(balance - request.getValue()).isEqualTo(300d);
		reset(cardRepository);
	}
	
	@Test
	void givenTransactionDTO_whenDoTransaction_thenReturnCardNotFound() throws NotFoundException {

		var request = TransactionDTO.builder().number("1111222233337777").password("7777").value(2000d).build();

		cardService = new CardServiceImpl(cardRepository, cardAssembler);

		BDDMockito.given(cardRepository.findByNumber(Mockito.any(String.class)))
				.willReturn(Optional.ofNullable(null));
		
		TransactionException thrown = org.junit.jupiter.api.Assertions.assertThrows(
				TransactionException.class,
		           () -> cardService.doTransaction(request));
		
		Assertions.assertThat(thrown.getMessage()).isEqualTo(Constants.CARD_NOT_FOUND);
		reset(cardRepository);
	}
	
	@Test
	void givenTransactionDTO_whenDoTransaction_thenReturnInsufficientBalance() throws NotFoundException {

		var request = TransactionDTO.builder().number("1111222233337777").password("7777").value(2000d).build();

		cardService = new CardServiceImpl(cardRepository, cardAssembler);

		BDDMockito.given(cardRepository.findByNumber(Mockito.any(String.class)))
				.willReturn(ScenarioFactory.CARD_FOR_BALANCE_OPTIONAL);
		
		TransactionException thrown = org.junit.jupiter.api.Assertions.assertThrows(
				TransactionException.class,
		           () -> cardService.doTransaction(request));
		
		Assertions.assertThat(thrown.getMessage()).isEqualTo(Constants.CARD_INSUFFICIENT_BALANCE);
		reset(cardRepository);
	}
	
	@Test
	void givenTransactionDTO_whenDoTransaction_thenReturnInvalidPassword() throws NotFoundException {

		var request = TransactionDTO.builder().number("1111222233337777").password("000").value(2000d).build();

		cardService = new CardServiceImpl(cardRepository, cardAssembler);

		BDDMockito.given(cardRepository.findByNumber(Mockito.any(String.class)))
				.willReturn(ScenarioFactory.CARD_FOR_BALANCE_OPTIONAL);
		
		TransactionException thrown = org.junit.jupiter.api.Assertions.assertThrows(
				TransactionException.class,
		           () -> cardService.doTransaction(request));
		
		Assertions.assertThat(thrown.getMessage()).isEqualTo(Constants.CARD_INVALID_PASSWORD);
		reset(cardRepository);
	}

}
