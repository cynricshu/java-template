package com.cynricshu.model.response;

import java.util.List;

import com.cynricshu.domain.dto.CommonResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * PageResponse
 *
 * @author Cynric Shu
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
