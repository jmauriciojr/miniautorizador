package br.com.test.miniautorizador.service;

import static br.com.test.miniautorizador.commons.constants.Constants.INITIAL_BALANCE;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_EVENT_INFO;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_ENTITY;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_EVENT;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_MESSAGE;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_METHOD;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.ResponseCardDTO;
import br.com.test.miniautorizador.domain.document.Card;
import br.com.test.miniautorizador.exception.CardNotFoundException;
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

	private Card initializeBalance(CreateCardDTO cardDTO) {
		var card = this.cardAssembler.fromDTO(cardDTO);
		card.setBalance(INITIAL_BALANCE);
		
		return card;
	}
	

}
