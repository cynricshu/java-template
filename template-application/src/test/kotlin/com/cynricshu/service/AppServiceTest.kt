package com.cynricshu.service

import com.cynricshu.Application
import com.cynricshu.model.request.AppCreateRequest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

/**
 * AppServiceTest
 *
 * @author Cynric Shu
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class])
class AppServiceTest {
    @InjectMocks
    lateinit var appService: AppService

    @Test
    fun testCreate() {
        val appCreateRequest = AppCreateRequest()
        appCreateRequest.name = "firstTest"
        appCreateRequest.templateName = "asrengine"
        appCreateRequest.version = "1.0.0"

        appService.create(appCreateRequest)
    }
}