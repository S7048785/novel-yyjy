package com.novel;

import com.novel.properties.ExcludePathProperties;
import com.novel.properties.FrontIpProperties;
import org.babyfish.jimmer.client.EnableImplicitApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableImplicitApi
@EnableConfigurationProperties({ExcludePathProperties.class, FrontIpProperties.class})
@SpringBootApplication
public class NovelManageSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovelManageSystemApplication.class, args);
	}

}
