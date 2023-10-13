package com.woowa.woowakit.domain.coupon.domain;

import javax.persistence.Embeddable;

import lombok.Getter;

@Getter
@Embeddable
public class Discount {

	protected int value;
}
