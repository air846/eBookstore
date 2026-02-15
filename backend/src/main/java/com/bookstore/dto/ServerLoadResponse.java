package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerLoadResponse {

    /**
     * CPU 使用率 (0-100)
     */
    private Double cpuUsage;

    /**
     * 内存使用率 (0-100)
     */
    private Double memoryUsage;

    /**
     * 总内存 (MB)
     */
    private Long totalMemory;

    /**
     * 已使用内存 (MB)
     */
    private Long usedMemory;

    /**
     * 可用内存 (MB)
     */
    private Long freeMemory;

    /**
     * JVM 总内存 (MB)
     */
    private Long jvmTotalMemory;

    /**
     * JVM 已使用内存 (MB)
     */
    private Long jvmUsedMemory;

    /**
     * JVM 最大内存 (MB)
     */
    private Long jvmMaxMemory;

    /**
     * 系统运行时间 (秒)
     */
    private Long uptime;

    /**
     * 操作系统名称
     */
    private String osName;

    /**
     * 操作系统架构
     */
    private String osArch;

    /**
     * 可用处理器数量
     */
    private Integer availableProcessors;
}
