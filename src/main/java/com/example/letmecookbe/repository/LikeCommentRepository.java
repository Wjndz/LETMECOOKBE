package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.Comment;
import com.example.letmecookbe.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeCommentRepository extends JpaRepository<LikeComment, String> {
    boolean existsByCommentIdAndAccountId(String commentId, String accountId);

    void deleteByCommentIdAndAccountId(String commentId, String accountId);

    List<LikeComment> getAllLikeCommentByAccountIdAndCommentId(String accountId, String commentId);
}
