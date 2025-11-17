package road_friend.road_friend_server.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class BusStop {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String number;
    private String mobileNumber;

    private double latitude;
    private double longitude;
    private String address;

    @OneToMany(mappedBy = "busStop", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
}