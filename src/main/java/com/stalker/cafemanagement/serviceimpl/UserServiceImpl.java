package com.stalker.cafemanagement.serviceimpl;

import com.stalker.cafemanagement.POJO.User;
import com.stalker.cafemanagement.constant.CafeConstant;
import com.stalker.cafemanagement.dao.UserDao;
import com.stalker.cafemanagement.jwt.CustomerDetailsService;
import com.stalker.cafemanagement.jwt.JwtFilter;
import com.stalker.cafemanagement.jwt.JwtUtil;
import com.stalker.cafemanagement.service.UserService;
import com.stalker.cafemanagement.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerDetailsService customerDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {

        try{
            if (validateSignUpRequest(requestMap)) {
                User user = userDao.findByEmail(requestMap.get("email"));
                if(Objects.isNull(user)) {
                    user = new User();
                    user.setName(requestMap.get("name"));
                    user.setContactNumber(requestMap.get("contactNumber"));
                    user.setEmail(requestMap.get("email"));
                    user.setPassword(requestMap.get("password"));
                    user.setStatus(CafeConstant.ACTIVE);
                    user.setRole(CafeConstant.USER);
                    userDao.save(user);
                    return  CafeUtils.getResponseEntity(CafeConstant.USER_SIGN_UP_SUCCESS, HttpStatus.OK);
                }else{
                    return CafeUtils.getResponseEntity(CafeConstant.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                }
            }else{

                return CafeUtils.getResponseEntity(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return CafeUtils.getResponseEntity(e.getMessage().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> signIn(Map<String, String> requestMap) {
        System.out.println("inside signin");
        try{
            if (validateSignInRequest(requestMap)) {
                Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
                if(auth.isAuthenticated()){
                    if(customerDetailsService.getUserDetails().getStatus().equalsIgnoreCase(CafeConstant.ACTIVE)){
                        return new ResponseEntity<>("{\"token\":\"" + jwtUtil.generateToken(customerDetailsService.getUserDetails().getEmail(),customerDetailsService.getUserDetails().getRole()) + "\"}", HttpStatus.OK);
                    }
                }

            }else{
                return CafeUtils.getResponseEntity(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return CafeUtils.getResponseEntity(e.getMessage().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return CafeUtils.getResponseEntity(CafeConstant.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }

    private boolean validateSignUpRequest(Map<String, String> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("password");
    }

    private boolean validateSignInRequest(Map<String, String> requestMap) {
        return requestMap.containsKey("email") && requestMap.containsKey("password");
    }
}


/**
 *  User user = userDao.findByEmail(requestMap.get("email"));
 *                 if(Objects.nonNull(user) && user.getPassword().equals(requestMap.get("password"))) {
 *                     return CafeUtils.getResponseEntity(CafeConstant.USER_SIGN_IN_SUCCESS, HttpStatus.OK);
 *                 }else{
 *                     return CafeUtils.getResponseEntity(CafeConstant.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
 *                 }
 */