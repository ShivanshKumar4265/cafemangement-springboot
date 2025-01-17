package com.stalker.cafemanagement.restimpl;

import com.stalker.cafemanagement.constant.CafeConstant;
import com.stalker.cafemanagement.rest.UserRest;
import com.stalker.cafemanagement.service.UserService;
import com.stalker.cafemanagement.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserRestImpl implements UserRest {
    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try{
            return userService.signUp(requestMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

