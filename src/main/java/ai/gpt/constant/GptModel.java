package ai.gpt.constant;

public enum GptModel {
    DAVINCI_03("text-davinci-003", "https://api.openai.com/v1/completions"),
    TURBO_3_5_16K("gpt-3.5-turbo-16k", "https://api.openai.com/v1/chat/completions"),
    CUSTOM_01("curie:ft-11st-test-2023-07-11-09-20-24", "https://api.openai.com/v1/completions");

    private String model;
    private String url;

    GptModel(String model, String url) {
        this.model = model;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getModel() {
        return model;
    }

}