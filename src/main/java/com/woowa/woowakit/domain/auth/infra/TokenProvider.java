package com.woowa.woowakit.domain.auth.infra;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenProvider {

	private final String secretKey;
	private final long validityInMilliseconds;

	public TokenProvider(
		@Value("${security.jwt.token.secret-key}") final String secretKey,
		@Value("${security.jwt.token.expire-length}") final long validityInMilliseconds
	) {
		this.secretKey = secretKey;
		this.validityInMilliseconds = validityInMilliseconds;
	}

	public String createToken(final String payload) {
		final Claims claims = Jwts.claims().setSubject(payload);
		final Date now = new Date();
		final Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public String getPayload(final String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateToken(final String token) {
		try {
			final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

			return !claims.getBody().getExpiration().before(new Date());
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
}
