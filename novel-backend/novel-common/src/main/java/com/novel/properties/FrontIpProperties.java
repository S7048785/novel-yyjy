package com.novel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Nyxcirea
 * @date 2025/12/15
 * @description: 前端IP配置
 */
@Data
@ConfigurationProperties(prefix = "novel.front")
public class FrontIpProperties {
	private List<String> ip;
}