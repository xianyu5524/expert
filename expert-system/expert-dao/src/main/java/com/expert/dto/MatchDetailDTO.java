package com.expert.dto;

import com.expert.entity.Expert;
import com.expert.entity.Invitation;
import com.expert.entity.MatchScore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 匹配详情DTO
 */

@Data
public class MatchDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Expert expert;
    private MatchScore matchDetail;      // 匹配度详情
    private List<Invitation> invitationHistory; // 历史邀请记录

}
