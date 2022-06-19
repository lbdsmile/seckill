package com.smile.seckill.service;

import com.smile.seckill.dao.UserDao;
import com.smile.seckill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务
 */
@Service
public class UserService {

    @Autowired
    UserDao userDao;

    /**
     * 获取用户ID
     * @param id
     * @return
     */
    public User getById(int id){
        return userDao.getById(id);
    }

    /**
     * 测试 插入用户
     * @return
     */
    @Transactional
    public Boolean insertUser(){
        User userSmlie = new User();
        userSmlie.setId((int) Math.random()*1000);
        userSmlie.setName("userSmlie");
        userDao.insert(userSmlie);

        User userHappy = new User();
        userHappy.setId((int) Math.random()*1000);
        userHappy.setName("userHappy");
        userDao.insert(userHappy);

        return true;
    }
}