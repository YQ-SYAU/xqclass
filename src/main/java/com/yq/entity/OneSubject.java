package com.yq.entity;

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
@Table(name="oneSubject")
public class OneSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;//分类名称

    private Integer sort;//排序
    //指定数据库字段类型
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;//创建时间
    /**
     * 配置一级分类和二级分类的关系（一对多）
     *  mappedBy：指定从表实体类中引用主表对象的名称。
     */
    @OneToMany(mappedBy ="oneSubject",cascade = CascadeType.REMOVE)
    private List<TwoSubject> twoSubjects=new ArrayList<>();

    /**
     * 配置一级分类和课程的关系（一对多）
     */
    @OneToMany(mappedBy = "oneSubject",cascade = CascadeType.REMOVE)
    private List<Course> courses=new ArrayList<>();
}
