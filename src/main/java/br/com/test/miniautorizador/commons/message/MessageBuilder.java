package br.com.test.miniautorizador.commons.message;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import br.com.test.miniautorizador.commons.enums.ErrorMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageBuilder {
	
	private final MessageSource messageSource;
	
	public String getMessage(String messageCode) {
		return this.messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
	}

    public String getMessage(ErrorMessage message, Object... params) {
        String baseMessage = message.getMessage();
        return String.format(baseMessage, params);
    }
}
