package com.yq.dao;

import com.yq.entity.OneSubject;
import com.yq.entity.TwoSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 二级分类 Repository
 */

public interface TwoSubjectRepository extends JpaRepository<TwoSubject,Integer> {
    /**
     * 通过一级分类id,根据sort升序时间降序
     */
    @Query(value = "select * from two_subject where one_subject_id=? order by sort asc,create_time asc",nativeQuery = true)
    List<TwoSubject> findByOneId(Integer oneId);

}
