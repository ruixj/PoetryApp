package com.poetryapp.poetry.entity;

import lombok.Data;

@Data
public class Unit {
    private Long id;
    private String name;
    private Long gradeId;
    private Integer orderNum = 0;
}
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "grade_id", nullable = false)
    private Long gradeId;
    @Column(nullable = false, length = 200)
    private String name;
    @Column(name = "order_num")
    private Integer orderNum = 0;
}
