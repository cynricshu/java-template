// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cynricshu.exception.ConsoleException;
import com.cynricshu.model.node.NodeSummary;
import com.cynricshu.model.request.PageRequest;
import com.cynricshu.model.response.PageResponse;
import com.cynricshu.service.NodeService;

import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Node;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * NodeController
 *
 * @author Xiong Chao(xiongchao@baidu.com)
 */
@Slf4j
@RestController
@RequestMapping("/v1/node")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class NodeController {
    private final NodeService nodeService;

    @PostMapping("/list")
    public PageResponse<NodeSummary> list(@RequestBody PageRequest request) {
        List<NodeSummary> results;
        PageResponse<NodeSummary> ret = new PageResponse<>();
        try {
            results = nodeService.listNode(request);

        } catch (ConsoleException e) {
            ret.setSuccess(false);
            ret.setMessage(e.getErrorCode().name());
            return ret;
        }

        BeanUtils.copyProperties(request, ret);
        ret.setResults(results);
        ret.setTotalCount(results.size());

        return ret;
    }

    @GetMapping("/{name}")
    @SneakyThrows
    public V1Node detail(@PathVariable("name") String name) {
        CoreV1Api api = new CoreV1Api();
        V1Node v1Node = api.readNode(name, null, null, null);
        return v1Node;
    }

    @SneakyThrows
    @PutMapping("/{name}/metadata/label")
    public V1Node updateLabels(@PathVariable("name") String name, @RequestBody Map<String, String> labels) {
        CoreV1Api api = new CoreV1Api();
        V1Node v1Node = api.readNode(name, null, null, null);
        v1Node.getMetadata().setLabels(labels);

        V1Node result = api.replaceNode(name, v1Node, null);
        return result;
    }
}
