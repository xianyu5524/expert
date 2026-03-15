package com.expert.aspect;

import com.expert.annotation.RequireRole;
import com.expert.context.BaseContext;
import com.expert.enums.UserRole;
import com.expert.exception.BaseException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class RoleAspectTest {

    private RoleAspect roleAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature signature;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        roleAspect = new RoleAspect();
        when(joinPoint.getSignature()).thenReturn(signature);
    }

    @AfterEach
    public void tearDown() {
        BaseContext.removeCurrentId();
        BaseContext.removeCurrentRole();
    }

    @Test
    public void testCheckRole_Success() throws NoSuchMethodException {
        // Mock method with annotation
        Method method = TestController.class.getMethod("adminMethod");
        when(signature.getMethod()).thenReturn(method);

        // Set context
        BaseContext.setCurrentRole("ADMIN");
        BaseContext.setCurrentId(1L);

        // Execute (should not throw)
        roleAspect.checkRole(joinPoint);
    }

    @Test
    public void testCheckRole_Forbidden() throws NoSuchMethodException {
        // Mock method with annotation
        Method method = TestController.class.getMethod("adminMethod");
        when(signature.getMethod()).thenReturn(method);

        // Set context
        BaseContext.setCurrentRole("EXPERT");
        BaseContext.setCurrentId(1L);

        // Execute (should throw)
        assertThrows(BaseException.class, () -> roleAspect.checkRole(joinPoint));
    }

    @Test
    public void testCheckRole_NoRole() throws NoSuchMethodException {
        // Mock method with annotation
        Method method = TestController.class.getMethod("adminMethod");
        when(signature.getMethod()).thenReturn(method);

        // Set context (no role)
        BaseContext.setCurrentId(1L);
        // Role is null

        // Execute (should throw)
        assertThrows(BaseException.class, () -> roleAspect.checkRole(joinPoint));
    }

    @Test
    public void testCheckRole_SuperAdminSuccess() throws NoSuchMethodException {
        // Mock method with annotation requiring ADMIN or SUPER_ADMIN
        Method method = TestController.class.getMethod("multiRoleMethod");
        when(signature.getMethod()).thenReturn(method);

        // Set context
        BaseContext.setCurrentRole("SUPER_ADMIN");
        BaseContext.setCurrentId(1L);

        // Execute (should not throw)
        roleAspect.checkRole(joinPoint);
    }

    static class TestController {
        @RequireRole(UserRole.ADMIN)
        public void adminMethod() {}

        @RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
        public void multiRoleMethod() {}
    }
}
