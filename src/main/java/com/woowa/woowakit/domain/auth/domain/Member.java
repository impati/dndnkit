package com.woowa.woowakit.domain.auth.domain;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.woowa.woowakit.domain.auth.exception.LoginFailException;
import com.woowa.woowakit.domain.model.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private Email email;

	@Embedded
	private EncodedPassword encodedPassword;

	@Embedded
	private MemberName name;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	private Member(
		final Email email,
		final EncodedPassword encodedPassword,
		final String name,
		final Role role
	) {
		this.email = email;
		this.encodedPassword = encodedPassword;
		this.name = MemberName.from(name);
		this.role = role;
	}

	public void validatePassword(final String password, final PasswordEncoder passwordEncoder) {
		if (!encodedPassword.isMatch(password, passwordEncoder)) {
			throw new LoginFailException();
		}
	}

	public String getEmail() {
		return email.getValue();
	}

	public String getRoleName() {
		return role.name();
	}

	public String getName() {
		return name.getValue();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Member)) {
			return false;
		}

		final Member member = (Member)o;
		return Objects.equals(id, member.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Member{" +
			"id=" + id +
			", email=" + email +
			", encodedPassword=" + encodedPassword +
			", name='" + name + '\'' +
			'}';
	}
}
