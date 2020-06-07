package com.yq.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(JUnit4.class)
public class FfmpegTest {

    /**
     * 测试ping命令 捕获日志
     */
    @Test
    public void testProcessBuilder() throws IOException {
        //java用来操作第三方应用程序的工具类
        ProcessBuilder processBuilder = new ProcessBuilder();
        //设置第三方程序的命令
      // processBuilder.command("ping","127.0.0.1");
        processBuilder.command("ipconfig");
        //将标准输入流和错误输入流合并
        processBuilder.redirectErrorStream(true);
        //启动进程
        Process process = processBuilder.start();
        //通过标准输入流获取正常和错误的信息
        InputStream inputStream = process.getInputStream();
        //转成字符流
        InputStreamReader reader = new InputStreamReader(inputStream,"gbk");

        //获取流中的数据
        int len=-1;
        //缓存区大小
        char[] c= new char[1024];
        while((len=reader.read(c))!=-1){
            String s=new String(c,0,len);
            System.out.println(s);
        }
        //关流
        inputStream.close();
        reader.close();
    }

    /**
     * 测试ffmpeg
     */
    @Test
    public void testFfmpeg() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> command=new ArrayList<>();
        command.add("C:\\aaa\\java\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe");
        command.add("-i");
        command.add("G:\\黑马\\5\\阶段5 3.微服务项目【学成在线】·\\day14 媒资管理\\视频\\1.avi");
        command.add("-y");//覆盖输出的文件
        command.add("-c:v");
        command.add("libx264");
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        command.add("G:\\黑马\\5\\阶段5 3.微服务项目【学成在线】·\\day14 媒资管理\\视频\\1.mp4");
        processBuilder.command(command);
        //将标准输入流和错误输入流合并
        processBuilder.redirectErrorStream(true);
        //启动进程
        Process process = processBuilder.start();
        //通过标准输入流获取正常和错误的信息
        InputStream inputStream = process.getInputStream();
        //转成字符流
        InputStreamReader reader = new InputStreamReader(inputStream,"gbk");

        //获取流中的数据
        int len=-1;
        //缓存区大小
        char[] c= new char[1024];
        while((len=reader.read(c))!=-1){
            String s=new String(c,0,len);
            System.out.println(s);
        }
        //关流
        inputStream.close();
        reader.close();

    }


}
