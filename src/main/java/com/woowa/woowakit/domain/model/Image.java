package com.woowa.woowakit.domain.model;

import java.util.Objects;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Embeddable
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Image {

	private String value;

	protected Image() {
	}

	public static Image from(final String value) {
		return new Image(value);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Image)) {
			return false;
		}

		final Image image = (Image)o;
		return Objects.equals(value, image.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
