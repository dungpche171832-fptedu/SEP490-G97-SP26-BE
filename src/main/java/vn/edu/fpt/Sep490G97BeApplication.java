package vn.edu.fpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync

public class Sep490G97BeApplication {

    public static void main(String[] args) {
        SpringApplication.run(Sep490G97BeApplication.class, args);
    }
}
