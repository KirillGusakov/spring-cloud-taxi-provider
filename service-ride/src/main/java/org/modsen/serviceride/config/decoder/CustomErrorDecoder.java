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
                if (url.contains("passengers")) {
                    String passengerId = extractIdFromUrl(url, "passengers");
                    return new NoSuchElementException("Passenger with id = " + passengerId + " not found");
                }
                if (url.contains("drivers")) {
                    String driverId = extractIdFromUrl(url, "drivers");
                    return new NoSuchElementException("Driver with id = " + driverId + " not found");
                }
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

    private String extractIdFromUrl(String url, String entity) {
        String[] parts = url.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(entity) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return "unknown";
    }


}
