package com.lgh.pull2.repository;

import com.lgh.pull.entity.AddOnArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/6/17.
 */
@Repository
public interface AddOnArticleRepository extends JpaRepository<AddOnArticle, Integer> {

    @Query("select a from AddOnArticle as a where a.typeid in ?1")
    List<AddOnArticle> findByTypes(List<Integer> types);

    @Query("select a from AddOnArticle as a where a.aid in ?1")
    List<AddOnArticle> findById(List<Integer> id);

    @Query("select a from AddOnArticle as a where a.aid >= ?1 and a.aid<=?2")
    List<AddOnArticle> findById(int startId, int endId);

    @Query("select a.aid,a.downUrls from AddOnArticle a")
    List findUrls();

    @Query("select a.aid,a.fromTTId from AddOnArticle a where a.fromTTId>0 and a.staus<>'本剧完结' and a.staus<>'本季完结' order by a.aid")
    List findTTID();

    @Modifying
    @Query("update AddOnArticle a set a.downUrls=?2 where a.aid=?1")
    Integer updateDownUrls(Integer aid, String urls);

    @Query("select a.downUrls from AddOnArticle a where a.aid=?1")
    String findDownUrlsById(Integer aid);

    @Query("select a.aid from AddOnArticle a where a.fromZMZId=?1")
    List<Integer> findByZmzId(Integer zmzId);



    @Query("select a.aid from AddOnArticle a where a.englishName=?1")
    List<Integer> findByEnglishName(String englishName);

    @Query("select a.aid,a.fromZMZId,a.movieName,a.season from AddOnArticle a where a.fromZMZId>0 and a.fromTTId=0 and a.staus<>'本剧完结' and a.staus<>'本季完结' order by a.aid")
    List findZMZTTID();


    @Query("select a.aid,a.fromTTId from AddOnArticle a where a.fromTTId>0 and a.imdb=''")
    List findByImdbTTID();

    @Query("select a.aid,a.fromTTId from AddOnArticle a where a.fromTTId>0")
    List findByTTID();

    @Modifying
    @Query("update AddOnArticle a set a.imdb=?2 where a.aid=?1")
    Integer updateImdb(Integer aid, String imdb);

    //美剧
    @Query("select a.aid,a.beginTime,a.movieName,a.englishName,a.staus,a.curCollection from AddOnArticle a where a.typeid in ?1 and a.beginTime like '2017%' and a.areaName='美国' order by a.beginTime")
    List findByThisYear(List<Integer> types);

    @Query("select a.aid,a.beginTime,a.movieName,a.englishName,a.summary from AddOnArticle a where a.typeid in ?1 and a.beginTime like ?2 order by a.beginTime")
    List findByThisMonth(List<Integer> types, String month);


    @Query("select a.aid,a.downUrls,a.movieName from AddOnArticle as a where a.typeid in ?1")
    List findAllByTypes(List<Integer> types);

    @Modifying
    @Query("update AddOnArticle a set a.curCollection=?2 where a.aid=?1")
    Integer updateCurCollection(Integer aid, int currentCollection);

    @Modifying
    @Query("update AddOnArticle a set a.body=?2 where a.aid=?1")
    Integer updateBody(Integer aid, String body);

    @Modifying
    @Query("update AddOnArticle a set a.recommend=?2 where a.aid=?1")
    Integer updateRecommend(Integer aid, String recommend);

    @Modifying
    @Query("update AddOnArticle a set a.beginTime=?2 where a.aid=?1")
    Integer updateBeginTime(Integer aid, String beginTime);

    /**
     * 更新未开播的状态
     *
     * @return
     */
    @Modifying
    @Query("update AddOnArticle a set a.staus='连载中' where a.staus='未开播' and a.downUrls like '%</a>%'")
    Integer updateStatusByNoBegin();


    @Query("select a.aid,a.summary,a.movieName from AddOnArticle as a")
    List findAllSummary();

    @Query("select a.aid,a.beginTime from AddOnArticle as a")
    List findAllBeginTime();

    @Query("select a.aid,a.beginTime,a.fromZMZId,a.season,a.movieName from AddOnArticle as a where a.staus='连载中'")
    List findBeginTimeBySerializing();

    @Query("select a.aid,a.recommend,a.movieName,a.englishName from AddOnArticle as a")
    List findAllRecommend();

    @Query("select a.aid from AddOnArticle a where a.movieName=?1")
    List<Integer> findByMovieName(String movieName);

    @Query("select a.aid from AddOnArticle a where a.movieName like ?1 order by a.movieName desc")
    List<Integer> findByLikeMovieName(String movieName);

    @Modifying
    @Query("update AddOnArticle a set a.summary=?2 where a.aid=?1")
    Integer updateSummary(Integer aid, String summary);

    @Query("select a.aid,a.movieName from AddOnArticle as a where a.aid<>?1 and  a.movieName like ?2 and a.englishName=?3 order by a.movieName")
    List findAllLikeNameAndEnglishName(Integer id, String name, String englishName);


    @Query("select a.aid,a.fromZMZId,a.movieName from AddOnArticle a where a.fromZMZId>0 and a.staus='连载中'")
    List findSerializing();

    @Modifying
    @Query("update AddOnArticle a set a.staus=?2 where a.aid=?1")
    Integer updateStatus(Integer id, String status);

    @Modifying
    @Query("update AddOnArticle a set a.maxCollection=?2 where a.aid=?1")
    Integer updateMaxCollection(Integer aid, Integer maxCollection);


    @Query("SELECT a.aid,a.fromDBId FROM AddOnArticle a where a.maxCollection=0 and a.fromDBId>0 and (a.staus='连载中' or a.staus='未开播')")
    List findListAbountDB();

    @Query("select a.aid from AddOnArticle a where a.fromZMZId=?1 and a.season=?2")
    List<Integer> findSeason(Integer fromZMZId, Integer season);

}
