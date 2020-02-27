package com.cynricshu.service

import com.cynricshu.Application
import com.cynricshu.model.dataobject.AppDo
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
 * @author Cynric Shu
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppMapperTest {
    @Autowired
    lateinit var appMapper: AppMapper

    @Test
    fun testCreate() {
        val appPo = AppDo()
        appPo.name = "firstTest"
        appPo.createTime = Instant.now()

        appMapper.insert(appPo)
    }
}
