package com.yq.service;

import com.yq.dao.TwoSubjectRepository;
import com.yq.entity.TwoSubject;
import com.yq.entity.dto.TwoSubjectDto;
import com.yq.exception.CustomException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 二级分类service
 */
@Service
public class TwoSubjectService {

    @Autowired
    TwoSubjectRepository twoSubjectRepository;


    /**
     * 保存分类 和更新分类
     * @return
     */
    @CacheEvict(value = "subject",key = "'subjectList'")
    public TwoSubject saveTwoSubject(TwoSubject twoSubject) {
        if(twoSubject.getId()==null){
            twoSubject.setCreateTime(new Date());
        }
        TwoSubject save = twoSubjectRepository.save(twoSubject);
        return save;
    }

    /**
     * 根据id 查询分类
     * @param id
     * @return
     */
    public TwoSubjectDto findById(Integer id) {
        Optional<TwoSubject> subjectOptional = twoSubjectRepository.findById(id);
        if(!subjectOptional.isPresent()){
            //分类不存在
           throw  new CustomException("改二级分类不存在");
        }
        TwoSubject twoSubject = subjectOptional.get();
        TwoSubjectDto twoSubjectDto = new TwoSubjectDto();
        BeanUtils.copyProperties(twoSubject,twoSubjectDto);
        return twoSubjectDto;
    }

    /**
     * 根据id删除二级分类
     * @param id
     */
    @CacheEvict(value = "subject",key = "'subjectList'")
    public void delTwoSubjectById(Integer id) {
        twoSubjectRepository.deleteById(id);
    }

    /**
     * 通过一级分类id,根据sort和时间升序
     * @param oneId 一级分类id
     * @return
     */
    public List<TwoSubject> findAll(Integer oneId) {
        List<TwoSubject> twoSubjectList = twoSubjectRepository.findByOneId(oneId);
        return twoSubjectList;
    }
}
