package com.novel.satoekn;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.novel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
* 获取用户权限列表
* @param loginId 用户ID
* @param loginType 登录类型
* @return 权限列表
*/
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 实现权限加载逻辑
        return Collections.emptyList();
    }

    /**
* 获取用户角色列表（带Redis缓存）
* @param loginId 用户ID
* @param loginType 登录类型
* @return 角色列表
*/
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 1. 尝试从Redis获取缓存
        String roleList = redisTemplate.opsForValue().get("role:" + loginId);
        if (StrUtil.isNotEmpty(roleList)) {
            return JSONUtil.toList(roleList, String.class);
        }

        // 2. 从数据库查询角色信息
        List<String> roles = userRepository.getUserRole(Long.parseLong((String) loginId));

        // 3. 存入Redis缓存
        redisTemplate.opsForValue().set("role:" + loginId, JSONUtil.toJsonStr(roles));

        return roles;
    }
}