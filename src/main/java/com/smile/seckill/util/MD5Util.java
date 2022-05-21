package com.smile.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5加密工具类
 * 思考：关于 salt 盐 动态化生成的支持与存储，是否可以同步将盐存储到数据库中？
 */
public class MD5Util {

    private static final String salt = "1a2b3c4d"; // 加密所用的 salt

    /**
     * 计算MD5的加密值
     * Calculates the MD5 digest(摘要) and returns the value as a 32 character hex string.
     * @param src
     * @return
     */
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    /**
     * 对输入明文密码加密，将输入的明文密码转换成表单实际上送的加密后的密码
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5)
                + salt.charAt(4);
        return md5(str);
    }

    /**
     * 将表单密码再次加密，转换成数据库中存储的密码
     * @param formPass
     * @param salt
     * @return
     */
    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5)
                + salt.charAt(4);
        return md5(str);
    }

    /**
     * 将输入的明文密码，两次加密后，转换成数据库存储的密码
     * @param inputPass
     * @param saltDB
     * @return
     */
    public static String inputPassToDbPass(String inputPass, String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }

    // 该静态类的测试方法
//    public static void main(String[] args) {
//        System.out.println(inputPassToFormPass("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9
//		System.out.println(formPassToDBPass(inputPassToFormPass("123456"), "1a2b3c4d"));
//		System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));//b7797cce01b4b131b433b6acf4add449
//    }
}