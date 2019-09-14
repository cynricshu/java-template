package com.cynricshu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

/**
 * ClusterConsole
 *
 * @author Xiong Chao(xiongchao@baidu.com)
 */
@SpringBootApplication(scanBasePackageClasses = Application.class, exclude = ErrorMvcAutoConfiguration.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
