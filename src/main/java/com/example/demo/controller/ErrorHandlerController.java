package com.example.demo.controller;

import com.example.demo.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class ErrorHandlerController {
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  public Mono<Object> httpRequestInternalServerErrorException(RuntimeException e) {
    log.error(e.getClass().getName(), e);
    return Mono.just(String.format(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e));
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(DataNotFoundException.class)
  public Mono<Object> httpRequestDataNotFoundException(DataNotFoundException e) {
    log.warn(e.getClass().getName(), e);
    return Mono.just(String.format(HttpStatus.NOT_FOUND.toString(), e));
  }
}
