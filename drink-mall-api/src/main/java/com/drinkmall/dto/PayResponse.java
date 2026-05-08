package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayResponse {
    private String orderNo;
    private String prepayId;
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String packageStr;
    private String signType;
    private String paySign;
}
