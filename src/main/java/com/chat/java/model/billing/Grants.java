package com.chat.java.model.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * ClassName:Grants
 * Package:com.chat.java.model
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/23 - 17:00
 * @Version: v1.0
 */
@Data
public class Grants {
    private String object;
    @JsonProperty("data")
    private List<Datum> data;
}
