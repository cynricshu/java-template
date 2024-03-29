package com.cynricshu.common.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class IdResponse extends CommonResponse {
    private String id;
}
