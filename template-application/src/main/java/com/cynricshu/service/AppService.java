package com.cynricshu.service;

import java.time.Instant;
import java.util.HashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.cynricshu.model.app.App;
import com.cynricshu.model.dataobject.AppDo;
import com.cynricshu.model.request.AppCreateRequest;
import com.cynricshu.mybatis.mapper.AppMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * AppService
 *
 * @author Cynric Shu
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class AppService {
    private final ObjectMapper objectMapper;
    private final AppMapper appMapper;

    public long create(AppCreateRequest request) {
        // generate default labels
        if (CollectionUtils.isEmpty(request.getLabels())) {
            request.setLabels(new HashMap<String, String>() {
                {
                    put("k8s-console", request.getName());
                }
            });
        }

        return createFromTemplate(request);
    }

    private long createFromTemplate(AppCreateRequest request) {
        String templateName = request.getTemplateName();
        Preconditions.checkArgument(!StringUtils.isEmpty(templateName));

        App app = new App();
        app.setName(request.getName());
        app.setCreateTime(Instant.now());

        appMapper.insert(toDbObject(app));
        return 0;
    }

    @SneakyThrows
    private AppDo toDbObject(App bizObject) {
        AppDo dbObject = new AppDo();
        BeanUtils.copyProperties(bizObject, dbObject);

        return dbObject;
    }
}
