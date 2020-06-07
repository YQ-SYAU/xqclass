package com.yq.dao;

import com.yq.entity.Banner;
import com.yq.entity.Chapter;
import com.yq.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 章节 Repository
 */

public interface ChapterRepository extends JpaRepository<Chapter,Integer> {

    //根据课程id查询所属章节
    List<Chapter> findByCourseOrderBySortAscCreateTimeAsc(Course course);
    //根据课程删除其下的所有章节
    void deleteByCourse(Course course);
}
