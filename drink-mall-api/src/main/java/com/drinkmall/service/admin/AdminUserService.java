package com.drinkmall.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.dto.AdminReferralNodeResponse;
import com.drinkmall.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface AdminUserService {
    Page<User> getUsers(String keyword, Integer ageVerified, Integer page, Integer size);
    Map<String, Object> getUserDetail(Long userId);
    List<AdminReferralNodeResponse> getReferralAuditTree(Long adminId, Long userId, Integer maxDepth);
    void exportUsers(HttpServletResponse response, String keyword);
}
