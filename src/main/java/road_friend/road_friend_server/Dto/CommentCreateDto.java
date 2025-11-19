package road_friend.road_friend_server.Dto;


import lombok.Data;

@Data
public class CommentCreateDto {
    private String content;
    private Boolean isAnonymous;
}
