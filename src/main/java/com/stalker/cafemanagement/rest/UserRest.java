package com.stalker.cafemanagement.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = "/users")
public interface UserRest {

    @PostMapping(path = "/sign_up")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);
}