package com.expert.entity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Expert implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    private LocalDate birthDate;

    @NotBlank(message = "职称不能为空")
    private String title;

    @NotBlank(message = "单位不能为空")
    private String unit;

    private String department;
    private String field;
    private String expertType;
    private Integer weight;
    private Integer guidanceCount;
    private String bankAccount;
    private String bankName;

    @NotBlank(message = "电话不能为空")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;
    private String avatar;

    private String introduction;
    private String status;
    private LocalDateTime lastInvite;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
