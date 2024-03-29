package com.cynricshu.model.app;

import java.time.Instant;

import lombok.Data;

/**
 * App
 *
 * @author Cynric Shu
 */
@Data
public class App {
    private long id;
    private String name;
    private Instant createTime;
}
