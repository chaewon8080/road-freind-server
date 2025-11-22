package road_friend.road_friend_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String password;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;     // USER or ADMIN




}