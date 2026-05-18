package com.drinkmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drinkmall.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM users WHERE inviter_id = #{userId} ORDER BY created_at DESC")
    List<User> selectDirectInvitees(Long userId);

    @Select("""
            SELECT child.*
            FROM users parent
            JOIN users child ON child.inviter_id = parent.id
            WHERE parent.inviter_id = #{userId}
            ORDER BY child.created_at DESC
            """)
    List<User> selectIndirectInvitees(Long userId);

    @Select("""
            WITH RECURSIVE team AS (
                SELECT id FROM users WHERE inviter_id = #{userId}
                UNION ALL
                SELECT u.id FROM users u JOIN team t ON u.inviter_id = t.id
            )
            SELECT COUNT(*) FROM team
            """)
    Long countTeamMembers(Long userId);

    @Select("""
            WITH RECURSIVE team AS (
                SELECT u.*, 1 AS referral_depth
                FROM users u
                WHERE u.inviter_id = #{userId}
                UNION ALL
                SELECT child.*, team.referral_depth + 1 AS referral_depth
                FROM users child
                JOIN team ON child.inviter_id = team.id
                WHERE team.referral_depth < #{maxDepth}
            )
            SELECT * FROM team ORDER BY referral_depth ASC, created_at DESC
            """)
    List<User> selectReferralDescendants(Long userId, Integer maxDepth);
}
