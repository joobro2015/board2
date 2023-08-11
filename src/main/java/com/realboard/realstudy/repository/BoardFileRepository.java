package com.realboard.realstudy.repository;

import com.realboard.realstudy.entity.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardFileRepository  extends JpaRepository<BoardFileEntity, Long> {

}
