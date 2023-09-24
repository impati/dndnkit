package com.woowa.woowakit.domain.auth.domain;

import org.springframework.http.HttpStatus;

import com.woowa.woowakit.domain.auth.exception.MemberException;

public class MemberNameException extends MemberException {

	public MemberNameException() {
		super("사용자 닉네임은 한글자 이상 255글자 이하여야합니다.", HttpStatus.BAD_REQUEST);
	}
}
