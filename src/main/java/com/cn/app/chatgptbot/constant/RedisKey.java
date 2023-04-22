package com.cn.app.chatgptbot.constant;

/**
 * redis key
 * the key about redis
 */
public enum RedisKey {
    /**
     * 阿里云短信配置
     */
    AILYUN_SMS_CONFIG(3, "aliyun.config.sms"),
    /**
     * 微信支付配置
     */
    WX_PAY(2, "wx.pay"),
    /**
     * 微信公众号配置
     */
    WX_GZH(1, "wx.gzh"),
    /**
     * 白辰易支付配置
     */
    PAY_CONFIG(0, "payConfig");

    private Integer value;
    private String name;

    RedisKey(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static RedisKey convert(String name){
        return RedisKey.valueOf(name);
    }

    public static RedisKey convert(int value){
        RedisKey[] enums = RedisKey.values();
        for(RedisKey e : enums){
            if(e.value == value) {
                return e;
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return this.name();
    }
}
