package org.datatower.toptal.challenge.controller.handler;

import org.datatower.toptal.challenge.common.HealthDataException;
import org.datatower.toptal.challenge.controller.dtos.error.ErrorInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@ControllerAdvice
public class ControllerExceptionHandler {

	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public List<ErrorInfo> handleMessageNotReadable(HttpMessageNotReadableException ex) {
		ErrorInfo error = ErrorInfo.builder().message(ex.getMessage()).build();
		return List.of(error);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<ErrorInfo> handleValidationExceptions(MethodArgumentNotValidException ex) {
		return ex.getBindingResult().getAllErrors().stream()
			.filter(error -> error instanceof FieldError)
			.map(error -> (FieldError) error)
			.map(fieldError -> new ErrorInfo(fieldError.getDefaultMessage(), fieldError.getField()))
			.collect(Collectors.toList());
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public List<ErrorInfo> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
		return List.of(ErrorInfo.builder().message(ex.getMessage()).build());
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public List<ErrorInfo> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		ErrorInfo errorInfo = ErrorInfo.builder()
			.message(format("Type of Parameter[%s] is incorrect", ex.getName()))
			.build();
		return List.of(errorInfo);
	}

	@ResponseBody
	@ExceptionHandler(HealthDataException.class)
	public ResponseEntity<List<ErrorInfo>> handleTmsException(HealthDataException ex) {
		int status = ex.getCode().getHttpStatusCode();
		String message = Optional.ofNullable(ex.getMessage())
								 .orElse(ex.getCode().name());
		ErrorInfo errorInfo = ErrorInfo.builder()
									   .message(message)
									   .build();
		return ResponseEntity.status(status)
							 .body(List.of(errorInfo));
	}
}
