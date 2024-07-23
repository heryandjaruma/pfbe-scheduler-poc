package com.example.demo.exception;

public class DataNotFoundException extends RuntimeException {

  private final String field;

  public DataNotFoundException(String field, String message) {
    super(message);
    this.field = field;
  }

  public DataNotFoundException(Class clazz, String message) {
    super(message);
    this.field = clazz.getSimpleName();
  }

  public String getField(){
    return this.field;
  }
}
