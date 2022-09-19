package com.fit2cloud.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @Author:张少虎
 * @Date: 2022/9/19  9:44 AM
 * @Version 1.0
 * @注释:
 */
@Component
public class ThreadpoolConfig {
    /**
     * 定时任务线程池
     *
     * @return 定时任务线程池
     */
    @Bean(name = "jobThreadPool")
    public ThreadPoolExecutor jobThreadPool() {
        //todo 核心线程2个,最大线程8个,活跃时间30秒,活跃时间单位秒,阻塞线程200个,线程生产工厂:默认的线程工厂,拒绝策略:当线程超过最大线程+阻塞队列后会抛出错误RejectedExecutionException
        return new ThreadPoolExecutor(2, 8, 30, TimeUnit.SECONDS, new LinkedBlockingQueue(200), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 工作线程池
     *
     * @return 工作线程池
     */
    @Bean(name = "workThreadPool")
    public ThreadPoolExecutor workThreadPool() {
        //todo 核心线程2个,最大线程8个,活跃时间30秒,活跃时间单位秒,阻塞线程200个,线程生产工厂:默认的线程工厂,拒绝策略:当线程超过最大线程+阻塞队列后会抛出错误RejectedExecutionException
        return new ThreadPoolExecutor(2, 8, 30, TimeUnit.SECONDS, new LinkedBlockingQueue(200), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }
}
