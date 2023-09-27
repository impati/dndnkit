package com.woowa.woowakit.domain.product.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.woowa.woowakit.domain.auth.annotation.Admin;
import com.woowa.woowakit.domain.product.application.ImageUploader;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/images")
public class ProductImageController {

	private final ImageUploader imageUploader;

	@Admin
	@PostMapping
	public String upload(@RequestPart("data") final MultipartFile multipartFile) {
		return imageUploader.upload(multipartFile);
	}
}
