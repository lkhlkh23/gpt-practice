package ai.gpt.service;

import com.google.common.collect.Lists;
import io.github.flashvayne.chatgpt.property.ChatgptProperties;
import io.github.flashvayne.chatgpt.property.MultiChatProperties;
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

import static ai.gpt.constant.GptModel.TURBO_3_5_16K;

@SpringBootTest
class BulkTurboServiceTest {

    @Autowired
    @Qualifier("customProperties")
    private ChatgptProperties properties;

    private TurboService service;


    @BeforeEach
    void setUp() {
        final MultiChatProperties setting = new MultiChatProperties();
        setting.setMaxTokens(1000);
        setting.setModel(TURBO_3_5_16K.getModel());
        setting.setTopP(1.0);
        setting.setTemperature(0.5);
        properties.setMulti(setting);

        this.service = new TurboService(properties);
    }

    @Test
    void test_sendMessage() throws IOException {
        final Map<Long, List<String>> reviews = ReviewReader.getInstance().readExcelReviews("review-top-100-200.xlsx", 60, 100, 100, ": ");
        final Map<Long, String> summary = new HashMap<>();
        for (final Long productNo : reviews.keySet()) {
            final List<String> abstracts = new ArrayList<>();
            Lists.partition(reviews.get(productNo), 50)
                 .forEach(s -> {
                     final String prompt = "리뷰를 300~600 바이트 사이로 요약해줘: " + String.join("\n", s) + "--output begin example–- [요약] : 리뷰요약내용. --output end exmaple--";
                     abstracts.add(service.sendMessage(prompt));
                 });
            final String prompt = "리뷰를 300~600 바이트 사이로 요약해줘: " + String.join("\n", abstracts) + "--output begin example–- [요약] : 리뷰요약내용. --output end exmaple--";
            final String result = service.sendMessage(prompt);
            summary.put(productNo, result);
        }

        ReviewWriter.getInstance().saveReviews(summary);
    }

}