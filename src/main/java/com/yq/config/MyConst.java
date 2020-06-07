package com.yq.config;

/**
 * 配置一些常量
 */
public interface MyConst {

    //===========================课程数目========================
    /**
     * 首页查询最新课程数目
     */
    Integer COURSE_NEW_SIZE=4;
    /**
     * 首页查询最热课程数目
     */
    Integer COURSE_HOT_SIZE=8;

    //=============ffmpeg路径===============
    //windows环境目录
  //  String FFMPEF_PATH="C:\\aaa\\java\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe";
    //linux环境目录
   String FFMPEF_PATH="/usr/bin/ffmpeg";

    //================文件访问路径父目录=========

    /**
     * 磁盘路径
     */
   //windows 目录
  //String F_SYSTEM="G:";
    //linux目录
  String F_SYSTEM="/yq";
    /**
     * 图片父路径
     */
    String IMG_PATH="/static/img/";
    /**
     * 资料父路径
     */
    String DATA_PATH="/static/data/";
    /**
     * 视频父路径
     */
    String VIDEO_PATH="/static/video/";
}
