package com.tantan.l2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
//@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//  @Bean
//  public Executor asyncExecutor() {
//    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//    executor.setCorePoolSize(5000);
//    executor.setMaxPoolSize(50000);
//    executor.setQueueCapacity(1000000);
//    executor.setWaitForTasksToCompleteOnShutdown(true);
//    executor.setAwaitTerminationSeconds(60);
//    executor.setThreadNamePrefix("l2-mt-");
//    executor.initialize();
//    return executor;


}
