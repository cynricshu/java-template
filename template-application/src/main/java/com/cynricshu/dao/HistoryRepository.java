package com.cynricshu.dao;

import com.cynricshu.dao.entity.HistoryPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryPO, Long> {
    List<HistoryPO> findByUidAndRequestTimeBetween(String uid, Instant startTime, Instant endTime);

    List<HistoryPO> findByRequestTimeBetween(Instant startTime, Instant endTime);
}
