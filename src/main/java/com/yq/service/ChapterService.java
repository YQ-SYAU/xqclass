package com.yq.service;


import com.yq.dao.ChapterRepository;
import com.yq.entity.*;
import com.yq.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * 章节Service
 */

@Service
public class ChapterService {

    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    VideoService videoService;
    /**
     * 根据课程id查询所属章节
     * @param courseId
     * @return
     */
    public List<Chapter> findByCourseId(Integer courseId) {
        if(courseId==null){
            return null;
        }

        List<Chapter> chapters = chapterRepository.findByCourseOrderBySortAscCreateTimeAsc(new Course().setId(courseId));
        //1.遍历章节
        for(Chapter chapter:chapters){
            //根据章节id查询小节
            List<Video> videos=videoService.findVideoByChapter(chapter);
            chapter.setVideos(videos);
        }
        return chapters;
    }

    /**
     * 保存章节 或修改章节
     * @param chapter
     */
    public void save(Chapter chapter) {
        if(chapter!=null){
            //判断是更新还是添加
            Integer chapterId = chapter.getId();
            if(chapterId==null){
                //添加  设置时间
               chapter.setCreateTime(new Date());
            }else{
                //查询原章节
                Optional<Chapter> optional = chapterRepository.findById(chapterId);
                if(!optional.isPresent()){
                    throw new CustomException("更新的章节已不存在");
                }
                //设置时间
                Chapter sourceChapter = optional.get();
                chapter.setCreateTime(sourceChapter.getCreateTime());
            }
            //保存或更新
            chapterRepository.save(chapter);
        }
    }

    /**
     * 根据id删除章节
     * @param id
     */
    @Transactional
    public void delById(Integer id) {
        chapterRepository.deleteById(id);
    }

    /**
     * 根据课程id删除其下所有的章节
     */
    public void delByCourseId(Course course) {
        chapterRepository.deleteByCourse(course);
    }
}
