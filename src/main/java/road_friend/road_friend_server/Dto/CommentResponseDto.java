package road_friend.road_friend_server.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private Long id;

    private Long authorId;
    private String authorNickName;
    private Boolean isAnonymous;

    private String content;
    private int likeCount;
    private boolean liked;

    private LocalDateTime createdAt;
}