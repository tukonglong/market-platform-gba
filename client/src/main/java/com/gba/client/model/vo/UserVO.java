package com.gba.client.model.vo;

import com.gba.client.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 管理端用户信息
 * </p>
 *
 * @author lxd
 * @since 2024-01-09 03:13:54
 */
@Data
@Accessors(chain = true)
@Schema(description = "<用户>响应体")
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "令牌")
    private String token;

    @Schema(description = "流水号")
    private String serialNumber;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "电话")
    private String tel;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "上次登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "用户角色")
    private List<RoleVO> roles;

    @Schema(description = "菜单")
    private List<MenuVO> menus;

    @Schema(description = "按钮Code")
    private List<String> buttonCodes;

    @Schema(description = "乐观锁")
    private Integer version;

    @Schema(description = "上次登录ip")
    private String lastIp;

    /**
     * vo 转 entity
     *
     * @return entity
     */
    public User toEntity() {
        User entity = new User();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    /**
     * vos 转 entities
     *
     * @param vos
     * @return entities
     */
    public static List<User> toEntities(List<UserVO> vos) {
        return vos.stream().map(UserVO::toEntity).collect(Collectors.toList());
    }

    /**
     * entity 转 vo
     *
     * @param entity
     * @return vo
     */
    public static UserVO fromEntity(User entity) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * entities 转 vos
     *
     * @param entities
     * @return vos
     */
    public static List<UserVO> fromEntities(List<User> entities) {
        return entities.stream().map(UserVO::fromEntity).collect(Collectors.toList());
    }
}
