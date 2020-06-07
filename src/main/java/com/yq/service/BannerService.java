package com.yq.service;


import com.yq.dao.BannerRepository;
import com.yq.entity.Banner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 轮播图
 */
@Service
public class BannerService {

    @Autowired
    BannerRepository bannerRepository;

    /**
     * 保存
     * @param banner
     */
    @CacheEvict(value = "banner",key = "'bannerList'")
    public Banner saveBanner(Banner banner) {
        //设置时间
        banner.setCreateTime(new Date());
        return bannerRepository.save(banner);
    }

    /**
     * 查询所有轮播图 根据sort升序序再根据时间降序
     * @return
     */
    @Cacheable(value = "banner",key = "'bannerList'")
    public List<Banner> findAll() {
        ArrayList<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC,"sort"));
        orders.add(new Sort.Order(Sort.Direction.DESC,"createTime"));
        //直接new Sort()不行，他内部构造方法的访问修饰符
        return bannerRepository.findAll(Sort.by(orders));
    }

    /**
     * 根据id删除轮播图
     * @param id
     */
    @CacheEvict(value = "banner",key = "'bannerList'")
    public void deleteById(Integer id) {
        //多次点击浏览器，id不存在报错
        bannerRepository.deleteById(id);
    }
}
