package com.cn.app.chatgptbot.constant;

/**
 * redis key
 * the key about redis
 */
public enum RedisKey {
    /**
     * 阿里云短信配置
     */
    AILYUN_SMS_CONFIG(1, "aliyun.config.sms"),
    /**
     * 白辰易支付配置
     */
    PAY_CONFIG(1, "payConfig");

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
