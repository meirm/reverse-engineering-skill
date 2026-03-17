Build with Claude

Features overview

Claude's API surface is organized into five areas:

- **Model capabilities:** Control how Claude reasons and formats responses.
- **Tools:** Let Claude take actions on the web or in your environment.
- **Tool infrastructure:** Handles discovery and orchestration at scale.
- **Context management:** Keeps long-running sessions efficient.
- **Files and assets:** Manage the documents and data you provide to Claude.

If you're new, start with [model capabilities](https://platform.claude.com/docs/en/build-with-claude/overview#model-capabilities) and [tools](https://platform.claude.com/docs/en/build-with-claude/overview#tools). Return to the other sections when you're ready to optimize cost, latency, or scale.

## Model capabilities

Ways to steer Claude and Claude's direct outputs, including response format, reasoning depth, and input modalities.

| Feature | Description | Availability |
| --- | --- | --- |
| [1M token context window](https://platform.claude.com/docs/en/build-with-claude/context-windows#1m-token-context-window) | An extended context window that allows you to process much larger documents, maintain longer conversations, and work with more extensive codebases. | Claude API (Beta)<br>Amazon Bedrock (Beta)<br>Google Cloud's Vertex AI (Beta)<br>Microsoft Foundry (Beta) |
| [Adaptive thinking](https://platform.claude.com/docs/en/build-with-claude/adaptive-thinking) | Let Claude dynamically decide when and how much to think. The recommended thinking mode for Opus 4.6. Use the effort parameter to control thinking depth. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |
| [Batch processing](https://platform.claude.com/docs/en/build-with-claude/batch-processing) | Process large volumes of requests asynchronously for cost savings. Send batches with a large number of queries per batch. Batch API calls cost 50% less than standard API calls. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI |
| [Citations](https://platform.claude.com/docs/en/build-with-claude/citations) | Ground Claude's responses in source documents. With Citations, Claude can provide detailed references to the exact sentences and passages it uses to generate responses, leading to more verifiable, trustworthy outputs. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |
| [Data residency](https://platform.claude.com/docs/en/build-with-claude/data-residency) | Control where model inference runs using geographic controls. Specify `"global"` or `"us"` routing per request via the `inference_geo` parameter. | Claude API |
| [Effort](https://platform.claude.com/docs/en/build-with-claude/effort) | Control how many tokens Claude uses when responding with the effort parameter, trading off between response thoroughness and token efficiency. Supported on Opus 4.6 and Opus 4.5. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |
| [Extended thinking](https://platform.claude.com/docs/en/build-with-claude/extended-thinking) | Enhanced reasoning capabilities for complex tasks, providing transparency into Claude's step-by-step thought process before delivering its final answer. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |
| [PDF support](https://platform.claude.com/docs/en/build-with-claude/pdf-support) | Process and analyze text and visual content from PDF documents. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |
| [Search results](https://platform.claude.com/docs/en/build-with-claude/search-results) | Enable natural citations for RAG applications by providing search results with proper source attribution. Achieve web search-quality citations for custom knowledge bases and tools. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |
| [Structured outputs](https://platform.claude.com/docs/en/build-with-claude/structured-outputs) | Guarantee schema conformance with two approaches: JSON outputs for structured data responses, and strict tool use for validated tool inputs. | Claude API<br>Amazon Bedrock<br>Microsoft Foundry (Beta) |

## Tools

Built-in tools that Claude invokes via `tool_use`. Server-side tools are run by the platform; client-side tools are implemented and executed by you.

### Server-side tools

| Feature | Description | Availability |
| --- | --- | --- |
| [Code execution](https://platform.claude.com/docs/en/agents-and-tools/tool-use/code-execution-tool) | Run code in a sandboxed environment for advanced data analysis, calculations, and file processing. Free when used with web search or web fetch. | Claude API<br>Microsoft Foundry (Beta) |
| [Web fetch](https://platform.claude.com/docs/en/agents-and-tools/tool-use/web-fetch-tool) | Retrieve full content from specified web pages and PDF documents for in-depth analysis. | Claude API<br>Microsoft Foundry (Beta) |
| [Web search](https://platform.claude.com/docs/en/agents-and-tools/tool-use/web-search-tool) | Augment Claude's comprehensive knowledge with current, real-world data from across the web. | Claude API<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |

### Client-side tools

| Feature | Description | Availability |
| --- | --- | --- |
| [Bash](https://platform.claude.com/docs/en/agents-and-tools/tool-use/bash-tool) | Execute bash commands and scripts to interact with the system shell and perform command-line operations. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |
| [Computer use](https://platform.claude.com/docs/en/agents-and-tools/tool-use/computer-use-tool) | Control computer interfaces by taking screenshots and issuing mouse and keyboard commands. | Claude API (Beta)<br>Amazon Bedrock (Beta)<br>Google Cloud's Vertex AI (Beta)<br>Microsoft Foundry (Beta) |
| [Memory](https://platform.claude.com/docs/en/agents-and-tools/tool-use/memory-tool) | Enable Claude to store and retrieve information across conversations. Build knowledge bases over time, maintain project context, and learn from past interactions. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |
| [Text editor](https://platform.claude.com/docs/en/agents-and-tools/tool-use/text-editor-tool) | Create and edit text files with a built-in text editor interface for file manipulation tasks. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |

## Tool infrastructure

Infrastructure that supports discovering, orchestrating, and scaling tool use.

| Feature | Description | Availability |
| --- | --- | --- |
| [Agent Skills](https://platform.claude.com/docs/en/agents-and-tools/agent-skills/overview) | Extend Claude's capabilities with Skills. Use pre-built Skills (PowerPoint, Excel, Word, PDF) or create custom Skills with instructions and scripts. Skills use progressive disclosure to efficiently manage context. | Claude API (Beta)<br>Microsoft Foundry (Beta) |
| [Fine-grained tool streaming](https://platform.claude.com/docs/en/agents-and-tools/tool-use/fine-grained-tool-streaming) | Stream tool use parameters without buffering/JSON validation, reducing latency for receiving large parameters. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |
| [MCP connector](https://platform.claude.com/docs/en/agents-and-tools/mcp-connector) | Connect to remote [MCP](https://platform.claude.com/docs/en/mcp) servers directly from the Messages API without a separate MCP client. | Claude API (Beta)<br>Microsoft Foundry (Beta) |
| [Programmatic tool calling](https://platform.claude.com/docs/en/agents-and-tools/tool-use/programmatic-tool-calling) | Enable Claude to call your tools programmatically from within code execution containers, reducing latency and token consumption for multi-tool workflows. | Claude API<br>Microsoft Foundry (Beta) |
| [Tool search](https://platform.claude.com/docs/en/agents-and-tools/tool-use/tool-search-tool) | Scale to thousands of tools by dynamically discovering and loading tools on-demand using regex-based search, optimizing context usage and improving tool selection accuracy. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |

## Context management

Infrastructure for controlling and optimizing Claude's context window.

| Feature | Description | Availability |
| --- | --- | --- |
| [Compaction](https://platform.claude.com/docs/en/build-with-claude/compaction) | Server-side context summarization for long-running conversations. When context approaches the window limit, the API automatically summarizes earlier parts of the conversation. Supported on Opus 4.6 and Haiku 4.5. | Claude API (Beta)<br>Amazon Bedrock (Beta)<br>Google Cloud's Vertex AI (Beta)<br>Microsoft Foundry (Beta) |
| [Context editing](https://platform.claude.com/docs/en/build-with-claude/context-editing) | Automatically manage conversation context with configurable strategies. Supports clearing tool results when approaching token limits and managing thinking blocks in extended thinking conversations. | Claude API (Beta)<br>Amazon Bedrock (Beta)<br>Google Cloud's Vertex AI (Beta)<br>Microsoft Foundry (Beta) |
| [Automatic prompt caching](https://platform.claude.com/docs/en/build-with-claude/prompt-caching#automatic-caching) | Simplify prompt caching to a single API parameter. The system automatically caches the last cacheable block in your request, moving the cache point forward as conversations grow. | Claude API<br>Microsoft Foundry (Beta) |
| [Prompt caching (5m)](https://platform.claude.com/docs/en/build-with-claude/prompt-caching) | Provide Claude with more background knowledge and example outputs to reduce costs and latency. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |
| [Prompt caching (1hr)](https://platform.claude.com/docs/en/build-with-claude/prompt-caching#1-hour-cache-duration) | Extended 1-hour cache duration for less frequently accessed but important context, complementing the standard 5-minute cache. | Claude API<br>Microsoft Foundry (Beta) |
| [Token counting](https://platform.claude.com/docs/en/api/messages-count-tokens) | Token counting enables you to determine the number of tokens in a message before sending it to Claude, helping you make informed decisions about your prompts and usage. | Claude API<br>Amazon Bedrock<br>Google Cloud's Vertex AI<br>Microsoft Foundry (Beta) |

## Files and assets

Manage files and assets for use with Claude.

| Feature | Description | Availability |
| --- | --- | --- |
| [Files API](https://platform.claude.com/docs/en/build-with-claude/files) | Upload and manage files to use with Claude without re-uploading content with each request. Supports PDFs, images, and text files. | Claude API (Beta)<br>Microsoft Foundry (Beta) |

Was this page helpful?