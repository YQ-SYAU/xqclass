package com.yq.service;

import com.yq.config.MyConst;
import com.yq.dao.RoleRepository;
import com.yq.dao.UserRepository;
import com.yq.entity.Course;
import com.yq.entity.Role;
import com.yq.entity.User;
import com.yq.entity.dto.*;
import com.yq.exception.CustomException;
import com.yq.utils.RedisUtil;
import com.yq.utils.UUIDUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * 用户Service
 */
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    CourseService courseService;

    /**
     * 查询所有用户，根据createTime降序
     *
     * @return
     */
    public List<UserDto> findAll() {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        List<User> users = userRepository.findAll(Sort.by(order));
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            Integer userId = user.getId();
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            //查找粉丝数
            userDto.setFans(this.countFan(BType.USER_FAN, userId));
            //查找作者作品数
            Integer productions = courseService.countCourseByUId(userId);
            userDto.setProductions(productions);
            userDtos.add(userDto);
        }

        return userDtos;
    }

    /**
     * 根据id查询用户  封装成返回对象
     */
    public UserDto findByUser(Integer id) {
        Optional<User> optional = userRepository.findById(id);
        if (!optional.isPresent()) {
            //用户不存在
            throw new CustomException("该用户不存在");
        }
        User user = optional.get();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        //查找粉丝数
        userDto.setFans(this.countFan(BType.USER_FAN, id));
        //查找作者作品数
        Integer productions = courseService.countCourseByUId(id);
        userDto.setProductions(productions);
        //查找收藏数
        userDto.setCollects(this.countCollect(BType.COLLECT_COURSE, id));
        //查找关注数
        userDto.setAttentions(this.countAttention(BType.ATTENTION_AUTH, id));


        return userDto;
    }

    /**
     * 查询作者收藏课程的数目
     *
     * @param collectCourseReport 业务类型
     * @return
     */
    private Integer countCollect(BType collectCourseReport, Integer userId) {
        //获取key
        String key = RedisUtil.getKey(collectCourseReport, userId);
        Integer count = redisTemplate.opsForHash().size(key).intValue();
        return count;
    }

    /**
     * 更新用户
     *
     * @param avatarFile 头像文件
     * @param codeFile   二维码文件
     * @param user       修改的信息实体
     */
    public User update(MultipartFile avatarFile, MultipartFile codeFile, User user) {
        //先查询来的信息
        Optional<User> optional = userRepository.findById(user.getId());
        if (!optional.isPresent()) {
            throw new CustomException("用户不存在");
        }
        User sourceUser = optional.get();
        //封装数据
        sourceUser.setGender(user.getGender());
        sourceUser.setToken(user.getToken());
        sourceUser.setNickname(user.getNickname());
        //保存头像
        if (!avatarFile.isEmpty()) {
            //删除原来的头像
            String avatar = sourceUser.getAvatar();
            File source_avatar_file = new File(MyConst.F_SYSTEM + avatar);
            if (source_avatar_file.isFile() && source_avatar_file.exists()) {
                source_avatar_file.delete();
            }
            //获取原始名称
            String originalFilename = avatarFile.getOriginalFilename();
            //获取后缀名
            String extend = originalFilename.substring(originalFilename.lastIndexOf("."));
            //新文件名称
            String filename = UUIDUtils.getId() + extend;
            //写文件
            try {
          String avatarPath = MyConst.F_SYSTEM+MyConst.IMG_PATH;
           File avatarDir = new File(avatarPath);
           if(!avatarDir.exists()){
               avatarDir.mkdirs();
           }
                avatarFile.transferTo(new File(avatarDir,filename));
                //保存文件地址
                sourceUser.setAvatar(MyConst.IMG_PATH+filename);
            } catch (IOException e) {
                throw new CustomException("保存用户头像失败");
            }
        }
        //保存收款码
        if (!codeFile.isEmpty()) {
            //删除原来的收款码
            String code = sourceUser.getCode();
            File source_code_file = new File(MyConst.F_SYSTEM + code);
            if (source_code_file.isFile() && source_code_file.exists()) {
                source_code_file.delete();
            }

            //获取原始名称
            String originalFilename = codeFile.getOriginalFilename();
            //获取后缀名
            String extend = originalFilename.substring(originalFilename.lastIndexOf("."));
            //新文件名称
            String filename = UUIDUtils.getId() + extend;
            //写文件
            try {
                String codePath = MyConst.F_SYSTEM+MyConst.IMG_PATH;
                File codeDir = new File(codePath);
                if(!codeDir.exists()){
                    codeDir.mkdirs();
                }
                codeFile.transferTo(new File(codeDir,filename));
                //保存文件地址
                sourceUser.setCode(MyConst.IMG_PATH+filename);
            } catch (IOException e) {
                throw new CustomException("保存二维码失败");
            }
        }
        userRepository.save(sourceUser);
        //判断是不是管理员
        for (Role role : sourceUser.getRoles()) {
            if ("admin".equals(role.getRoleName())) {
                //是管理员
                sourceUser.setFlag(1);
            }
        }
        return sourceUser;
    }

    /**
     * 根据用户id查询用户
     *
     * @param uId
     * @return
     */
    public User findById(Integer uId) {
        Optional<User> optional = userRepository.findById(uId);
        if (!optional.isPresent()) {
            //用户不存在
            throw new CustomException("该用户不存在");
        }
        return optional.get();
    }

    /**
     * 根据openID查找用户
     *
     * @param openID 用户openID
     * @return
     */
    public User findByOpenId(String openID) {
        return userRepository.findByQq(openID);
    }

    /**
     * 添加一个新用户
     * gender nickname figureurl_qq msg
     */
    @Transactional
    public User save(Map<String, Object> map, String openID) {
        User user = this.createUser(map, openID);
        //设置创建时间
        user.setCreateTime(new Date());
        userRepository.save(user);
        return user;
    }


    //封装用户信息
    private User createUser(Map<String, Object> map, String openID) {
        //将头像保存在静态资源服务器 返回路径
        String avatar = saveAvatar((String) map.get("figureurl_qq"));
        //获取昵称和性别
        String gender = (String) map.get("gender");
        String nickname = (String) map.get("nickname");
        System.out.println("nickname = " + nickname);
        User user = new User();
        user.setAvatar(avatar);
        if ("女".equals(gender)) {
            user.setGender(0);
        } else {
            user.setGender(1);
        }
        user.setNickname(nickname);
        user.setCreateTime(new Date());
        user.setQq(openID);
        //设置为用户角色
        Role role = roleRepository.findByRoleName("user");
        ArrayList<Role> userRole = new ArrayList<>();
        userRole.add(role);
        user.setRoles(userRole);
        return user;
    }

    //将头像保存在静态资源服务器
    private String saveAvatar(String url) {
        InputStream in = null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            in = entity.getContent();

            //文件地址  测试过  .jpg和.png的图片都可以
            String fileName = UUIDUtils.getId() + ".jpg";
            File dir = new File(MyConst.F_SYSTEM+MyConst.IMG_PATH);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            //输出文件
            IOUtils.copy(in,fileOutputStream);
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(in);
            return MyConst.IMG_PATH+fileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("保存用户头像出错");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





    /**
     * 关注和取关(添加用户关注信息 和作者粉丝信息)
     * @param attentionAuth 业务类型
     * @param authId 作者id
     * @param userId 用户id
     * @return
     */
    public boolean attention(BType attentionAuth, Integer authId, Integer userId) {

        //===============用户关注==================
        //获取关注key
        String key = RedisUtil.getKey(attentionAuth,userId);
        //查询作者户信息，放入用户关注集合中
        //User user = this.findById(authId);
        //封装关注用户信息

        //SimpleUserDto simpleUserDto = new SimpleUserDto();
        //simpleUserDto.setId(user.getId());
        //simpleUserDto.setAvatar(user.getAvatar());simpleUserDto.setNickname(user.getNickname());
        Boolean attention = redisTemplate.opsForSet().isMember(key, authId);
        if(attention){
            //取关
            redisTemplate.opsForSet().remove(key,authId);
        }else{
            //关注
            redisTemplate.opsForSet().add(key,authId);
        }

        //===================操作粉丝============================
        //获取key
        String fanKey = RedisUtil.getKey(BType.USER_FAN,authId);
        if(attention){
            //取关
            redisTemplate.opsForSet().remove(fanKey,userId);
        }else{
            //关注
            redisTemplate.opsForSet().add(fanKey,userId);
        }
        return attention;
    }

    /**
     * 判断用户是否已经关注
     * @param userId
     * @param authId
     * @return
     */
    public boolean isAttentioned(Integer userId, Integer authId) {
        //获取key
        String key = RedisUtil.getKey(BType.ATTENTION_AUTH,userId);
        //封装关注用户信息
       return redisTemplate.opsForSet().isMember(key, authId);
    }

    /**
     * 查询作者粉丝数
     * @param attentionAuth 业务类型
     * @param authId 作者id
     * @return 粉丝数
     */
    public Integer countFan(BType attentionAuth, Integer authId) {
        //获取key
        String key = RedisUtil.getKey(attentionAuth, authId);
        Long countFan = redisTemplate.opsForSet().size(key);
        return countFan.intValue();
    }
    /**
     * 查询作者关注数
     * @param attentionAuth 业务类型
     * @param userId 用户id
     * @return 粉丝数
     */
    public Integer countAttention(BType attentionAuth, Integer userId) {
        //获取key
        String key = RedisUtil.getKey(attentionAuth, userId);
        Long countAttention = redisTemplate.opsForSet().size(key);
        return countAttention.intValue();
    }

    /**
     * 课程收藏和取消收藏
     * @param typeCollect
     * @param userId
     * @param courseId
     * @return
     */
    public boolean collect(BType typeCollect, Integer userId, Integer courseId) {
        //获取key
        String key = RedisUtil.getKey(typeCollect, userId);
        //查询并封装课程信息
        Course course = courseService.findById(courseId);
        CollectCourse collectCourse =  courseService.toCollect(course);
        //判断数据是否已经存在
        //Boolean collect = redisTemplate.opsForSet().isMember(key,collectCourse);
        Boolean collect = redisTemplate.opsForHash().hasKey(key,courseId);
        if(collect){
            //存在，取消收藏
            //redisTemplate.opsForSet().remove(key,collectCourse);
            redisTemplate.opsForHash().delete(key,courseId);
        }else{
            //不存在，收藏
            //redisTemplate.opsForSet().add(key,collectCourse);
            redisTemplate.opsForHash().put(key,courseId,collectCourse);
        }
        return collect;
    }

    /**
     * 判断用户是否已经收藏课程
     * @param userId
     * @param courseId
     */
    public Boolean isCollectd(Integer userId, Integer courseId) {
        String key = RedisUtil.getKey(BType.COLLECT_COURSE, userId);
        //获取课程
        //查询并封装课程信息
        Course course = courseService.findById(courseId);
        CollectCourse collectCourse = new CollectCourse();
        BeanUtils.copyProperties(course,collectCourse);
//        return redisTemplate.opsForSet().isMember(key, collectCourse);
        return redisTemplate.opsForHash().hasKey(key,courseId);

    }

    /**
     * 查找用户收藏的课程
     * @param id 用户id
     * @return
     */
    public Map<Integer,CollectCourse> findUserCollectCourses(Integer id) {
        //获取key
        String key = RedisUtil.getKey(BType.COLLECT_COURSE, id);
        HashMap<Integer, CollectCourse> map= (HashMap<Integer, CollectCourse>) redisTemplate.opsForHash().entries(key);
        //判断课程是否失效  数据库中的课程和redis在的数据不一致，则失效
        map.forEach((courseId,redisCourse)->{
            //数据库中的数据
            Course c1 = courseService.findByIdToRedis(courseId);
            if(c1==null){
                //课程已被删除
                redisCourse.setCover(MyConst.IMG_PATH + "1.jpg");
            }else {
                //判断课程数据是否被修该
                CollectCourse mysqlCourse=courseService.toCollect(c1);
                //比较两对象的属性值是否相等（重写hashcode和equals方法）
                if (!mysqlCourse.equals(redisCourse)) {
                    //数据失效
                    redisCourse.setCover(MyConst.IMG_PATH + "1.jpg");
                }
            }
        });

        return map;
    }

    /**
     * 查询用户关注的列表
     * @param userId
     * @return
     */
    public List<SimpleUserDto> findUserAttention(Integer userId)  {
        String key = RedisUtil.getKey(BType.ATTENTION_AUTH, userId);
        //获取关注列表
        Set<Integer> uIds = redisTemplate.opsForSet().members(key);
        //f根据id查询用户封装数据
        ArrayList<SimpleUserDto> users = new ArrayList<>();
        for(Integer uid:uIds) {
            User user = this.findById(uid);
            SimpleUserDto simpleUserDto = new SimpleUserDto();
            simpleUserDto.setId(user.getId());
            simpleUserDto.setAvatar(user.getAvatar());
            simpleUserDto.setNickname(user.getNickname());
            users.add(simpleUserDto);
        }
        return users;
    }

    /**
     * 为了安全，需要判断当前操作的用户是否有该课程
     * @param user
     * @param courseId
     * @return
     */
    public boolean hasCourse(User user, Integer courseId) {
        Course course = courseService.findByIdAndUser(courseId, user);
        if(course==null){
            return false;
        }
        return true;
    }

    /**
     * 取消课程收藏
     * @param collectCourse
     * @param userId
     * @param courseId
     */
    public void deleteCollect(BType collectCourse, Integer userId, Integer courseId) {
        //获取key
        String key = RedisUtil.getKey(collectCourse, userId);
        //删除
        redisTemplate.opsForHash().delete(key,courseId);
    }
}
