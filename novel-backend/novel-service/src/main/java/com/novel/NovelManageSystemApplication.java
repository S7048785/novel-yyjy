package com.novel;

import com.novel.properties.ExcludePathProperties;
import org.babyfish.jimmer.sql.EnableDtoGeneration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@EnableConfigurationProperties({ExcludePathProperties.class})
@SpringBootApplication
public class NovelManageSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovelManageSystemApplication.class, args);
	}

}
