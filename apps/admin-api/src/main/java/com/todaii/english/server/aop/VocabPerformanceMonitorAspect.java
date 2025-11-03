package com.todaii.english.server.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class VocabPerformanceMonitorAspect {
	/*
	 * "return type, package-name.class-name.method-name(args)"
	 * 
	 * dấu *(1) đại diện kiểu trả về của phương thức gốc, dấu *(2) đại diện tất cả
	 * lớp trong package, dấu *(3) đại diện tất cả phương thức trong package (..)
	 * đại diện cho bất kỳ kiểu hoặc số lượng agrs của phương thức gốc
	 */
	@Around("execution(* com.todaii.english.server.vocabulary.VocabGroupApiController.*(..)) || execution(* com.todaii.english.server.vocabulary.VocabDeckApiController.*(..))")
	public Object monitorTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		// dùng ms sẽ xuất 0 vì chạy nhanh quá
		long startAt = System.nanoTime();
		// target-object bị chặn để @Around thực hiện, nếu ko gọi .proceed() thì Advice
		// thực hiện xog sẽ kết thúc
		Object obj = proceedingJoinPoint.proceed();
		long endAt = System.nanoTime();

		log.info("Time taken: " + (endAt - startAt)/(10e5) + " ms in " + proceedingJoinPoint.getSignature().getName());

		return obj;
	}
}
