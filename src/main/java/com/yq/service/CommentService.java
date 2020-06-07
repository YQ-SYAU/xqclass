package com.yq.service;

import com.yq.dao.CommentRepository;
import com.yq.entity.Comment;
import com.yq.entity.Course;
import com.yq.entity.User;
import com.yq.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 评论
 */
@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserService userService;
    @Autowired
    CourseService courseService;
    //存放迭代找出的所有子代集合
    private List<Comment> tempReplays = new ArrayList<>();

    /**
     * 保存评论
     *
     * @param comment 评论实体类
     * @param uId     用户id用来获取评论人的信息
     */
    public void save(Comment comment, Integer uId) {
        User commentUser = userService.findById(uId);
        //设置评论的用户信息
        comment.setAvatar(commentUser.getAvatar());
        comment.setNickname(commentUser.getNickname());
        comment.setUId(commentUser.getId());
        //判断是不是作者自己的评论
        //1.根据课程id查询课程
        Integer cId = comment.getCourse().getId();
        Course course = courseService.findById(cId);
        //2.获取课程的用户id与评论人的id比较
        User courseUser = course.getUser();
        if (commentUser.getId() == courseUser.getId()) {
            //是作者的评论
            comment.setAuthorComment(true);
        } else {
            comment.setAuthorComment(false);
        }
        //判断是不是回复评论   不是回复评论的话，父评论id为 -1
        Integer parentCommentId = comment.getParentComment().getId();
        if (parentCommentId != -1) {
            //此评论为回复评论
            Optional<Comment> optional = commentRepository.findById(parentCommentId);
            if (!optional.isPresent()) {
                throw new CustomException("该评论的父评论不存在");
            }
            comment.setParentComment(optional.get());
        } else {
            comment.setParentComment(null);
        }
        comment.setCreateTime(new Date());
        commentRepository.save(comment);
    }

    /**
     * 根据课程id查询其下的所有评论
     *
     * @param cId 课程id
     * @return 评论列表
     */
    public List<Comment> findAllByCId(Integer cId) {
        //不能直接new sort,它的构造方法访问修饰符为private
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //得到所有顶级评论（没有父评论）
        List<Comment> comments = commentRepository.findByCourseIdAndParentCommentNull(cId, sort);
        //把顶级评论下所有的子代评论  放到一个list集合中
        combineChildren(comments);
        //返回修改后的数据
        return comments;
    }

    /**
     * 循环迭代出顶级评论下的子代评论，将这些子代评论变为顶级评论的直接子代评论
     *
     * @param comments 顶级评论列表
     * @return
     */
    private void combineChildren(List<Comment> comments) {
        //遍历顶级评论
        for (Comment comment : comments) {
            //获取顶级评论的   直接子代(二级)评论列表
            List<Comment> replyComments = comment.getReplyComments();
            //遍历直接子代（二级）评论列表，将直接子代（二级）评论列表和其下的所有子代评论列表放入临时数据集合中
            for (Comment reply : replyComments) {
                recursively(reply);
            }
            //修改顶级的reply集合
            comment.setReplyComments(tempReplays);
            //清除临时数据区
            tempReplays=new ArrayList<>();
        }
    }

    /**
     * 递归  将所有子代评论都放入临时数据集合中
     * @param reply
     */
    private void recursively(Comment reply) {
        tempReplays.add(reply);
        //判断是否还有直接子代评论列表
        if(reply.getReplyComments().size()>0){
            List<Comment> replyComments = reply.getReplyComments();
            for(Comment comment:replyComments){
                recursively(comment);
            }
        }

    }

}