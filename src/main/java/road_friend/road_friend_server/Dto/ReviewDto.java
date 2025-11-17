package road_friend.road_friend_server.Dto;

import jakarta.persistence.*;
import lombok.Data;
import road_friend.road_friend_server.domain.BusStop;
import road_friend.road_friend_server.domain.Member;

import java.time.LocalDateTime;

@Data
public class ReviewDto {


    private Long id;

    private Long authorId;

    private String authorNickName;

    private Long busStopId;
    private String busStopName;

    private Boolean isAnonymous; //익명 여부

    private String content;
    private String[] categoryTags; // "민원", "실시간 제보", "일반"
    private String[] dayTags; // 월~일
    private String[] timeTags;  // 00:00~23:00
    private String imageUrl;
    private int likeCount;

    private LocalDateTime createdAt=LocalDateTime.now();

}