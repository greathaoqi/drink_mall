package com.drinkmall.service.support;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentAccessDecision {
    private boolean levelAllowed;
    private boolean canView;
    private boolean canBuy;
    private String watchLevel;
    private String levelText;
    private String lockReason;
}
