package br.com.test.miniautorizador.exception;

import static br.com.test.miniautorizador.commons.constants.Constants.UNAVAILABLE_STACK_TRACE;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionResolver {

	private ExceptionResolver() {
	}

	public static String getRootException(Throwable exception) {
		var rootCause = defaultIfNull(ExceptionUtils.getRootCause(exception), exception);

		if (isNotEmpty(rootCause.getStackTrace())) {
			return buildMessage(rootCause);
		}

		if (isNotEmpty(exception.getStackTrace())) {
			return buildMessage(exception);
		}

		return String.format("%s in class: %s line: %d", exception, UNAVAILABLE_STACK_TRACE, 0);
	}

	private static String buildMessage(Throwable exception) {
		return String.format("%s in class? %s Line: %d", exception, exception.getStackTrace()[0].getClassName(),
				exception.getStackTrace()[0].getLineNumber());
	}

}
