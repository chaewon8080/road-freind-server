package road_friend.road_friend_server.Repository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import road_friend.road_friend_server.domain.Member;
import road_friend.road_friend_server.domain.Report;

import java.util.List;

@Transactional
@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private final EntityManager em;

    public void save(Report report) {

            em.persist(report);

    }
    public Report findOne(Long id) {
        return em.find(Report.class, id);
    }
    public void delete(Report report) {
        em.remove(report);
    }

    public List<Report> findAll(){

        return em.createQuery("select r from Report r", Report.class).getResultList();



    }


}
