// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cynricshu.exception.ConsoleErrorCode;
import com.cynricshu.exception.ConsoleException;
import com.cynricshu.model.Summary;
import com.cynricshu.model.node.NodeCapacity;
import com.cynricshu.model.node.NodeSummary;
import com.cynricshu.model.request.PageRequest;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.models.V1NodeStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * NodeService
 *
 * @author Xiong Chao(xiongchao@baidu.com)
 */
@Slf4j
@Service
public class NodeService {
    public List<NodeSummary> listNode(PageRequest request) throws ConsoleException {
        CoreV1Api api = new CoreV1Api();
        List<NodeSummary> results;

        try {
            V1NodeList list = api.listNode(null, null, null, null, null,
                    request.getPageSize(), null, null, null);

            results = list.getItems().stream().map(it -> {
                NodeSummary summary = Summary.fromObjectMeta(new NodeSummary(), it.getMetadata());

                V1NodeStatus v1NodeStatus = it.getStatus();
                summary.fillIp(v1NodeStatus);
                summary.fillStatus(v1NodeStatus);
                summary.setCapacity(NodeCapacity.fromNodeStatus(v1NodeStatus));

                return summary;
            }).collect(Collectors.toList());

        } catch (ApiException e) {
            log.error("list node fail, message:{}, detail: {}", e.getMessage(), e.getResponseBody());
            throw new ConsoleException(ConsoleErrorCode.INVOKE_K8S_API_ERROR);
        }

        return results;
    }
}
