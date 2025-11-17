package road_friend.road_friend_server.Dto;

import lombok.Data;

@Data
public class MemberDto {
    private String email;
    private String password;
    private String nickname;
}