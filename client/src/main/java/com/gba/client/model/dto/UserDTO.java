package com.gba.client.model.dto;

import com.gba.client.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author lxd
 * @since 2023-12-28 05:18:32
 */
@Data
@Accessors(chain = true)
@Schema(description = "<用户>请求体")
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "id")
    private Long id;

    @Schema(description = "流水号")
    private String serialNumber;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "邮箱")
    @Email(message = "请输入正确的邮箱地址")
    private String email;

    @Schema(description = "电话")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String tel;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "密码/确认密码")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,15}$", message = "密码格式不正确")
    @NotNull(message = "密码不能为空")
    private String password;

    @Schema(description = "旧密码")
    @NotNull(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,15}$", message = "密码格式不正确")
    @NotNull(message = "新密码不能为空")
    private String newPassword;

    @Schema(description = "角色ids")
    private List<Long> roleIds;

    @Schema(description = "状态 0:启用, 1:禁用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "乐观锁")
    private Integer version;

    @Schema(description = "图形验证码")
    private String captcha;

    @Schema(description = "图形验证码Key")
    private String captchaKey;

    @Schema(description = "登录ip")
    private String ip;

    /**
     * dto 转 entity
     *
     * @return entity
     */
    public User toEntity() {
        User entity = new User();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    /**
     * dtoList 转 entities
     *
     * @param dtoList
     * @return entities
     */
    public static List<User> toEntities(List<UserDTO> dtoList) {
        return dtoList.stream().map(UserDTO::toEntity).collect(Collectors.toList());
    }

    /**
     * entity 转 dto
     *
     * @param entity
     * @return dto
     */
    public static UserDTO fromEntity(User entity) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /**
     * entities 转 dtoList
     *
     * @param entities
     * @return dtoList
     */
    public static List<UserDTO> fromEntities(List<User> entities) {
        return entities.stream().map(UserDTO::fromEntity).collect(Collectors.toList());
    }
}
