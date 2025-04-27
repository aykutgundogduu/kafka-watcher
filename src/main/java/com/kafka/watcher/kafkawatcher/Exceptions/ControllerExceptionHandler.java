package com.kafka.watcher.kafkawatcher.Exceptions;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleProductNotFoundException(Exception ex) {
    HttpHeaders headers = new HttpHeaders();
    if(ex.getClass().getTypeName() == NoResourceFoundException.class.getTypeName()) 
    {
      //Does not work?
      //headers.add("Location", "/errors/404");
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(ex.getMessage());
  }

}