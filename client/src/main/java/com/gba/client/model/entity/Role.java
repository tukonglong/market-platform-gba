package com.gba.client.model.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gba.common.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.PropertyName;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 客户端角色
 * </p>
 *
 * @author lxd
 * @since 2023-12-30 08:48:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("client_role")
@Schema(name = "角色")
public class Role extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色code")
    @TableField("code")
    private String code;

    @Schema(description = "角色名")
    @TableField(value = "name", condition = SqlCondition.LIKE)
    @DiffInclude
    @PropertyName("角色名")
    private String name;

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
}
