package br.com.test.miniautorizador.exception;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
public class TransactionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String message;

	private final String description;

	public BusinessExceptionBody getOnlyBody() {
		return BusinessExceptionBody.builder().message(this.message).description(this.description)
				.build();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class BusinessExceptionBody {
		private String code;

		private String message;

		private String description;

		@JsonIgnore
		public boolean isEmpty() {
			return StringUtils.isAllEmpty(code, message, description);
		}

	}
}
