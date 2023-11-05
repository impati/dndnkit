package com.woowa.woowakit.domain.coupon.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponFrameRepository extends JpaRepository<CouponFrame, Long> {

	@Query("SELECT cf FROM CouponFrame cf WHERE cf.endDate.value >= :now ")
	List<CouponFrame> findAvailableCouponFrame(@Param("now") final LocalDate now);
}
