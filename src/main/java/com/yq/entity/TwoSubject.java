package com.yq.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 课程分类
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name="twoSubject")
public class TwoSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;//分类名称

    private Integer sort;//排序
    //指定数据库字段类型
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;//创建时间
    /**
     * 配置二级分类和一级分类的关系（多对一）
     */
    @ManyToOne
    @JsonIgnore //将不需要返回的属性上添加忽略
    private OneSubject oneSubject;

    /**
     * 配置二级分类和课程的关系（一对多）
     */
    @OneToMany(mappedBy = "twoSubject",cascade = CascadeType.REMOVE)
    private List<Course> courses=new ArrayList<>();
}
