package road_friend.road_friend_server.Dto;

import lombok.Data;

@Data
public class MemberDto {
    private Long id;
    private String email;
    private String password;
    private String nickname;
}