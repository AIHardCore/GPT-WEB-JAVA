package com.cn.app.chatgptbot.model.wx;

import lombok.Data;

@Data
public class PrepayResult {
    private String appId; // 公众号appid
    private String timeStamp; // String	是	时间戳从1970年1月1日00:00:00至今的秒数,即当前的时间
    private String nonceStr; //	String	是	随机字符串，长度为32个字符以下。
    private String Package; //	String	是	统一下单接口返回的 prepay_id 参数值，提交格式如：prepay_id=*
    private String signType; //	String	是	签名类型，默认为MD5，支持HMAC-SHA256和MD5。注意此处需与统一下单的签名类型一致
    private String paySign; // 支付签名
}
