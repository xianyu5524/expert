package com.expert.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT令牌相关配置,用于读取配置文件中的jwt配置信息
 */
@Component
@ConfigurationProperties(prefix = "expert.jwt")//用于读取以expert.jwt开头的配置
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String adminSecretKey; //  密钥
    private long adminTtl;  //  过期时间
    private String adminTokenName; //  令牌名称

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
