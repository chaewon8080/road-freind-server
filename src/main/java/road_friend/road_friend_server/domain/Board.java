package road_friend.road_friend_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

//게시판
@Entity
@Getter
@Setter
public class Board {
    @Id
    @GeneratedValue
    private Long id;

    private String departure; // 출발지역
    private String arrival;   // 도착지역

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();
}