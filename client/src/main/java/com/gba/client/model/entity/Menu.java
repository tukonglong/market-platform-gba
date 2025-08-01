package com.gba.client.model.entity;

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
 * 菜单
 * </p>
 *
 * @author lxd
 * @since 2024-01-17 05:55:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("client_menu")
@Schema(description = "菜单")
public class Menu extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "上级id")
    @TableField("pid")
    @DiffInclude
    @PropertyName("所属父类")
    private Long pid;

    @Schema(description = "权限标识(用于权限控制, 格式: 项目路由:模块路由:功能路由, 例: admin:user:insert)")
    @TableField("code")
    @DiffInclude
    @PropertyName("权限标识")
    private String code;

    @Schema(description = "节点路径")
    @TableField("path")
    private String path;

    @Schema(description = "菜单名称")
    @TableField("title")
    @DiffInclude
    @PropertyName("菜单名称")
    private String title;

    @Schema(description = "页面地址")
    @TableField("url")
    @DiffInclude
    @PropertyName("页面地址")
    private String url;

    @Schema(description = "图标地址")
    @TableField("icon")
    @DiffInclude
    @PropertyName("图标地址")
    private String icon;

    @Schema(description = "等级")
    @TableField("level")
    @DiffInclude
    @PropertyName("等级")
    private Integer level;

    @Schema(description = "排序")
    @TableField("sort")
    @DiffInclude
    @PropertyName("排序")
    private Integer sort;

    @Schema(description = "节点类型 1:菜单 2:按钮")
    @TableField("type")
    @DiffInclude
    @PropertyName("节点类型")
    private Integer type;

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

    /**
     * 类型
     */
    @Getter
    @AllArgsConstructor
    public enum Type {
        MENU(1, "菜单"),

        BUTTON(2, "按钮"),
        ;
        private final Integer code;

        private final String value;

        public static String getValue(Integer code) {
            for (Type value : Type.values()) {
                if (Objects.equals(value.getCode(), code)) {
                    return value.getValue();
                }
            }
            return "";
        }
    }
}
