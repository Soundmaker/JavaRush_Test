package com.game.controller;

import com.game.exception.ExceptionBAD_REQUEST;
import com.game.exception.ExceptionNOT_FOUND;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PlayerControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ExceptionBAD_REQUEST.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String badRequest(ExceptionBAD_REQUEST exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ExceptionNOT_FOUND.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String notFound(ExceptionNOT_FOUND exception) {
        return exception.getMessage();
    }
}