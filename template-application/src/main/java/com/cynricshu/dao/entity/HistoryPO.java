package com.cynricshu.dao.entity;

import lombok.Data;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Data
@Entity
@Table(name = "history")
public class HistoryPO extends AbstractPersistable<Long> {
    private String uid;
    private Instant requestTime;
    private String items;
}
