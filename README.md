# Java AI Responses Rosetta

One use case, three implementations, one shared test suite: the OpenAI Responses API (`POST /v1/responses`)
as supported by Spring AI on Spring Boot, LangChain4j on Spring Boot and LangChain4j on Quarkus.

The Responses API is the successor of Chat Completions. Its wire format is published as an open,
multi-provider specification named Open Responses (openresponses.org), which OpenAI donated to the Agentic AI
Foundation of the Linux Foundation. Ollama, vLLM, OpenRouter and Hugging Face implement it. Compared with Chat
Completions it adds two capabilities: server side conversation state, through `store` and
`previous_response_id`, and hosted tools executed by the provider, such as `web_search`.

This project follows the method of the book *Java AI Frameworks* and its companion repository
(github.com/nicolasduminil/java-ai-rosetta): the same behaviour is implemented idiomatically on each stack and
held to one conformance suite, so that every difference observed is a property of the stack.

## Stacks and versions

| Stack | Library | Runtime | Responses model |
|---|---|---|---|
| `responses-spring-ai-boot` | Spring AI 2.1.0 | Spring Boot 4.2.0 | `OpenAiResponsesChatModel`, selected by `spring.ai.openai.chat.api=responses` |
| `responses-langchain4j-boot` | LangChain4j 1.24.0 | Spring Boot 4.2.0 | `OpenAiResponsesChatModel`, declared as a `@Bean` |
| `responses-langchain4j-quarkus` | Quarkus LangChain4j 1.15.1 (LangChain4j 1.23.0) | Quarkus 3.40.1.1 | produced by the extension, selected by `quarkus.langchain4j.openai.chat-model.mode=responses` |

Java 21 is required. The Maven wrapper (`./mvnw`) downloads Maven 3.9.16, so no local Maven is needed.
Every version is the latest stable release at the time of writing. Quarkus is the single exception: Quarkus
LangChain4j 1.15.1, the latest stable release of the extension, is built on Quarkus 3.40.1.1, and the line
targeting Quarkus 4 is a release candidate.

## Layout

```
java-ai-responses-rosetta/
├── responses-common/              # wire contract, deterministic tool data, shared conformance suite (test jar)
├── responses-langchain4j-common/  # Responses service and tool shared by the two LangChain4j stacks
├── responses-spring-ai-boot/
├── responses-langchain4j-boot/
└── responses-langchain4j-quarkus/
```

Each stack exposes `POST /chat`, which accepts `{"question", "previousResponseId", "webSearch"}` and returns
`{"answer", "responseId"}`. A blank question is rejected with 400. Each stack registers one local function tool,
`orderStatus`, whose result (`QX7731` for order 42) the model can obtain only by calling the tool.

The two LangChain4j stacks share their service class, `LangChain4jResponsesService`. The library code is
identical on both runtimes and the runtimes differ only in the wiring, which this sharing makes explicit.

## How each stack reaches the Responses API

Spring AI selects the Responses model through configuration and uses the usual `ChatClient`:

```properties
spring.ai.model.chat=openai
spring.ai.openai.chat.api=responses
spring.ai.openai.responses.model=gpt-4o-mini
```

```java
var prompt = chatClient.prompt().user(question.question());
if (question.webSearch())
  prompt = prompt.options(OpenAiResponsesChatOptions.builder().hostedTools(HostedTool.WebSearch.of()));
```

LangChain4j provides `OpenAiResponsesChatModel`, marked `@Experimental`. Its Responses specific parameters travel
in `OpenAiResponsesChatRequestParameters`, which the application passes on each `ChatRequest`. The AI service
abstraction builds generic request parameters, so this project calls the chat model directly and runs the tool
loop itself:

```java
var parameters = OpenAiResponsesChatRequestParameters.builder()
  .toolSpecifications(toolSpecifications)
  .previousResponseId(question.previousResponseId());
if (question.webSearch())
  parameters.serverTools(List.of(Map.of("type", "web_search")));
```

On Spring Boot the model is a `@Bean` built from a `@ConfigurationProperties` record. On Quarkus the extension
builds it from `quarkus.langchain4j.openai.*` once the build time property `chat-model.mode` is set to
`responses`, and a CDI producer hands it to the shared service.

## Conformance suite

The suite lives in `responses-common` and runs in two tiers.

The local tier, `AbstractLocalResponsesIT`, runs against Ollama's `/v1/responses` in a Testcontainers container
with `qwen2.5:0.5b`. It needs no key and runs in CI.

| Test | Spring AI | LangChain4j Boot | LangChain4j Quarkus |
|---|---|---|---|
| factual answer with a response id | passes | passes | passes |
| local function tool | passes | passes | passes |
| a stateless backend keeps no conversation state | passes | passes | passes |
| blank question gives 400 | passes | passes | passes |
| malformed payload gives 400 | passes | passes | passes |

The OpenAI tier, `AbstractOpenAiResponsesIT`, runs against OpenAI and only when `OPENAI_API_KEY` is set, since
OpenAI is the backend here that stores responses.

| Test | Spring AI | LangChain4j Boot | LangChain4j Quarkus |
|---|---|---|---|
| a follow up recalls an earlier turn through `previous_response_id` | skipped, unsupported | expected to pass | expected to pass |
| hosted `web_search` returns the answer | expected to pass | expected to pass | expected to pass |

The OpenAI tier was compiled and its skip behaviour verified. Its results against OpenAI remain to be recorded,
because the runs described here were made without an OpenAI key.

## Findings

1. Server side state is the decisive difference. Spring AI 2.1.0 sends `store: false` on every request and
   provides no option for `previous_response_id`, so the conversation history stays in the application, as with
   Chat Completions. LangChain4j sends `previous_response_id` on both runtimes, as the request log of the Quarkus
   stack shows (`"previous_response_id" : "resp_320779"`).
2. Ollama implements the Open Responses request and response format and function calling, and ignores
   `previous_response_id` silently: it answers a follow up as a new conversation and reports no error. The local
   test "a stateless backend keeps no conversation state" records this behaviour, which all three stacks share.
   An application that relies on server side state therefore needs a provider that stores responses.
3. Hosted tools are typed in Spring AI (`HostedTool.WebSearch`, `FileSearch`, `CodeInterpreter`,
   `ImageGeneration`, `Mcp`) and expressed as raw maps in LangChain4j (`serverTools`).
4. The Responses model is opt in on every stack. Spring AI defaults to `chat-completions` and Quarkus LangChain4j
   to `chat-completion`. Request logs confirm that the three stacks call `/v1/responses` with the settings above.
5. LangChain4j's AI services build generic request parameters, so the Responses parameters are set at the chat
   model level, where the application also drives the tool loop.

## Running

Docker is required for the local tier:

```bash
./mvnw verify
```

The OpenAI tier runs in addition when a key is present, at a small cost per run:

```bash
OPENAI_API_KEY=sk-... ./mvnw verify
```

To run one stack against OpenAI, start it and call it:

```bash
OPENAI_API_KEY=sk-... ./mvnw -pl responses-spring-ai-boot spring-boot:run
curl localhost:8080/chat -H 'Content-Type: application/json' -d '{"question":"What is the status of order 42?"}'
```

## Continuous integration

`.circleci/config.yml` pre-pulls the Ollama model and runs `./mvnw verify` on a machine executor. The OpenAI tier
is skipped there unless `OPENAI_API_KEY` is defined as a project environment variable.

## Version history

1.0.0: first version, three stacks and the two tier conformance suite.

1.0.1: Maven wrapper pinned to Maven 3.9.16, used by the README and the CircleCI pipeline. Unused AssertJ version management removed from the parent POM.
