package com.lgh.pull2.repository;

import com.lgh.pull.entity.Archives;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/6/17.
 */
@Repository
public interface ArchivesRepository extends JpaRepository<Archives, Integer> {

    @Query("select a from Archives a where a.title=?1")
    List<Archives> findByTitle(String title);

    @Query("select a from Archives a where a.litPic='' or a.litPic is null")
    List<Archives> findByLitPic();

    @Query("select max(a.id)   from Archives a")
    Integer findMaxId();

    @Query("select a.id,a.litPic from Archives a")
    List findImageUrls();

    @Modifying
    @Query("update Archives a set a.updateTime=?2 where a.id=?1")
    Integer updateUpdateTime(Integer id, Long updateTime);


    @Query("select a.litPic from Archives a where a.id=?1")
    String getLitPic(Integer id);


}
