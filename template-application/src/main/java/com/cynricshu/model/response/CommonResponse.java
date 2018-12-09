// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.response;

import lombok.Data;

/**
 * CommonResponse
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
public class CommonResponse {
    private boolean success = true;
    private String status;
    private String message;

    public CommonResponse success(boolean success) {
        this.success = success;
        return this;
    }

    public CommonResponse message(String message) {
        this.message = message;
        return this;
    }
}
