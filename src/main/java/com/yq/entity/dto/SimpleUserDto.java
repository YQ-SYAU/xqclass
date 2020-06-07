package com.yq.entity.dto;

import lombok.Data;

/**
 * 用于显示用户关注列表
 */
@Data
public class SimpleUserDto {

    private Integer id;
    private String avatar;
    private String nickname;
}
