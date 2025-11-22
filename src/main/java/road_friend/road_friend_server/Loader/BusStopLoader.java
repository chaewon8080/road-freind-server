package road_friend.road_friend_server.Loader;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Component
@RequiredArgsConstructor
public class BusStopLoader implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {

        String file = "/data/busstoplist_utf8.csv";

        try (
                Connection conn = dataSource.getConnection();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(getClass().getResourceAsStream(file), StandardCharsets.UTF_8))
        ) {
            conn.setAutoCommit(false);

            String line;
            boolean first = true;
            int batchSize = 1000;
            int count = 0;
            int success = 0;
            int skipped = 0;

            List<String> rows = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }

                String[] t = line.split(",");

                try {
                    double lat = Double.parseDouble(t[2].replace("\"", "").trim());
                    double lng = Double.parseDouble(t[3].replace("\"", "").trim());

                    String number = t[0].replace("\"", "").trim();
                    String name = t[1].replace("\"", "").trim();
                    String mobile = t[5].replace("\"", "").trim();
                    String addr = t[7].replace("\"", "").trim();

                    // 한 줄의 SQL 값 생성
                    String sqlRow = String.format(
                            "('%s','%s',%.6f,%.6f,'%s','%s')",
                            escape(number), escape(name), lat, lng, escape(mobile), escape(addr)
                    );

                    rows.add(sqlRow);
                    count++;
                    success++;

                } catch (Exception e) {
                    skipped++;
                }

                // 1000개 모이면 bulk insert 실행
                if (rows.size() == batchSize) {
                    executeBulkInsert(conn, rows);
                    rows.clear();
                    System.out.println("Inserted so far: " + success + " / Skipped: " + skipped);
                }
            }

            // 마지막 남은 데이터 처리
            if (!rows.isEmpty()) {
                executeBulkInsert(conn, rows);
            }

            conn.commit();

            System.out.println("=== FINISHED ===");
            System.out.println("Inserted: " + success);
            System.out.println("Skipped: " + skipped);
        }
    }

    // SQL에서 따옴표 깨짐 방지용 escape
    private String escape(String s) {
        return s.replace("'", "''");
    }

    private void executeBulkInsert(Connection conn, List<String> rows) throws SQLException {
        String sql = "INSERT INTO bus_stop (number, name, latitude, longitude, mobile_number, address) VALUES "
                + String.join(",", rows);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}

