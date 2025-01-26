package com.stalker.cafemanagement.dao;

import com.stalker.cafemanagement.POJO.User;
import com.stalker.cafemanagement.wrapper.UserWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {

    User findByEmail(@Param("email") String email);
    List<UserWrapper> getAllUsers();
}
