package com.yq.service;


import com.yq.config.MyConst;
import com.yq.dao.CourseRepository;
import com.yq.dao.OneSubjectRepository;
import com.yq.dao.TwoSubjectRepository;
import com.yq.entity.Course;
import com.yq.entity.OneSubject;
import com.yq.entity.TwoSubject;
import com.yq.entity.User;
import com.yq.entity.dto.*;
import com.yq.exception.CustomException;
import com.yq.utils.MyBeanUtils;
import com.yq.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Null;
import java.io.File;
import java.util.*;


/**
 * 课程Service
 */
@Service
public class CourseService {

    @Autowired
    CourseRepository courseRepository;
    @Autowired
    ChapterService chapterService;
    @Autowired
    UserService userService;
    @Autowired
    OneSubjectRepository oneSubjectRepository;
    @Autowired
    TwoSubjectRepository twoSubjectRepository;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    /**
     * 保存或更新课程信息
     * @param course
     * @return
     */
    @CacheEvict(value = "course",allEntries = true)
    public Integer saveCourse(Course course) {

        //判断是新增还是更新
        if(course.getId()==null){
            //新增
            //设置初始浏览量
            course.setSkim(0);
            Course save=courseRepository.save(course);
            return save.getId();
        }
        //更新
        //1.查询原有数据
        Optional<Course> optional = courseRepository.findById(course.getId());
        if(!optional.isPresent()){
            //数据不存在
            throw new CustomException("课程不存在");
        }
        Course edit = optional.get();
        //课程状态为boolean类型，默认为false，需手动先保存起来
        boolean status = edit.isStatus();
        BeanUtils.copyProperties(course,edit,MyBeanUtils.getNullPropertyNames(course));
        edit.setStatus(status);
        courseRepository.save(edit);
        return course.getId();
    }



    /**
     * 查询用户的课程(发布和未发布)
     */
    public List<CourseDto> findAllCourses(User user) {
        List<Course> courseList = courseRepository.findByUser(user);
        ArrayList<CourseDto> CourseDtos = new ArrayList<>();
        //封装课程传输对象
        for(Course course:courseList){
            CourseDto CourseDto = new CourseDto();
            BeanUtils.copyProperties(course,CourseDto);
            //查询设置课程点赞量
            CourseDto.setLike(  this.countLike(BType.LIKED_COURSE,course.getId()+""));
            CourseDtos.add(CourseDto);
        }
        return CourseDtos;

    }

    /**
     * 根据课程id和用户id删除用户的一个视频
     */
    @Transactional
    @CacheEvict(value = "course",allEntries = true)
    public void delCourseById(Integer courseId,Integer userId) {

        Course course = this.findById(courseId);
        //删除redis存放的精品课程
        //1.1删除以该课程id为key 的set中的数据
        String key = RedisUtil.getKey(BType.LIKED_COURSE, courseId);
        redisTemplate.delete(key);
        //删除zst中的数据对应的课程
        String zKey = RedisUtil.getReportKey(BType.LIKED_COURSE);
        redisTemplate.opsForZSet().remove(zKey,courseId);

        //删除课程封面
        String cover = course.getCover();
        if(cover!=null && cover.length()>0){
            File coverFile = new File(MyConst.F_SYSTEM + cover);
            if(coverFile.exists()&&coverFile.isFile()){
                coverFile.delete();
            }
        }
        //删除资料
        String data = course.getData();
        if(data!=null && data.length()>0){
            File dataFile = new File(MyConst.F_SYSTEM + data);
            if(dataFile.exists()&&dataFile.isFile()){
                dataFile.delete();
            }
        }
        //删除章节
        chapterService.delByCourseId(course);
        //删除数据库
        courseRepository.deleteById(courseId, userId);

    }

