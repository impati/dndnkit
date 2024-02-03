package com.woowa.woowakit.domain.coupon.api;

import com.woowa.woowakit.domain.auth.annotation.Admin;
import com.woowa.woowakit.domain.auth.annotation.User;
import com.woowa.woowakit.domain.coupon.application.CouponGroupCommandService;
import com.woowa.woowakit.domain.coupon.application.CouponGroupQueryService;
import com.woowa.woowakit.domain.coupon.domain.CouponGroup;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.dto.request.BrandCouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CategoryCouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.CouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.request.ProductCouponGroupCreateRequest;
import com.woowa.woowakit.domain.coupon.dto.response.CouponGroupResponse;
import com.woowa.woowakit.domain.coupon.dto.response.CouponGroupResponses;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon-groups")
public class CouponGroupController {

    private final CouponGroupCommandService couponGroupCommandService;
    private final CouponGroupQueryService couponGroupQueryService;

    @Admin
    @PostMapping("/product")
    public ResponseEntity<Void> createProductCouponGroup(
            @Valid @RequestBody final ProductCouponGroupCreateRequest request
    ) {
        Long id = couponGroupCommandService.create(CouponTarget.from(request.getProductId()), request);

        return ResponseEntity.created(URI.create("/coupon-groups/" + id)).build();
    }

    @Admin
    @PostMapping("/brand")
    public ResponseEntity<Void> createBrandCouponGroup(
            @Valid @RequestBody final BrandCouponGroupCreateRequest request
    ) {
        Long id = couponGroupCommandService.create(CouponTarget.from(request.getBrand()), request);

        return ResponseEntity.created(URI.create("/coupon-groups/" + id)).build();
    }

    @Admin
    @PostMapping("/category")
    public ResponseEntity<Void> createCategoryCouponGroup(
            @Valid @RequestBody final CategoryCouponGroupCreateRequest request
    ) {
        Long id = couponGroupCommandService.create(CouponTarget.from(request.getCategory()), request);

        return ResponseEntity.created(URI.create("/coupon-groups/" + id)).build();
    }

    @Admin
    @PostMapping("/all")
    public ResponseEntity<Void> createAllCouponGroup(@Valid @RequestBody final CouponGroupCreateRequest request) {
        Long id = couponGroupCommandService.create(CouponTarget.all(), request);

        return ResponseEntity.created(URI.create("/coupon-groups/" + id)).build();
    }

    @Admin
    @GetMapping("/{couponGroupId}")
    public ResponseEntity<CouponGroupResponse> findCouponGroup(@PathVariable final Long couponGroupId) {
        CouponGroup couponGroup = couponGroupQueryService.getCouponGroup(couponGroupId);

        return ResponseEntity.ok(CouponGroupResponse.from(couponGroup));
    }

    @Admin
    @PostMapping("/{couponGroupId}/deploy")
    public ResponseEntity<Void> deployCouponGroup(@PathVariable final Long couponGroupId) {
        couponGroupCommandService.deploy(couponGroupId);

        return ResponseEntity.noContent().build();
    }

    @User
    @GetMapping
    public ResponseEntity<CouponGroupResponses> findCouponGroups() {
        List<CouponGroup> couponGroups = couponGroupQueryService.getCouponGroups(LocalDate.now());

        return ResponseEntity.ok(CouponGroupResponses.from(couponGroups));
    }
}
