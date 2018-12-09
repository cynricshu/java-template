// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.response;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * PageResponse
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageResponse<T> extends CommonResponse {
    private int totalCount;
    private List<T> results;
    private int pageNo;
    private int pageSize;
    private String orderBy;
    private String order;
}
