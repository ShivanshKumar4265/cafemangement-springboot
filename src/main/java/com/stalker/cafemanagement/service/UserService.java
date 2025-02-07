package com.stalker.cafemanagement.service;

import com.stalker.cafemanagement.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    public ResponseEntity<String> signUp(Map<String, String> requestMap);
    public ResponseEntity<String> signIn(Map<String, String> requestMap);
    public ResponseEntity<String> updateUser(Map<String, String> requestMap);
    public ResponseEntity<List<UserWrapper>> getUser();
}
