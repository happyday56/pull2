package com.lgh.pull2.repository;

import com.lgh.pull.entity.SourceArticleFour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/7/29.
 */
@Repository
public interface SourceArticleFourRepository extends JpaRepository<SourceArticleFour,Long> {

    @Query("select s from SourceArticleFour s where s.title=?1")
    SourceArticleFour findByTitle(String title);

    @Query("select s from SourceArticleFour s where s.keywords=''")
    List<SourceArticleFour> findByKeyworsNull();
}
