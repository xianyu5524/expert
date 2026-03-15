package com.expert.controller;

import com.expert.InvitationsService;
import com.expert.annotation.RequireRole;
import com.expert.dto.InvitationHistoryQueryDTO;
import com.expert.dto.InvitationRequestDTO;
import com.expert.dto.InvitationResponseDTO;
import com.expert.enums.UserRole;
import com.expert.result.PageResult;
import com.expert.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invitations")
@Slf4j
public class InvitationsController {

    @Autowired
    private InvitationsService invitationsService;

    /**
     * 发起邀请
     */
    @PostMapping
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Result invite(@RequestBody InvitationRequestDTO invitationRequestDTO) {
        log.info("发起邀请: {}", invitationRequestDTO);
        invitationsService.invite(invitationRequestDTO);
        return Result.success();
    }

    /**
     * 专家回复邀请
     */
    @PutMapping("/{id}/response")
    @RequireRole(UserRole.EXPERT)
    public Result respond(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String notes) {
        log.info("回复邀请: id={}, status={}, notes={}", id, status, notes);
        invitationsService.respond(id, status, notes);
        return Result.success();
    }

    /**
     * 获取当前专家的邀请列表
     */
    @GetMapping("/expert/me")
    @RequireRole(UserRole.EXPERT)
    public Result<List<InvitationResponseDTO>> getMyInvitations() {
        return Result.success(invitationsService.getMyInvitations());
    }

    /**
     * 获取项目的邀请历史
     */
    @GetMapping("/project/history")
    @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Result<PageResult> getProjectInvitationHistory(InvitationHistoryQueryDTO queryDTO) {
        return Result.success(invitationsService.getProjectInvitationHistory(queryDTO));
    }
}
