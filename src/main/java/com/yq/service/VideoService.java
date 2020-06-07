package com.yq.service;

import com.yq.dao.MediaRepository;
import com.yq.dao.VideoRepository;
import com.yq.entity.Chapter;
import com.yq.entity.Media;
import com.yq.entity.Video;
import com.yq.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 小节（视频） service
 */
@Service
public class VideoService {

    @Autowired
    VideoRepository videoRepository;
    @Autowired
    MediaRepository mediaRepository;
    /**
     * 保存视频
     */
    public void saveVideo(Video video,Integer mediaId) {


        //查找视频获取地址
        Optional<Media> optional = mediaRepository.findById(mediaId);
        if(!optional.isPresent()){
            //视频不存在
            throw new CustomException("选择的视频不存在");
        }
        Media media = optional.get();
        if (video != null) {
            //设置时间
           video.setCreateTime(new Date());
            //设置文件地址url
            video.setUrl(media.getFileUrl());
            //设置视频名称
            video.setMediaName(media.getFileOriginalName());
            //保存
            videoRepository.save(video);
        }


    }

    /**
     * 根据id删除视频
     * @param id
     */
    public void delById(Integer id) {
        if(id!=null) {
            videoRepository.deleteById(id);
        }
    }

    /**
     * 更新小节
     * @param video 小节对象
     * @param mediaId 视频id
     */
    public void updateVideo(Video video, Integer mediaId) {
        //先查询原来小节信息
        Optional<Video> optionalVideo = videoRepository.findById(video.getId());
        if(!optionalVideo.isPresent()){
            //更新的小节信息已不存在
            throw new CustomException("更新的小节信息已不存在");
        }
        Video sourceVideo = optionalVideo.get();

        if(mediaId!=null){
            //更新视频
            Optional<Media> optional = mediaRepository.findById(mediaId);
            if(!optional.isPresent()){
                //视频不存在
                throw new CustomException("选择的视频不存在");
            }
           Media media = optional.get();
            //设置文件地址url
            video.setUrl(media.getFileUrl());
            //设置视频名称
            video.setMediaName(media.getFileOriginalName());
        }else{
            //没更新视频 视频信息还是设置为原来的
            video.setMediaName(sourceVideo.getMediaName());
            video.setUrl(sourceVideo.getUrl());
        }
        //更新
        //设置原来创建的时间
        video.setCreateTime(sourceVideo.getCreateTime());
        videoRepository.save(video);

    }

    /**
     * 根据章节id查询小节，按sort和时间升序
     * @param chapter
     * @return
     */
    public List<Video> findVideoByChapter(Chapter chapter) {
        return videoRepository.findByChapterId(chapter.getId());
    }
}