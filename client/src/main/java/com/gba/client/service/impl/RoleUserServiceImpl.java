package com.gba.client.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SimpleQuery;
import com.gba.client.mapper.RoleUserMapper;
import com.gba.client.model.entity.RoleUser;
import com.gba.client.service.RoleUserService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lxd
 * @since 2024-01-16 04:24:23
 */
@Service
public class RoleUserServiceImpl extends ServiceImpl<RoleUserMapper, RoleUser> implements RoleUserService {
    @Override
    public List<Long> getRoleIdsByUserId(long userId) {
        return SimpleQuery.list(Wrappers.<RoleUser>lambdaQuery().eq(RoleUser::getUserId, userId), RoleUser::getRoleId);
    }

    @Override
    public Map<Long, List<Long>> getRoleIdsByUserIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Maps.newHashMap();
        }

        return lambdaQuery().in(RoleUser::getUserId, userIds)
                .list()
                .stream()
                .collect(Collectors.groupingBy(RoleUser::getUserId
                        , Collectors.mapping(RoleUser::getRoleId, Collectors.toList())));
    }

    @Override
    public List<Long> getUserIdsByRoleId(long roleId) {
        return SimpleQuery.list(Wrappers.<RoleUser>lambdaQuery().eq(RoleUser::getRoleId, roleId), RoleUser::getRoleId);
    }
}
