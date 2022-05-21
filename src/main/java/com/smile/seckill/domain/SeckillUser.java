package com.smile.seckill.domain;

import lombok.Data;

import java.util.Date;

/**
 * 秒杀用户信息实体类
 */
@Data
public class SeckillUser {
    private Long id;
    private String nickname;
    private String password;
    private String salt;
    private String head;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;
}
