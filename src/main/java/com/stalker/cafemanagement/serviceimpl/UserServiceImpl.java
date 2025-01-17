package com.stalker.cafemanagement.serviceimpl;

import com.stalker.cafemanagement.POJO.User;
import com.stalker.cafemanagement.constant.CafeConstant;
import com.stalker.cafemanagement.dao.UserDao;
import com.stalker.cafemanagement.service.UserService;
import com.stalker.cafemanagement.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

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

    private boolean validateSignUpRequest(Map<String, String> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("password");
    }
}
