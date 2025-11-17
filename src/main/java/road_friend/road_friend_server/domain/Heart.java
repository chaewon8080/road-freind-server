package road_friend.road_friend_server.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


//리뷰, 게시판, 글, 댓글 공통으로 사용
@Entity
@Getter
@Setter
public class Heart {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private Long targetId; // 좋아요 대상 (리뷰 or 게시글 or 댓글)
    private String targetType; // "REVIEW", "BOARD", "COMMENT"

}
