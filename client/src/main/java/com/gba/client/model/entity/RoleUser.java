package com.gba.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lxd
 * @since 2024-01-16 04:24:23
 */
@Data
@Accessors(chain = true)
@TableName("client_role_user")
@Schema(description = "角色用户关联关系")
public class RoleUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "角色")
    @TableField("role_id")
    private Long roleId;

    @Schema(description = "用户")
    private Long userId;
}