    /**
     * 发布课程
     * @param courseId
     */
    @CacheEvict(value = "course",allEntries = true)
    public void publish(Integer courseId) {
        Optional<Course> optional = courseRepository.findById(courseId);
        if(!optional.isPresent()){
            //课程不存在
            throw new CustomException("课程不存在");
        }
        Course course = optional.get();
        //设置发布时间
        course.setCreateTime(new Date());
        //设置发布状态
        course.setStatus(true);
        courseRepository.save(course);
    }

    /**
     *查找所有发布的课程
     */
    public List<CourseDto> findAllPublish() {
        List<Course> courseList = courseRepository.findByStatus(true);
        ArrayList<CourseDto> CourseDtos = new ArrayList<>();
        //封装课程传输对象
        for(Course course:courseList){
            CourseDto CourseDto = new CourseDto();
            BeanUtils.copyProperties(course,CourseDto);
            //查询设置课程点赞量
            CourseDto.setLike(  this.countLike(BType.LIKED_COURSE,course.getId()+""));
            CourseDtos.add(CourseDto);
        }
        return  CourseDtos;
    }

    /**
     * 根据id删除课程
     * @param courseId 课程id
     */
    @Transactional
    public void deleteById(Integer courseId) {
        //删除redis存放的精品课程
        //1.1删除以该课程id为key 的set中的数据
        String key = RedisUtil.getKey(BType.LIKED_COURSE, courseId);
        redisTemplate.delete(key);
        //删除zst中的数据对应的课程
        String zKey = RedisUtil.getReportKey(BType.LIKED_COURSE);
        redisTemplate.opsForZSet().remove(zKey,courseId);
        //删除数据库中的数据
        courseRepository.deleteById(courseId);

    }

    /**
     * 查询最新的课程4个
     * @return
     */
    @Cacheable(value = "course",key = "'new'")
    public List<SimpleCourse> findCourseNew() {
        List<Course> courseNew = courseRepository.findCourseNew(MyConst.COURSE_NEW_SIZE);
        return toSimpleCourse(courseNew);
    }

    /**
     * 查询最热课程8个
     * @return
     */
    @Cacheable(value = "course",key = "'hot'")
    public List<SimpleCourse> findCourseHot() {
        List<Course> courseHot = courseRepository.findCourseHot(MyConst.COURSE_HOT_SIZE);
        return toSimpleCourse(courseHot);
    }

