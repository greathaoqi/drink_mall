package com.drinkmall.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.dto.AdminReferralNodeResponse;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.User;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final PointsLogMapper pointsLogMapper;
    private final OperationLogMapper operationLogMapper;

    @Override
    public Page<User> getUsers(String keyword, Integer ageVerified, Integer page, Integer size) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null) wrapper.like(User::getNickname, keyword).or().like(User::getPhone, keyword);
        if (ageVerified != null) wrapper.eq(User::getAgeVerified, ageVerified);
        wrapper.orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Map<String, Object> getUserDetail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) return null;
        Map<String, Object> detail = new HashMap<>();
        detail.put("user", user);
        detail.put("orderCount", orderMapper.selectCount(new LambdaQueryWrapper<com.drinkmall.entity.Order>().eq(com.drinkmall.entity.Order::getUserId, userId)));
        detail.put("totalSpent", BigDecimal.ZERO);
        detail.put("pointsBalance", user.getPoints() != null ? user.getPoints() : 0);
        return detail;
    }

    @Override
    public List<AdminReferralNodeResponse> getReferralAuditTree(Long adminId, Long userId, Integer maxDepth) {
        int depth = normalizeDepth(maxDepth);
        List<User> users = userMapper.selectReferralDescendants(userId, depth);
        OperationLog log = new OperationLog();
        log.setAdminUserId(adminId);
        log.setModule("referral");
        log.setAction("deep_query");
        log.setTargetId(String.valueOf(userId));
        log.setDetail("maxDepth=" + depth + ", resultCount=" + users.size());
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
        return users.stream()
                .map(user -> AdminReferralNodeResponse.fromUser(user, normalizeNodeDepth(user)))
                .toList();
    }

    @Override
    public void exportUsers(HttpServletResponse response, String keyword) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=users.xlsx");
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("用户数据");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("昵称");
            header.createCell(2).setCellValue("手机号");
            header.createCell(3).setCellValue("年龄验证");
            header.createCell(4).setCellValue("积分");
            header.createCell(5).setCellValue("注册时间");
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            if (keyword != null) wrapper.like(User::getNickname, keyword);
            List<User> users = userMapper.selectList(wrapper);
            int rowNum = 1;
            for (User u : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(u.getId());
                row.createCell(1).setCellValue(u.getNickname() != null ? u.getNickname() : "");
                row.createCell(2).setCellValue(u.getPhone() != null ? u.getPhone() : "");
                row.createCell(3).setCellValue(Boolean.TRUE.equals(u.getAgeVerified()) ? "已验证" : "未验证");
                row.createCell(4).setCellValue(u.getPoints() != null ? u.getPoints() : 0);
                row.createCell(5).setCellValue(u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
            }
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    private int normalizeDepth(Integer maxDepth) {
        if (maxDepth == null || maxDepth < 3) {
            return 3;
        }
        return Math.min(maxDepth, 50);
    }

    private Integer normalizeNodeDepth(User user) {
        return user.getReferralDepth() == null ? inferDepthFromInviter(user) : user.getReferralDepth();
    }

    private Integer inferDepthFromInviter(User user) {
        return user.getInviterId() == null ? 0 : 1;
    }
}
