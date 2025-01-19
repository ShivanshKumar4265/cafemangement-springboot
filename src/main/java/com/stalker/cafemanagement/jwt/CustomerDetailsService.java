package com.stalker.cafemanagement.jwt;

import com.stalker.cafemanagement.POJO.User;
import com.stalker.cafemanagement.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;


@Service
@Slf4j
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    private User userDetails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userDetails = userDao.findByEmail(username);
//        log.info("User details: " + userDetails.getEmail());
        if (!Objects.isNull(userDetails))
            return new org.springframework.security.core.userdetails.User(userDetails.getEmail(), userDetails.getPassword(), new ArrayList<>());
        else
            throw new UsernameNotFoundException("User not found");
    }

    public User getUserDetails() {
        return userDetails;
    }
}
