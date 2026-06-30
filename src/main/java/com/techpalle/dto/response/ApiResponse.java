package com.techpalle.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{

	  private boolean success;

	    private int statusCode;

	    private String message;

	    private T data;

	    private List<String> errors; 

	    private LocalDateTime timestamp;

	    private String path;

	    private String traceId; 

	    public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
	        return ApiResponse.<T>builder()
	                .success(true)
	                .statusCode(statusCode)
	                .message(message)
	                .data(data)
	                .timestamp(LocalDateTime.now())
	                .build();
	    }

	    public static <T> ApiResponse<T> success(T data, String message) {
	        return success(data, message, 200);
	    }

	    public static <T> ApiResponse<T> success(T data) {
	        return success(data, "Success");
	    }

	    public static <T> ApiResponse<T> error(String message, int statusCode, List<String> errors) {
	        return ApiResponse.<T>builder()
	                .success(false)
	                .statusCode(statusCode)
	                .message(message)
	                .errors(errors)
	                .timestamp(LocalDateTime.now())
	                .build();
	    }

	    public static <T> ApiResponse<T> error(String message, int statusCode) {
	        return error(message, statusCode, null);
	    }
}
