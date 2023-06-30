package ai.gpt.service;

import ai.gpt.constant.GptModel;
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
class TurboServiceTest {

    @Autowired
    @Qualifier("customProperties")
    private ChatgptProperties properties;

    private TurboService service;


    @BeforeEach
    void setUp() {
        properties.setUrl(GptModel.TURBO_3_5_16K.getUrl());
        properties.setModel(GptModel.TURBO_3_5_16K.getModel());
        properties.setTemperature(1.0);
        properties.setTopP(0.5);

        this.service = new TurboService(properties);
    }

    @Test
    void test_sendMessage() throws IOException, InterruptedException {
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
            Thread.sleep(60000);
        }

        ReviewWriter.getInstance().saveReviews(summary, ".");
    }

}