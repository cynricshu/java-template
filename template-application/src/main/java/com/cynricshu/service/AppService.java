// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cynricshu.exception.ConsoleErrorCode;
import com.cynricshu.exception.ConsoleException;
import com.cynricshu.exception.ConsoleRuntimeException;
import com.cynricshu.model.Constant;
import com.cynricshu.model.app.App;
import com.cynricshu.model.app.AppTemplate;
import com.cynricshu.model.k8s.K8sObjectTracker;
import com.cynricshu.model.po.AppPO;
import com.cynricshu.model.request.AppCreateRequest;
import com.cynricshu.mybatis.mapper.AppMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1DaemonSet;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * AppService
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class AppService {
    private final ObjectMapper objectMapper;
    private final AppMapper appMapper;

    public long create(AppCreateRequest request) throws ConsoleException {
        // generate default labels
        if (CollectionUtils.isEmpty(request.getLabels())) {
            request.setLabels(new HashMap<String, String>() {
                {
                    put("k8s-console", request.getName());
                }
            });
        }

        if (StringUtils.isNotBlank(request.getTemplateName())) {
            return createFromTemplate(request);
        } else {
            throw new ConsoleException("templateName cannot be null",
                    ConsoleErrorCode.WRONG_ARGUMENTS);
        }
    }

    private long createFromTemplate(AppCreateRequest request) {
        String templateName = request.getTemplateName();
        Preconditions.checkArgument(StringUtils.isNotBlank(templateName));

        AppTemplate appTemplate = Constant.TEMPLATE_MAP.get(templateName);
        Preconditions.checkNotNull(appTemplate);

        List<Object> allObject = Lists.newArrayList();

        log.debug("START: load yaml file");
        for (String path : appTemplate.getConfigFilePath()) {
            try {
                allObject.addAll(Yaml.loadAll(new File(path)));
            } catch (IOException e) {
                log.error("load yaml fail:{}", e.getMessage());
            }
        }
        log.debug("END: load yaml file");

        List<V1Deployment> deployments = new ArrayList<>();
        List<V1DaemonSet> daemonSets = new ArrayList<>();
        List<V1Service> services = new ArrayList<>();

        allObject.forEach(it -> {
            if (it instanceof V1Deployment) {
                deployments.add((V1Deployment) it);
            }

            if (it instanceof V1Service) {
                services.add((V1Service) it);
            }

            if (it instanceof V1DaemonSet) {
                daemonSets.add((V1DaemonSet) it);
            }
        });

        appTemplate.updateName(allObject);
        appTemplate.updateVersion(deployments, daemonSets);

        App app = new App();
        app.setName(request.getName());
        app.setK8sObjectTrackers(createK8sObjects(request.getNamespace(), deployments, daemonSets, services));
        app.setCreateTime(Instant.now());

        appMapper.insert(toDbObject(app));
        return 0;
    }

    @SneakyThrows
    private AppPO toDbObject(App bizObject) {
        AppPO dbObject = new AppPO();
        BeanUtils.copyProperties(bizObject, dbObject);

        dbObject.setK8sObjectTrackers(objectMapper.writeValueAsString(bizObject.getK8sObjectTrackers()));

        return dbObject;
    }

    private List<K8sObjectTracker> createK8sObjects(String namespace, List<V1Deployment> deployments,
            List<V1DaemonSet> daemonSets, List<V1Service> services) {
        List<K8sObjectTracker> trackers = new ArrayList<>();

        trackers.addAll(createDaemonSet(namespace, daemonSets));
        trackers.addAll(createDeployment(namespace, deployments));
        trackers.addAll(createService(namespace, services));

        return trackers;
    }

    private List<K8sObjectTracker> createDeployment(String namespace, List<V1Deployment> deployments) {
        List<K8sObjectTracker> trackers = new ArrayList<>();

        AppsV1Api appsApi = new AppsV1Api();
        for (V1Deployment deployment : deployments) {
            try {
                V1Deployment result = appsApi.createNamespacedDeployment(namespace, deployment, null);
                trackers.add(K8sObjectTracker.fromObjectMeta(result.getMetadata()));
            } catch (ApiException e) {
                log.error("create deployment fail! message:{}, detail:{}", e.getMessage(), e.getResponseBody());
                throw new ConsoleRuntimeException(ConsoleErrorCode.INVOKE_K8S_API_ERROR);
            }
        }

        return trackers;
    }

    private List<K8sObjectTracker> createDaemonSet(String namespace, List<V1DaemonSet> daemonSets) {
        List<K8sObjectTracker> trackers = new ArrayList<>();

        AppsV1Api appsApi = new AppsV1Api();

        for (V1DaemonSet daemonSet : daemonSets) {
            try {
                V1DaemonSet result = appsApi.createNamespacedDaemonSet(namespace, daemonSet, null);
                trackers.add(K8sObjectTracker.fromObjectMeta(result.getMetadata()));
            } catch (ApiException e) {
                log.error("create daemonset fail! message:{}, detail:{}", e.getMessage(), e.getResponseBody());
                throw new ConsoleRuntimeException(ConsoleErrorCode.INVOKE_K8S_API_ERROR);
            }
        }

        return trackers;
    }

    private List<K8sObjectTracker> createService(String namespace, List<V1Service> services) {
        List<K8sObjectTracker> trackers = new ArrayList<>();

        CoreV1Api coreApi = new CoreV1Api();

        for (V1Service service : services) {
            try {
                V1Service result = coreApi.createNamespacedService(namespace, service, null);
                trackers.add(K8sObjectTracker.fromObjectMeta(result.getMetadata()));
            } catch (ApiException e) {
                log.error("create service fail! message:{}, detail:{}", e.getMessage(), e.getResponseBody());
                throw new ConsoleRuntimeException(ConsoleErrorCode.INVOKE_K8S_API_ERROR);
            }
        }

        return trackers;
    }
}
