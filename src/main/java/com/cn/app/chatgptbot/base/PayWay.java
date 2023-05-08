package com.cn.app.chatgptbot.base;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum PayWay {
    //0：微信支付、1：支付宝支付、2：QQ支付
    /**
     * 微信支付
     */
    WX_PAY(0, "微信支付"),
    /**
     * 支付宝支付
     */
    ALI_PAY(1, "支付宝支付"),
    /**
     * QQ支付
     */
    QQ_PAY(2, "QQ支付");

    @EnumValue
    private Integer value;
    private String name;

    PayWay(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static PayWay convert(String name){
        return PayWay.valueOf(name);
    }

    public static PayWay convert(int value){
        PayWay[] enums = PayWay.values();
        for(PayWay e : enums){
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
