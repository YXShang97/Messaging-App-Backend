package com.yuxin.messaging.advice;

import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.exception.MessagingServiceException;
import com.yuxin.messaging.response.CommonResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Log4j2
public class ExceptionHandlers {
    @ExceptionHandler(MessagingServiceException.class)
    @ResponseBody
    public ResponseEntity<CommonResponse> handleMessagingServiceException(MessagingServiceException messagingServiceException) {
        log.warn("Encountered exception: {}", messagingServiceException.getStatus().getMessage(), messagingServiceException);
        return ResponseEntity
                .status(messagingServiceException.getStatus().getHttpStatus())
                .body(new CommonResponse(messagingServiceException.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<CommonResponse> handleException(Exception exception) {
        log.warn("Unknown exception: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(Status.UNKNOWN_EXCEPTION.getHttpStatus())
                .body(new CommonResponse(Status.UNKNOWN_EXCEPTION));
    }
}
