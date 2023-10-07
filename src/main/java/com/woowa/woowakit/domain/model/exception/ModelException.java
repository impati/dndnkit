package com.woowa.woowakit.domain.model.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ModelException extends RuntimeException {

	private final HttpStatus httpStatus;

	public ModelException(final String message, final HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
}
