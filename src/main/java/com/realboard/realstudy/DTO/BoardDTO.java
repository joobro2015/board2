package com.realboard.realstudy.DTO;

import com.realboard.realstudy.entity.BoardEntity;
import com.realboard.realstudy.entity.BoardFileEntity;
import lombok.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//DTO(Data Transfer Object), VO, Bean
@Getter
@Setter
@ToString
@NoArgsConstructor //기본생성자
@AllArgsConstructor // 모든필드를 매개변수로 하는 생성자
@Component
public class BoardDTO {
    private Long id;
    private String boardWriter;
    private String boardPass;
    private String boardTitle;
    private String boardContents;
    private int boardHits;
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;

    private List<MultipartFile> boardFile; //save.html -> Controller 파일 담는 용도

//    ---------------------서비스 단에서 사용-------------------------------------------
    private List<String> originalFileName; //원본 파일 이름
    private List<String> storedFileName; //서버 저장용 파일 이름
    private int fileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)
//    -------------------------------------------------------------------------------

    public BoardDTO(Long id, String boardWriter, String boardTitle, int boardHits, LocalDateTime boardCreatedTime) {
        this.id = id;
        this.boardWriter = boardWriter;
        this.boardTitle = boardTitle;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }

    public static BoardDTO toBoardDTO(BoardEntity boardEntity){
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardPass(boardEntity.getBoardPass());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());
        if(boardEntity.getFileAttached() == 0){
            boardDTO.setFileAttached(boardEntity.getFileAttached()); //0
        } else{
            List<String> originalFileNamelist = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            boardDTO.setFileAttached(boardEntity.getFileAttached()); //1 - 파일 이름을 가져와야함
            // originalFileName, storedFileName : board_file_table(BoardFileEntity)
            // join
            // select * from board_table b, board_file_table bf where b.id = bf.board_id
            // and where b.id = ?
            for(BoardFileEntity boardFileEntity : boardEntity.getBoardFileEntityList()) {
//               boardDTO.setOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName()); //list 타입의 데이터를 접근할떄는 인덱스개념으로 해서 get을 사용
//                boardDTO.setStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());
                originalFileNamelist.add(boardFileEntity.getOriginalFileName());
                storedFileNameList.add(boardFileEntity.getStoredFileName());
            }
            boardDTO.setOriginalFileName(originalFileNamelist);
            boardDTO.setStoredFileName(storedFileNameList);
        }

        return boardDTO;
    }


}
