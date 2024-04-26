package br.com.test.miniautorizador.controller;

import static br.com.test.miniautorizador.commons.constants.Constants.LOG_EVENT_INFO;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_HEADER_CONSUMER;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_ENTITY_ID;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_EVENT;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_MESSAGE;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_METHOD;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.test.miniautorizador.commons.constants.Constants;
import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.ResponseCardDTO;
import br.com.test.miniautorizador.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import javassist.NotFoundException;
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
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Salvo com sucesso!"),
			@ApiResponse(responseCode = "400", description = "Dados não processados devido a sintaxe incorreta ou falta de informações."),
			@ApiResponse(responseCode = "401", description = "Acesso não autorizado!"),
			@ApiResponse(responseCode = "404", description = "Não Encontrado!"),
			@ApiResponse(responseCode = "500", description = "Sistema indisponível") })
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping()
	public ResponseEntity<ResponseCardDTO> create(
			@RequestHeader(value = Constants.X_CONSUMER_NAME, required = false) String consumerName,
			@Parameter(description = "Cartão [*Obrigatório]", required = true) @Valid @RequestBody CreateCardDTO card)
			throws NotFoundException {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_ENTITY_ID + LOG_KEY_EVENT + LOG_HEADER_CONSUMER,
				"Creating Card", "create", card.getNumber(), LOG_EVENT_INFO, consumerName);

		return ResponseEntity.ok(this.cardService.create(card));
	}

	@Operation(summary = "Obter Saldo do Cartão")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Consultado com sucesso!"),
			@ApiResponse(responseCode = "200", description = "Saldo recuperado com sucesso."),
			@ApiResponse(responseCode = "404", description = "Cartão não Existe!")})
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{numeroCartao}")
	public ResponseEntity<Double> getBalance(
			@RequestHeader(value = Constants.X_CONSUMER_NAME, required = false) String consumerName,
			@Parameter(description = "Número do Cartão [*Obrigatório]", required = true) @PathVariable("numeroCartao") String numeroCartao)
			throws NotFoundException {
		log.info(LOG_KEY_MESSAGE + LOG_KEY_METHOD + LOG_KEY_ENTITY_ID + LOG_KEY_EVENT + LOG_HEADER_CONSUMER,
				"Get Balance", "getBalance", numeroCartao, LOG_EVENT_INFO, consumerName);

		return ResponseEntity.ok(this.cardService.getBalance(numeroCartao));
	}
}
