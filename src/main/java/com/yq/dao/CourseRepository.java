package com.yq.dao;

import com.yq.entity.Course;
import com.yq.entity.OneSubject;
import com.yq.entity.TwoSubject;
import com.yq.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/**
 * 课程 Repository
 */

public interface CourseRepository extends JpaRepository<Course,Integer> {

    //根据用户id和课程id删除课程
    @Query(value = "delete from course where id=? and user_id=?",nativeQuery = true)
    @Modifying
    void deleteById(Integer id,Integer userId);

    //根据用户查询其下的所有课程
    List<Course> findByUser(User user);

    //后台查看所有发布的课程
    List<Course> findByStatus(boolean status);

    /**
     * 查询最新发布的4个课程
     */
    @Query(value = "select * from course where status=1 order by create_time desc limit ?",nativeQuery = true)
    List<Course> findCourseNew(Integer size);
    /**
     * 查询最热发布的8个课程
     */
    @Query(value = "select * from course  where status=1 order by skim desc limit ?",nativeQuery = true)
    List<Course> findCourseHot(Integer size);

    /**
     * 分页查询一级分类下的  最新课程 显示8条
     */
    @Query("select c from Course c where c.status=true and c.oneSubject=?1")
    Page<Course> findCoursePageByOne(OneSubject oneSubject, Pageable pageable);

    /**
     * 分页查询二级分类下的  最新课程 显示8条
     */
    @Query("select c from Course c where c.status=true and c.twoSubject=?1")
    Page<Course> findCoursePageByTwo(TwoSubject twoSubject, Pageable pageable);

    /**
     * 根据用户id查询该用户所有发布的课程
     */
    List<Course> findByUserIdAndStatusOrderByCreateTime(Integer uId,boolean status);

    /**
     * 查询课程包含的年份
     * select date_format(c.create_time,'%Y') as year from course c group by year order by year desc;
     */
    @Query("select function('date_format',c.createTime,'%Y') as year from Course c group by function('date_format',c.createTime,'%Y') order by year desc")
    List<String> findGroupYear();
    /**
     * 根据年份查询发布的课程
     *select * from course c where date_format(c.create_time,'%Y') = '2019';
     */
    @Query("select c from Course c where function('date_format',c.createTime,'%Y') =?1 and c.status=true ")
    List<Course> findByYear(String year);

    /**
     * 查收发布的总课数
     * @return
     */
    @Query(value = "select count(1) from course where status=1",nativeQuery = true)
    Integer countCourse();

    /**
     *搜索课程
     */
    @Query("select c from Course c where c.name like ?1")
    Page<Course> findByQuery(String query, Pageable pageable);

    /**
     * 根据课程id和用户查找课程
     */
    Course findByIdAndUser(Integer id,User user);

    /**
     *根据作者id查询作品数 (全是发布的)
     */
    Integer countByUserIdAndStatus(Integer uId,boolean status);
}
