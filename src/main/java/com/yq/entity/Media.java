package com.yq.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * 视频
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name="media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //文件名称
    private String fileName;
    //文件原始名称
    private String fileOriginalName;
    //文件的md5值
    private String fileMd5;
    //文件路径
    private String filePath;
    //文件url
    private String fileUrl;
    //文件类型
    private String fileType;
    //mimetype
    private String mimeType;
    //文件大小
    private String fileSize;
    //上传时间
    //指定数据库字段类型
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    //处理状态  1.处理中 2.处理成功 3.处理失败 4.无需处理
    private String processStatus;
    @ManyToOne
    private User user;
}