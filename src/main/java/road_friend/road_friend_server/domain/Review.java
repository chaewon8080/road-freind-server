package road_friend.road_friend_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_stop_id")
    private BusStop busStop;

    private Boolean isAnonymous; //익명 여부

    private String content;
    private String[] categoryTags; // "민원", "실시간 제보", "일반"
    private String[] dayTags; // 월~일
    private String[] timeTags;  // 00:00~23:00
    private String imageUrl;
    private int likeCount;

    private LocalDateTime createdAt;
}