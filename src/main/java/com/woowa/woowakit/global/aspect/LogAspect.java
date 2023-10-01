package com.woowa.woowakit.global.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.woowa.woowakit.global.log.LogTrace;
import com.woowa.woowakit.global.log.TraceStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

	private final LogTrace logTrace;

	@Around("execution(* com.woowa.woowakit.domain..*(..))")
	public Object execute(final ProceedingJoinPoint joinPoint) throws Throwable {
		TraceStatus status = null;
		try {
			final String message = joinPoint.getSignature().toShortString();
			status = logTrace.begin(message);

			final Object result = joinPoint.proceed();

			logTrace.end(status);
			return result;
		} catch (Exception e) {
			logTrace.exception(status, e);
			throw e;
		}
	}
}
