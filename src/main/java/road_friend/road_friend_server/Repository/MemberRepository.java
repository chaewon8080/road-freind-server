package road_friend.road_friend_server.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import road_friend.road_friend_server.domain.Member;

import java.util.List;

@Repository
@Transactional
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public void saveMember(Member member) {

        em.persist(member);

    }

    public Member findByEmail(String email) {
        List<Member> result = em.createQuery(
                        "select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();

        if(result.isEmpty()){
            return null;
        }

        return result.get(0);
    }
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();

    }





    public  void updateMember(Member member){
        em.merge(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }



    public void delete(Member member){
        em.remove(member);
        em.flush();
    }

}