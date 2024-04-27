package br.com.test.miniautorizador.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.document.Card;
import br.com.test.miniautorizador.exception.CardNotFoundException;
import br.com.test.miniautorizador.feature.ScenarioFactory;
import br.com.test.miniautorizador.repository.CardRepository;
import br.com.test.miniautorizador.service.CardAssembler;
import br.com.test.miniautorizador.service.CardService;


@WebMvcTest(CardController.class)
class CardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CardService cardService;

	@MockBean
	private CardRepository cardRepository;

	@MockBean
	private CardAssembler cardAssembler;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void givenCard_whenCreate_ExpectedOK() throws Exception {

		when(cardRepository.existsByNumber(ScenarioFactory.CARD_NUMBER_1)).thenReturn(false);
		when(cardAssembler.fromDTO(ArgumentMatchers.any(CreateCardDTO.class))).thenReturn(ScenarioFactory.NEW_CARD);
		when(cardRepository.insert(Mockito.any(Card.class))).thenReturn(ScenarioFactory.NEW_CARD);

		when(cardAssembler.fromEntity(ArgumentMatchers.any(Card.class))).thenReturn(ScenarioFactory.RESPONSE_CARD_DTO);
		
		ResultActions response = mockMvc.perform(post("/cartoes")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(ScenarioFactory.NEW_CARD_DTO)));

		response.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(content().string(containsString("numeroCartao, 1111222233334444")));
		
        verify(cardService, VerificationModeFactory.times(1)).create(Mockito.any());
        reset(cardService);
	}

	@Test
	void givenCardNumber_whenGetBalance_ExpectedOK() throws Exception {

		when(cardRepository.findByNumber(Mockito.any(String.class)))
				.thenReturn(ScenarioFactory.CARD_FOR_BALANCE_OPTIONAL);

		when(cardService.getBalance(anyString())).thenReturn(500d);

		mockMvc.perform(get("/cartoes/1111222233334444")).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
		
        verify(cardService, VerificationModeFactory.times(1)).getBalance(Mockito.any());
        reset(cardService);
	}

	@Test
	void givenCardNumber_whenGetBalance_ExpectedError() throws Exception {

		when(cardRepository.findByNumber(Mockito.any(String.class))).thenReturn(null);

		when(cardService.getBalance(anyString())).thenThrow(CardNotFoundException.class);

		mockMvc.perform(get("/cartoes/1111222233334444")).andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
		
        verify(cardService, VerificationModeFactory.times(1)).getBalance(Mockito.any());
        reset(cardService);
	}

}
