package com.expert;

import com.expert.dto.InvitationHistoryQueryDTO;
import com.expert.result.PageResult;

public interface InvitationService {
    PageResult getInvitationHistory(InvitationHistoryQueryDTO queryDTO);
}
