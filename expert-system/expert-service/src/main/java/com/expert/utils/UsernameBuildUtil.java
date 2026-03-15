package com.expert.utils;

import com.expert.entity.Expert;
import com.expert.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsernameBuildUtil {

    @Autowired
    SysUserMapper sysUserMapper;
    public String generateUsername(Expert expert){
        // 方案1: 使用手机号
        if (expert.getPhone() != null && !expert.getPhone().trim().isEmpty()) {
            String phone = expert.getPhone().trim();
            // 验证手机号格式
            if (phone.matches("^1[3-9]\\d{9}$")) {
                // 检查手机号是否已存在
                if (!sysUserMapper.existsByPhone(phone)) {
                    return phone;
                }
            }
        }

        // 方案2: 使用邮箱前缀
        if (expert.getEmail() != null && !expert.getEmail().trim().isEmpty()) {
            String email = expert.getEmail().trim();
            String emailPrefix = email.split("@")[0];
            String cleanPrefix = emailPrefix.replaceAll("[^a-zA-Z0-9]", "");
            if (cleanPrefix.length() >= 3) {
                String username = cleanPrefix.toLowerCase();
                if (!sysUserMapper.existsByUsername(username)) {
                    return username;
                }
            }
        }

        // 方案3: 生成唯一用户名
        return generateUniqueUsername(expert);
    }

    private String generateUniqueUsername(Expert expert) {
        String baseUsername;

        // 使用姓名拼音 + 随机数
        if (expert.getName() != null && !expert.getName().trim().isEmpty()) {
            // 简单实现：取姓名前两个字的拼音首字母
            String name = expert.getName().trim();
            String pinyinPrefix = convertToPinyinPrefix(name);
            baseUsername = pinyinPrefix + System.currentTimeMillis() % 10000;
        } else {
            baseUsername = "exp" + System.currentTimeMillis();
        }

        // 检查是否唯一，如果不唯一则添加随机后缀
        String username = baseUsername;
        int attempts = 0;
        while (sysUserMapper.existsByUsername(username) && attempts < 5) {
            String randomSuffix = String.valueOf((int)(Math.random() * 1000));
            username = baseUsername + randomSuffix;
            attempts++;
        }

        // 如果仍然冲突，使用时间戳
        if (sysUserMapper.existsByUsername(username)) {
            username = "user" + System.currentTimeMillis();
        }

        return username;
    }

    // 简单的拼音转换（你可以使用更专业的拼音库）
    private String convertToPinyinPrefix(String name) {
        // 这里使用简单的实现，实际项目中可以使用 pinyin4j 等库
        if (name.length() >= 2) {
            return name.substring(0, 2).toLowerCase();
        }
        return name.toLowerCase();
    }
}
