package com.cynricshu.model.request;

import java.time.Instant;

import com.cynricshu.model.k8s.K8sConstant;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AppListRequest
 *
 * @author Cynric Shu
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppListRequest extends PageRequest {
    private String namespace = K8sConstant.K8S_DEFAULT_NAMESPACE;
    private Instant createTime;
}
