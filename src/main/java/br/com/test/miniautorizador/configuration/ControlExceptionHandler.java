package br.com.test.miniautorizador.configuration;

import static br.com.test.miniautorizador.commons.constants.Constants.HTTP_CODE_NOT_FOUND;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_HTTP_CODE;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_EXCEPTION;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_DESCRIPTION;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_EVENT;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_MESSAGE;
import static br.com.test.miniautorizador.commons.constants.Constants.LOG_KEY_METHOD;
import static br.com.test.miniautorizador.commons.constants.Constants.HTTP_CODE_BAD_REQUEST;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.com.test.miniautorizador.exception.BusinessException;
import br.com.test.miniautorizador.exception.CardAlreadyExistsException;
import br.com.test.miniautorizador.exception.CardNotFoundException;
import br.com.test.miniautorizador.exception.ExceptionResolver;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ControlExceptionHandler {

	public static final String X_AUTH_TRACEID = "X-auth-traceid";
	public static final String CONSTRAINT_VALIDATION_FAILED = "Constraint validation failed";

	@ExceptionHandler({ BusinessException.class })
	protected ResponseEntity<Object> handleConflict(BusinessException ex, WebRequest request) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());
		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"BusinessException", LOG_EXCEPTION, ex.getHttpStatusCode().value(), ex.getMessage(),
				ex.getDescription());
		return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());

	}

	@ExceptionHandler({ Throwable.class })
	public ResponseEntity<Object> handleException(Throwable eThrowable) {

		BusinessException ex = BusinessException.builder().httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
				.message(Optional.ofNullable(eThrowable.getMessage()).orElse(eThrowable.toString()))
				.description(ExceptionResolver.getRootException(eThrowable)).build();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());
		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"Throwable", LOG_EXCEPTION, ex.getHttpStatusCode().value(), ex.getMessage(), ex.getDescription());
		return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());

	}
	
	@ExceptionHandler({ CardNotFoundException.class })
	public ResponseEntity<Object> handleCardNotFoundException(CardNotFoundException ex) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());
		
		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"Throwable", LOG_EXCEPTION, HTTP_CODE_BAD_REQUEST, ex.getMessage(), ExceptionResolver.getRootException(ex));
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).headers(responseHeaders).body(null);

	}
	
	@ExceptionHandler({ CardAlreadyExistsException.class })
	public ResponseEntity<Object> handleCardAlreadyExistsException(CardAlreadyExistsException ex) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());
		
		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"Throwable", LOG_EXCEPTION, HTTP_CODE_BAD_REQUEST, ex.getMessage(), ExceptionResolver.getRootException(ex));
		
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY.value()).headers(responseHeaders).body(ex.getOnlyBody().getBody());

	}


	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exMethod,
			WebRequest request) {

		String error = exMethod.getName() + " should be "
				+ Objects.requireNonNull(exMethod.getRequiredType()).getName();

		BusinessException ex = BusinessException.builder().httpStatusCode(HttpStatus.BAD_REQUEST)
				.message(CONSTRAINT_VALIDATION_FAILED).description(error).build();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());

		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"MethodArgumentTypeMismatchException", LOG_EXCEPTION, ex.getHttpStatusCode().value(), ex.getMessage(),
				ex.getDescription());
		return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
	}


	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exMethod, WebRequest request) {
		List<String> errors = new ArrayList<>();
		for (ConstraintViolation<?> violation : exMethod.getConstraintViolations()) {
			errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
					+ violation.getMessage());
		}

		BusinessException ex = BusinessException.builder().httpStatusCode(HttpStatus.BAD_REQUEST)
				.message(CONSTRAINT_VALIDATION_FAILED).description(errors.toString()).build();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());

		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"ConstraintViolationException", LOG_EXCEPTION, ex.getHttpStatusCode().value(), ex.getMessage(),
				ex.getDescription());
		return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
	}


	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public ResponseEntity<Object> validationError(MethodArgumentNotValidException exMethod) {

		BindingResult bindingResult = exMethod.getBindingResult();

		List<FieldError> fieldErrors = bindingResult.getFieldErrors();

		List<String> fieldErrorDtos = fieldErrors.stream()
				.map(f -> f.getField().concat(":").concat(f.getDefaultMessage())).map(String::new)
				.collect(Collectors.toList());

		BusinessException ex = BusinessException.builder().httpStatusCode(HttpStatus.BAD_REQUEST)
				.message(CONSTRAINT_VALIDATION_FAILED).description(fieldErrorDtos.toString()).build();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());

		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"MethodArgumentNotValidException", LOG_EXCEPTION, ex.getHttpStatusCode().value(), ex.getMessage(),
				ex.getDescription());
		return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
	}


	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> validationError(HttpMessageNotReadableException exMethod) {

		Throwable mostSpecificCause = exMethod.getMostSpecificCause();
		String message = (mostSpecificCause.getMessage() != null) ? mostSpecificCause.getMessage()
				: exMethod.getMessage();
		BusinessException ex = BusinessException.builder().httpStatusCode(HttpStatus.BAD_REQUEST)
				.message(CONSTRAINT_VALIDATION_FAILED).description(message).build();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());

		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"HttpMessageNotReadableException", LOG_EXCEPTION, ex.getHttpStatusCode().value(), ex.getMessage(),
				ex.getDescription());
		return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
	}

	@ExceptionHandler({ MissingServletRequestParameterException.class })
	public ResponseEntity<Object> handleException(MissingServletRequestParameterException e) {

		BusinessException ex = BusinessException.builder().httpStatusCode(HttpStatus.BAD_REQUEST)
				.message(Optional.ofNullable(e.getMessage()).orElse(e.toString()))
				.description(ExceptionResolver.getRootException(e)).build();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());
		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"MissingServletRequestParameterException", LOG_EXCEPTION, ex.getHttpStatusCode().value(),
				ex.getMessage(), ex.getDescription());

		return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());

	}

	@ExceptionHandler({ NotFoundException.class })
	public ResponseEntity<Object> handleException(NotFoundException e) {

		BusinessException ex = BusinessException.builder().httpStatusCode(HttpStatus.NOT_FOUND)
				.message(Optional.ofNullable(e.getMessage()).orElse(e.toString()))
				.description(ExceptionResolver.getRootException(e)).build();
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());

		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"NotFoundException", LOG_EXCEPTION, ex.getHttpStatusCode().value(), ex.getMessage(),
				ex.getDescription());
		return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());

	}

	@ExceptionHandler({ EmptyResultDataAccessException.class })
	public ResponseEntity<Object> handleException(EmptyResultDataAccessException e) {

		BusinessException ex = BusinessException.builder().httpStatusCode(HttpStatus.NOT_FOUND)
				.message(HTTP_CODE_NOT_FOUND).description(ExceptionResolver.getRootException(e)).build();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());

		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"EmptyResultDataAccessException", LOG_EXCEPTION, ex.getHttpStatusCode().value(), ex.getMessage(),
				ex.getDescription());
		return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
	}

	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
	public ResponseEntity<Object> handleException(HttpRequestMethodNotSupportedException e) {

		BusinessException ex = BusinessException.builder().httpStatusCode(HttpStatus.METHOD_NOT_ALLOWED)
				.message(Optional.ofNullable(e.getMessage()).orElse(e.toString()))
				.description(ExceptionResolver.getRootException(e)).build();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(X_AUTH_TRACEID, this.getTraceID());

		log.error(LOG_KEY_METHOD + LOG_KEY_EVENT + LOG_KEY_HTTP_CODE + LOG_KEY_MESSAGE + LOG_KEY_DESCRIPTION,
				"HttpRequestMethodNotSupportedException", LOG_EXCEPTION, ex.getHttpStatusCode().value(),
				ex.getMessage(), ex.getDescription());
		return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
	}



	private String getTraceID() {
		// Get TraceID for NewRelic?
		String traceId = "";
		traceId = Optional.ofNullable(traceId.trim().isEmpty() ? null : traceId).orElse("not available");

		return traceId;
	}

}
