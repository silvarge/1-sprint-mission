package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.common.CustomApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 존재하지 않는 요청에 대한 예외
    @ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public CustomApiResponse<?> handleNoPageFoundException(Exception e) {
        log.error("Invalid route or method: : {}", e.getMessage());
        return CustomApiResponse.fail(new DiscodeitException(ErrorCode.METHOD_NOT_ALLOWED));
    }

    // 커스텀 예외
    @ExceptionHandler(value = {DiscodeitException.class})
    public CustomApiResponse<?> handleCustomException(DiscodeitException e) {
        ExceptionDto eDto = ExceptionDto.of(e);
        log.warn("DiscodeitException caught - exceptionType: {} | detail: {}", eDto.getExceptionType(), eDto);
        return CustomApiResponse.fail(eDto);
    }

    // 기본 예외
    @ExceptionHandler(value = {Exception.class})
    public CustomApiResponse<?> handleException(Exception e) {
        log.error("Unhandled exception caught in GlobalExceptionHandler : {}", e.getMessage());
        return CustomApiResponse.fail(new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
