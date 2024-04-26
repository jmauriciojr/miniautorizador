package br.com.test.miniautorizador.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.test.miniautorizador.domain.document.Card;

@Repository
public interface CardRepository  extends MongoRepository<Card, String> {
	
	Optional<Card> findByNumber(String number);
	
}
