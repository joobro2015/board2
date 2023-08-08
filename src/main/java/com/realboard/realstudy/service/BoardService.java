package com.realboard.realstudy.service;

import com.realboard.realstudy.DTO.BoardDTO;
import com.realboard.realstudy.entity.BoardEntity;
import com.realboard.realstudy.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


// DTO -> Entity (Entity Class)
// Entity -> DTO (DTO Class)
@Service
public class BoardService {


    //DTO객체는 컨토럴러와 서비스간에서 사용한다
    //entity클래스는 레포지토리에서 사용해야한다.
    // 그래서 밑에처럼 변환

    @Autowired
    private BoardRepository boardRepository;
    //레포지터리는 기본적으로 엔티티클래스만 받아준다고 생각
    public void save(BoardDTO boardDTO) {
        //dto객체를 entity로 옮겨담는다. 데이터베이스로 옮겨담음
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        boardRepository.save(boardEntity);
    }

    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        //entity 객체를 dto로 옮겨담는다. 데이터베이스에서 가져옴
        for (BoardEntity boardEntity: boardEntityList){
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }

    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHit(id);
    }

    public BoardDTO findById(Long id) {
         Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
         if(optionalBoardEntity.isPresent()){
             BoardEntity boardEntity = optionalBoardEntity.get();
             BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
             return boardDTO;
         }else {
             return null;
         }
    }
}
