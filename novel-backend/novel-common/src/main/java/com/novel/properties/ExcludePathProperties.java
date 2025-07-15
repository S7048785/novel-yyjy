package com.novel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author YYJY
 */
@Data
@ConfigurationProperties(prefix = "novel.exclude")
public class ExcludePathProperties {
	private List<String> path;
}
