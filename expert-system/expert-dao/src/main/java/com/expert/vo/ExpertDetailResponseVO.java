package com.expert.vo;

import com.expert.entity.Expert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertDetailResponseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Expert expert;
    private List projects;  //专家项目参与情况
    private List invitations;  //邀请专家历史记录
    private List achievements;  //专家个人成就

}
