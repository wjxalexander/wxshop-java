package com.jingxin.wxshop.service;

import com.jingxin.wxshop.DAO.UserDao;
import com.jingxin.wxshop.generate.User;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserDao userDao;
    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUserIfNotExist(String tel) {
        // 并发问题
        User user = new User();
        user.setTel(tel);
        user.setUsername(tel + "@wxshop.com");
        user.setEmail(tel);
        try {
            userDao.insertUser(user);
        }catch (PersistenceException e){
            return userDao.getByTel(tel);
        }catch (Exception e){
            e.printStackTrace();
        }
        return user;
    }

    public Optional<User> getUserByTel(String tel) {
        return Optional.ofNullable(userDao.getByTel(tel));
    }
}
