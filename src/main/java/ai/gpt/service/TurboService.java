package ai.gpt.service;

import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.property.ChatgptProperties;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import io.github.flashvayne.chatgpt.service.impl.DefaultChatgptService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurboService implements GptService {

    private ChatgptService service;

    public TurboService(final ChatgptProperties properties) {
        this.service = new DefaultChatgptService(properties);
    }

    @Override
    public String sendMessage(final String prompt) {
        return service.multiChat(List.of(new MultiChatMessage("user", prompt)));
    }

}
