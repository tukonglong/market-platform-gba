package com.gba.client.model.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gba.common.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.PropertyName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

/**
 * <p>
 * 客户端用户信息
 * </p>
 *
 * @author lxd
 * @since 2023-12-30 08:51:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("client_user")
@Schema(name = "用户实体类", description = "客户端用户信息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseEntity implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "流水号")
    @TableField(value = "serial_number", condition = SqlCondition.LIKE)
    private String serialNumber;

    @Schema(description = "昵称")
    @TableField(value = "nick_name", condition = SqlCondition.LIKE)
    @NotNull(message = "昵称不能为空")
    @DiffInclude
    @PropertyName("昵称")
    private String nickName;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "电话")
    @TableField("tel")
    private String tel;

    @Schema(description = "用户名")
    @TableField(value = "username", condition = SqlCondition.LIKE)
    @NotNull(message = "用户名不能为空")
    @DiffInclude
    @PropertyName("用户名")
    private String username;

    @Schema(description = "密码")
    @TableField("password")
    @DiffInclude
    @PropertyName("密码")
    private String password;

    @Schema(description = "加价")
    @TableField("increase")
    private BigDecimal increase;

    @Schema(description = "授信")
    @TableField("credit")
    private Integer credit;

    @Schema(description = "上次登录时间")
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 状态
     */
    @Getter
    @AllArgsConstructor
    public enum Status {
        ENABLE(0, "启用"),

        DISABLE(1, "禁用"),
        ;
        private final Integer code;

        private final String value;

        public static String getValue(Integer code) {
            for (Status value : Status.values()) {
                if (Objects.equals(value.getCode(), code)) {
                    return value.getValue();
                }
            }
            return "";
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
