package com.yq.dao;

import com.yq.entity.Chapter;
import com.yq.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 小节（视频） Repository
 */

public interface VideoRepository extends JpaRepository<Video,Integer> {
    /**
     * 通过章节id,根据sort升序时间降序
     */
    @Query(value = "select * from video where chapter_id=? order by sort asc,create_time asc",nativeQuery = true)
    List<Video> findByChapterId(Integer chapterId);

}
