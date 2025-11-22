package road_friend.road_friend_server.Dto;


import lombok.Data;
import road_friend.road_friend_server.domain.ReportType;

@Data
public class ReportDto {

    private ReportType type;
    private Long targetId;
    private String reporterEmail;
    private String reason;
}
