package com.realboard.realstudy.service;

import com.realboard.realstudy.DTO.BoardDTO;
import com.realboard.realstudy.entity.BoardEntity;
import com.realboard.realstudy.entity.BoardFileEntity;
import com.realboard.realstudy.repository.BoardFileRepository;
import com.realboard.realstudy.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    private BoardFileRepository boardFileRepository;
    //레포지터리는 기본적으로 엔티티클래스만 받아준다고 생각
    public void save(BoardDTO boardDTO) throws IOException {
        //dto객체를 entity로 옮겨담는다. 데이터베이스로 옮겨담음
        //파일 첨부 여부에 따라 로직을 분리
        if(boardDTO.getBoardFile().isEmpty()){
            //첨부 파일 없음.
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);

        }else {
            //첨부 파일 있음.
            /*
                1. DTO에 담긴 파일을 꺼냄
                2. 파일의 이름 가져옴
                3. 서버 저장용 이름을 만듦
                // 내사진.jpg => 1231241241_내사진.jpg
                4. 저장 경로 설정
                5. 해당 경로에 파일 저장
                6. board_table 에 해당 데이터 save 처리
                7. board_file_table에 해당 데이터 save 처리
             */
             BoardEntity  boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
             Long savedId = boardRepository.save(boardEntity).getId(); //단순히 pk값으로 사용되지는 않는다.
             BoardEntity board = boardRepository.findById(savedId).get();
//            다수의 파일 저장시 코드 위치 조정 부모 객체 먼저 가져오기

            for(MultipartFile boardFile: boardDTO.getBoardFile()) {

//                MultipartFile boardFile = boardDTO.getBoardFile(); // 1 원래 DTO에서 파일을 꺼내는거였으나 반복문에서 꺼내도록 수정
                String originalFilename = boardFile.getOriginalFilename();
                ; // 2.
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename; //3.
                String savePath = "C:/springboot_img/" + storedFileName; // C:/springboot_img/2809738012740_내사진. 4.
                boardFile.transferTo(new File(savePath)); // 5.

                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFileName);
                boardFileRepository.save(boardFileEntity);
//            ----------------저장까지-------------------------
            }
        }

    }

    @Transactional
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

    @Transactional
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
