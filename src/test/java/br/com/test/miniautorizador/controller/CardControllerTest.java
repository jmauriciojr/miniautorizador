package br.com.test.miniautorizador.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import br.com.test.miniautorizador.domain.TransactionDTO;
import br.com.test.miniautorizador.exception.CardNotFoundException;
import br.com.test.miniautorizador.exception.TransactionException;
import br.com.test.miniautorizador.feature.ScenarioFactory;
import br.com.test.miniautorizador.repository.CardRepository;
import br.com.test.miniautorizador.service.CardService;

@WebMvcTest(CardController.class)
class CardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CardService cardService;

	@MockBean
	private CardRepository cardRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void givenCard_whenCreate_ExpectedOK() throws Exception {

		when(cardService.create(ArgumentMatchers.any(CreateCardDTO.class)))
				.thenReturn(ScenarioFactory.RESPONSE_CARD_DTO);

		ResultActions response = mockMvc.perform(post("/cartoes").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(ScenarioFactory.NEW_CARD_DTO)));

		response.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(content().string(objectMapper.writeValueAsString(ScenarioFactory.NEW_CARD_DTO)));

		verify(cardService, VerificationModeFactory.times(1)).create(Mockito.any());
		reset(cardService);
	}

	@Test
	void givenCardNumber_whenGetBalance_ExpectedOK() throws Exception {

		when(cardService.getBalance(anyString())).thenReturn(500d);

		mockMvc.perform(get("/cartoes/1111222233334444")).andExpect(status().isOk())
				.andExpect(content().string("500.0"));

		verify(cardService, VerificationModeFactory.times(1)).getBalance(Mockito.any());
		reset(cardService);
	}

	@Test
	void givenCardNumber_whenGetBalance_ExpectedError() throws Exception {

		when(cardService.getBalance(anyString())).thenThrow(CardNotFoundException.class);

		mockMvc.perform(get("/cartoes/1111222233334444")).andExpect(status().isNotFound());

		verify(cardService, VerificationModeFactory.times(1)).getBalance(Mockito.any());
		reset(cardService);
	}

	@Test
	void givenTransaction_whenDoTransaction_ExpectedOK() throws Exception {

		doNothing().when(cardService).doTransaction(ArgumentMatchers.any(TransactionDTO.class));

		ResultActions response = mockMvc
				.perform(post("/cartoes/transacoes").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(ScenarioFactory.TRANSACTION_DTO)));

		response.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().string("OK"));

		verify(cardService, VerificationModeFactory.times(1)).doTransaction(Mockito.any());
		reset(cardService);
	}

	@ParameterizedTest
	@ValueSource(strings = { "CARTAO_INEXISTENTE", "SENHA_INVALIDA", "SALDO_INSUFICIENTE" })
	void givenTransaction_whenDoTransaction_ExpectedCardNotFound(String msg) throws Exception {

		doThrow(new TransactionException(msg, msg)).when(cardService)
				.doTransaction(ArgumentMatchers.any(TransactionDTO.class));

		ResultActions response = mockMvc
				.perform(post("/cartoes/transacoes").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(ScenarioFactory.TRANSACTION_DTO)));

		response.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity()).andExpect(content().string(msg));

		verify(cardService, VerificationModeFactory.times(1)).doTransaction(Mockito.any());
		reset(cardService);
	}

}
