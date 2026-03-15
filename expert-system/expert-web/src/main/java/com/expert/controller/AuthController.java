package com.expert.controller;

import com.expert.SysUserService;
import com.expert.context.BaseContext;
import com.expert.dto.LoginRequestDTO;
import com.expert.dto.RegisterRequestDTO;
import com.expert.entity.UserInfo;
import com.expert.enums.ResponseCodeEnum;
import com.expert.result.Result;
import com.expert.vo.LoginResponseVO;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证授权管理模块
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private SysUserService sysUserService;
    /**
     * 登录
     *
     * @return
     */
    //todo 认证功能尚未实现
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@RequestBody @Validated LoginRequestDTO loginRequestDTO) {
        log.info("用户登录{}", loginRequestDTO);

        LoginResponseVO loginResponseVO =sysUserService.login(loginRequestDTO);

        if(loginResponseVO == null){
            return Result.error("用户名或密码错误");
        }else {
            return Result.success(loginResponseVO);
        }
    }

    /**
     * 登出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {return Result.success();}


    /**
     * 获取当前用户信息
     *
     * @return
     */
    @GetMapping("/me")
    public Result<UserInfo> getMe(){
        Long currentId = BaseContext.getCurrentId();
        UserInfo userInfo=sysUserService.getMe();
        return Result.success(userInfo);
    }

    /**
     * 刷新Token
     *
     * @return
     */

    //todo 这里可能要做修改，根据前端自定义的token请求头获取token
    @PostMapping("/refresh")
    public Result<String> refresh(HttpServletRequest request){
        log.info("刷新token");
        String token = request.getHeader("Authorization");

        if(token!=null&&token.startsWith("Bearer ")){
            token=token.substring(7);
            String newToken = sysUserService.refresh(token);
            return Result.success(newToken);
        }else {
            return Result.error(ResponseCodeEnum.BAD_REQUEST);
        }
    }

    /**
     * 注册
     *
     * @return
     */
    //todo 未完成
    @PostMapping("/register")
    public Result<UserInfo> register(@RequestBody RegisterRequestDTO registerRequestDTO){
        log.info("用户账号注册:{}",registerRequestDTO);
        UserInfo userInfo = sysUserService.register(registerRequestDTO);
        return Result.success(userInfo);
    }
}


