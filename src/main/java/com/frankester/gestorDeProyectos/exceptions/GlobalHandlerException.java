package com.frankester.gestorDeProyectos.exceptions;

import com.frankester.gestorDeProyectos.exceptions.custom.*;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.repository.support.QueryMethodParameterConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(ChatRoomAlreadyExistException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> chatRoomAlreadyExistException(ChatRoomAlreadyExistException ex){

        Map<String, String> errorMessage = new HashMap<>();

        errorMessage.put("message", ex.getLocalizedMessage());

        return errorMessage;
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> chatRoomNotFoundException(ChatRoomNotFoundException ex){

        Map<String, String> errorMessage = new HashMap<>();

        errorMessage.put("message", ex.getLocalizedMessage());

        return errorMessage;
    }

    @ExceptionHandler(ProyectoNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> proyectoNotFoundException(ProyectoNotFoundException ex){

        Map<String, String> errorMessage = new HashMap<>();

        errorMessage.put("message", ex.getLocalizedMessage());

        return errorMessage;
    }

    @ExceptionHandler(TareaNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> tareaNotFoundException(TareaNotFoundException ex){

        Map<String, String> errorMessage = new HashMap<>();

        errorMessage.put("message", ex.getLocalizedMessage());

        return errorMessage;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> userAlreadyExistsException(UserAlreadyExistsException ex){

        Map<String, String> errorMessage = new HashMap<>();

        errorMessage.put("message", ex.getLocalizedMessage());

        return errorMessage;
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> usuarioNotFoundException(UsuarioNotFoundException ex){

        Map<String, String> errorMessage = new HashMap<>();

        errorMessage.put("message", ex.getLocalizedMessage());

        return errorMessage;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> validationError(MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> validationError(InvalidDataAccessApiUsageException ex){

        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getLocalizedMessage());
        return errors;
    }

    @ExceptionHandler(QueryMethodParameterConversionException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> validationError(QueryMethodParameterConversionException ex){

        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return errors;
    }


    @ExceptionHandler(IOException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> ioException(IOException ex){

        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return errors;
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> fileNotFoundException(FileNotFoundException ex){

        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return errors;
    }
}
