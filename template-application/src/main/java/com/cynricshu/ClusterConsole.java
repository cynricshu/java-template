// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

/**
 * ClusterConsole
 *
 * @author Xiong Chao(xiongchao@baidu.com)
 */
@SpringBootApplication(scanBasePackageClasses = ClusterConsole.class, exclude = ErrorMvcAutoConfiguration.class)
public class ClusterConsole {
    public static void main(String[] args) {
        SpringApplication.run(ClusterConsole.class, args);
    }
}
