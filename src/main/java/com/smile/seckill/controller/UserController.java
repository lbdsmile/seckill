package com.smile.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smile.seckill.domain.SeckillUser;
import com.smile.seckill.redis.RedisService;
import com.smile.seckill.result.Result;
import com.smile.seckill.service.SeckillUserService;

/**
 * 用户控制层
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
    SeckillUserService userService;
	
	@Autowired
	RedisService redisService;

    /**
     * 获取用户信息
     * @param model
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public Result<SeckillUser> info(Model model, SeckillUser user) {
        return Result.success(user);
    }
}