package com.yq.service;

import com.yq.config.MyConst;
import com.yq.dao.MediaRepository;
import com.yq.dao.UserRepository;
import com.yq.entity.Media;
import com.yq.entity.User;
import com.yq.utils.FileSizeUtil;
import com.yq.utils.HlsVideoUtil;
import com.yq.utils.Mp4VideoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;


@Service
public class MediaService {

    @Autowired
    MediaRepository mediaRepository;
    @Autowired
    UserRepository userRepository;

    /**
     * 查询用户已经处理成功的视频列表
     * @param user
     * @return
     */
    public List<Media> findAllOK(User user) {
        return mediaRepository.findByUserAndProcessStatusOrderByCreateTimeDesc(user,"处理成功");
    }

    /**
     * 根据用户id和视频id删除视频
     * @param id
     */
    @Transactional
    public void deleteById(Integer id,Integer userId) {
        mediaRepository.deleteById(id,userId);
    }

    /**
     * 文件上传前的注册，检查文件是否存在(文件存在并且数据库中也有，表明文件存在)
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     */
    public boolean register(String fileMd5, String fileName, long fileSize, String mimetype, String fileExt,User user) {
        //1.检查文件在磁盘上是否存在
        //文件所属目录
        String fileFolderPath=MyConst.F_SYSTEM+this.getFileFolderPath(fileMd5);
        //文件路径
        String filePath=MyConst.F_SYSTEM+this.getFilePath(fileMd5,fileExt);
        File file=new File(filePath);
        //文件是否存在
        boolean exists = file.exists();


        //2.检查文件在数据库中是否存在

        Media media = mediaRepository.findByFileMd5AndUser(fileMd5,user);
            if(exists && media!=null){
            //文件存在
            return false;
        }
        //文件不存在时作一些准备工作，检查文件所在目录是否存在，如果不存在则创建
        File fileFolder = new File(fileFolderPath);
        if(!fileFolder.exists()){
            fileFolder.mkdirs();
        }
        return true;
    }

    /**
     * 检查分块是否存在
     * @param fileMd5 文件md5
     * @param chunk 块的下标
     * @param chunkSize 块的大小
     */
    public boolean checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        //检查分块文件所在目录
        String chunkFileFolderPath = MyConst.F_SYSTEM+this.getChunkFileFolderPath(fileMd5);

