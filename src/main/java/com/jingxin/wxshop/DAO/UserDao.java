package com.jingxin.wxshop.DAO;

import com.jingxin.wxshop.generate.User;
import com.jingxin.wxshop.generate.UserExample;
import com.jingxin.wxshop.generate.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDao {
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    public UserDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void insertUser(User user) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.insert(user);
        }
    }

    public User getByTel(String tel) {
        try(SqlSession sqlSession = sqlSessionFactory.openSession(true)){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            UserExample userExample = new UserExample();
            userExample.createCriteria().andTelEqualTo(tel);
           return  userMapper.selectByExample(userExample).get(0);
        }
    }
}
