package com.laoji.novelai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NovelAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NovelAiApplication.class, args);
    }
}