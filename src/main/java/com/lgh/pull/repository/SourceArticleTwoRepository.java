package com.lgh.pull.repository;

import com.lgh.pull.entity.SourceArticleTwo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/6/20.
 */
@Repository
public interface SourceArticleTwoRepository extends JpaRepository<SourceArticleTwo,Long> {
}
