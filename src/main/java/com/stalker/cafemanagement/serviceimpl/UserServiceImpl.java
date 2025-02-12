package com.stalker.cafemanagement.serviceimpl;

import com.stalker.cafemanagement.POJO.User;
import com.stalker.cafemanagement.constant.CafeConstant;
import com.stalker.cafemanagement.dao.UserDao;
import com.stalker.cafemanagement.jwt.CustomerDetailsService;
import com.stalker.cafemanagement.jwt.JwtFilter;
import com.stalker.cafemanagement.jwt.JwtUtil;
import com.stalker.cafemanagement.service.UserService;
import com.stalker.cafemanagement.utils.CafeUtils;
import com.stalker.cafemanagement.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {

        try {
            if (validateSignUpRequest(requestMap)) {
                User user = userDao.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    user = new User();
                    user.setName(requestMap.get("name"));
                    user.setContactNumber(requestMap.get("contactNumber"));
                    user.setEmail(requestMap.get("email"));
                    user.setPassword(requestMap.get("password"));
                    user.setStatus(CafeConstant.INACTIVE);
                    user.setRole(CafeConstant.USER);
                    userDao.save(user);
                    return CafeUtils.getResponseEntity(CafeConstant.USER_SIGN_UP_SUCCESS, HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity(CafeConstant.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                }
            } else {

                return CafeUtils.getResponseEntity(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return CafeUtils.getResponseEntity(e.getMessage().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> signIn(Map<String, String> requestMap) {
        System.out.println("inside signin");
        try {
            if (validateSignInRequest(requestMap)) {
                Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
                if (auth.isAuthenticated()) {
                    if (customerDetailsService.getUserDetails().getStatus().equalsIgnoreCase(CafeConstant.ACTIVE)) {
                        return new ResponseEntity<>("{\"token\":\"" + jwtUtil.generateToken(customerDetailsService.getUserDetails().getEmail(), customerDetailsService.getUserDetails().getRole()) + "\"}", HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Wait for admin approval", HttpStatus.BAD_REQUEST);
                    }
                }

            } else {
                return CafeUtils.getResponseEntity(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return CafeUtils.getResponseEntity(e.getMessage().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return CafeUtils.getResponseEntity(CafeConstant.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getUser() {
        try {
            System.out.println("inside get user " + jwtFilter.isAdmin());
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            // Logging the exception can help with debugging in real-world apps
            e.printStackTrace();
            throw new RuntimeException("Error occurred while fetching users", e);
        }
    }

    @Override
    public ResponseEntity<String> updateUser(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optionalUser = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if (optionalUser.isEmpty()) {
                    CafeUtils.getResponseEntity(CafeConstant.USER_DOESNT_EXIST, HttpStatus.BAD_REQUEST);
                } else {
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("User Updated Successfully", HttpStatus.OK);
                }
            } else {
                CafeUtils.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return CafeUtils.getResponseEntity(CafeConstant.SOME_THING_WENT_WRONG, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {

            Optional<User> optionalUser = userDao.findById(Integer.parseInt(requestMap.get("id")));
            if (optionalUser.isEmpty()) {
                CafeUtils.getResponseEntity(CafeConstant.USER_DOESNT_EXIST, HttpStatus.BAD_REQUEST);
            } else {
                String password = optionalUser.get().getPassword();
                if (password.equals(requestMap.get("old_password"))) {
                    userDao.updatePassword(Integer.parseInt(requestMap.get("id")), requestMap.get("old_password"), requestMap.get("new_password"));
                    return CafeUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Old Password is Incorrect", HttpStatus.UNAUTHORIZED);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return CafeUtils.getResponseEntity(CafeConstant.SOME_THING_WENT_WRONG, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<Map<String, Object>> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            Map<String, Object> response = new LinkedHashMap<>();

            if (user == null) {
                response.put("status", "failure");
                response.put("message", "User does not exist.");
                response.put("data", null);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                response.put("status", "success");
                response.put("message", "Password forgotten.");
                response.put("data", user); // This will include the user data
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private boolean validateSignUpRequest(Map<String, String> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("password");
    }

    private boolean validateSignInRequest(Map<String, String> requestMap) {
        return requestMap.containsKey("email") && requestMap.containsKey("password");
    }
}