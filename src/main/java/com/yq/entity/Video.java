package com.yq.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * 小节
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name="video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;//小节名称
    /**
     * 指定默认值
     */
    @Column(columnDefinition = "int default 0")
    private Integer sort;//排序
    //指定数据库字段类型
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;//创建时间
    private String url;//视频地址
    private String mediaName;//视频原始名称
    /**
     * 配置小节和章节的关系（多对一）
     *@JoinColumn
     *      作用：用于定义主键字段和外键字段的对应关系。
     *      属性：
     *     	name：指定外键字段的名称
     *     	referencedColumnName：指定引用主表的主键字段名称
     *     	unique：是否唯一。默认值不唯一
     */
    @ManyToOne(targetEntity = Chapter.class)
    @JoinColumn(name="chapterId",referencedColumnName = "id")
    @JsonIgnore
    private Chapter chapter;
}
