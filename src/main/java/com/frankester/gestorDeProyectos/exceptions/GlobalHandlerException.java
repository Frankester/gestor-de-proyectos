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

    Map<String, String> handlingErrorException(Throwable ex){
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

    @ExceptionHandler(ChatRoomAlreadyExistException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> chatRoomAlreadyExistException(ChatRoomAlreadyExistException ex){
       return this.handlingErrorException(ex);
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> chatRoomNotFoundException(ChatRoomNotFoundException ex){

        return this.handlingErrorException(ex);
    }

    @ExceptionHandler(ProyectoNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> proyectoNotFoundException(ProyectoNotFoundException ex){

        return this.handlingErrorException(ex);
    }

    @ExceptionHandler(TareaNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> tareaNotFoundException(TareaNotFoundException ex){

        return this.handlingErrorException(ex);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> userAlreadyExistsException(UserAlreadyExistsException ex){

        return this.handlingErrorException(ex);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> usuarioNotFoundException(UsuarioNotFoundException ex){

        return this.handlingErrorException(ex);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> validationError(InvalidDataAccessApiUsageException ex){

        return this.handlingErrorException(ex);
    }

    @ExceptionHandler(QueryMethodParameterConversionException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> validationError(QueryMethodParameterConversionException ex){

        return this.handlingErrorException(ex);
    }


    @ExceptionHandler(IOException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> ioException(IOException ex){

        return this.handlingErrorException(ex);
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> fileNotFoundException(FileNotFoundException ex){

        return this.handlingErrorException(ex);
    }


    @ExceptionHandler(PanelDeControlNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Map<String, String> panelDeControlNotFoundException(PanelDeControlNotFoundException ex){

        return this.handlingErrorException(ex);
    }

    @ExceptionHandler(VerificationCodeTriesExaustedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> verificationCodeTriesExaustedException(VerificationCodeTriesExaustedException ex){

        return this.handlingErrorException(ex);
    }

    @ExceptionHandler(VerificationCodeInvalidCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> verificationCodeInvalidCodeException(VerificationCodeInvalidCodeException ex){

        return this.handlingErrorException(ex);
    }

    @ExceptionHandler(VerificationCodeExpirationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> verificationCodeExpirationException(VerificationCodeExpirationException ex) {

        return this.handlingErrorException(ex);
    }

}
