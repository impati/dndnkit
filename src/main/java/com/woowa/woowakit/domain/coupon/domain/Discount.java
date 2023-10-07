package com.woowa.woowakit.domain.coupon.domain;

import javax.persistence.Embeddable;

import lombok.Getter;

@Getter
@Embeddable
public abstract class Discount {

	protected int value;
}
