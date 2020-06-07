package com.yq.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yq.entity.Course;
import com.yq.entity.OneSubject;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程分类
 */
@Data
@Accessors(chain = true)

public class TwoSubjectDto {

    private Integer id;

    private String name;//分类名称

    private Integer sort;//排序

    private String createTime;//创建时间

}
