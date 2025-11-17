package road_friend.road_friend_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;  // 어느 게시판에 속하는가

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    private String title;
    private String content;

    private Boolean isAnonymous; //익명 여부


    private String category;      // 질문, 꿀팁, 자유
    private String departureTag;  // 기흥역, 동백, 죽전 ...
    private String arrivalTag;    // 이태원, 강남역 ...
    private String timeTag;       // 08:00, 09:00 등

    private int likeCount;
    private String imageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

}
