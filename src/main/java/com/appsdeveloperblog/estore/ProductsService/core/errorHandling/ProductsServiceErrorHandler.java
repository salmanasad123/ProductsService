package com.appsdeveloperblog.estore.ProductsService.core.errorHandling;

import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ProductsServiceErrorHandler {

    @ExceptionHandler(value = {IllegalStateException.class})
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException illegalStateException,
                                                              WebRequest webRequest) {

        ErrorMessage errorMessage = new ErrorMessage(new Date(), illegalStateException.getMessage());

        return new ResponseEntity<>(errorMessage, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherExceptions(Exception exception,
                                                        WebRequest webRequest) {

        ErrorMessage errorMessage = new ErrorMessage(new Date(), exception.getMessage());

        return new ResponseEntity<>(errorMessage, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // if any exception is thrown from command handler or query handler method, then axon framework
    // will wrap this error into command execution or query execution exception, so we are handling
    // command execution exception here at a centralized place
    @ExceptionHandler(value = {CommandExecutionException.class})
    public ResponseEntity<Object> handleOtherExceptions(CommandExecutionException commandExecutionException,
                                                        WebRequest webRequest) {

        ErrorMessage errorMessage = new ErrorMessage(new Date(), commandExecutionException.getMessage());

        return new ResponseEntity<>(errorMessage, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
