package com.yq.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户
 *  @ Data = @Setter+@Getter+@EqualsAndHashCode+@NoArgsConstructor
 */

@Data
@Accessors(chain = true)
@Entity
@Table(name="user")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//用户id

    private String nickname;//用户昵称

    @Transient  //不进行序列化，即不与数据库字段映射
    private Integer flag;//是不是管理员，用来显示标签  1代表是管理员

    private String qq;//用户的oppenID,即用户的唯一标识

    private String weChat;//用户微信号

    private String avatar;//用户头像

    private String code;//打赏用的二维码

    private String token;//用户签名

    private Integer gender;//用户性别 0女  1男
    //指定数据库字段类型
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;//用户注册时间

    /**
     * 配置用户和课程的关系（一对多）
     */
    @OneToMany(mappedBy = "user")
    private List<Course> courses=new ArrayList<>();

    /**
     * 配置用户和角色的关系（多对多）
     * @JoinTable
     *     作用：针对中间表的配置
     *     属性：
     *     	name：配置中间表的名称
     *     	joinColumns：中间表的外键字段关联当前实体类所对应表的主键字段
     *     	inverseJoinColumn：中间表的外键字段关联对方表的主键字段
     */
    @ManyToMany
    @JoinTable(name="user_role",//中间表名称
        joinColumns = {@JoinColumn(name="user_id",referencedColumnName = "id")},
            inverseJoinColumns={@JoinColumn(name="role_id",referencedColumnName = "id")})
    private List<Role> roles = new ArrayList<>();

    /**
     * 配置用户和课程的关系（一对多）
     */
    @OneToMany(mappedBy = "user")
    private List<Media> medias=new ArrayList<>();
}
