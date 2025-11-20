package road_friend.road_friend_server.Dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostResponseDto {


    private Long id;

    private Long authorId;

    private String authorNickName;

    private Long boardId;

    private Boolean isAnonymous; //익명 여부

    private String content;

    private String category;      // 질문, 꿀팁, 자유
    private String departureTag;  // 기흥역, 동백, 죽전 ...
    private String arrivalTag;    // 이태원, 강남역 ...
    private String timeTag;       // 08:00, 09:00 등

    private String imageUrl;
    private String title;

    private int likeCount;

    private LocalDateTime createdAt=LocalDateTime.now();
    private List<CommentResponseDto> comments = new ArrayList<>();
    private int commentCount;

    private boolean liked;


}
