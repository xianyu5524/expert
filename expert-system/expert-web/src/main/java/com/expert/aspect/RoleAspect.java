package com.expert.aspect;

import com.expert.annotation.RequireRole;
import com.expert.context.BaseContext;
import com.expert.enums.ResponseCodeEnum;
import com.expert.enums.UserRole;
import com.expert.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 权限校验切面
 */
@Aspect
@Component
@Slf4j
public class RoleAspect {

    @Pointcut("@annotation(com.expert.annotation.RequireRole)")
    public void rolePointCut() {}

    @Before("rolePointCut()")
    public void checkRole(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        
        String currentRoleStr = BaseContext.getCurrentRole();
        
        if (currentRoleStr == null) {
            throw new BaseException("未登录或无角色信息");
        }

        UserRole[] requiredRoles = requireRole.value();
        boolean hasRole = false;
        
        for (UserRole role : requiredRoles) {
            if (role.name().equals(currentRoleStr)) {
                hasRole = true;
                break;
            }
        }

        if (!hasRole) {
            log.warn("用户 {} 尝试访问 {}，但角色 {} 不满足要求 {}", 
                    BaseContext.getCurrentId(), 
                    method.getName(), 
                    currentRoleStr, 
                    Arrays.toString(requiredRoles));
            throw new BaseException("权限不足，拒绝访问");
        }
    }
}
