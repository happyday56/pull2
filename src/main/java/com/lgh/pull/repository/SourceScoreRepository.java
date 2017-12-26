package com.lgh.pull.repository;

import com.lgh.pull.entity.SourceScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by hot on 2017/7/11.
 */
@Repository
public interface SourceScoreRepository extends JpaRepository<SourceScore, Long> {
}
