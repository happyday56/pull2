package com.lgh.pull2.repository;

import com.lgh.pull.entity.Arctiny;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/9/1.
 */
@Repository
public interface ArctinyRepository extends JpaRepository<Arctiny,Integer> {
}
