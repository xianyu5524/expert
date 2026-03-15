package com.expert.mapper;

import com.expert.dto.LoginRequestDTO;
import com.expert.dto.UserPageRequestDTO;
import com.expert.entity.SysUser;
import com.expert.entity.UserInfo;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysUserMapper {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND password = #{password}")
    SysUser login(LoginRequestDTO loginRequestDTO);

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser getByUsername(String username);

    @Select("select * from sys_user where user_id=#{currentId}")
    UserInfo getInfo(Long currentId);

    @Options(useGeneratedKeys = true,keyColumn = "user_id",keyProperty = "userId")
    @Insert("insert into sys_user(username, password, name, email, phone, role, status, last_login_time, created_admin_id, created_time, updated_time) " +
            "values (#{username},#{password},#{name},#{email},#{phone},#{role},#{status},#{lastLoginTime},#{createdAdminId},#{createdTime},#{updatedTime})")
    void insert(SysUser sysUser);

    @Select("select count(*)>0 from sys_user where phone=#{phone}")
    boolean existsByPhone(String phone);

    @Select("select count(*)>0 from sys_user where username=#{username}")
    boolean existsByUsername(String username);

    void delete(List<Integer> userIds);

    Page<UserInfo> getPageList(UserPageRequestDTO userPageRequestDTO);

    @Select("select * from sys_user where user_id=#{id}")
    SysUser getUserById(Long id);

    void update(SysUser user);

    @Update("update sys_user set status=#{status},updated_time=#{updatedTime} where user_id=#{userId}")
    void updateStatus(SysUser user);

    @Update("update sys_user set password=#{password},updated_time=#{updatedTime} where user_id=#{userId}")
    void updatePassword(SysUser user);
}
