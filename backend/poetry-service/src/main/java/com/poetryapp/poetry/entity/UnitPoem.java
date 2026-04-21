package com.poetryapp.poetry.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "unit_poems")
@IdClass(UnitPoemId.class)
public class UnitPoem {
    @Id
    @Column(name = "unit_id")
    private Long unitId;

    @Id
    @Column(name = "poem_id")
    private Long poemId;

    @Column(name = "order_num")
    private Integer orderNum = 0;
}
