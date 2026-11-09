package com.example.Resmat.configurations;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import com.samskivert.mustache.Mustache;

@Configuration
public class AppConfig {

    @Bean
    public BeanPostProcessor mustacheHackerBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (ClassUtils.isAssignable(bean.getClass(), Mustache.Compiler.class)
                        || "mustacheCompiler".equals(beanName)) {
                    Mustache.Compiler compiler = (Mustache.Compiler) bean;
                    return compiler.defaultValue("").nullValue("");
                }
                return bean;
            }
        };
    }
}