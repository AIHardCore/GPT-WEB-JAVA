package com.cn.app.chatgptbot.model.req;

import com.cn.app.chatgptbot.base.PayWay;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

/**
 * ClassName:CreateOrderReq
 * Package:com.cn.app.chatgptbot.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/22 - 09:03
 * @Version: v1.0
 */
@Data
public class CreateOrderReq {

    @ApiModelProperty(value = "产品id")
    @NonNull
    private Long productId;

    @ApiModelProperty(value = "数量")
    @NonNull
    private Integer payNumber;

    @ApiModelProperty(value = "支付类型")
    @NonNull
    private PayWay type;
}
