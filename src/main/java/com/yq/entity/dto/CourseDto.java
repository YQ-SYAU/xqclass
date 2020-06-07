package com.yq.entity.dto;

import com.yq.entity.Course;

/**
 * 课程数据传输对象
 */

public class CourseDto extends Course {

    //课程点赞数
    private Integer like;
    //作者信息
    private UserDto userDto;
    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }
}
