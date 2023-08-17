package ai.gpt.service;

import ai.gpt.constant.GptModel;
import com.google.common.collect.Lists;
import io.github.flashvayne.chatgpt.property.ChatgptProperties;
import io.github.flashvayne.chatgpt.property.MultiChatProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import util.ReviewReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ai.gpt.constant.GptModel.TURBO_3_5_16K;

@SpringBootTest
class PromptRefactoredTurboServiceTest {

    @Autowired
    @Qualifier("customProperties")
    private ChatgptProperties properties;

    private TurboService service;

    @BeforeEach
    void setUp() {
        final MultiChatProperties setting = new MultiChatProperties();
        setting.setModel(TURBO_3_5_16K.getModel());
        properties.setMulti(setting);

        this.service = new TurboService(properties);
    }

    @Test
    @DisplayName("리뷰에 대한 평가 기준 제시해서 테스트 진행")
    void test_sendMessage_01() throws IOException {
        final Map<Long, List<String>> reviews = ReviewReader.getInstance().readTextReviews("review-prompt-refactored.txt");
        for (final Long productNo : reviews.keySet()) {
            for (final String content : reviews.get(productNo)) {
                System.out.println("------------------------------------------------------------------");
                final StringBuilder prompt = new StringBuilder();
                prompt.append("아래 상품의 리뷰를 사이즈, 핏, 착용감 측면에서 상품 평가를 해주세요.")
                      .append("아래 조건중 하나를 선택하시고, 이유도 같이 설명해주세요.")
                      .append("사이즈는 많이 작아요, 약간 작아요, 잘 맞아요, 약간 커요.")
                      .append("핏은 슬림해요, 적당해요, 오버핏이에요")
                      .append("착용감은 불편해요, 편해요, 아주편해요\n")
                      .append(content);
                final String result = service.sendMessage(prompt.toString());
                System.out.println("[결과] : " + result);
            }
        }
    }

    @Test
    @DisplayName("리뷰에 대한 평가 기준 및 기준에 대한 설명을 제시해서 테스트 진행")
    void test_sendMessage_02() throws IOException {
        final Map<Long, List<String>> reviews = ReviewReader.getInstance().readTextReviews("review-prompt-refactored.txt");
        for (final Long productNo : reviews.keySet()) {
            for (final String content : reviews.get(productNo)) {
                System.out.println("------------------------------------------------------------------");
                final StringBuilder prompt = new StringBuilder();
                prompt.append("상품 리뷰를 사이즈, 핏, 착용감기준으로 모두 이유와 함께 평가해줘")
                      .append("사이즈 : ")
                      .append("많이 작음 (옷이 작아서 몸에 맞지 않거나 불편함이 드는 경우)")
                      .append("약간 작음 (전체적으로 잘 맞지만, 몇몇 부분에서 타이트한 느낌이 드는 경우)")
                      .append("잘 맞음 (착용자의 몸에 맞는 정확하게 맞는 경우)")
                      .append("약간 큼 (옷이 조금 크게 나와 전체적으로 여유로운 느낌이 드는 경우)")
                      .append("핏 : ")
                      .append("슬림 (옷이 몸에 밀착되어 착용자의 체형을 감싸는 경우)")
                      .append("적당 (착용자의 몸에 자연스럽게 맞는 형태이며 넉넉한 느낌을 주는 경우)")
                      .append("오버핏 (착용자의 몸에 넉넉하게 떨어지는 형태이며 여우로운 느낌을 주는 경우)")
                      .append("착용감")
                      .append("불편 (움직임이 제한되거나 압박감이 느껴지는 경우)")
                      .append("편함 (움직임에 제한이 없고 쾌적한 감촉을 주는 경우)")
                      .append("아주 편함 (편함의 한단계 더 발전된 상태)")
                      .append(content)
                      .append("\n");
                final String result = service.sendMessage(prompt.toString());
                System.out.println("[결과] : " + result);
            }
        }
    }

    @Test
    @DisplayName("리뷰에 대한 단순한 해시테그 추출 테스트 진행")
    void test_sendMessage_03() throws IOException {
        final Map<Long, List<String>> reviews = ReviewReader.getInstance().readTextReviews("review-prompt-refactored.txt");
        for (final Long productNo : reviews.keySet()) {
            for (final String content : reviews.get(productNo)) {
                final StringBuilder prompt = new StringBuilder();
                prompt.append("리뷰에 포함된 유용한 문구를 해시태그 형식으로 추출해줘, ")
                      .append(content + " : ");
                final String result = service.sendMessage(prompt.toString());
                System.out.println("[결과] : " + result);
            }
        }
    }

