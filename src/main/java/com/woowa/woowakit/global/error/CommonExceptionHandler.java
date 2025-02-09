package com.woowa.woowakit.global.error;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.woowa.woowakit.domain.auth.exception.MemberException;
import com.woowa.woowakit.domain.cart.exception.CartException;
import com.woowa.woowakit.domain.model.exception.ModelException;
import com.woowa.woowakit.domain.order.exception.OrderException;
import com.woowa.woowakit.domain.product.exception.ProductException;
import com.woowa.woowakit.domain.stock.exception.StockException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(final Exception exception) {
		log.error("예상하지 못한 에러입니다.", exception);
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"예상하지 못한 에러입니다."));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> illegalArgumentExceptionHandler(
		final IllegalArgumentException exception
	) {
		log.warn("잘못된 요청입니다.", exception);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
				"잘못된 요청입니다."));
	}

	@ExceptionHandler(WooWaException.class)
	public ResponseEntity<ErrorResponse> wooWaExceptionHandler(final WooWaException exception) {
		log.info(exception.getMessage());
		return ResponseEntity
			.status(exception.getHttpStatus())
			.body(new ErrorResponse(exception.getHttpStatus().value(), exception.getMessage()));
	}

	@ExceptionHandler(ModelException.class)
	public ResponseEntity<ErrorResponse> modelExceptionHandler(final ModelException exception) {
		log.info(exception.getMessage());
		return ResponseEntity
			.status(exception.getHttpStatus())
			.body(new ErrorResponse(exception.getHttpStatus().value(), exception.getMessage()));
	}

	@ExceptionHandler(MemberException.class)
	public ResponseEntity<ErrorResponse> memberExceptionHandler(final MemberException exception) {
		log.info(exception.getMessage());
		return ResponseEntity
			.status(exception.getHttpStatus())
			.body(new ErrorResponse(exception.getHttpStatus().value(), exception.getMessage()));
	}

	@ExceptionHandler(CartException.class)
	public ResponseEntity<ErrorResponse> cartItemExceptionHandler(final CartException exception) {
		log.info(exception.getMessage());
		return ResponseEntity
			.status(exception.getHttpStatus())
			.body(new ErrorResponse(exception.getHttpStatus().value(), exception.getMessage()));
	}

	@ExceptionHandler(OrderException.class)
	public ResponseEntity<ErrorResponse> orderExceptionHandler(final OrderException exception) {
		log.info(exception.getMessage());
		return ResponseEntity
			.status(exception.getHttpStatus())
			.body(new ErrorResponse(exception.getHttpStatus().value(), exception.getMessage()));
	}

	@ExceptionHandler(ProductException.class)
	public ResponseEntity<ErrorResponse> productExceptionHandler(final ProductException exception) {
		log.info(exception.getMessage());
		return ResponseEntity
			.status(exception.getHttpStatus())
			.body(new ErrorResponse(exception.getHttpStatus().value(), exception.getMessage()));
	}

	@ExceptionHandler(StockException.class)
	public ResponseEntity<ErrorResponse> stockExceptionHandler(final StockException exception) {
		log.info(exception.getMessage());
		return ResponseEntity
			.status(exception.getHttpStatus())
			.body(new ErrorResponse(exception.getHttpStatus().value(), exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(
		final MethodArgumentNotValidException exception) {
		String errorMessage = "입력값이 잘못되었습니다.\n";

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				errorMessage + String.join(", ", getFieldErrorMessages(exception))
			));
	}

	private List<String> getFieldErrorMessages(final MethodArgumentNotValidException ex) {
		return ex.getBindingResult().getAllErrors().stream().map(error -> {
			String fieldName = ((FieldError)error).getField();
			String message = error.getDefaultMessage();
			return fieldName + ": " + message;
		}).collect(Collectors.toUnmodifiableList());
	}

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> sqlIntegrityConstraintViolationExceptionHandler(
		final SQLIntegrityConstraintViolationException exception
	) {
		log.warn("잘못된 요청입니다.", exception);
		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(new ErrorResponse(HttpStatus.CONFLICT.value(),
				"잘못된 요청입니다."));
	}
}
