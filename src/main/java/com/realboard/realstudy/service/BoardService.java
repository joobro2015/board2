package com.realboard.realstudy.service;

import com.realboard.realstudy.DTO.BoardDTO;
import com.realboard.realstudy.entity.BoardEntity;
import com.realboard.realstudy.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public BoardDTO update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 3; // 한 페이지에 보여줄 글 갯수
        //page 위치에 있는 값은 0부터 시작 DB는 0부터 시작하기때문에
        Page<BoardEntity> boardEntities =
                boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC,"id")));
        //of뒤의값 (몇페이지를 보고싶은지, 보여줄 갯수, 정렬기준(properties 엔티티 기준))

        System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글갯수
        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 갯수
        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부

        // 목록 : id, writer, title, hits, createdTime
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), board.getBoardWriter(), board.getBoardTitle(), board.getBoardHits(), board.getCreatedTime()));
        return boardDTOS;
    }
}
