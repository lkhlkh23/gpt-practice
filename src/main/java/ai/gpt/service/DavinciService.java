package ai.gpt.service;

import io.github.flashvayne.chatgpt.property.ChatgptProperties;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import io.github.flashvayne.chatgpt.service.impl.DefaultChatgptService;
import org.springframework.stereotype.Service;

@Service
public class DavinciService implements GptService{

    private ChatgptService service;

    public DavinciService(final ChatgptProperties properties) {
        this.service = new DefaultChatgptService(properties);
    }

    @Override
    public String sendMessage(final String prompt) {
        return service.sendMessage(prompt);
    }

}
