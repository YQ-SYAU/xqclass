package com.yq.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 课程
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name="course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;//课程名称

    private String cover;//课程封面路径

    private String coverName;//课程封面名称

    private String data;//课程资料路径

    private String dataSize;//课程资料大小

    private String dataName;//课程资料名称

    private boolean status;//课程状态 true（1 发布），false（0 未发布）

    private String introduction;//课程简介
    //指定数据库字段类型
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;//课程发布时间

    private Integer skim;//课程浏览次数

    private String totalTime;//总课时

    /**
     * 配置课程和章节的关系（一对多）
     *  	放弃关系维护权有对方维护 mappedBy：指定从表实体类中引用主表对象的名称。
     */
    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL)
    private List<Chapter> chapters=new ArrayList<>();

    /**
     * 配置课程和评论的关系（一对多）
     */
    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL)
    private List<Comment> comments=new ArrayList<>();

    /**
     * 配置课程和用户的关系（多对一）
     */
    @ManyToOne
    @JsonIgnore
    private User user;

    /**
     * 配置课程和一级分类的关系（多对一）
     */
    @ManyToOne
    @JsonIgnore //将不需要返回的属性上添加忽略    序列化是忽略该属性
    private OneSubject oneSubject;

    /**
     * 配置课程和二级分类的关系（多对一）
     */
    @ManyToOne
    @JsonIgnore //将不需要返回的属性上添加忽略
    private TwoSubject twoSubject;
}
