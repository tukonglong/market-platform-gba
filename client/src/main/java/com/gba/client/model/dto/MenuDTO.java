package com.gba.client.model.dto;

import com.gba.client.model.entity.Menu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单
 * </p>
 *
 * @author lxd
 * @since 2024-01-17 05:55:08
 */
@Data
@Accessors(chain = true)
@Schema(description = "<菜单>请求体")
public class MenuDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "id")
    private Long id;

    @Schema(description = "页面标识(用于权限控制, 格式: 模块路由:功能路由, 例: user:insert)")
    private String code;

    @Schema(description = "上级id")
    private Long pid;

    @Schema(description = "节点路径")
    private String path;

    @Schema(description = "菜单名称")
    private String title;

    @Schema(description = "页面地址")
    private String url;

    @Schema(description = "图标地址")
    private String icon;

    @Schema(description = "等级")
    private Integer level;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "节点类型 1:菜单 2:按钮")
    private Integer type;

    @Schema(description = "状态 0:启用, 1:禁用")
    private Integer status;

    @Schema(description = "乐观锁")
    private Integer version;

    /**
     * dto 转 entity
     *
     * @return entity
     */
    public Menu toEntity() {
        Menu entity = new Menu();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    /**
     * dtoList 转 entities
     *
     * @param dtoList
     * @return entities
     */
    public static List<Menu> toEntities(List<MenuDTO> dtoList) {
        return dtoList.stream().map(MenuDTO::toEntity).collect(Collectors.toList());
    }

    /**
     * entity 转 dto
     *
     * @param entity
     * @return dto
     */
    public static MenuDTO fromEntity(Menu entity) {
        if(entity == null) {
          return null;
        }

        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /**
     * entities 转 dtoList
     *
     * @param entities
     * @return dtoList
     */
    public static List<MenuDTO> fromEntities(List<Menu> entities) {
        return entities.stream().map(MenuDTO::fromEntity).collect(Collectors.toList());
    }
}
