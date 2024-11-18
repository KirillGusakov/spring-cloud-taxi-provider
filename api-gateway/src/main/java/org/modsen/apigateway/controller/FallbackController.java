package org.modsen.apigateway.controller;

import org.modsen.apigateway.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/drivers")
    public ResponseEntity<ResponseMessage> driversFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseMessage("Driver Service is currently unavailable. Please try again later."));
    }

    @GetMapping("/passengers")
    public ResponseEntity<ResponseMessage> passengersFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseMessage("Passenger Service is currently unavailable. Please try again later."));
    }

    @GetMapping("/ratings")
    public ResponseEntity<ResponseMessage> ratingsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseMessage("Rating Service is currently unavailable. Please try again later."));
    }

    @GetMapping("/rides")
    public ResponseEntity<ResponseMessage> ridesFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseMessage("Ride Service is currently unavailable. Please try again later."));
    }
}

