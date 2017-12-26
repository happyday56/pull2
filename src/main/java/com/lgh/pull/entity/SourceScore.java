package com.lgh.pull.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by hot on 2017/7/11.
 */
@Entity
@Getter
@Setter
public class SourceScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80)
    private String title;

    @Column(length = 80)
    private String englishTitle;

    @Column(scale = 2, precision = 16)
    private BigDecimal score;
 
}
