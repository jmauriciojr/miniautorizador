package br.com.test.miniautorizador.commons.constants;

import java.math.BigDecimal;

public class Constants {

	private Constants() {
	}

	public static final String LOG_KEY_METHOD = "method={} ";
    public static final String LOG_KEY_EVENT = "event={} ";
    public static final String LOG_KEY_MESSAGE = "message=\"{}\" ";
    public static final String LOG_KEY_DESCRIPTION = "description=\"{}\" ";
    public static final String LOG_KEY_ENTITY_ID = "entityId={} ";
	public static final String LOG_KEY_ENTITY = "entity={} ";
    public static final String LOG_EXCEPTION = "exception";
    public static final String LOG_SUCCESS = "success";
    public static final String LOG_EVENT_INFO = "info";
    public static final String LOG_EVENT_ERROR = "error";
    public static final String LOG_KEY_CAUSE = "cause={} ";
    public static final String THROWABLE = "Throwable";
    
	public static final String LOG_KEY_HTTP_CODE = "httpCode={} ";
	public static final String LOG_KEY_ENTITY_STATUS = "entityStatus={} ";
	public static final String LOG_EVENT_BUSINESS_EXCEPTION = "BusinessException";
	public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";


	public static final String LOG_HEADER_CONSUMER = " header-consumer={}";
	
	public static final String HTTP_CODE_BAD_REQUEST = "500";
	public static final String HTTP_CODE_NOT_FOUND = "404";
	public static final String VALIDATE = "validate";
	
	public static final String X_CONSUMER_NAME = "x-consumer-name";

	public static final String UNAVAILABLE_STACK_TRACE = "";

	// Card
	public static final BigDecimal CARD_INITIAL_BALANCE = BigDecimal.valueOf(500);
	public static final String CARD_NOT_FOUND = "CARTAO_INEXISTENTE";
	public static final String CARD_INVALID_PASSWORD = "SENHA_INVALIDA";
	public static final String CARD_INSUFFICIENT_BALANCE = "SALDO_INSUFICIENTE";
	
}