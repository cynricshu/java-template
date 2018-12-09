// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ResultResponse
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResultResponse<T> extends CommonResponse {
    private T result;

    public static <T> ResultResponse<T> result(T result) {
        ResultResponse<T> resultResponse = new ResultResponse<>();
        resultResponse.setResult(result);
        return resultResponse;
    }
}
