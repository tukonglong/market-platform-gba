package com.gba.client.model.vo;

import com.gba.client.model.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 客户端角色
 * </p>
 *
 * @author lxd
 * @since 2024-01-10 10:52:14
 */
@Data
@Accessors(chain = true)
@Schema(description = "<角色>响应体")
public class RoleVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "id")
    private Long id;

    @Schema(description = "code")
    private String code;

    @Schema(description = "含义")
    private String name;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "菜单")
    private List<MenuVO> menus;

    @Schema(description = "乐观锁")
    private Integer version;

    /**
     * vo 转 entity
     *
     * @return entity
     */
    public Role toEntity() {
        Role entity = new Role();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    /**
     * vos 转 entities
     *
     * @param vos
     * @return entities
     */
    public static List<Role> toEntities(List<RoleVO> vos) {
        return vos.stream().map(RoleVO::toEntity).collect(Collectors.toList());
    }

    /**
     * entity 转 vo
     *
     * @param entity
     * @return vo
     */
    public static RoleVO fromEntity(Role entity) {
        if(entity == null) {
            return null;
        }

        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * entities 转 vos
     *
     * @param entities
     * @return vos
     */
    public static List<RoleVO> fromEntities(List<Role> entities) {
        return entities.stream().map(RoleVO::fromEntity).collect(Collectors.toList());
    }
}
