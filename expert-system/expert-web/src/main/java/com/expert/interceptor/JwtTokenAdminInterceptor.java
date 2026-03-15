package com.expert.interceptor;

import com.expert.constant.JwtClaimsConstant;
import com.expert.context.BaseContext;
import com.expert.enums.ResponseCodeEnum;
import com.expert.properties.JwtProperties;
import com.expert.result.Result;
import com.expert.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    // todo 这里后续需要改成从自定义token中获取令牌
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截的不是动态方法，直接放行
            return true;
        }

        // 1、从请求头中获取令牌
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // String token = request.getHeader(jwtProperties.getAdminTokenName());
        // 2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            System.out.println(claims);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            String role = (String) claims.get("role"); // Extract role

            BaseContext.setCurrentId(userId);
            if (role != null) {
                BaseContext.setCurrentRole(role);
            }

            log.info("当前用户id：{}, 角色: {}", userId, role);
            // 3、通过，放行
            return true;
        } catch (Exception e) {
            // 4、不通过，响应401状态码
            response.setStatus(ResponseCodeEnum.UNAUTHORIZED.getCode());
            return false;
        }
    }
}
