package com.cynricshu.model.request;

import static com.cynricshu.model.Constant.DEFAULT_PAGE_SIZE;

import lombok.Data;

/**
 * PageRequest
 *
 * @author Cynric Shu
 */
@Data
public class PageRequest {
    private int pageNo = 1;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private String orderBy;
    private String order;
}
