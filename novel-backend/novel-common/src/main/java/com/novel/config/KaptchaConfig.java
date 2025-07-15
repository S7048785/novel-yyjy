package com.novel.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author YYJY
 */
@Configuration
public class KaptchaConfig {
    /**
     * 配置Kaptcha验证码生成器
     *
     * @return DefaultKaptcha实例，配置了验证码的各种属性
     */
    @Bean
    public DefaultKaptcha defaultKaptcha() {
        // 创建DefaultKaptcha实例
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        // 创建Properties对象用于存储验证码配置
        Properties properties = new Properties();
        // 设置验证码边框为无
        properties.setProperty("kaptcha.border", "no");
        // 设置验证码字体颜色为黑色
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        // 设置验证码字符间距为5像素
        properties.setProperty("kaptcha.textproducer.char.space", "5");
        // 设置验证码长度为4个字符
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 设置验证码图片宽度为160像素
        properties.setProperty("kaptcha.image.width", "160");
        // 设置验证码图片高度为60像素
        properties.setProperty("kaptcha.image.height", "60");
        // 设置验证码字体大小为40像素
        properties.setProperty("kaptcha.textproducer.font.size", "40");
        // 使用Properties创建Config对象
        Config config = new Config(properties);
        // 将配置应用到DefaultKaptcha实例
        defaultKaptcha.setConfig(config);
        // 返回配置好的DefaultKaptcha实例
        return defaultKaptcha;
    }
}