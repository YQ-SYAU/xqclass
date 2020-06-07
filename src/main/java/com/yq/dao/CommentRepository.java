package com.yq.dao;

import com.yq.entity.Comment;
import com.yq.entity.Course;
import com.yq.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 评论 Repository
 */

public interface CommentRepository extends JpaRepository<Comment,Integer> {

    /**
     * 根据课程查询该课程下的所有  顶级评论（没有父评论）
     */
//    @Query("select c from Comment c where c.course=?1 group by c.createTime")
    List<Comment> findByCourseIdAndParentCommentNull(Integer cid,Sort sort);
}
