package com.poetryapp.poetry.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data @Entity @Table(name = "grades")
public class Grade {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "textbook_id", nullable = false)
    private Long textbookId;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 20)
    private String level; // PRIMARY | MIDDLE
    @Column(name = "grade_number", nullable = false)
    private Integer gradeNumber;
    @Column(name = "order_num")
    private Integer orderNum = 0;
}
