package com.drinkmall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .findFirst()
                .ifPresent(c -> ((MappingJackson2HttpMessageConverter) c)
                        .setSupportedMediaTypes(List.of(
                                new MediaType("application", "json", StandardCharsets.UTF_8),
                                new MediaType("application", "*+json", StandardCharsets.UTF_8)
                        )));
    }
}
