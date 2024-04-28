package br.com.test.miniautorizador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.test.miniautorizador.controller.CardController;

@SpringBootTest
class MiniAutorizadorApplicationTests {

	@Autowired
	private CardController cardController;
	
	@Test
	void contextLoads() {
		assertThat(cardController).isNotNull();
	}

}
