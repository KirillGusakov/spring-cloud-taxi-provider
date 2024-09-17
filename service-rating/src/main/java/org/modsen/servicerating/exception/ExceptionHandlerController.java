package org.modsen.servicerating.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationResponse onMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<Violation> list = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(exe -> new Violation(exe.getField(), exe.getDefaultMessage()))
                .toList();
        return new ViolationResponse(list);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ViolationResponse onConstraintViolationException(ConstraintViolationException ex) {
        List<Violation> list = ex.getConstraintViolations()
                .stream()
                .map(nxe -> new Violation(nxe.getPropertyPath().toString(), nxe.getMessage()))
                .toList();
        return new ViolationResponse(list);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse onNoSuchElementException(NoSuchElementException ex) {
        return new ExceptionResponse(ex.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse onHttpClientErrorException(HttpClientErrorException ex) {
        return new ExceptionResponse(ex.getMessage());
    }
}