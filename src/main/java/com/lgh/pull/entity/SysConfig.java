package com.lgh.pull.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "dede_sysconfig")
public class SysConfig {
    @Id
    @Column(name = "varname")
    private String varname;

    @Lob
    @Column(name = "value")
    private String value;
}
