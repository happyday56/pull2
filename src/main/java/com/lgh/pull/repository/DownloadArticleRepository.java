package com.lgh.pull.repository;

import com.lgh.pull.entity.DownloadArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadArticleRepository extends JpaRepository<DownloadArticle,Integer> {


}
