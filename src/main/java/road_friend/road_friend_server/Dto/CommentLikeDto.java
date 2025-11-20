package road_friend.road_friend_server.Dto;

import lombok.Data;

@Data
public class CommentLikeDto {

    private Long postId;
    private boolean liked;
    private int likeCount;
}