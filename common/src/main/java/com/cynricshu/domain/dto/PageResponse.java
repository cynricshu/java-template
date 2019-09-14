package com.cynricshu.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PageResponse<T> extends CommonResponse {
    private PageResult<T> page;

    public PageResponse(PageResult<T> page) {
        this.setSuccess(true);
        this.setCode(0);
        this.setMessage("success");
        this.page = page;
    }
}
