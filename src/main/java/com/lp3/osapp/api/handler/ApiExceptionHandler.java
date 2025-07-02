package com.lp3.osapp.api.handler;

import com.lp3.osapp.exception.RegraNegocioException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<Problema.Campo> camposComErro = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            camposComErro.add(new Problema.Campo(error.getField(), error.getDefaultMessage()));
        }

        Problema problema = new Problema();
        problema.setStatus(status.value());
        problema.setTitulo("Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.");
        problema.setDataHora(OffsetDateTime.now());
        problema.setCampos(camposComErro);

        return handleExceptionInternal(ex, problema, headers, status, request);
    }


    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Object> handleRegraNegocio(RegraNegocioException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Problema problema = new Problema();
        problema.setStatus(status.value());
        problema.setTitulo(ex.getMessage());
        problema.setDataHora(OffsetDateTime.now());

        return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
    }
}