package road_friend.road_friend_server.Loader;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import road_friend.road_friend_server.Api.BusStopService;
import road_friend.road_friend_server.Repository.BusStopRepository;
import road_friend.road_friend_server.domain.BusStop;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BusStopDataLoader implements CommandLineRunner {

    private final BusStopService busStopService;
    private final BusStopRepository busStopRepository;

    @Override
    public void run(String... args) throws Exception {

        if(busStopRepository.findAll().size()>0) return;

        List<BusStop> busStops = busStopService.loadBusStops();
        for(BusStop busStop : busStops){
            busStopRepository.saveBusStop(busStop);

        }
        System.out.println("버스정류장 데이터 저장 완료: " + busStops.size());
    }
}
