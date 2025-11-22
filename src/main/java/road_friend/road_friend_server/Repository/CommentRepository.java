package road_friend.road_friend_server.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import road_friend.road_friend_server.domain.Comment;

import java.util.List;

@Transactional
@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final EntityManager em;

    // 댓글 저장
    public void save(Comment comment) {
        em.persist(comment);
    }

    // 특정 게시글에 달린 댓글
    public List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId) {
        return em.createQuery(
                        "select c from Comment c " +
                                "where c.post.id = :postId " +
                                "order by c.createdAt asc",
                        Comment.class
                )
                .setParameter("postId", postId)
                .getResultList();
    }

    // 댓글 조회
    public Comment findOne(Long id) {
        return em.find(Comment.class, id);
    }

    // 삭제
    public void delete(Comment comment) {
        em.remove(comment);
    }
}