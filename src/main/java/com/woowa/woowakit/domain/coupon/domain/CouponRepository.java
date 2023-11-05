package com.woowa.woowakit.domain.coupon.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

	@Query("SELECT c FROM Coupon c WHERE c.memberId = :member AND c.expiryDate.value >= :now AND c.enabled = true")
	List<Coupon> findCouponByMemberId(
		@Param("member") final Long memberId,
		@Param("now") final LocalDate now
	);
}
