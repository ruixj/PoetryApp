package com.poetryapp.game.repository;

import com.poetryapp.game.entity.PoemRef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoemRefRepository extends JpaRepository<PoemRef, Long> {}
