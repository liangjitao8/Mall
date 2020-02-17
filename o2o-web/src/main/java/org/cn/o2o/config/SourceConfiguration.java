package org.cn.o2o.config;

import com.google.code.kaptcha.servlet.KaptchaServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.ServletException;

@Configuration

public class SourceConfiguration extends WebMvcConfigurationSupport{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*registry.addResourceHandler("/resources/**")
        .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/resources");*/
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {  //如果是Windows系统
            registry.addResourceHandler("/upload/**")
                    .addResourceLocations("file:D:/projectO2O/upload/"); //媒体资源
        } else {  //linux 和mac
            registry.addResourceHandler("/upload/**")
                    .addResourceLocations("file:/home/image/upload");   //媒体资源
        }
        super.addResourceHandlers(registry);
    }
    @Bean
    public ServletRegistrationBean servletRegistrationBean() throws ServletException{
        ServletRegistrationBean servlet = new ServletRegistrationBean(new KaptchaServlet(),"/Kaptcha");
        servlet.addInitParameter("kaptcha.border","no");
        servlet.addInitParameter("kaptcha.textproducer.font.color","red");
        servlet.addInitParameter("kaptcha.image.width","135");
        servlet.addInitParameter("kaptcha.textproducer.char.string","ACDEFHKPRSTWX345679");
        servlet.addInitParameter("kaptcha.image.height","50");
        servlet.addInitParameter("kaptcha.textproducer.font.size","43");
        servlet.addInitParameter("kaptcha.noise.color","black");
        servlet.addInitParameter("kaptcha.textproducer.char.length","4");
        servlet.addInitParameter("kaptcha.textproducer.font.names","Arial");
        return servlet;
    }

    /**
     * 视图解析器
     * @return
     */
    @Bean(name="viewResolver")
    public ViewResolver viewResolver(){
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        //设置容器
        viewResolver.setApplicationContext(getApplicationContext());
        viewResolver.setCache(false);
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    /**
     * 文件上传解析配置
     * @return
     */
    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver(){
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setResolveLazily(true); //resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
        resolver.setMaxInMemorySize(20971420);
        resolver.setMaxUploadSize(20971420);//上传文件大小 20M 20*1024*1024
        return resolver;
    }
}
