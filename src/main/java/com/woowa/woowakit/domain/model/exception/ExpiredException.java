package com.woowa.woowakit.domain.model.exception;

import org.springframework.http.HttpStatus;

public class ExpiredException extends ModelException {

	public ExpiredException() {
		super("만료일이 지났습니다.", HttpStatus.BAD_REQUEST);
	}
}
