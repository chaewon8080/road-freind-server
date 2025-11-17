package road_friend.road_friend_server.Dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import road_friend.road_friend_server.domain.Post;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardDto {
    private Long id;

    private String departure; // 출발지역
    private String arrival;   // 도착지역


    private List<PostResponseDto> posts = new ArrayList<>();
}