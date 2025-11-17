package road_friend.road_friend_server.Loader;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import road_friend.road_friend_server.Repository.BoardRepository;
import road_friend.road_friend_server.domain.Board;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BoardLoader implements CommandLineRunner {

    private final BoardRepository boardRepository;

    @Override
    public void run(String... args){

        //이미 생성됐으면 실행x
        if(boardRepository.findAll().size() >0) return;

        List<String> cities = List.of(
                "서울특별시",
                "부산광역시",
                "대구광역시",
                "인천광역시",
                "광주광역시",
                "대전광역시",
                "울산광역시",
                "세종특별자치시",
                "경기도",
                "강원특별자치도",
                "충청북도",
                "충청남도",
                "전북특별자치도",
                "전라남도",
                "경상북도",
                "경상남도",
                "제주특별자치도"
        );

        for (String departure : cities) {
            for (String arrival : cities) {
                if (!departure.equals(arrival)) {
                    Board board = new Board();
                    board.setDeparture(departure);
                    board.setArrival(arrival);
                    boardRepository.saveBoard(board);

                }
            }
        }







    }
}
