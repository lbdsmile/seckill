package com.smile.seckill.controller;

import com.smile.seckill.domain.User;
import com.smile.seckill.rabbitmq.MQSender;
import com.smile.seckill.redis.RedisService;
import com.smile.seckill.redis.UserKey;
import com.smile.seckill.result.CodeMsg;
import com.smile.seckill.result.Result;
import com.smile.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 调试示例控制器
 */
@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    /**
     * thymeleaf 测试
     * @param model
     * @return
     */
    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "Smile");
        return "hello";
    }

    /**
     * 返回信息测试
     * @return
     */
    @RequestMapping("/hello")
    @ResponseBody
    public Result home() {
        return Result.success("hello");
    }

    /**
     * 返回错误测试
     * @return
     */
    @RequestMapping("/error")
    @ResponseBody
    public Result<String> error() {
        return Result.error(CodeMsg.SESSION_ERROR);
    }

    /**
     * 通过id获取用户信息
     * @return
     */
    @RequestMapping("/db/get")
    @ResponseBody
    public Result getUser() {
        return Result.success(userService.getById(2));
    }

    /**
     * 测试插入用户
     * @return
     */
    @RequestMapping("/db/set")
    @ResponseBody
    public Result setUser() {
        return Result.success(userService.insertUser());
    }

    /**
     * 将用户信息加入 Redis
     * @return
     */
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result redisSet() {
        User user  = new User();
        user.setId(1);
        user.setName("1111");
        return Result.success(redisService.set(UserKey.getById, ""+1, user));
    }

    /**
     * 通过 id 从 Redis 中取出用户信息
     * @return
     */
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result redisGet() {
        // UserKey : id 1
        User user = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(user);
    }

    /**
     * MQ queue 测试
     * @return
     */
    @RequestMapping("/mq")
    @ResponseBody
    public Result mq() {
        sender.send("我是MQ！");
        return Result.success("MQ发送成功");
    }
    /**
     * MQ topic 模式测试
     * @return
     */
	@RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> topic() {
		sender.sendTopic("hello,imooc");
        return Result.success("Hello，world");
    }
    /**
     * MQ fanout 模式测试
     * @return
     */
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> fanout() {
        sender.sendFanout("hello, MQ");
        return Result.success("Hello，world");
    }
    /**
     * MQ header 模式测试
     * @return
     */
    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> header() {
        sender.sendHeader("hello, MQ");
        return Result.success("Hello，world");
    }
}