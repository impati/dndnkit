package com.woowa.woowakit.domain.member.fixture;

import static com.woowa.woowakit.domain.auth.domain.Member.*;

import com.woowa.woowakit.domain.auth.domain.Email;
import com.woowa.woowakit.domain.auth.domain.EncodedPassword;
import com.woowa.woowakit.domain.auth.domain.Member;
import com.woowa.woowakit.domain.auth.domain.Role;
import com.woowa.woowakit.domain.auth.infra.PBKDF2PasswordEncoder;

public class MemberFixture {

	public static MemberBuilder anMember() {
		return Member.builder()
			.name("탐탐")
			.email(Email.from("jiwonjjang@jiwon.com"))
			.encodedPassword(EncodedPassword.of("hellojiwon1234", new PBKDF2PasswordEncoder()))
			.role(Role.USER);
	}
}
