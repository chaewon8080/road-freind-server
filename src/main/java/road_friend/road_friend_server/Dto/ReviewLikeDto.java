package road_friend.road_friend_server.Dto;

import lombok.Data;

@Data
public class ReviewLikeDto {

    private Long reviewId;
    private boolean liked;
    private int likeCount;
}
