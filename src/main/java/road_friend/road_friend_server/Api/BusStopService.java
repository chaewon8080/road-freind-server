package road_friend.road_friend_server.Api;

import org.springframework.stereotype.Service;
import road_friend.road_friend_server.domain.BusStop;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusStopService {

    public List<BusStop> loadBusStops() {
        List<BusStop> result = new ArrayList<>();
        String file = "/data/busstoplist.csv";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(file), Charset.forName("EUC-KR"))
        )) {
            String line;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; }

                String[] tokens = line.split(",");

                if (tokens.length > 7) {
                    BusStop busStop = new BusStop();
                    busStop.setNumber(tokens[0].trim().replace("\"", ""));
                    busStop.setName(tokens[1].trim().replace("\"", ""));

                    try {
                        busStop.setLatitude(Double.parseDouble(tokens[2].trim().replace("\"", "")));
                    } catch (NumberFormatException e) {
                        busStop.setLatitude(0.0);
                        System.out.println("위도 변환 실패: " + tokens[2]);
                    }

                    try {
                        busStop.setLongitude(Double.parseDouble(tokens[3].trim().replace("\"", "")));
                    } catch (NumberFormatException e) {
                        busStop.setLongitude(0.0);
                        System.out.println("경도 변환 실패: " + tokens[3]);
                    }

                    busStop.setMobileNumber(tokens[5].trim().replace("\"", ""));
                    busStop.setAddress(tokens[7].trim().replace("\"", ""));
                    result.add(busStop);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV 읽기 실패: " + file, e);
        }

        return result;
    }
}
