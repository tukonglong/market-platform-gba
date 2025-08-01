package com.gba.client.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.constant.Constant;
import com.gba.client.mapper.RoleMapper;
import com.gba.client.model.entity.Role;
import com.gba.client.model.vo.RoleVO;
import com.gba.client.service.RoleService;
import com.gba.client.service.RoleUserService;
import com.gba.common.util.RedisDataBase1Utils;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 客户端角色 服务实现类
 * </p>
 *
 * @author lxd
 * @since 2024-01-10 10:52:14
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RedisDataBase1Utils redisDataBase1Utils;
    private final RoleUserService roleUserService;


    @Override
    public Map<Long, Role> getRoleAll() {
        Map<Long, Role> roleMap = lambdaQuery()
                .list().stream()
                .collect(Collectors.toMap(Role::getId, Function.identity()));

        // 将所有角色信息缓存到Redis的Hash中
        Map<String, Role> cacheMap = roleMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> String.valueOf(entry.getKey()),
                        Map.Entry::getValue
                ));
        redisDataBase1Utils.setMap(Constant.ROLE, cacheMap);

        return roleMap;
    }

    @Override
    public List<RoleVO> getRolesByUserId(long userId) {
        List<Long> roleIds = roleUserService.getRoleIdsByUserId(userId);
        Map<String, Role> roleMap = redisDataBase1Utils.getMultiMapValue(Constant.ROLE, roleIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toSet()));

        // 如果缓存中未找到所有角色信息，重新加载
        if (MapUtils.isEmpty(roleMap) || roleMap.size() != roleIds.size()) {
            getRoleAll();
            roleMap = redisDataBase1Utils.getMultiMapValue(Constant.ROLE, roleIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet()));
        }

        return roleMap.values()
                .stream()
                .filter(role -> Role.Status.ENABLE.getCode().equals(role.getStatus()))
                .map(RoleVO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, List<RoleVO>> getRolesByUserIds(List<Long> userIds) {
        Map<Long, List<Long>> userRolesMap = roleUserService.getRoleIdsByUserIds(userIds);
        Map<Long, List<RoleVO>> result = Maps.newConcurrentMap();

        userRolesMap.forEach((userId, roleIds) -> {
            Map<String, Role> roleMap = redisDataBase1Utils.getMultiMapValue(Constant.ROLE, roleIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet()));

            // 如果缓存中未找到所有角色信息，重新加载
            if (MapUtils.isEmpty(roleMap) || roleMap.size() != roleIds.size()) {
                getRoleAll();
                roleMap = redisDataBase1Utils.getMultiMapValue(Constant.ROLE, roleIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.toSet()));
            }

            List<RoleVO> roleVOS = roleMap.values()
                    .stream()
                    .filter(role -> Role.Status.ENABLE.getCode().equals(role.getStatus()))
                    .map(RoleVO::fromEntity)
                    .collect(Collectors.toList());
            result.put(userId, roleVOS);
        });

        return result;
    }
}
