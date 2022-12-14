package com.voyager.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author Voyager1
 * @create 2022-03-29 21:43
 */

/**
 * Kaptcha 配置类（验证码）
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100"); //宽度
        properties.setProperty("kaptcha.image.height", "40"); //高度
        properties.setProperty("kaptcha.textproducer.font.size", "32"); //字体大小
        properties.setProperty("kaptcha.textproducer.font.color", "black"); //字体颜色
        // 随机生成字符的范围
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // 生成几个字符
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 添加噪声
        properties.setProperty("kaptcha.textproducer.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
