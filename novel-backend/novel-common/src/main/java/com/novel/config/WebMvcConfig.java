package com.novel.config;

import com.novel.interceptor.JwtTokenInterceptor;
import com.novel.properties.ExcludePathProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
	private ExcludePathProperties excludePathProperties;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new JwtTokenInterceptor())
				.addPathPatterns("/**")
				.excludePathPatterns(
						"/doc.html",
						"/webjars/**",
						"swagger-ui/**",
						"/swagger-resources/**",
						"/v3/**").excludePathPatterns(excludePathProperties.getPath());
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
	// 如果上面的配置仍然doc.html空白404，就用下面这个
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/systemPictures/**")
				.addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "uploadFile" + File.separator + "systemPictures" + File.separator);
		registry.addResourceHandler("/uploadFile/pluginFiles/logo/**")
				.addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "uploadFile" + File.separator + "pluginFiles" + File.separator + "logo" + File.separator);
		
		registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}