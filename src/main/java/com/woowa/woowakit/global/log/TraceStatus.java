package com.woowa.woowakit.global.log;

import lombok.Getter;

@Getter
public class TraceStatus {

	private final TraceId traceId;
	private final Long startTimeMs;
	private final String message;

	public TraceStatus(
		final TraceId traceId,
		final Long startTimeMs,
		final String message
	) {
		this.traceId = traceId;
		this.startTimeMs = startTimeMs;
		this.message = message;
	}
}
