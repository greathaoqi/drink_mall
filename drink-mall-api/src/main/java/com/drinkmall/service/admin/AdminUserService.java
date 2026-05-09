package com.drinkmall.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.User;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface AdminUserService {
    Page<User> getUsers(String keyword, Integer ageVerified, Integer page, Integer size);
    Map<String, Object> getUserDetail(Long userId);
    void exportUsers(HttpServletResponse response, String keyword);
}
