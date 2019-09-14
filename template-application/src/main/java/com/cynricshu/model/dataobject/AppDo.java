package com.cynricshu.model.dataobject;

import java.time.Instant;

import lombok.Data;

/**
 * AppPO
 *
 * @author Cynric Shu
 */
@Data
public class AppDo {
    private long id;
    private String name;
    private Instant createTime;
}
