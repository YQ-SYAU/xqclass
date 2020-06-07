package com.yq.dao;

import com.yq.entity.OneSubject;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 一级分类 Repository
 */

public interface OneSubjectRepository extends JpaRepository<OneSubject,Integer> {

}
