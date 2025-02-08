package com.stalker.cafemanagement.rest;

import com.stalker.cafemanagement.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/users")
public interface UserRest {

    @PostMapping(path = "/sign_up")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/sign_in")
    public ResponseEntity<String> signIn(@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping(path = "/get_users")
    public ResponseEntity<List<UserWrapper>> getUsers();

    @PostMapping(path = "/update_user")
    public ResponseEntity<String> updateUser(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/change_password")
    public ResponseEntity<String> changePassword(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/forgot_password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody(required = true) Map<String, String> requestMap);
}
