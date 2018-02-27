package ewing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@MapperScan("ewing.mapper")
@SpringBootApplication
public class StartApp {

    public static void main(String[] args) {
        SpringApplication.run(StartApp.class, args);
    }

}
