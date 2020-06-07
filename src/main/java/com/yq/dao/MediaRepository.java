package com.yq.dao;


import com.yq.entity.Media;
import com.yq.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * 视频dao
 */

public interface MediaRepository extends JpaRepository<Media,Integer> {

    /**
     * 根据用户id和视频id删除视频
     * @param id
     * @param userId
     */
    @Query(value = "delete from media where id=? and user_id=?",nativeQuery = true)
    @Modifying
    void deleteById(Integer id,Integer userId);

    /**
     * 根据用户和视频的md5来查找视频
     */
    Media findByFileMd5AndUser(String md5,User user);
    /**
     *
     * 查询每个用户所属视频列表根据创建时间降序
     * @param user
     */
    List<Media> findByUserOrderByCreateTimeDesc(User user);
    /**
     * 查询用户已经处理成功的视频列表
     */
    List<Media> findByUserAndProcessStatusOrderByCreateTimeDesc(User user,String ProcessStatus);

    /**
     * 删除用户的全部视频
     */
    void deleteByUser(User user);
}
