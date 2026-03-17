Agent Skills

Using Skills with the API

Copy page

Agent Skills extend Claude's capabilities through organized folders of instructions, scripts, and resources. This guide shows you how to use both pre-built and custom Skills with the Claude API.

For complete API reference including request/response schemas and all parameters, see:

- [Skill Management API Reference](https://platform.claude.com/docs/en/api/skills/list-skills) - CRUD operations for Skills
- [Skill Versions API Reference](https://platform.claude.com/docs/en/api/skills/list-skill-versions) - Version management

This feature is in beta and is **not** eligible for [Zero Data Retention (ZDR)](https://platform.claude.com/docs/en/build-with-claude/zero-data-retention). Beta features are excluded from ZDR.

## Quick Links

[Get started with Agent Skills\\
\\
Create your first Skill](https://platform.claude.com/docs/en/agents-and-tools/agent-skills/quickstart) [Create Custom Skills\\
\\
Best practices for authoring Skills](https://platform.claude.com/docs/en/agents-and-tools/agent-skills/best-practices)

## Overview

For a deep dive into the architecture and real-world applications of Agent Skills, read the engineering blog post: [Equipping agents for the real world with Agent Skills](https://www.anthropic.com/engineering/equipping-agents-for-the-real-world-with-agent-skills).

Skills integrate with the Messages API through the code execution tool. Whether using pre-built Skills managed by Anthropic or custom Skills you've uploaded, the integration shape is identical: both require code execution and use the same `container` structure.

### Using Skills

Skills integrate identically in the Messages API regardless of source. You specify Skills in the `container` parameter with a `skill_id`, `type`, and optional `version`, and they execute in the code execution environment.

**You can use Skills from two sources:**

| Aspect | Anthropic Skills | Custom Skills |
| --- | --- | --- |
| **Type value** | `anthropic` | `custom` |
| **Skill IDs** | Short names: `pptx`, `xlsx`, `docx`, `pdf` | Generated: `skill_01AbCdEfGhIjKlMnOpQrStUv` |
| **Version format** | Date-based: `20251013` or `latest` | Epoch timestamp: `1759178010641129` or `latest` |
| **Management** | Pre-built and maintained by Anthropic | Upload and manage via [Skills API](https://platform.claude.com/docs/en/api/skills/create-skill) |
| **Availability** | Available to all users | Private to your workspace |

Both skill sources are returned by the [List Skills endpoint](https://platform.claude.com/docs/en/api/skills/list-skills) (use the `source` parameter to filter). The integration shape and execution environment are identical. The only difference is where the Skills come from and how they're managed.

### Prerequisites

To use Skills, you need:

1. **Claude API key** from the [Console](https://platform.claude.com/settings/keys)
2. **Beta headers:**
   - `code-execution-2025-08-25` - Enables code execution (required for Skills)
   - `skills-2025-10-02` - Enables Skills API
   - `files-api-2025-04-14` - For uploading/downloading files to/from container
3. **Code execution tool** enabled in your requests

* * *

## Using Skills in Messages

### Container Parameter

Skills are specified using the `container` parameter in the Messages API. You can include up to 8 Skills per request.

The structure is identical for both Anthropic and custom Skills. Specify the required `type` and `skill_id`, and optionally include `version` to pin to a specific version:

Shell

```
curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: code-execution-2025-08-25,skills-2025-10-02" \
  -H "content-type: application/json" \
  -d '{
    "model": "claude-opus-4-6",
    "max_tokens": 4096,
    "container": {
      "skills": [\
        {\
          "type": "anthropic",\
          "skill_id": "pptx",\
          "version": "latest"\
        }\
      ]
    },
    "messages": [{\
      "role": "user",\
      "content": "Create a presentation about renewable energy"\
    }],
    "tools": [{\
      "type": "code_execution_20250825",\
      "name": "code_execution"\
    }]
  }'
```

### Downloading Generated Files

When Skills create documents (Excel, PowerPoint, PDF, Word), they return `file_id` attributes in the response. You must use the Files API to download these files.

**How it works:**

1. Skills create files during code execution
2. Response includes `file_id` for each created file
3. Use Files API to download the actual file content
4. Save locally or process as needed

**Example: Creating and downloading an Excel file**

Shell

Shell

Python

Python

TypeScript

TypeScript

C#

C#

Go

Go

Java

Java

PHP

PHP

Ruby

Ruby

Shell

```
# Step 1: Use a Skill to create a file
RESPONSE=$(curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: code-execution-2025-08-25,skills-2025-10-02" \
  -H "content-type: application/json" \
  -d '{
    "model": "claude-opus-4-6",
    "max_tokens": 4096,
    "container": {
      "skills": [\
        {"type": "anthropic", "skill_id": "xlsx", "version": "latest"}\
      ]
    },
    "messages": [{\
      "role": "user",\
      "content": "Create an Excel file with a simple budget spreadsheet"\
    }],
    "tools": [{\
      "type": "code_execution_20250825",\
      "name": "code_execution"\
    }]
  }')

# Step 2: Extract file_id from response (using jq)
FILE_ID=$(echo "$RESPONSE" | jq -r '.content[] | select(.type=="bash_code_execution_tool_result") | .content | select(.type=="bash_code_execution_result") | .content[] | select(.file_id) | .file_id')

# Step 3: Get filename from metadata
FILENAME=$(curl "https://api.anthropic.com/v1/files/$FILE_ID" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: files-api-2025-04-14" | jq -r '.filename')

# Step 4: Download the file using Files API
curl "https://api.anthropic.com/v1/files/$FILE_ID/content" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: files-api-2025-04-14" \
  --output "$FILENAME"

echo "Downloaded: $FILENAME"
```

**Additional Files API operations:**

Shell

```
# Get file metadata
curl "https://api.anthropic.com/v1/files/$FILE_ID" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: files-api-2025-04-14"

# List all files
curl "https://api.anthropic.com/v1/files" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: files-api-2025-04-14"

# Delete a file
curl -X DELETE "https://api.anthropic.com/v1/files/$FILE_ID" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: files-api-2025-04-14"
```

For complete details on the Files API, see the [Files API documentation](https://platform.claude.com/docs/en/api/files-content).

### Multi-Turn Conversations

Reuse the same container across multiple messages by specifying the container ID:

Python

```
# First request creates container
response1 = client.beta.messages.create(
    model="claude-opus-4-6",
    max_tokens=4096,
    betas=["code-execution-2025-08-25", "skills-2025-10-02"],
    container={
        "skills": [{"type": "anthropic", "skill_id": "xlsx", "version": "latest"}]
    },
    messages=[{"role": "user", "content": "Analyze this sales data"}],
    tools=[{"type": "code_execution_20250825", "name": "code_execution"}],
)

# Continue conversation with same container
messages = [\
    {"role": "user", "content": "Analyze this sales data"},\
    {"role": "assistant", "content": response1.content},\
    {"role": "user", "content": "What was the total revenue?"},\
]

response2 = client.beta.messages.create(
    model="claude-opus-4-6",
    max_tokens=4096,
    betas=["code-execution-2025-08-25", "skills-2025-10-02"],
    container={
        "id": response1.container.id,  # Reuse container
        "skills": [{"type": "anthropic", "skill_id": "xlsx", "version": "latest"}],
    },
    messages=messages,
    tools=[{"type": "code_execution_20250825", "name": "code_execution"}],
)
```

### Long-Running Operations

Skills may perform operations that require multiple turns. Handle `pause_turn` stop reasons:

Shell

```
# Initial request
RESPONSE=$(curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: code-execution-2025-08-25,skills-2025-10-02" \
  -H "content-type: application/json" \
  -d '{
    "model": "claude-opus-4-6",
    "max_tokens": 4096,
    "container": {
      "skills": [\
        {\
          "type": "custom",\
          "skill_id": "skill_01AbCdEfGhIjKlMnOpQrStUv",\
          "version": "latest"\
        }\
      ]
    },
    "messages": [{\
      "role": "user",\
      "content": "Process this large dataset"\
    }],
    "tools": [{\
      "type": "code_execution_20250825",\
      "name": "code_execution"\
    }]
  }')

# Check stop_reason and handle pause_turn in a loop
STOP_REASON=$(echo "$RESPONSE" | jq -r '.stop_reason')
CONTAINER_ID=$(echo "$RESPONSE" | jq -r '.container.id')

while [ "$STOP_REASON" = "pause_turn" ]; do
  # Continue with same container
  RESPONSE=$(curl https://api.anthropic.com/v1/messages \
    -H "x-api-key: $ANTHROPIC_API_KEY" \
    -H "anthropic-version: 2023-06-01" \
    -H "anthropic-beta: code-execution-2025-08-25,skills-2025-10-02" \
    -H "content-type: application/json" \
    -d "{
      \\\"model\\\": \\\"claude-opus-4-6\\",
      \\\"max_tokens\\\": 4096,
      \\\"container\\\": {
        \\\"id\\\": \\\"$CONTAINER_ID\\",
        \\\"skills\\\": [{\
          \\\"type\\\": \\\"custom\\\",\
          \\\"skill_id\\\": \\\"skill_01AbCdEfGhIjKlMnOpQrStUv\\\",\
          \\\"version\\\": \\\"latest\\\"\
        }]
      },
      \\\"messages\\\": [/* include conversation history */],
      \\\"tools\\\": [{\
        \\\"type\\\": \\\"code_execution_20250825\\\",\
        \\\"name\\\": \\\"code_execution\\\"\
      }]
    }")

  STOP_REASON=$(echo "$RESPONSE" | jq -r '.stop_reason')
done
```

The response may include a `pause_turn` stop reason, which indicates that the API paused a long-running Skill operation. You can provide the response back as-is in a subsequent request to let Claude continue its turn, or modify the content if you wish to interrupt the conversation and provide additional guidance.

### Using Multiple Skills

Combine multiple Skills in a single request to handle complex workflows:

Shell

```
curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: code-execution-2025-08-25,skills-2025-10-02" \
  -H "content-type: application/json" \
  -d '{
    "model": "claude-opus-4-6",
    "max_tokens": 4096,
    "container": {
      "skills": [\
        {\
          "type": "anthropic",\
          "skill_id": "xlsx",\
          "version": "latest"\
        },\
        {\
          "type": "anthropic",\
          "skill_id": "pptx",\
          "version": "latest"\
        },\
        {\
          "type": "custom",\
          "skill_id": "skill_01AbCdEfGhIjKlMnOpQrStUv",\
          "version": "latest"\
        }\
      ]
    },
    "messages": [{\
      "role": "user",\
      "content": "Analyze sales data and create a presentation"\
    }],
    "tools": [{\
      "type": "code_execution_20250825",\
      "name": "code_execution"\
    }]
  }'
```

* * *

## Managing Custom Skills

### Creating a Skill

Upload your custom Skill to make it available in your workspace. You can upload using either a directory path or individual file objects.

Shell

```
curl -X POST "https://api.anthropic.com/v1/skills" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: skills-2025-10-02" \
  -F "display_title=Financial Analysis" \
  -F "files[]=@financial_skill/SKILL.md;filename=financial_skill/SKILL.md" \
  -F "files[]=@financial_skill/analyze.py;filename=financial_skill/analyze.py"
```

**Requirements:**

- Must include a SKILL.md file at the top level
- All files must specify a common root directory in their paths
- Total upload size must be under 8MB
- YAML frontmatter requirements:
  - `name`: Maximum 64 characters, lowercase letters/numbers/hyphens only, no XML tags, no reserved words ("anthropic", "claude")
  - `description`: Maximum 1024 characters, non-empty, no XML tags

For complete request/response schemas, see the [Create Skill API reference](https://platform.claude.com/docs/en/api/skills/create-skill).

### Listing Skills

Retrieve all Skills available to your workspace, including both Anthropic pre-built Skills and your custom Skills. Use the `source` parameter to filter by skill type:

Shell

```
# List all Skills
curl "https://api.anthropic.com/v1/skills" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: skills-2025-10-02"

# List only custom Skills
curl "https://api.anthropic.com/v1/skills?source=custom" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: skills-2025-10-02"
```

See the [List Skills API reference](https://platform.claude.com/docs/en/api/skills/list-skills) for pagination and filtering options.

### Retrieving a Skill

Get details about a specific Skill:

Shell

```
curl "https://api.anthropic.com/v1/skills/skill_01AbCdEfGhIjKlMnOpQrStUv" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: skills-2025-10-02"
```

### Deleting a Skill

To delete a Skill, you must first delete all its versions:

Shell

```
# Delete all versions first, then delete the Skill
curl -X DELETE "https://api.anthropic.com/v1/skills/skill_01AbCdEfGhIjKlMnOpQrStUv" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: skills-2025-10-02"
```

Attempting to delete a Skill with existing versions returns a 400 error.

### Versioning

Skills support versioning to manage updates safely:

**Anthropic-Managed Skills:**

- Versions use date format: `20251013`
- New versions released as updates are made
- Specify exact versions for stability

**Custom Skills:**

- Auto-generated epoch timestamps: `1759178010641129`
- Use `"latest"` to always get the most recent version
- Create new versions when updating Skill files

Shell

```
# Create a new version
NEW_VERSION=$(curl -X POST "https://api.anthropic.com/v1/skills/skill_01AbCdEfGhIjKlMnOpQrStUv/versions" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: skills-2025-10-02" \
  -F "files[]=@updated_skill/SKILL.md;filename=updated_skill/SKILL.md")

VERSION_NUMBER=$(echo "$NEW_VERSION" | jq -r '.version')

# Use specific version
curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: code-execution-2025-08-25,skills-2025-10-02" \
  -H "content-type: application/json" \
  -d "{
    \\\"model\\\": \\\"claude-opus-4-6\\",
    \\\"max_tokens\\\": 4096,
    \\\"container\\\": {
      \\\"skills\\\": [{\
        \\\"type\\\": \\\"custom\\",\
        \\\"skill_id\\\": \\\"skill_01AbCdEfGhIjKlMnOpQrStUv\\",\
        \\\"version\\\": \\\"$VERSION_NUMBER\\\"\
      }]
    },
    \\\"messages\\\": [{\\\"role\\\": \\\"user\\\", \\\"content\\\": \\\"Use updated Skill\\\"}],
    \\\"tools\\\": [{\\\"type\\\": \\\"code_execution_20250825\\\", \\\"name\\\": \\\"code_execution\\\"}]
  }"

# Use latest version
curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: code-execution-2025-08-25,skills-2025-10-02" \
  -H "content-type: application/json" \
  -d '{
    "model": "claude-opus-4-6",
    "max_tokens": 4096,
    "container": {
      "skills": [{\
        "type": "custom",\
        "skill_id": "skill_01AbCdEfGhIjKlMnOpQrStUv",\
        "version": "latest"\
      }]
    },
    "messages": [{"role": "user", "content": "Use latest Skill version"}],
    "tools": [{"type": "code_execution_20250825", "name": "code_execution"}]
  }'
```

See the [Create Skill Version API reference](https://platform.claude.com/docs/en/api/skills/create-skill-version) for complete details.

* * *

## How Skills Are Loaded

When you specify Skills in a container:

1. **Metadata Discovery:** Claude sees metadata for each Skill (name, description) in the system prompt
2. **File Loading:** Skill files are copied into the container at `/skills/{directory}/`
3. **Automatic Use:** Claude automatically loads and uses Skills when relevant to your request
4. **Composition:** Multiple Skills compose together for complex workflows

The progressive disclosure architecture ensures efficient context usage: Claude only loads full Skill instructions when needed.

* * *

## Use Cases

### Organizational Skills

**Brand & Communications**

- Apply company-specific formatting (colors, fonts, layouts) to documents
- Generate communications following organizational templates
- Ensure consistent brand guidelines across all outputs

**Project Management**

- Structure notes with company-specific formats (OKRs, decision logs)
- Generate tasks following team conventions
- Create standardized meeting recaps and status updates

**Business Operations**

- Create company-standard reports, proposals, and analyses
- Execute company-specific analytical procedures
- Generate financial models following organizational templates

### Personal Skills

**Content Creation**

- Custom document templates
- Specialized formatting and styling
- Domain-specific content generation

**Data Analysis**

- Custom data processing pipelines
- Specialized visualization templates
- Industry-specific analytical methods

**Development & Automation**

- Code generation templates
- Testing frameworks
- Deployment workflows

### Example: Financial Modeling

Combine Excel and custom DCF analysis Skills:

Shell

```
# Create custom DCF analysis Skill
DCF_SKILL=$(curl -X POST "https://api.anthropic.com/v1/skills" \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: skills-2025-10-02" \
  -F "display_title=DCF Analysis" \
  -F "files[]=@dcf_skill/SKILL.md;filename=dcf_skill/SKILL.md")

DCF_SKILL_ID=$(echo "$DCF_SKILL" | jq -r '.id')

# Use with Excel to create financial model
curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: code-execution-2025-08-25,skills-2025-10-02" \
  -H "content-type: application/json" \
  -d "{
    \\\"model\\\": \\\"claude-opus-4-6\\",
    \\\"max_tokens\\\": 4096,
    \\\"container\\\": {
      \\\"skills\\\": [\
        {\
          \\\"type\\\": \\\"anthropic\\",\
          \\\"skill_id\\\": \\\"xlsx\\",\
          \\\"version\\\": \\\"latest\\\"\
        },\
        {\
          \\\"type\\\": \\\"custom\\",\
          \\\"skill_id\\\": \\\"$DCF_SKILL_ID\\",\
          \\\"version\\\": \\\"latest\\\"\
        }\
      ]
    },
    \\\"messages\\\": [{\
      \\\"role\\\": \\\"user\\\",\
      \\\"content\\\": \\\"Build a DCF valuation model for a SaaS company with the attached financials\\\"\
    }],
    \\\"tools\\\": [{\
      \\\"type\\\": \\\"code_execution_20250825\\",\
      \\\"name\\\": \\\"code_execution\\\"\
    }]
  }"
```

* * *

## Limits and Constraints

### Request Limits

- **Maximum Skills per request:** 8
- **Maximum Skill upload size:** 8MB (all files combined)
- **YAML frontmatter requirements:**
  - `name`: Maximum 64 characters, lowercase letters/numbers/hyphens only, no XML tags, no reserved words
  - `description`: Maximum 1024 characters, non-empty, no XML tags

### Environment Constraints

Skills run in the code execution container with these limitations:

- **No network access** - Cannot make external API calls
- **No runtime package installation** - Only pre-installed packages available
- **Isolated environment** - Each request gets a fresh container

See the [code execution tool documentation](https://platform.claude.com/docs/en/agents-and-tools/tool-use/code-execution-tool) for available packages.

* * *

## Best Practices

### When to Use Multiple Skills

Combine Skills when tasks involve multiple document types or domains:

**Good use cases:**

- Data analysis (Excel) + presentation creation (PowerPoint)
- Report generation (Word) + export to PDF
- Custom domain logic + document generation

**Avoid:**

- Including unused Skills (impacts performance)

### Version Management Strategy

**For production:**

```
# Pin to specific versions for stability
container = {
    "skills": [\
        {\
            "type": "custom",\
            "skill_id": "skill_01AbCdEfGhIjKlMnOpQrStUv",\
            "version": "1759178010641129",  # Specific version\
        }\
    ]
}
```

**For development:**

```
# Use latest for active development
container = {
    "skills": [\
        {\
            "type": "custom",\
            "skill_id": "skill_01AbCdEfGhIjKlMnOpQrStUv",\
            "version": "latest",  # Always get newest\
        }\
    ]
}
```

### Prompt Caching Considerations

When using prompt caching, note that changing the Skills list in your container breaks the cache:

Shell

```
# First request creates cache
curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: code-execution-2025-08-25,skills-2025-10-02,prompt-caching-2024-07-31" \
  -H "content-type: application/json" \
  -d '{
    "model": "claude-opus-4-6",
    "max_tokens": 4096,
    "container": {
      "skills": [\
        {"type": "anthropic", "skill_id": "xlsx", "version": "latest"}\
      ]
    },
    "messages": [{"role": "user", "content": "Analyze sales data"}],
    "tools": [{"type": "code_execution_20250825", "name": "code_execution"}]
  }'

# Adding/removing Skills breaks cache
curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "anthropic-beta: code-execution-2025-08-25,skills-2025-10-02,prompt-caching-2024-07-31" \
  -H "content-type: application/json" \
  -d '{
    "model": "claude-opus-4-6",
    "max_tokens": 4096,
    "container": {
      "skills": [\
        {"type": "anthropic", "skill_id": "xlsx", "version": "latest"},\
        {"type": "anthropic", "skill_id": "pptx", "version": "latest"}\
      ]
    },
    "messages": [{"role": "user", "content": "Create a presentation"}],
    "tools": [{"type": "code_execution_20250825", "name": "code_execution"}]
  }'
```

For best caching performance, keep your Skills list consistent across requests.

### Error Handling

Handle Skill-related errors gracefully:

Python

```
import anthropic

client = anthropic.Anthropic()

try:
    response = client.beta.messages.create(
        model="claude-opus-4-6",
        max_tokens=4096,
        betas=["code-execution-2025-08-25", "skills-2025-10-02"],
        container={
            "skills": [\
                {\
                    "type": "custom",\
                    "skill_id": "skill_01AbCdEfGhIjKlMnOpQrStUv",\
                    "version": "latest",\
                }\
            ]
        },
        messages=[{"role": "user", "content": "Process data"}],
        tools=[{"type": "code_execution_20250825", "name": "code_execution"}],
    )
except anthropic.BadRequestError as e:
    if "skill" in str(e):
        print(f"Skill error: {e}")
        # Handle skill-specific errors
    else:
        raise
```

* * *

## Next Steps

[API Reference\\
\\
Complete API reference with all endpoints](https://platform.claude.com/docs/en/api/skills/create-skill) [Authoring Guide\\
\\
Best practices for writing effective Skills](https://platform.claude.com/docs/en/agents-and-tools/agent-skills/best-practices) [Code Execution Tool\\
\\
Learn about the code execution environment](https://platform.claude.com/docs/en/agents-and-tools/tool-use/code-execution-tool)

Was this page helpful?

Ask Docs

[Chat avatar](https://platform.claude.com/docs/images/book-icon-light.svg)

a.claude.ai

# a.claude.ai is blocked

**a.claude.ai** refused to connect.

ERR_BLOCKED_BY_RESPONSE

**a.claude.ai** refused to connect.