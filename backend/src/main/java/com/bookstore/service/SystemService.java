package com.bookstore.service;

import com.bookstore.dto.CarouselSaveRequest;
import com.bookstore.entity.Carousel;

import java.util.List;

public interface SystemService {

    List<Carousel> listEnabledCarousels();

    List<Carousel> listAllCarousels();

    void createCarousel(CarouselSaveRequest request);

    void updateCarousel(Long id, CarouselSaveRequest request);

    void deleteCarousel(Long id);
}
