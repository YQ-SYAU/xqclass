package com.yq.dao;

import com.yq.entity.Role;
import com.yq.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 角色 Repository
 */

public interface RoleRepository extends JpaRepository<Role,Integer> {

    Role findByRoleName(String roleName);
}
