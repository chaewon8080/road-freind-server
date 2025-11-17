package road_friend.road_friend_server.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import road_friend.road_friend_server.Dto.BusStopDto;
import road_friend.road_friend_server.domain.BusStop;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class BusStopRepository {

    @PersistenceContext
    private EntityManager em;

    public void saveBusStop(BusStop busStop) {
        em.persist(busStop);
    }

    public BusStop findOne(Long id) {
        return em.find(BusStop.class, id);
    }

    public List<BusStop> findAll() {
        return em.createQuery("select b from BusStop b", BusStop.class).getResultList();

    }

    public List<BusStopDto> findNearestStops(double lat, double lng) {
        List<Object[]> results = em.createNativeQuery("""
                            SELECT 
                                id, name, number, mobile_number, latitude, longitude, address,
                                (6371 * ACOS(
                                    COS(RADIANS(:lat)) * COS(RADIANS(latitude)) *
                                    COS(RADIANS(longitude) - RADIANS(:lng)) +
                                    SIN(RADIANS(:lat)) * SIN(RADIANS(latitude))
                                )) AS distance
                            FROM bus_stop
                            ORDER BY distance
                            LIMIT 50
                        """)
                .setParameter("lat", lat)
                .setParameter("lng", lng)
                .getResultList();

        List<BusStopDto> dtoList = new ArrayList<>();
        for (Object[] row : results) {
            BusStopDto dto = new BusStopDto();
            dto.setId(((Number) row[0]).longValue());
            dto.setName((String) row[1]);
            dto.setNumber((String) row[2]);
            dto.setMobileNumber((String) row[3]);
            dto.setLatitude(((Number) row[4]).doubleValue());
            dto.setLongitude(((Number) row[5]).doubleValue());
            dto.setAddress((String) row[6]);
            dto.setDistance(((Number) row[7]).doubleValue()); // ✅ 계산된 distance

            BusStop busStop = findOne(((Number) row[0]).longValue());
            dto.setReviewCount(busStop.getReviews().size());
            dtoList.add(dto);
        }

        return dtoList;
    }


    public List<BusStop> searchBusStops(String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>();

        String[] words = keyword.trim().split("\\s+");

        StringBuilder jpql = new StringBuilder("SELECT b FROM BusStop b WHERE ");
        for (int i = 0; i < words.length; i++) {
            jpql.append("b.name LIKE :word").append(i);
            if (i < words.length - 1) {
                jpql.append(" AND ");
            }
        }

        TypedQuery<BusStop> query = em.createQuery(jpql.toString(), BusStop.class);

        for (int i = 0; i < words.length; i++) {
            query.setParameter("word" + i, "%" + words[i] + "%");
        }

        return query.getResultList();
    }

}