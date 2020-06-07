package com.yq.dao;

import com.yq.entity.Banner;
import com.yq.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户 Repository
 */

public interface UserRepository extends JpaRepository<User,Integer> {

    //根据openID查找用户
    User findByQq(String openID);
}
