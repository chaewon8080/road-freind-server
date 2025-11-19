package road_friend.road_friend_server.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import road_friend.road_friend_server.domain.ReviewLike;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class ReviewLikeRepository {

    private final EntityManager em;

    public void save(ReviewLike reviewLike) {
        em.persist(reviewLike);
    }

    public void delete(ReviewLike reviewLike) {
        em.remove(reviewLike);
    }

    public ReviewLike findOne(Long id) {
        return em.find(ReviewLike.class, id);
    }

    // 특정 회원이 특정 리뷰에 좋아요 눌렀는지 확인
    public ReviewLike findByMemberAndReview(Long memberId, Long reviewId) {
        List<ReviewLike> result = em.createQuery(
                        "select r from ReviewLike r where r.member.id = :memberId and r.review.id = :reviewId",
                        ReviewLike.class)
                .setParameter("memberId", memberId)
                .setParameter("reviewId", reviewId)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }


    public List<ReviewLike> findAllByReviewId(Long reviewId) {
        return em.createQuery(
                        "select r from ReviewLike r where r.review.id = :reviewId",
                        ReviewLike.class
                )
                .setParameter("reviewId", reviewId)
                .getResultList();
    }
}
