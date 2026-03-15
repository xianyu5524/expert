package com.expert.controller;


import com.expert.InvitationService;
import com.expert.RecommendationsService;
import com.expert.dto.InvitationHistoryQueryDTO;
import com.expert.dto.InvitationResponseDTO;
import com.expert.dto.RecommendationQueryDTO;
import com.expert.mapper.InvitationsMapper;
import com.expert.result.PageResult;
import com.expert.result.Result;
import com.expert.vo.ExpertRecommendationVO;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@Slf4j
public class RecommendationsController {
    @Autowired
    RecommendationsService recommendationsService;

    @Autowired
    InvitationService invitationService;


    @PostMapping()
    public Result<List<ExpertRecommendationVO>> getRecommendations(@RequestBody RecommendationQueryDTO queryDTO){
        log.info("获取该项目的推荐专家列表：{}",queryDTO);
        List<ExpertRecommendationVO> list=recommendationsService.getRecommendExpertList(queryDTO);
        return Result.success(list);
    }

    @GetMapping("/invitations/history")
    public Result<PageResult> getInvitationHistory(InvitationHistoryQueryDTO queryDTO){
        log.info("获取专家的邀请记录：{}",queryDTO);
        PageResult pageResult=invitationService.getInvitationHistory(queryDTO);
        return Result.success(pageResult);
    }
}
