package road_friend.road_friend_server.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import road_friend.road_friend_server.domain.CommentLike;
import road_friend.road_friend_server.domain.PostLike;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class CommentLikeRepository {

    private final EntityManager em;

    public void save(CommentLike c) {
        em.persist(c);
    }

    public void delete(CommentLike c) {
        em.remove(c);
    }

    public CommentLike findOne(Long id) {
        return em.find(CommentLike.class, id);
    }

    // 특정 회원이 특정 댓글에 좋아요 눌렀는지 확인
    public CommentLike findByMemberAndComment(Long memberId, Long commentId) {
        List<CommentLike> result = em.createQuery(
                        "select c from CommentLike c where c.member.id = :memberId and c.comment.id = :commentId",
                        CommentLike.class)
                .setParameter("memberId", memberId)
                .setParameter("commentId", commentId)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }


    public List<CommentLike> findAllByCommentId(Long commentId) {
        return em.createQuery(
                        "select c from CommentLike c where c.comment.id = :commentId",
                        CommentLike.class
                )
                .setParameter("commentId", commentId)
                .getResultList();
    }
}