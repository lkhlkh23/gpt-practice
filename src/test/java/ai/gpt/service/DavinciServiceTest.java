package ai.gpt.service;

import com.google.common.collect.Lists;
import io.github.flashvayne.chatgpt.property.ChatgptProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import util.ReviewReader;
import util.ReviewWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DavinciServiceTest {

    @Autowired
    @Qualifier("customProperties")
    private ChatgptProperties properties;

    private DavinciService service;


    @BeforeEach
    void setUp() {
        properties.setTemperature(0.5);
        properties.setTopP(0.5);

        this.service = new DavinciService(properties);
    }

    @Test
    void test_sendMessage() throws IOException {
        final Map<Long, List<String>> reviews = ReviewReader.getInstance().readReviews("review.txt");
        final Map<Long, List<String>> summary = new HashMap<>();
        for (final Long productNo : reviews.keySet()) {
            Lists.partition(reviews.get(productNo), 10)
                 .forEach(s -> {
                     final String prompt = "다음 리뷰를 한줄로 요약해주세요:\n" + String.join("\n", s);
                     final String result = service.sendMessage(prompt);
                     if(summary.containsKey(productNo)) {
                         summary.get(productNo).add(result);
                     } else {
                        summary.put(productNo, new ArrayList<>(List.of(result)));
                     }
                 });
        }

        ReviewWriter.getInstance().saveReviews(summary, ".");
    }

}