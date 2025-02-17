<h4 align="right"><a href="./readme.md">English</a> | <strong>简体中文</strong> | <a href="./readme_ja.md">
日本語</a></h4>

# tiny-agents： 一个基于 Java 的 LLM（大语言模型）开发SDK。

---

## 基本能力

- LLM 的访问能力
- Prompt、Prompt Template
- Function Calling 定义、调用和执行等能力
- 记忆的能力（Memory）
- Embedding
- vision
- image
- audio

## 简单对话

使用 OpenAI 大语言模型:

```java

@Test
public void testChat() {
    OpenAILlmConfig config = new OpenAILlmConfig();
    config.setApiKey("<your key>");

    Llm llm = new OpenAILlm(config);
    String response = llm.chat("请问你叫什么名字");

    System.out.println(response);
}
```

使用 “通义千问” 大语言模型:

```java

@Test
public void testChat() {
    QwenLlmConfig config = new QwenLlmConfig();
    config.setApiKey("<your key>");
    config.setModel("qwen-turbo");

    Llm llm = new QwenLlm(config);
    String response = llm.chat("请问你叫什么名字");

    System.out.println(response);
}
```

使用 “讯飞星火” 大语言模型:

```java

@Test
public void testChat() {
    SparkLlmConfig config = new SparkLlmConfig();
    config.setAppId("xxxx");
    config.setApiKey("xxx");
    config.setApiSecret("xxx");

    Llm llm = new SparkLlm(config);
    String response = llm.chat("请问你叫什么名字");

    System.out.println(response);
}
```

## 历史对话示例

```java
public static void main(String[] args) {
    SparkLlmConfig config = new SparkLlmConfig();
    config.setAppId("****");
    config.setApiKey("****");
    config.setApiSecret("****");

    Llm llm = new SparkLlm(config);

    HistoriesPrompt prompt = new HistoriesPrompt();

    System.out.println("您想问什么？");
    Scanner scanner = new Scanner(System.in);
    String userInput = scanner.nextLine();

    while (userInput != null) {

        prompt.addMessage(new HumanMessage(userInput));

        llm.chatStream(prompt, (context, response) -> {
            System.out.println(">>>> " + response.getMessage().getContent());
        });

        userInput = scanner.nextLine();
    }
}
```

## Function Calling

- 第一步: 通过注解定义本地方法

```java
public class WeatherFunctions {

    @FunctionDef(name = "get_the_weather_info", description = "根据传入的城市和日期，返回该地区对应日期的的天气信息")
    public static String getWeatherInfo(
            @FunctionParam(name = "city", description = "the city name", required = true) String name,
            @FunctionParam(name = "date", description = "the date，required format YYYY-MM-DD HH:MM:SS", required = false) String date
    ) {
        System.out.println(">>> tool execute => getWeatherInfo: " + name);
        return name + "的" + date + "天气是：晴天，温度是：35度，风向是：西北风。";
    }
}
```

- 第二步: 通过 Prompt、Functions 传入给大模型，然后得到结果

```java
 public static void main(String[] args) {

    OllamaLlmConfig config = new OllamaLlmConfig();
    config.setEndpoint("http://localhost:11434");
    config.setModel("qwen2.5:14b");
    config.setDebug(true);

    Llm llm = new OllamaLlm(config);

    FunctionPrompt prompt = new FunctionPrompt("现在是日期和时间多少，并返回现在北京天气情况？", WeatherFunctions.class);
    prompt.addFunctions(List.of(new Function() {
        @Override
        public String getName() {
            return "now_time";
        }

        @Override
        public String getDescription() {
            return "计算当前实时日期和时间,YYYY-MM-DD HH:MM:SS 格式";
        }

        @Override
        public Parameter[] getParameters() {
            return new Parameter[]{
                    Parameter.builder()
                            .name("desc").type("string")
                            .description("需要计算的时间描述。比如前一天、今天、现在等。默认返回当前实时时间")
                            .required(true).build()
            };
        }

        @Override
        public Object invoke(Map<String, Object> argsMap) {
            return "2025-2-11 22:36:12";
        }
    }));


    AiMessageResponse response = ToolExecContext.of(prompt).execChat(llm);

    System.out.println(response.getMessage());
}
```

## Memory

| 存储类型        | 支持情况  | 描述         |
|-------------|-------|------------|
| Mongo       | ✅ 已支持 | -          |
| Redis       | ✅ 已支持 | -          |
| Mysql       | ✅ 已支持 | Mybatis 框架 |    
| PostgresSQL | ✅ 已支持 | Mybatis 框架 |

## 生态支持

### 大语言模型

| 大语言模型名称           | 支持情况  | 描述                                   |
|-------------------|-------|--------------------------------------|
| OpenAI            | ✅ 已支持 | 同时支持提供兼容OpenAI接口的本地部署框架（ vllm/sgl 等） |
| Ollama 部署模型       | ✅ 已支持 | -                                    |
| 星火大模型             | ✅ 已支持 | -                                    |
| 通义千问              | ✅ 已支持 | -                                    |
| 智普 ChatGLM        | ✅ 已支持 | -                                    |
| 月之暗面 Moonshot     | ✅ 已支持 | -                                    |
| 扣子 Coze           | ✅ 已支持 | -                                    |
| GiteeAI           | ✅ 已支持 | -                                    |
| Deepseek          | ✅ 已支持 | -                                    |
| 千帆                | ✅ 已支持 | -                                    |
| siliconflow（硅基流动） | ✅ 已支持 | -                                    |

### Function Calling 方法调用

| 大语言模型名称    | 支持情况  | 描述 |
|------------|-------|----|
| Openai     | ✅ 已支持 | -  |
| 星火大模型      | ✅ 已支持 | -  |
| 智普 ChatGLM | ✅ 已支持 | -  |
| Ollama     | ✅ 已支持 | -  |
| 通义千问       | ✅ 已支持 | -  |

### 多模态

| 大语言模型名称 | 支持情况  | 描述 |
|---------|-------|----|
| Openai  | ✅ 已支持 | -  |
| Ollama  | ✅ 已支持 | -  |

### 图片生成模型

| 大语言模型名称   | 支持情况  | 描述 |
|-----------|-------|----|
| Openai    | ✅ 已支持 | -  |
| Stability | ✅ 已支持 | -  |

### 语音文字转换

| 大语言模型名称 | 支持情况  | 描述 |
|---------|-------|----|
| Openai  | ✅ 已支持 | -  |
