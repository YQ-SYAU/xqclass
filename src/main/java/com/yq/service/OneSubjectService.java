package com.yq.service;

import com.yq.dao.OneSubjectRepository;
import com.yq.entity.OneSubject;
import com.yq.entity.TwoSubject;
import com.yq.entity.dto.OneSubjectDto;
import com.yq.entity.dto.TwoSubjectDto;
import com.yq.exception.CustomException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 一级分类service
 */
@Service
public class OneSubjectService {

    @Autowired
    OneSubjectRepository oneSubjectRepository;
    @Autowired
    TwoSubjectService twoSubjectService;
    /**
     * 查询所有分类  先根据sort和createTime升序
     * @return 一级分类列表（包含二级分类）
     */
    @Cacheable(value = "subject",key = "'subjectList'")
    public List<OneSubjectDto> findAllSubject() {
        //查询分类
        List<Sort.Order> orders=new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC,"sort"));
        orders.add(new Sort.Order(Sort.Direction.ASC,"createTime"));
        List<OneSubject> oneSubjects = oneSubjectRepository.findAll(Sort.by(orders));
        //封装一级分类
        //1.遍历一级分类list,得到一级分类对象，获取每个一级分类对象值
        List<OneSubjectDto> oneSubjectDtos=new ArrayList<>();
        for(OneSubject oneSubject:oneSubjects){
            OneSubjectDto oneSubjectDto = new OneSubjectDto();
            BeanUtils.copyProperties(oneSubject,oneSubjectDto);
            //封装二级分类
            //1.遍历二级分类list
            List<TwoSubjectDto> twoSubjectDtos=new ArrayList<>();
            //查询二级分类 根据sort升序时间降序
            Integer oneId=oneSubject.getId();
            List<TwoSubject> twoSubjects=twoSubjectService.findAll(oneId);
            for(TwoSubject twoSubject:twoSubjects){
                TwoSubjectDto twoSubjectDto=new TwoSubjectDto();
                BeanUtils.copyProperties(twoSubject,twoSubjectDto);
                twoSubjectDtos.add(twoSubjectDto);
            }
            //把一级下面的二级分类放到一级分类里面
            oneSubjectDto.setTwoSubjects(twoSubjectDtos);
            //把封装好的一级分类放入list
            oneSubjectDtos.add(oneSubjectDto);
        }
        return oneSubjectDtos;

    }

    /**
     * 保存分类 和更新分类
     * @param oneSubject
     * @return
     */
    @CacheEvict(value = "subject",key = "'subjectList'")
    public OneSubject saveOneSubject(OneSubject oneSubject) {
        //判断是更新还是添加
        if(oneSubject.getId()==null){
            oneSubject.setCreateTime(new Date());
        }
        OneSubject save = oneSubjectRepository.save(oneSubject);
        return save;
    }



    /**
     * 根据id删除一级分类  级联删除
     * @param id
     */
    @CacheEvict(value = "subject",key = "'subjectList'")
    public void delOneSubjectById(Integer id) {
        oneSubjectRepository.deleteById(id);
    }

    /**
     * 根据id查询一级分类
     * @param id
     * @return
     */
    public OneSubjectDto findById(Integer id) {
        Optional<OneSubject> optional = oneSubjectRepository.findById(id);
        if(!optional.isPresent()){
            throw new CustomException("该分类不存在");
        }
        OneSubject oneSubject = optional.get();
        OneSubjectDto oneSubjectDto = new OneSubjectDto();
        BeanUtils.copyProperties(oneSubject,oneSubjectDto);
        return oneSubjectDto;
    }
}
