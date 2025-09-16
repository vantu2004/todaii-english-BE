package com.todaii.english.shared.common;
public class ApiResponse<T> {
  private final boolean success;
  private final T data;
  private final String message;
  public ApiResponse(boolean success, T data, String message) {
    this.success = success; this.data = data; this.message = message;
  }
  public static <T> ApiResponse<T> ok(T data){ return new ApiResponse<>(true, data, null); }
  public static <T> ApiResponse<T> fail(String msg){ return new ApiResponse<>(false, null, msg); }
  public boolean isSuccess(){ return success; }
  public T getData(){ return data; }
  public String getMessage(){ return message; }
}
