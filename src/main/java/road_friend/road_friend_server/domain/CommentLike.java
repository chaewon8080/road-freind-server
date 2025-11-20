package road_friend.road_friend_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CommentLike {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;   // 좋아요 누른 회원

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;   // 좋아요 대상 리뷰
}