package com.yq.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name="role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//角色ID

    private String roleName;//角色名称
    /**
     * 配置角色和用的关系（多对多） 推荐被动的一方放弃维护权
     */
    @ManyToMany(mappedBy = "roles")
    private List<User> users=new ArrayList<>();
}
