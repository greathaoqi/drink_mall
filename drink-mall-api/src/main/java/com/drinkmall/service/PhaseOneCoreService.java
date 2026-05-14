package com.drinkmall.service;

import com.drinkmall.dto.TeamOverviewResponse;
import com.drinkmall.entity.InvitationCode;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.User;
import com.drinkmall.enums.AssetType;
import com.drinkmall.enums.PaymentMethod;

import java.math.BigDecimal;

public interface PhaseOneCoreService {
    User registerUser(String openid, String inviteCode, String registerSource, boolean seedAccount);

    User registerUser(String openid, String unionid, Long inviterId, String inviteCode, String registerSource, boolean seedAccount);

    InvitationCode createInvitationCode(Long ownerUserId, Long adminId, String source);

    void validateBeforeCreateOrder(Long userId, Product product, PaymentMethod paymentMethod);

    void recordPaidOrder(Long userId, String zoneType, BigDecimal amount, String orderNo);

    void issueRetailReward(Long buyerUserId, BigDecimal payAmount, Long orderId, String orderNo);

    void validateWithdrawalAsset(AssetType assetType);

    TeamOverviewResponse getMiniTeamOverview(Long userId);
}
