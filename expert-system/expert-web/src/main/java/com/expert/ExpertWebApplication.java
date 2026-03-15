package com.expert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//使用redis的时候把exclude去掉
@EnableTransactionManagement //开启注解方式的事务管理
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class})
@Slf4j
public class ExpertWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpertWebApplication.class, args);
        log.info("expert-web started");
    }

}