    /**
     * 查询精品课程
     * @return
     */
    @Cacheable(value = "course",key = "'best'")
    public List<SimpleCourse2> findCourseBest() {
        Set<Integer> set = redisTemplate.opsForZSet().reverseRangeWithScores(BType.LIKED_COURSE_REPORT+"", 0, 7);
        List<SimpleCourse2> list = new ArrayList<>();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            //程序调试，发现iterator获取下一个元素的类型为 DefaultTypedTuple
            DefaultTypedTuple next= (DefaultTypedTuple) iterator.next();
            //获取课程id
            String cId = (String) next.getValue();
            Course course = this.findById(Integer.parseInt(cId));
            SimpleCourse2 simpleCourse2 = toOneSimpleCourse2(course);
            //获取分数
            Integer score = next.getScore().intValue();
            simpleCourse2.setLike(score);
            list.add(simpleCourse2);
        }
        return list;
    }
    /**
     * 封装为简单course
     * @param courseHot
     * @return
     */
    public List<SimpleCourse> toSimpleCourse(List<Course> courseHot) {
        List<SimpleCourse> simpleCourses = new ArrayList<>();
        for(Course c:courseHot){
            SimpleCourse simpleCourse = this.toOneSimpleCourse(c);
            simpleCourses.add(simpleCourse);
        }
        return simpleCourses;
    }
    /**
     * 封装单个课程
     */
    public SimpleCourse toOneSimpleCourse(Course c) {
        SimpleCourse simpleCourse = new SimpleCourse();
        simpleCourse.setCourseId(c.getId());
        simpleCourse.setCover(c.getCover());
        simpleCourse.setCreateTime(c.getCreateTime());
        if(c.getOneSubject()!=null){
            simpleCourse.setOneSubject(c.getOneSubject().getName());
        }
        if(c.getTwoSubject()!=null){
            simpleCourse.setTwoSubject(c.getTwoSubject().getName());
        }
        simpleCourse.setSkim(c.getSkim());
        simpleCourse.setName(c.getName());

        simpleCourse.setUserId(c.getUser().getId());
        simpleCourse.setNickName(c.getUser().getNickname());
       return simpleCourse;
    }
    /**
     * 封装单个课程
     */
    public SimpleCourse2 toOneSimpleCourse2(Course c) {
        SimpleCourse2 simpleCourse2 = new SimpleCourse2();
        simpleCourse2.setCourseId(c.getId());
        simpleCourse2.setCover(c.getCover());
        simpleCourse2.setCreateTime(c.getCreateTime());
        simpleCourse2.setTwoSubject(c.getTwoSubject().getName());
        simpleCourse2.setOneSubject(c.getOneSubject().getName());
        //和上面相比少置一个属性（浏览量）
        simpleCourse2.setName(c.getName());
        simpleCourse2.setNickName(c.getUser().getNickname());
        simpleCourse2.setUserId(c.getUser().getId());

        return simpleCourse2;
    }
    /**
     * 封装收藏课程
     */
    public CollectCourse toCollect(Course c) {
        CollectCourse collect = new CollectCourse();
        collect.setCourseId(c.getId());
        collect.setCover(c.getCover());
        collect.setCreateTime(c.getCreateTime());
        if(c.getTwoSubject()!=null){
            collect.setTwoSubject(c.getTwoSubject().getName());
        }
       if(c.getOneSubject()!=null){
           collect.setOneSubject(c.getOneSubject().getName());
       }
        collect.setName(c.getName());
        return collect;
    }

    /**
     * 分页查询查询一级分类下  发布的课程  分页大小为8
     * @return
     */
    public Page<Course> findCoursePageByOne(Integer oneId,Pageable pageable) {
        Optional<OneSubject> optional = oneSubjectRepository.findById(oneId);
        if(!optional.isPresent()){
            throw new CustomException("该一级分类不存在");
        }
        return courseRepository.findCoursePageByOne(optional.get(),pageable);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public Course findById(Integer id) {
        Optional<Course> optional = courseRepository.findById(id);
        if(!optional.isPresent()){
            throw new CustomException("课程已经不存在");
        }
        Course course = optional.get();
        return course;
    }
    /**
     * 查询课程详情，封装额外的数据
     * @param id
     * @return
     */
    public CourseDto findCourseInfo(Integer id){
        CourseDto courseDto=new CourseDto();
        //查询课程
        Course course = this.findById(id);
        //课程浏览量加1
        course.setSkim(course.getSkim()+1);
        courseRepository.save(course);
        BeanUtils.copyProperties(course,courseDto);
        //查询课程点赞数 没有默认 0
        Integer countLike = this.countLike(BType.LIKED_COURSE, String.valueOf(id));
        courseDto.setLike(countLike);
        //查询作者信息
        UserDto userDro = userService.findByUser(course.getUser().getId());
        courseDto.setUserDto(userDro);
        return courseDto;
    }

    /**
     * 分页查询二级分类下的课程
     * @param pageable
     * @return
     */
    public Page<Course> findCoursePageByTwo(Integer twoId, Pageable pageable) {
        Optional<TwoSubject> optional = twoSubjectRepository.findById(twoId);
        if(!optional.isPresent()){
            throw new CustomException("该二级分类不存在");
        }
        return courseRepository.findCoursePageByTwo(optional.get(),pageable);

    }

    /**
     * 根据用户id查询该用户所有发布的课程
     * @param uId
     */
    public List<Course> findPublishByUId(Integer uId) {
        return courseRepository.findByUserIdAndStatusOrderByCreateTime(uId, true);
    }

    /**
     * 按年份查询课程
     * @return
     */
    @Cacheable(value = "course",key = "'archive'")
    public Map<String, List<SimpleCourse>> archive() {
        //查询所有的年
        List<String> years=courseRepository.findGroupYear();
        Map<String,List<SimpleCourse>> map=new HashMap<>();
        //遍历年份，根据年份查询课程
        for(String year:years){
            List<Course> courseList = courseRepository.findByYear(year);
            //封装数据
            map.put(year, this.toSimpleCourse(courseList));
        }
        return map;
    }

    /**
     * 查询发布的总课课数
     * @return
     */
    @Cacheable(value = "course",key = "'count'")
    public Integer countCourse() {
      return courseRepository.countCourse();
    }

    /**
     * 搜索课程
     * @param query 搜索条件
     * @param pageable 分页参数
     * @return
     */
    public Page<Course> searchCourse(String query, Pageable pageable) {
         return courseRepository.findByQuery(query,pageable);
        //封装返回的数据
     //   return this.toSimpleCourse(courseList);
    }

    /**
     * 根据课程id和用户查找课程
     * @param id 课程id
     * @param user 用户
     * @return 一门课程
     */
    public Course findByIdAndUser(Integer id, User user) {
        return courseRepository.findByIdAndUser(id,user);
    }

    /**
     * 对课程点赞或取消赞
     * @param likedCourse
     * @param userId 点赞主体id
     * @param courseId 被点赞主体id
     */
    public boolean like(BType likedCourse, String userId, String courseId) {
        //获取key
        String key = RedisUtil.getKey(likedCourse, courseId);
        Boolean liked = stringRedisTemplate.opsForSet().isMember(key, userId);
        if(liked){
            //取消赞
            stringRedisTemplate.opsForSet().remove(key,userId);
        }else{
            //点赞
            stringRedisTemplate.opsForSet().add(key,userId+"");
        }
        //查询课程
        Optional<Course> optional = courseRepository.findById(Integer.parseInt(courseId));
        if(!optional.isPresent()){
            throw new CustomException("当前点赞的课程不存在");
        }

        //设置zset 存放精品课程
        String reportKey = RedisUtil.getReportKey(likedCourse);

        if(liked){
            //取消赞，分值减1
            redisTemplate.opsForZSet().incrementScore(reportKey,courseId,-1);
        }else{
            //点赞分值加1
            //先判断数据是否存在
            //Double score = redisTemplate.opsForZSet().score(reportKey, simpleCourse);
            //if(score==null){
                //数据库没有，新添
              //  redisTemplate.opsForZSet().add(reportKey,simpleCourse,1);
            //}else{
                //数据库有，分值加1
                redisTemplate.opsForZSet().incrementScore(reportKey,courseId,1);
           // }
        }
        return liked;
    }



    /**
     * 获取点赞数量
     * @param bType  业务类型
     * @param courseId 被点赞主体id
     * @return
     */
    public Integer countLike(BType bType, String courseId){
        //获取key
        String key = RedisUtil.getKey(bType, courseId);
        Long count = stringRedisTemplate.opsForSet().size(key);
        return count.intValue();
    }

    /**
     * 判断用户是否已经点过赞
     * @param courseId 被点赞主体id
     * @param userId 点赞主体id
     * @return
     */
    public boolean isLiked(String courseId, String userId) {
        //获取key
        String key = RedisUtil.getKey(BType.LIKED_COURSE, courseId);
        return stringRedisTemplate.opsForSet().isMember(key, userId);

    }

    /**
     * 根据作者id查询作品数
     * @param uId
     */
    public Integer countCourseByUId(Integer uId) {
        return courseRepository.countByUserIdAndStatus(uId,true);
    }


    /**
     * 根据id查询课程，收藏课程使用
     * @param courseId
     * @return
     */
    public Course findByIdToRedis(Integer courseId) {
        Optional<Course> optional = courseRepository.findById(courseId);
        if(!optional.isPresent()){
            return null;
        }
        Course course = optional.get();
        return course;
    }
}
