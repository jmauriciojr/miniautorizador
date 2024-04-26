package br.com.test.miniautorizador.commons.enums;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    ENTITY_NOT_FOUND_MESSAGE("Entidade nao encontrada", "Nao existe um registro com ID %d"),
    ENTITY_ALREADY_EXISTS_MESSAGE("Entidade ja existe", "Ja existe um registro com o campo %s '%s'"),
    VALIDATION_CONSTRAINTS_FAILED_MESSAGE("Falha na validacao de Campos", "O campo %s %s");

    private final String title;
    private final String message;

    ErrorMessage(String title, String message) {
        this.title = title;
        this.message = message;
    }

}
