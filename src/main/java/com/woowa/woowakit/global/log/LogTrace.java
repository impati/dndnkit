package com.woowa.woowakit.global.log;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogTrace {

	private static final String START_PREFIX = "-->";
	private static final String COMPLETE_PREFIX = "<--";
	private static final String EX_PREFIX = "<X-";

	private final ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

	public TraceStatus begin(final String message) {
		syncTraceId();
		final TraceId traceId = traceIdHolder.get();
		final Long startTimeMs = System.currentTimeMillis();
		log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

		return new TraceStatus(traceId, startTimeMs, message);
	}

	public void end(final TraceStatus status) {
		complete(status, null);
	}

	public void exception(final TraceStatus status, final Exception e) {
		complete(status, e);
	}

	private void complete(final TraceStatus status, final Exception e) {
		final Long stopTimeMs = System.currentTimeMillis();
		long resultTimeMs = stopTimeMs - status.getStartTimeMs();
		TraceId traceId = status.getTraceId();
		if (notExistException(e)) {
			log.info("[{}] {}{} time={}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()),
				status.getMessage(),
				resultTimeMs
			);
			releaseTraceId();
			return;
		}
		log.info("[{}] {}{} time={}ms ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()),
			status.getMessage(),
			resultTimeMs, e.toString()
		);
		releaseTraceId();
	}

	private boolean notExistException(final Exception e) {
		return e == null;
	}

	private void syncTraceId() {
		final TraceId traceId = traceIdHolder.get();
		if (traceId == null) {
			traceIdHolder.set(new TraceId());
			return;
		}
		traceIdHolder.set(traceId.createNextId());
	}

	private void releaseTraceId() {
		final TraceId traceId = traceIdHolder.get();
		if (traceId.isFirstLevel()) {
			traceIdHolder.remove();
			return;
		}
		traceIdHolder.set(traceId.createPreviousId());
	}

	private String addSpace(final String prefix, final int level) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append((i == level - 1) ? "|" + prefix : "|   ");
		}
		return sb.toString();
	}
}
