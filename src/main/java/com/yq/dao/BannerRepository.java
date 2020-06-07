package com.yq.dao;

import com.yq.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 轮播图 Repository
 */

public interface BannerRepository extends JpaRepository<Banner,Integer> {
}