        //块文件
        File chunckFile = new File(chunkFileFolderPath + chunk);
        if(chunckFile.exists()){
            //块文件存在
            return true;
        }else{
            //块文件不存在
            return false;
        }
    }

    /**
     * 上传分块
     */
    public void uploadchunk(MultipartFile file, String fileMd5, Integer chunk) throws IOException {
        //检查分块目录是否存在，不存在创建
        //1.1得到块目录
        String chunkFileFolderPath = MyConst.F_SYSTEM+this.getChunkFileFolderPath(fileMd5);
        //1.2 得到分块文件路径
        String chunckFilePath = chunkFileFolderPath+chunk;
        File chunkFileFolder = new File(chunkFileFolderPath);
        if(!chunkFileFolder.exists()){
            chunkFileFolder.mkdirs();
        }
        //写入块文件
        file.transferTo(new File(chunckFilePath));
    }

    /**
     * 合并分块
     */
    public boolean mergechunks(String fileMd5, String fileName, long fileSize, String mimetype, String fileExt,User user) {
        //1.合并所有分块
        //得到分块文件所属目录
        String chunkFileFolderPath =MyConst.F_SYSTEM+ this.getChunkFileFolderPath(fileMd5);
        File chunckFileFolder = new File(chunkFileFolderPath);
        //分块文件列表
        File[] files = chunckFileFolder.listFiles();
        List<File> fileList = Arrays.asList(files);

        //创建一个合并文件
        String filePath =MyConst.F_SYSTEM+ this.getFilePath(fileMd5, fileExt);
        File mergeFile = new File(filePath);

        //执行合并
        mergeFile = this.mergeFile(fileList, mergeFile);
        if(mergeFile==null){
            //合并文件失败
            return false;
        }
        //文件所在目录
        String fileFolderPath =MyConst.F_SYSTEM+ this.getFileFolderPath(fileMd5);

        //3.将文件信息写入数据库
        Media media = new Media();
        media.setFileMd5(fileMd5);
        //设置文件大小
        media.setFileSize(FileSizeUtil.getSize(fileSize));
        media.setCreateTime(new Date());
        media.setMimeType(mimetype);
        media.setFileType(fileExt);
        media.setFileOriginalName(fileName);
        media.setFileName(fileMd5 + "." +fileExt);
        media.setFilePath(filePath);

        media.setUser(user);
        mediaRepository.save(media);

        //删除分片
        delFile(chunkFileFolderPath);
        //删除临时目录
        chunckFileFolder.deleteOnExit();
        //文件访问的url根目录
        String fiel_url_folder= this.getFileFolderPath(fileMd5);

        //处理视频
         new Thread(new Runnable() {
             @Override
             public void run() {
                 //文件类型，只处理avi格式
                 if (!"avi".equals(fileExt)) {
//                     if("mp4".equals(fileExt)){
                        //本身就是mp4文件
                         media.setProcessStatus("处理中");
                         mediaRepository.save(media);
                         //mp4的文件路径
                         String mp4_videp_path =filePath;
                         //m3u8的文件名
                         String m3u8_name = fileMd5 + ".m3u8";
                         //m3u8文件所在目录
                         String m3u8floder_path = fileFolderPath + "hls/";

                         HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(MyConst.FFMPEF_PATH, mp4_videp_path, m3u8_name, m3u8floder_path);
                         //生成m3u8格式文件
                         String tsResult = hlsVideoUtil.generateM3u8();
                         if (tsResult == null || !tsResult.equals("success")) {
                             //处理失败
                             media.setProcessStatus("处理失败");
                             mediaRepository.save(media);
                         }
                         //处理成功
                         media.setProcessStatus("处理成功");
                         //设置文件访问的 url
                         media.setFileUrl(fiel_url_folder + "hls/" + m3u8_name);
                         mediaRepository.save(media);
//                     }else {
                         //不是mp4和avi格式
//                         media.setProcessStatus("不支持");
//                         mediaRepository.save(media);
//                         return;
//                     }
                 } else {
                     media.setProcessStatus("处理中");
                     mediaRepository.save(media);
                     //使用工具类将avi转换为mp4格式
                     //要处理的视频文件路径
                     String video_path = filePath;
                     //文件名称
                     String mp4_name = fileMd5 + ".mp4";
                     //生成mp4文件所在目录
                     String mp4folder_path = fileFolderPath;
                     Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(MyConst.FFMPEF_PATH, video_path, mp4_name, mp4folder_path);
                     String result = mp4VideoUtil.generateMp4();
                     if (result == null || !result.equals("success")) {
                         //处理失败
                         media.setProcessStatus("处理失败");
                         mediaRepository.save(media);
                         return;
                     }
                     //处理成功，将mp4生成m3u8和ts文件
                     //mp4的文件路径
                     String mp4_videp_path = mp4folder_path + mp4_name;
                     //m3u8的文件名
                     String m3u8_name = fileMd5 + ".m3u8";
                     //m3u8文件所在目录
                     String m3u8floder_path = fileFolderPath + "hls/";
                     HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(MyConst.FFMPEF_PATH, mp4_videp_path, m3u8_name, m3u8floder_path);
                     String tsResult = hlsVideoUtil.generateM3u8();
                     if (tsResult == null || !tsResult.equals("success")) {
                         //处理失败
                         media.setProcessStatus("处理失败");
                         mediaRepository.save(media);
                     }
                     //处理成功
                     media.setProcessStatus("处理成功");
                     //设置文件访问的 url
                     media.setFileUrl(fiel_url_folder + "hls/" + m3u8_name);
                     //重新设置文件名称
                     media.setFileName(fileMd5+".mp4");
                     mediaRepository.save(media);
                 }


             }
         }).start();
        return true;
    }


    /**
     * 删除分片
     * @param chunkFileFolderPath
     */
    private void delFile(String chunkFileFolderPath) {
        File file = new File(chunkFileFolderPath);
        if(file.isDirectory() && file.exists()){
           //文件夹必须为空才能删除
            File[] files = file.listFiles();
            for(File f:files){
                f.delete();
            }
        }
    }

    /**
     * 根据用户id查询其所有视频列表
     */
    public List<Media> findAll(User user) {

        List<Media> mediaList = mediaRepository.findByUserOrderByCreateTimeDesc(user);
        return mediaList;
    }

    //=======================================================


    //合并文件
    private File mergeFile(List<File> fileList, File mergeFile) {
        //如果合并文件已存在则删除，否则创建新文件
        try {
            if(mergeFile.exists()){
                mergeFile.delete();
            }else{
                mergeFile.createNewFile();
            }
            //对块文件进行排序
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if(Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                        return 1;
                    }
                    return -1;
                }
            });

            //创建一个写对象
            RandomAccessFile rw = new RandomAccessFile(mergeFile, "rw");
            byte[] b = new byte[1024];
            for(File chunkFile:fileList){
                //创建一个读对象
                RandomAccessFile r = new RandomAccessFile(chunkFile,"r");
                int len=-1;
                while((len=r.read(b))!=-1){
                    //写操作
                    rw.write(b,0,len);
                }
                r.close();
            }
            rw.close();
            return mergeFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //得到文件所属目录路径
    private String getFileFolderPath(String fileMd5){
        return  MyConst.VIDEO_PATH + fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/";
    }
    //得到文件的路径
    private String getFilePath(String fileMd5,String fileExt){
        return MyConst.VIDEO_PATH + fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" + fileMd5 + "." +fileExt;
    }

    //得到块文件所属目录路径
    private String getChunkFileFolderPath(String fileMd5){
        return MyConst.VIDEO_PATH + fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/chunk/";
    }

    //删除某个用户的全部视频
    @Transactional
    public void delAll(User user) {
        mediaRepository.deleteByUser(user);
    }
}
