package com.cynricshu.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResultResponse<T> extends CommonResponse {
    private T result;

    public ResultResponse(boolean success, int code, String message, String requestId, T result) {
        super(success, code, message, requestId);
        this.result = result;
    }
}
