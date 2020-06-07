package com.yq.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 章节
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name="chapter")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;//章节名称

    private Integer sort;//章节排序
    //指定数据库字段类型
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;//创建时间

    /**
     *  配置章节和小节的关系（一对多）
     *  mappedBy：指定从表实体类中引用主表对象的名称。
     *  放弃关系的维护权，声明关系.   cascade级联操作
     */
    @OneToMany(mappedBy = "chapter",cascade = CascadeType.REMOVE )
    private List<Video> videos=new ArrayList<>();//小节列表
    /**
     * @ManyToOne:配置章节和课程的关系（多对一）
     *   @JoinColumn作用：用于定义主键字段和外键字段的对应关系。
     *    属性：
     *        	name：指定外键字段的名称
     *         	referencedColumnName：指定引用主表的主键字段名称
     *         	unique：是否唯一。默认值不唯一
     */
    @ManyToOne
    @JoinColumn(name="courseId",referencedColumnName = "id")
    @JsonIgnore
    private Course course;
}
