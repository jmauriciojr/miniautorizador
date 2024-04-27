package br.com.test.miniautorizador.service;

import static br.com.test.miniautorizador.commons.constants.Constants.CARD_INITIAL_BALANCE;
import static br.com.test.miniautorizador.commons.constants.Constants.CARD_INSUFFICIENT_BALANCE;
import static br.com.test.miniautorizador.commons.constants.Constants.CARD_INVALID_PASSWORD;
import static br.com.test.miniautorizador.commons.constants.Constants.CARD_NOT_FOUND;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_EVENT_INFO;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_ENTITY;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_EVENT;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_MESSAGE;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_METHOD;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.ResponseCardDTO;
import br.com.test.miniautorizador.domain.TransactionDTO;
import br.com.test.miniautorizador.domain.document.Card;
import br.com.test.miniautorizador.exception.CardAlreadyExistsException;
import br.com.test.miniautorizador.exception.CardNotFoundException;
import br.com.test.miniautorizador.exception.TransactionException;
import br.com.test.miniautorizador.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

	private final CardRepository cardRepository;
	private final CardAssembler cardAssembler;

	@Transactional
	@Override
	public ResponseCardDTO create(CreateCardDTO cardDTO) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_ENTITY + LOG_KEY_EVENT, "Save: ", "create",
				cardDTO.getNumber(), LOG_EVENT_INFO);

		this.validateCard(cardDTO);
		return this.cardAssembler.fromEntity(this.cardRepository.insert(this.initializeBalance(cardDTO)));
	}

	@Override
	public Double getBalance(String cardNumber) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_ENTITY + LOG_KEY_EVENT, "Balance: ", "getBalance",
				cardNumber, LOG_EVENT_INFO);

		var card = this.cardRepository.findByNumber(cardNumber)
				.orElseThrow(() -> new CardNotFoundException("Cartão não existe",
						"Não foi possível encontrar um cartão com o número: " + cardNumber));
		return card.getBalance();
	}

	@Transactional
	@Override
	public synchronized void doTransaction(TransactionDTO transactionDTO) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_ENTITY + LOG_KEY_EVENT, "Do Transaction: ", "doTransaction",
				transactionDTO.getNumber(), LOG_EVENT_INFO);

		var card = this.cardRepository.findByNumber(transactionDTO.getNumber())
				.orElseThrow(() -> new TransactionException(CARD_NOT_FOUND, CARD_NOT_FOUND));

		this.validatePassword(card, transactionDTO.getPassword());
		this.validateBalance(card, transactionDTO.getValue());

		card.setBalance(card.getBalance() - transactionDTO.getValue());
		this.cardRepository.save(card);

	}

	private Card initializeBalance(CreateCardDTO cardDTO) {
		var card = this.cardAssembler.fromDTO(cardDTO);
		card.setBalance(CARD_INITIAL_BALANCE);

		return card;
	}

	private void validateCard(CreateCardDTO cardDTO) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_ENTITY + LOG_KEY_EVENT, "Validate: ", "validateCard",
				cardDTO.getNumber(), LOG_EVENT_INFO);

		this.cardRepository.findByNumber(cardDTO.getNumber()).ifPresent(card -> {
			throw new CardAlreadyExistsException("Cartão já existe",
					"Não foi possível criar um novo cartão com o mesmo número: " + cardDTO.getNumber(),
					this.cardAssembler.fromEntity(card));
		});
	}

	private void validatePassword(Card card, String password) {
		if (!card.getPassword().equals(password)) {
			throw new TransactionException(CARD_INVALID_PASSWORD, CARD_INVALID_PASSWORD);
		}
	}

	private void validateBalance(Card card, Double transactionValue) {
		if (card.getBalance() < transactionValue) {
			throw new TransactionException(CARD_INSUFFICIENT_BALANCE, CARD_INSUFFICIENT_BALANCE);
		}
	}
}
