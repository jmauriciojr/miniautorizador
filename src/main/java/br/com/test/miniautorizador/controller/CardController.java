package br.com.test.miniautorizador.controller;

import static br.com.test.miniautorizador.commons.constants.Constants.LOG_EVENT_INFO;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_HEADER_CONSUMER;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_ENTITY_ID;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_EVENT;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_MESSAGE;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_METHOD;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.ResponseCardDTO;
import br.com.test.miniautorizador.domain.TransactionDTO;
import br.com.test.miniautorizador.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cartoes")
@Slf4j
@Tag(name = "cartoes", description = "API de Cartões")
public class CardController {

	private final CardService cardService;

	@Operation(summary = "Criar um Cartão")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Criação com sucesso."),
			@ApiResponse(responseCode = "422", description = "Cartão já existe.") })
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseCardDTO> create(
			@Parameter(description = "Cartão [*Obrigatório]", required = true) @Valid @RequestBody CreateCardDTO card) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_ENTITY_ID + LOG_KEY_EVENT + LOG_HEADER_CONSUMER,
				"Creating Card", "create", card.getNumber(), LOG_EVENT_INFO);

		return new ResponseEntity<>(this.cardService.create(card), HttpStatus.CREATED);
	}

	@Operation(summary = "Obter Saldo do Cartão")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Obtenção com sucesso"),
			@ApiResponse(responseCode = "404", description = "Cartão não Existe!") })
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{numeroCartao}")
	public ResponseEntity<BigDecimal> getBalance(
			@Parameter(description = "Número do Cartão [*Obrigatório]", required = true) @PathVariable("numeroCartao") String numeroCartao) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_ENTITY_ID + LOG_KEY_EVENT + LOG_HEADER_CONSUMER,
				"Get Balance", "getBalance", numeroCartao, LOG_EVENT_INFO);

		return new ResponseEntity<>(this.cardService.getBalance(numeroCartao), HttpStatus.OK);
	}

	@Operation(summary = "Realizar Transação")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Transação realizada com sucesso"),
			@ApiResponse(responseCode = "422", description = "SALDO_INSUFICIENTE | SENHA_INVALIDA | CARTAO_INEXISTENTE") })
	@ResponseStatus(value = HttpStatus.OK)
	@PostMapping(value = "/transacoes" , consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doTransaction(
			@Parameter(description = "Cartão [*Obrigatório]", required = true) @Valid @RequestBody TransactionDTO transactionDTO) {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_ENTITY_ID + LOG_KEY_EVENT + LOG_HEADER_CONSUMER,
				"Do Transaction", "doTransaction", transactionDTO.getNumber(), LOG_EVENT_INFO);

		this.cardService.doTransaction(transactionDTO);

		return new ResponseEntity<>("OK", HttpStatus.OK);
	}
}
