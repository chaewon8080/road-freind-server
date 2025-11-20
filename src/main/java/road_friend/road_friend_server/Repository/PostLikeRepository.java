package road_friend.road_friend_server.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import road_friend.road_friend_server.domain.PostLike;
import road_friend.road_friend_server.domain.ReviewLike;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class PostLikeRepository {

    private final EntityManager em;

    public void save(PostLike postLike) {
        em.persist(postLike);
    }

    public void delete(PostLike postLike) {
        em.remove(postLike);
    }

    public PostLike findOne(Long id) {
        return em.find(PostLike.class, id);
    }

    // 특정 회원이 특정 글에 좋아요 눌렀는지 확인
    public PostLike findByMemberAndPost(Long memberId, Long postId) {
        List<PostLike> result = em.createQuery(
                        "select p from PostLike p where p.member.id = :memberId and p.post.id = :postId",
                        PostLike.class)
                .setParameter("memberId", memberId)
                .setParameter("postId", postId)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }


    public List<PostLike> findAllByPostId(Long postId) {
        return em.createQuery(
                        "select p from PostLike p where p.post.id = :postId",
                        PostLike.class
                )
                .setParameter("postId", postId)
                .getResultList();
    }
}