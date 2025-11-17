package road_friend.road_friend_server.Dto;

import lombok.Data;

@Data
public class BusStopDto {
    private Long id;
    private String name;
    private String number;
    private String mobileNumber;
    private double latitude;
    private double longitude;
    private String address;
    private double distance;
    private int reviewCount;
}