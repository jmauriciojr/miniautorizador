package br.com.test.miniautorizador.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.test.miniautorizador.domain.CreateCardDTO;
import br.com.test.miniautorizador.domain.ResponseCardDTO;
import br.com.test.miniautorizador.domain.document.Card;


@Component
public class CardAssembler {

	@Autowired
    private ModelMapper mapper;

    public ResponseCardDTO fromEntity(Card user) {
        mapper.getConfiguration().setAmbiguityIgnored(true);
        
        return this.mapper.map(user, ResponseCardDTO.class);
    }

    public Card fromDTO(CreateCardDTO userDTO) {
    	mapper.getConfiguration().setAmbiguityIgnored(true);
    	
        return this.mapper.map(userDTO, Card.class);
    }

}