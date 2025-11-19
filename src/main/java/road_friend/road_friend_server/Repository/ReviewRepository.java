package road_friend.road_friend_server.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import road_friend.road_friend_server.Dto.ReviewDto;
import road_friend.road_friend_server.domain.Member;
import road_friend.road_friend_server.domain.Review;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class ReviewRepository {

    @PersistenceContext
    private EntityManager em;
    private final BusStopRepository busStopRepository; // BusStop 조회용

    //특정 정류장 리뷰 조회
    public List<Review> getReviewsByBusStop(Long busStopId) {
        List<Review> reviews = em.createQuery(
                        "SELECT r FROM Review r WHERE r.busStop.id = :busStopId ORDER BY r.createdAt DESC",
                        Review.class
                )
                .setParameter("busStopId", busStopId)
                .getResultList();

        return reviews;
    }

    //리뷰 저장
    public void saveReview(Review review) {

        em.persist(review);

    }

    public Review findOne(Long reviewId){
        return em.find(Review.class,reviewId);

    }

    public void delete(Review review) {
        em.remove(review);
    }





}
