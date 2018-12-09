// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cynricshu.exception.ConsoleException;
import com.cynricshu.model.Constant;
import com.cynricshu.model.app.App;
import com.cynricshu.model.app.AppTemplate;
import com.cynricshu.model.request.AppCreateRequest;
import com.cynricshu.model.request.AppDeleteRequest;
import com.cynricshu.model.request.AppListRequest;
import com.cynricshu.model.response.CommonResponse;
import com.cynricshu.model.response.PageResponse;
import com.cynricshu.model.response.ResultResponse;
import com.cynricshu.mybatis.mapper.AppMapper;
import com.cynricshu.service.AppService;
import com.google.common.collect.Lists;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.models.V1DeleteOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 管理用户的应用，比如一个人脸识别应用，或者一个语音处理应用。
 * 这里不用Service，是为了和k8s的service概念区分开来。
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Slf4j
@RestController
@RequestMapping("/v1/app")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class AppController {
    private final AppService appService;
    private final AppMapper appMapper;

    @PostMapping("/list")
    public PageResponse<App> list(@RequestBody @Valid AppListRequest request) {
        List<App> results = Lists.newArrayList();
        results = appMapper.select(request);

        PageResponse<App> ret = new PageResponse<>();
        BeanUtils.copyProperties(request, ret);
        ret.setResults(results);

        return ret;
    }

    @GetMapping("/template/list")
    public ResultResponse<List<AppTemplate>> listTemplates() {
        List<AppTemplate> result = new ArrayList<>(Constant.TEMPLATE_MAP.values());

        return ResultResponse.result(result);
    }

    @PostMapping("/create")
    public CommonResponse create(@RequestBody @Valid AppCreateRequest request) {
        try {
            appService.create(request);
        } catch (ConsoleException e) {
            return new CommonResponse().success(false).message(e.getMessage());
        }
        return new CommonResponse();
    }

    @DeleteMapping("/delete")
    public CommonResponse delete(@RequestBody @Valid AppDeleteRequest request) {
        AppsV1beta1Api api = new AppsV1beta1Api();

        V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
        v1DeleteOptions.setPropagationPolicy(request.getPropagationPolicy());

        try {
            // the official api has internal error
            api.deleteNamespacedDeployment(
                    request.getName(), request.getNamespace(), v1DeleteOptions, null,
                    request.getGracePeriodSeconds(), null, request.getPropagationPolicy());
        } catch (ApiException e) {
            log.error("message:{}, detail: {}", e.getMessage(), e.getResponseBody());
            return new CommonResponse().success(false).message(e.getMessage());
        }

        return new CommonResponse();
    }

}
