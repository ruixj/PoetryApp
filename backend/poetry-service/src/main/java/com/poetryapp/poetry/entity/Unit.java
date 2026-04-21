package com.poetryapp.poetry.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data @Entity @Table(name = "units")
public class Unit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "grade_id", nullable = false)
    private Long gradeId;
    @Column(nullable = false, length = 200)
    private String name;
    @Column(name = "order_num")
    private Integer orderNum = 0;
}