    @Test
    @DisplayName("리뷰에 대한 단순한 해시테그 추출 및 분류 작업 테스트 진행")
    void test_sendMessage_04() throws IOException, InterruptedException {
        final Map<Long, List<String>> reviews = ReviewReader.getInstance().readTextReviews("review.txt");
        for (final Long productNo : reviews.keySet()) {
            final Map<String, Integer> results = new HashMap<>();
            Lists.partition(reviews.get(productNo), 10)
                 .forEach(s -> {
                     final String prompt = "리뷰에서 구매 결정에 필요한 문구를 해시태그 형식으로 추출해줘, :\n" + String.join("\n", s);
                     final String[] tags = service.sendMessage(prompt).replace("\n", "").replace(" ", "").split("#");
                     for (final String tag : tags) {
                         results.put(tag, results.getOrDefault(tag, 0) + 1);
                     }
                 });
            final List<Map.Entry<String, Integer>> sorted = new ArrayList<>(results.entrySet());
            sorted.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

            final String tagText = sorted.stream()
                                         .filter(s -> s.getKey().length() > 2)
                                         .map(s -> "#" + s.getKey())
                                         .limit(50)
                                         .collect(Collectors.joining(" "));

            final String prompt = "tag : " + tagText + ", 비슷한 내용의 tag끼리 분류해줘" + "--begin example--배송 : #빠름 #총알배송\n 가격 : #저렴 --end example--";
            final String result = service.sendMessage(prompt);
            System.out.println(result);
        }
    }

    @Test
    @DisplayName("해시태그에 대한 분류 작업 테스트 진행")
    void test_sendMessage_05() {
        final String text = "#흡수력좋음 #배송빠름 #쿠폰사용 #빠른배송 #유통기한넉넉 #가성비좋음 #슈퍼롱사이즈 #저렴한가격 #좋은가격 #양많음 #저렴하게구입 #10년이상사용 #항상구매 #십일절할인 #중형사용 #많이구입 " +
            "#문의빠른처리 #기분좋아요 #밤에안정적 #잘새지않아요 #흡수력좋고길이감좋고 #인터넷구매 #사용하고있어요 #예티켓탭 #쏘피오버나이트 #괜찮은제품 #구매재구매했어요 #흡수되는게겉도는느낌 #다양한사이즈파세요 #얇고가벼움 #안정감" +
            " #편하게잘수있어서 #무르지않음 #뭉침문제 #달달감 #생리대뽀송하고재질굿 #괜찮아요 #할인때마다쟁여놓고있음 #할인받아구매. #잘쓰고계속구매 #양많은날쓰기좋아요 #사이즈딱좋아 #계속사용중 #길고두툼함 #세일가격 " +
            "#재구매의사있음 #트러블없음 #유통기한길어서좋아요 #매번구매 #오래쓸수있음";
        final String prompt = "tag : " + text + ", 비슷한 내용의 tag끼리 분류해줘" + "--begin example--배송 : #빠름 #총알배송\n 가격 : #저렴 --end example--";
        final String result = service.sendMessage(prompt);
        System.out.println(result);
    }

    @Test
    @DisplayName("긍정리뷰와 부정리뷰 구분 테스트 진행")
    void test_sendMessage_06() {
        final List<String> questions = List.of(
            "겉절이를 주문했는데, 잘익은 묵은지가 도착했네요!"
        );

        for (final String question : questions) {
            final String prompt = "다음 리뷰는 긍정리뷰일까요? 부정리뷰일까요? 리뷰 : " + question;
            final String result = service.sendMessage(prompt);
            System.out.println(result);
        }
    }

    @Test
    @DisplayName("해시태그에 대한 기준 제시하여 해시태그 추출 후, 긍정/부정 구분하여 테스트 진행")
    void test_sendMessage_07() throws FileNotFoundException {
        final Map<Long, List<String>> reviews = ReviewReader.getInstance().readTextReviews("review-clothes-single.txt");
        for (final Long productNo : reviews.keySet()) {
            final Set<String> positiveTags = new HashSet<>();
            final Set<String> negativeTags = new HashSet<>();
            Lists.partition(reviews.get(productNo), 20)
                 .forEach(s -> {
                     final String prompt = "리뷰에서 디자인, 사이즈, 착용감, 가격관련 내용을 해시태그 형식으로 추출해줘, 긍정과 부정을 구분해줘, :\n" + String.join("\n", s)
                         + "--begin example--positive : #빠름 \\n negative : #비쌈 --end example--";
                     final String[] result = service.sendMessage(prompt).split("\n");
                     final String[] positives = result[0].replaceAll(" ", "").split("#");
                     for (final String positive : positives) {
                         if("".equals(positive)) {
                             continue;
                         }
                         positiveTags.add("#" + positive);
                     }
                     System.out.println("긍정 : " + positiveTags.toString());

                     final String[] negatives = result[result.length - 1].replaceAll(" ", "").split("#");
                     for (final String negative : negatives) {
                         if("".equals(negative)) {
                             continue;
                         }
                         negativeTags.add("#" + negative);
                     }
                     System.out.println("부정" + negativeTags.toString());
                 });

            final String format = "tag : " + "%s" + ", 비슷한 내용의 tag끼리 분류해줘." + "--begin example--배송 : #빠름 \n 가격 : #비쌈 --end example--";
            System.out.println(service.sendMessage(String.format(format, String.join(" ", String.join(" ", positiveTags)))));
            System.out.println(service.sendMessage(String.format(format, String.join(" ", String.join(" ", negativeTags)))));
        }
    }
}