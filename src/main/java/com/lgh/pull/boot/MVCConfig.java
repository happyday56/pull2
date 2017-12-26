package com.lgh.pull.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.List;

/**
 * Created by Administrator on 2016/4/10.
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.lgh.pull.controller")
public class MVCConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Autowired
    private WebApplicationContext webApplicationContext;
    /**
     * 禁止拦截静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/_resources/**").addResourceLocations("/_resources/");
        registry.addResourceHandler("bdunion.txt").addResourceLocations("bdunion.txt");
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new WebHandlerExceptionResolver());
    }

    /**
     * 视图显示Resolver
     *
     * @param registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        super.configureViewResolvers(registry);
        registry.viewResolver(viewResolver());
    }

    /**
     * thymeleaf解析
     *
     * @return
     */
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        SpringTemplateEngine engine = new SpringTemplateEngine();
        ServletContextTemplateResolver rootTemplateResolver = new ServletContextTemplateResolver(webApplicationContext.getServletContext());
        rootTemplateResolver.setPrefix("/WEB-INF/content/");
        rootTemplateResolver.setSuffix(".html");
        rootTemplateResolver.setCharacterEncoding("UTF-8");

        if (environment.acceptsProfiles("development")) {
            rootTemplateResolver.setCacheable(false);
        }

        engine.setTemplateResolver(rootTemplateResolver);
        resolver.setTemplateEngine(engine);
        resolver.setOrder(100);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setContentType("text/html;charset=utf-8");
        return resolver;
    }


}
