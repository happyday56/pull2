package com.lgh.pull2.repository;


import com.lgh.pull.entity.SourceArticleThree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hot on 2017/7/20.
 */
@Repository
public interface SourceArticleThreeRepository extends JpaRepository<SourceArticleThree,Long> {

    @Query("select s from SourceArticleThree s where s.title=?1")
    SourceArticleThree findByTitle(String title);

    @Query("select s from SourceArticleThree s where s.keywords=''")
    List<SourceArticleThree> findByKeyworsNull();
}
