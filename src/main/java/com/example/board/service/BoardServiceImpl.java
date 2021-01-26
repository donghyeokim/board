package com.example.board.service;

import com.example.board.entity.Member;
import com.example.board.entity.Reply;
import com.example.board.repository.ReplyRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.board.dto.BoardDTO;
import com.example.board.dto.PageRequestDTO;
import com.example.board.dto.PageResultDTO;
import com.example.board.entity.Board;
import com.example.board.repository.BoardRepository;

import javax.transaction.Transactional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Log4j2
public class BoardServiceImpl implements BoardService{
    private final BoardRepository repository;
    private final ReplyRepository replyRepository;
    @Override
    public Long register(BoardDTO dto){
        log.info(dto);
        Board board = dtoToEntity(dto);
        repository.save(board);
        return board.getBno();
    }

    @Override
    public PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO){
        log.info(pageRequestDTO);
        Function<Object[], BoardDTO> fn = (en -> entityToDTO((Board)en[0], (Member)en[1], (Long)en[2]));
        Page<Object[]> result = repository.getBoardWithReplyCount(pageRequestDTO.getPageable(Sort.by("bno").descending()));
        return new PageResultDTO<>(result, fn);
    }

    @Override
    public BoardDTO get(Long bno){
        Object result = repository.getBoardByBno(bno);
        Object[] arr = (Object[]) result;
        return entityToDTO((Board) arr[0], (Member) arr[1], (Long) arr[2]);
    }

    //Reply와 게시글이 함께 삭제되어야 하므로 Transactional 처리 해줌
    @Transactional
    @Override
    public void removeWithReplies(Long bno){
        //댓글부터 삭제
        replyRepository.deleteByBno(bno);
        repository.deleteById(bno);
    }

    //지연 로딩으로 인해 Transactional 처리 해줌
    @Transactional
    @Override
    public void modify(BoardDTO boardDTO){
        Board board = repository.getOne(boardDTO.getBno());

        if(board != null){
            board.changeTitle(boardDTO.getTitle());
            board.changeContent(boardDTO.getContent());
            repository.save(board);
        }
    }
}
