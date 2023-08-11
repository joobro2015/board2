package com.realboard.realstudy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BoardFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName;

    @ManyToOne(fetch = FetchType.LAZY) //eager 부모를 조회하면 자식도 다 가져옴.  /  lazy 필요한 상황에 내가 조회
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    public static BoardFileEntity toBoardFileEntity(BoardEntity boardEntity, String originalFileName, String storedFileName){
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setOriginalFileName(originalFileName);
        boardFileEntity.setStoredFileName(storedFileName);
        boardFileEntity.setBoardEntity(boardEntity); // *주의* pk값이 아니라 부모entity자체를 넘겨줘야한다.
        return boardFileEntity;
    }
}
