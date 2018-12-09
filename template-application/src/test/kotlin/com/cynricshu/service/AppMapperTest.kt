// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.service;

import com.cynricshu.model.po.AppPO
import com.cynricshu.mybatis.mapper.AppMapper
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.Instant

/**
 * AppMapperTest
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@RunWith(SpringRunner::class)
@SpringBootTest()
class AppMapperTest {
    @Autowired
    lateinit var appMapper: AppMapper

    @Test
    fun testCreate() {
        val appPo = AppPO()
        appPo.name = "firstTest"
        appPo.createTime = Instant.now()

        appMapper.insert(appPo)
    }
}
