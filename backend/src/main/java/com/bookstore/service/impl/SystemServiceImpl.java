package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.dto.CarouselSaveRequest;
import com.bookstore.dto.ServerLoadResponse;
import com.bookstore.entity.Carousel;
import com.bookstore.mapper.CarouselMapper;
import com.bookstore.service.SystemService;
import com.sun.management.OperatingSystemMXBean;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
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

    @Override
    public ServerLoadResponse getServerLoad() {
        ServerLoadResponse response = new ServerLoadResponse();

        // 获取操作系统信息
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        Runtime runtime = Runtime.getRuntime();

        // CPU 使用率
        double cpuLoad = osBean.getCpuLoad() * 100;
        response.setCpuUsage(Math.round(cpuLoad * 100.0) / 100.0);

        // 系统内存信息 (转换为 MB)
        long totalPhysicalMemory = osBean.getTotalMemorySize() / (1024 * 1024);
        long freePhysicalMemory = osBean.getFreeMemorySize() / (1024 * 1024);
        long usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;
        double memoryUsage = totalPhysicalMemory > 0 ? (usedPhysicalMemory * 100.0 / totalPhysicalMemory) : 0;

        response.setTotalMemory(totalPhysicalMemory);
        response.setFreeMemory(freePhysicalMemory);
        response.setUsedMemory(usedPhysicalMemory);
        response.setMemoryUsage(Math.round(memoryUsage * 100.0) / 100.0);

        // JVM 内存信息 (转换为 MB)
        long jvmTotal = runtime.totalMemory() / (1024 * 1024);
        long jvmFree = runtime.freeMemory() / (1024 * 1024);
        long jvmUsed = jvmTotal - jvmFree;
        long jvmMax = runtime.maxMemory() / (1024 * 1024);

        response.setJvmTotalMemory(jvmTotal);
        response.setJvmUsedMemory(jvmUsed);
        response.setJvmMaxMemory(jvmMax);

        // 系统信息
        response.setOsName(osBean.getName());
        response.setOsArch(osBean.getArch());
        response.setAvailableProcessors(osBean.getAvailableProcessors());

        // 运行时间 (秒)
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        response.setUptime(uptime);

        return response;
    }
}
