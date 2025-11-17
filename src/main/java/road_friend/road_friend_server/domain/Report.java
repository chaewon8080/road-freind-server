package road_friend.road_friend_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

////리뷰, 게시판, 글, 댓글, 사용자 공통으로 사용
@Entity
@Getter
@Setter
public class Report {
    @Id
    @GeneratedValue
    private Long id;

    // 신고자
    @ManyToOne(fetch = FetchType.LAZY)
    private Member reporter;

    // 신고 대상 ID (Post, Comment, Member 중 하나의 PK)
    private Long targetId;

    // 신고 대상 타입 ("POST", "COMMENT", "USER")
    private String targetType;

    private String reason;

    private LocalDateTime createdAt = LocalDateTime.now();
}