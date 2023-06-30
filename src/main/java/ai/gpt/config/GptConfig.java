package ai.gpt.config;

import io.github.flashvayne.chatgpt.property.ChatgptProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Configuration
public class GptConfig {

    @Value("${chatgpt.key-location}")
    private String location;

    @Primary
    @Qualifier("customProperties")
    @Bean
    public ChatgptProperties customProperties(@Autowired ChatgptProperties properties) throws IOException {
        properties.setApiKey(findKey());

        return properties;
    }

    private String findKey() throws IOException {
        return new BufferedReader(new FileReader(location)).readLine();
    }
}
