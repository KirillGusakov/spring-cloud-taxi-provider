package org.modsen.serviceride.config.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.NoSuchElementException;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());
        String url = response.request().url();
        switch (httpStatus) {
            case NOT_FOUND -> {
                return new NoSuchElementException("Resource not found: " + url);
            }
            case INTERNAL_SERVER_ERROR -> {
                return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
            default -> {
                return new ResponseStatusException(HttpStatus.valueOf(response.status()), "Generic error");
            }
        }
    }
}
