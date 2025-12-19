package road_friend.road_friend_server.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import road_friend.road_friend_server.AIChat.GptService;
import road_friend.road_friend_server.domain.Member;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gpt")
public class GptController {
    private final GptService gptService;

    @PostMapping("/chat")
    public Map<String, String> chat(
            @RequestBody Map<String, Object> body
    ) {
        List<Map<String, String>> messages =
                (List<Map<String, String>>) body.get("messages");

        String answer = gptService.askWithHistory(messages);

        return Map.of("answer", answer);
    }

}
