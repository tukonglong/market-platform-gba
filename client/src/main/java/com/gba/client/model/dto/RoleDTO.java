package com.gba.client.model.dto;

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
@Schema(description = "<角色>请求体")
public class RoleDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "id")
    private Long id;

    @Schema(description = "code")
    private String code;

    @Schema(description = "含义")
    private String name;

    @Schema(description = "菜单ids")
    private List<Long> menuIds;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态 0:启用, 1:禁用")
    private Integer status;

    @Schema(description = "乐观锁")
    private Integer version;

    /**
     * dto 转 entity
     *
     * @return entity
     */
    public Role toEntity() {
        Role entity = new Role();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    /**
     * dtoList 转 entities
     *
     * @param dtoList
     * @return entities
     */
    public static List<Role> toEntities(List<RoleDTO> dtoList) {
        return dtoList.stream().map(RoleDTO::toEntity).collect(Collectors.toList());
    }

    /**
     * entity 转 dto
     *
     * @param entity
     * @return dto
     */
    public static RoleDTO fromEntity(Role entity) {
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /**
     * entities 转 dtoList
     *
     * @param entities
     * @return dtoList
     */
    public static List<RoleDTO> fromEntities(List<Role> entities) {
        return entities.stream().map(RoleDTO::fromEntity).collect(Collectors.toList());
    }
}
