package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamOverviewResponse {
    private Long teamTotal;
    private List<TeamMemberResponse> directMembers;
    private List<TeamMemberResponse> indirectMembers;
    private List<TeamMemberResponse> thirdLevelMembers;
}
