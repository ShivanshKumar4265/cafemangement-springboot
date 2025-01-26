package com.stalker.cafemanagement.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CafeUtils {

    public CafeUtils() {
    }

    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus status) {
        return new ResponseEntity<String>("{\"message\":\"" + message + "\"}", status);
    }

}
