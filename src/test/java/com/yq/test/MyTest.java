package com.yq.test;

import com.yq.dao.CourseRepository;
import com.yq.entity.Course;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MyTest {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    CourseRepository courseRepository;

    @Test
    public void testRedis() {
//        Banner banner=new Banner();
//        banner.setId(1).setSort(1).setUrl("11111");
        Object test2 = redisTemplate.opsForValue().get("test2");
        System.out.println("test2 = " + test2);
    }

    @Test
    public void findTopBy() {
        List<Course> topByCreateTime = courseRepository.findCourseNew(2);
        for (Course course : topByCreateTime) {
            System.out.println("course = " + course.getName() + "===" + course.getCreateTime());
        }
    }

    /**
     * 测试下载网上图片
     */
    @Test
    public void downloadImg() {
        String url = "http://pic1.win4000.com/wallpaper/4/58491eef36a51.jpg";
        InputStream in = null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            in = entity.getContent();

            String path = "G:/test.jpg";
            File file = new File(path);

            FileOutputStream fout = new FileOutputStream(file);
            int l = -1;
            byte[] tmp = new byte[1024];
            while ((l = in.read(tmp)) != -1) {
                fout.write(tmp, 0, l);
            }
            fout.flush();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }

    }

    @Test
    public void downloadImg2() {
       // String url = "http://pic1.win4000.com/wallpaper/4/58491eef36a51.jpg";
        String url = "http://thirdqq.qlogo.cn/g?b=oidb&k=tc9A28xPjvlcH5bXsTCs5w&s=640&t=1589504809";
        InputStream in = null;

        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            in = entity.getContent();
            String path = "G:/test.png";
            File file = new File(path);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            IOUtils.copy(in,fileOutputStream);
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
