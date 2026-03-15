package com.expert.config;

import com.expert.interceptor.JwtTokenAdminInterceptor;
import com.expert.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Autowired
    JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login");
    }

    /**
     * 扩展消息转换器
     *
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        log.info("自定义消息转换器启用");
        //创建一个消息转换器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        //为消息转换器传入一个对象转换器，对象转换器将对象序列化为json数据
        converter.setObjectMapper(new JacksonObjectMapper());

        //将自定义的消息转换器加入消息转换器容器并设置其为第一序列
        converters.add(0,converter);
    }
}
