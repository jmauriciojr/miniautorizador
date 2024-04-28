package br.com.test.miniautorizador.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import br.com.test.miniautorizador.feature.ScenarioFactory;
import javassist.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class CardAssemblerTest {
	
	private CardAssembler cardAssembler;

	@Test
	void givenCard_whenFromEntity_thenReturnResponseCardDTO() throws NotFoundException {

		cardAssembler = new CardAssembler(new ModelMapper());
		var cardDTO = cardAssembler.fromEntity(ScenarioFactory.NEW_CARD);

		Assertions.assertThat(cardDTO.getNumber()).isEqualTo("1111222233334444");
	}
	
	@Test
	void givenCardDTO_whenFromDTO_thenReturnCard() throws NotFoundException {
		cardAssembler = new CardAssembler(new ModelMapper());
		var card = cardAssembler.fromDTO(ScenarioFactory.NEW_CARD_DTO);

		Assertions.assertThat(card.getNumber()).isEqualTo("1111222233334444");
	}
}
