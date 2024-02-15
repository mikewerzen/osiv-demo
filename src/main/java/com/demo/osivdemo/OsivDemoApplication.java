package com.demo.osivdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.demo")
public class OsivDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OsivDemoApplication.class, args);
    }

}
