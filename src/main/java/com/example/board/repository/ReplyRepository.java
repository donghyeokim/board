package com.example.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.board.entity.Reply;

import javax.management.relation.Relation;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
