package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.dto.CarouselSaveRequest;
import com.bookstore.entity.Carousel;
import com.bookstore.mapper.CarouselMapper;
import com.bookstore.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final CarouselMapper carouselMapper;

    @Override
    public List<Carousel> listEnabledCarousels() {
        return carouselMapper.selectList(new LambdaQueryWrapper<Carousel>()
                .eq(Carousel::getStatus, 1)
                .orderByAsc(Carousel::getSortOrder));
    }

    @Override
    public List<Carousel> listAllCarousels() {
        return carouselMapper.selectList(new LambdaQueryWrapper<Carousel>().orderByAsc(Carousel::getSortOrder));
    }

    @Override
    public void createCarousel(CarouselSaveRequest request) {
        Carousel carousel = new Carousel();
        carousel.setImageUrl(request.getImageUrl());
        carousel.setLink(request.getLink());
        carousel.setSortOrder(request.getSortOrder());
        carousel.setStatus(request.getStatus());
        carouselMapper.insert(carousel);
    }

    @Override
    public void updateCarousel(Long id, CarouselSaveRequest request) {
        Carousel carousel = new Carousel();
        carousel.setId(id);
        carousel.setImageUrl(request.getImageUrl());
        carousel.setLink(request.getLink());
        carousel.setSortOrder(request.getSortOrder());
        carousel.setStatus(request.getStatus());
        carouselMapper.updateById(carousel);
    }

    @Override
    public void deleteCarousel(Long id) {
        carouselMapper.deleteById(id);
    }
}
