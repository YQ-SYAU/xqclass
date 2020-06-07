package com.yq.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 评论
 */

@Data
@Accessors(chain = true)
@Entity
@Table(name="comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer uId;//用户id

    private String avatar;//评论人的头像

    private String nickname;//评论人的昵称

    private String content;//评论内容
    /**
     * 指定时间类型格式  以下 数据类型为 Date时可用
     */
    //指定数据库字段类型
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;//评论时间

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> replyComments=new ArrayList<>();//某个评论的回复列表

    @ManyToOne
    private Comment parentComment;//回复评论的父评论

    private boolean authorComment;//判断是不是作者评论的 true是  false不是

    @ManyToOne
    private Course course;
}
