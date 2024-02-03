package com.woowa.woowakit.domain.coupon.domain;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponGroupRepository extends JpaRepository<CouponGroup, Long> {

    @Query("SELECT cg FROM CouponGroup cg WHERE cg.endDate.value >= :now ")
    List<CouponGroup> findAvailableCouponGroup(@Param("now") final LocalDate now);

    @Modifying
    @Query("UPDATE CouponGroup cg SET cg.couponDeploy.deployAmount = cg.couponDeploy.deployAmount - 1 WHERE cg.id = :couponGroupId AND cg.couponDeploy.deployAmount > 0")
    void decreaseDeployAmount(@Param("couponGroupId") final Long couponGroupId);
}
