package com.lgh.pull2.repository;

import com.lgh.pull.entity.SourceArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by hot on 2017/6/13.
 */
@Repository
public interface SourceArticleRepository extends JpaRepository<SourceArticle,Long> {
}
