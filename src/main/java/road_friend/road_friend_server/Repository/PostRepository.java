package road_friend.road_friend_server.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import road_friend.road_friend_server.domain.Post;
import road_friend.road_friend_server.domain.Review;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class PostRepository {

    @PersistenceContext
    private EntityManager em;
    private final BoardRepository boardRepository;

    //특정 게시판 글 조회
    public List<Post> getPostsByBoard(Long boardId) {
        List<Post> posts = em.createQuery(
                        "SELECT p FROM Post p WHERE p.board.id = :boardId ORDER BY p.createdAt DESC",
                        Post.class
                )
                .setParameter("boardId", boardId)
                .getResultList();

        return posts;
    }

    //글 저장
    public void savePost(Post post) {

        em.persist(post);

    }





}