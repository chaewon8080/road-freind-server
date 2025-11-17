package road_friend.road_friend_server.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import road_friend.road_friend_server.Dto.BusStopDto;
import road_friend.road_friend_server.domain.Board;
import road_friend.road_friend_server.domain.BusStop;

import java.util.ArrayList;
import java.util.List;


@Repository
@Transactional
public class BoardRepository {

    @PersistenceContext
    private EntityManager em;

    public void saveBoard(Board board) {
        em.persist(board);
    }

    public Board findOne(Long id) {
        return em.find(Board.class, id);
    }

    public List<Board> findAll() {
        return em.createQuery("select b from Board b", Board.class).getResultList();

    }
}