// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.util;

import java.io.File;
import java.nio.file.Paths;

import io.kubernetes.client.models.V1beta2Deployment;
import io.kubernetes.client.util.Yaml;
import lombok.SneakyThrows;

/**
 * YamlUtil
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
public class YamlUtil {
    @SneakyThrows
    public static V1beta2Deployment loadV1beta2Deployment(String filePath, String filename) {
        File f = Paths.get(filePath, filename).toFile();
        return Yaml.loadAs(f, V1beta2Deployment.class);
    }
}
