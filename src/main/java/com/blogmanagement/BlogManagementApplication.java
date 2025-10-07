package com.blogmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableScheduling
@EnableAsync
//@EnableCaching
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class BlogManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogManagementApplication.class, args);
	}

}
