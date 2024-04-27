package io.github.maccoycookies.mcregistry;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Maccoy
 * @date 2024/4/27 17:17
 * Description
 */
@RestControllerAdvice
public class McExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(Exception exception) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

}
