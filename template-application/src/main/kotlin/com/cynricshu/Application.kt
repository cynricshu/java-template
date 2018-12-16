// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Application
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@SpringBootApplication(scanBasePackageClasses = [Application::class])
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}