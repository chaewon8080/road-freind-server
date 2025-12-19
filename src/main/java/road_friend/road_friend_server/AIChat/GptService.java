package road_friend.road_friend_server.AIChat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GptService {

    private final WebClient openAiWebClient;

    @Value("${openai.model}")
    private String model;

    public String askWithHistory(List<Map<String, String>> messages) {

        if (model == null) {
            throw new IllegalStateException("openai.model 이 null 입니다");
        }

        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("messages 가 null 또는 비어있습니다");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put(
                "instructions",
                "너는 길친구 서비스의 안내 챗봇이야. 친절하게 한국어로 답변해. 답변은 최대 5줄 이내로 해."
        );
        requestBody.put("input", messages);

        Map<String, Object> response = openAiWebClient.post()
                .uri("/responses")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return extractAssistantText(response);
    }



    @SuppressWarnings("unchecked")
    private String extractAssistantText(Map<String, Object> response) {
        Object outputObj = response.get("output");
        if (!(outputObj instanceof List<?> outputList)) return null;

        for (Object o : outputList) {
            if (!(o instanceof Map<?, ?>)) continue;
            Map<String, Object> item = (Map<String, Object>) o;

            // assistant message만 찾기
            if (!"message".equals(item.get("type"))) continue;

            Object contentObj = item.get("content");
            if (!(contentObj instanceof List<?> contentList)) continue;

            for (Object c : contentList) {
                if (!(c instanceof Map<?, ?>)) continue;
                Map<String, Object> content = (Map<String, Object>) c;

                if ("output_text".equals(content.get("type"))
                        && content.get("text") != null) {
                    return content.get("text").toString();
                }
            }
        }
        return null;
    }
}
