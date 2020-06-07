package com.yq.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * 轮播图
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name="banner")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="url")
    private String url;//图片地址

    private Integer sort;//排序
    //指定数据库字段类型
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;//创建时间

}
