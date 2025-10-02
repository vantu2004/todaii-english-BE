package com.todaii.english.client.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.todaii.english.shared.dto.GlobalErrorDTO;
import com.todaii.english.shared.exceptions.BusinessException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public GlobalErrorDTO handleGenericException(HttpServletRequest request, Exception ex) {
		GlobalErrorDTO globalErrorDTO = new GlobalErrorDTO();
		globalErrorDTO.setTimestamp(LocalDateTime.now());
		globalErrorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		// trả về lỗi ngắn gọn "Internal Server Error"
		globalErrorDTO.addError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		globalErrorDTO.setPath(request.getServletPath());

		LOGGER.error(ex.getMessage(), ex);

		return globalErrorDTO;
	}

	@ExceptionHandler(BusinessException.class)
	@ResponseBody
	public ResponseEntity<GlobalErrorDTO> handleBusinessException(HttpServletRequest request, BusinessException ex) {
		GlobalErrorDTO error = new GlobalErrorDTO();
		error.setTimestamp(LocalDateTime.now());
		error.setStatus(ex.getStatus());
		error.addError(ex.getMessageText());
		error.setPath(request.getServletPath());

		return ResponseEntity.status(ex.getStatus()).body(error);
	}

	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public GlobalErrorDTO handleBadRequestException(HttpServletRequest request, Exception ex) {
		GlobalErrorDTO globalErrorDTO = new GlobalErrorDTO();
		globalErrorDTO.setTimestamp(LocalDateTime.now());
		globalErrorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
		globalErrorDTO.addError(ex.getMessage());
		globalErrorDTO.setPath(request.getServletPath());

		LOGGER.error(ex.getMessage(), ex);

		return globalErrorDTO;
	}

	// validate query param, path param, không phải @RequestBody
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public GlobalErrorDTO handleConstrainViolationException(HttpServletRequest request, Exception ex) {
		ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex;

		GlobalErrorDTO globalErrorDTO = new GlobalErrorDTO();
		globalErrorDTO.setTimestamp(LocalDateTime.now());
		globalErrorDTO.setStatus(HttpStatus.BAD_REQUEST.value());

		Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
		constraintViolations.forEach(constraintViolation -> globalErrorDTO
				.addError(constraintViolation.getPropertyPath() + ": " + constraintViolation.getMessage()));

		globalErrorDTO.setPath(request.getServletPath());

		LOGGER.error(ex.getMessage(), ex);

		return globalErrorDTO;
	}

	// validate field trong DTO
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		GlobalErrorDTO globalErrorDTO = new GlobalErrorDTO();
		globalErrorDTO.setTimestamp(LocalDateTime.now());
		globalErrorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
		globalErrorDTO.setPath(((ServletWebRequest) request).getRequest().getServletPath());

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		fieldErrors.forEach(fieldError -> {
			String errorMessage = fieldError.getField() + ": " + fieldError.getDefaultMessage();
			globalErrorDTO.addError(errorMessage);
		});

		LOGGER.error(ex.getMessage(), ex);

		return new ResponseEntity<>(globalErrorDTO, headers, status);
	}

}
