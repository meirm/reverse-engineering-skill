# Extend Claude with skills

Create, manage, and share skills to extend Claude's capabilities in Claude Code. Includes custom commands and bundled skills.

**Official Documentation:** https://code.claude.com/docs/en/skills

---

## Overview

Skills extend what Claude can do. Create a `SKILL.md` file with instructions, and Claude adds it to its toolkit. Claude uses skills when relevant, or you can invoke one directly with `/skill-name`.

For built-in commands like `/help` and `/compact`, see interactive mode.

**Custom commands have been merged into skills.** A file at `.claude/commands/deploy.md` and a skill at `.claude/skills/deploy/SKILL.md` both create `/deploy` and work the same way. Your existing `.claude/commands/` files keep working.

Skills add optional features:
- A directory for supporting files
- Frontmatter to control whether you or Claude invokes them
- The ability for Claude to load them automatically when relevant

Claude Code skills follow the Agent Skills open standard, which works across multiple AI tools. Claude Code extends the standard with additional features like invocation control, subagent execution, and dynamic context injection.

---

## Bundled Skills

Bundled skills ship with Claude Code and are available in every session. Unlike built-in commands, bundled skills are prompt-based: they give Claude a detailed playbook and let it orchestrate the work using its tools.

- **`/simplify`**: Reviews your recently changed files for code reuse, quality, and efficiency issues, then fixes them. Run it after implementing a feature or bug fix to clean up your work. It spawns three review agents in parallel (code reuse, code quality, efficiency), aggregates their findings, and applies fixes.

- **`/batch <instruction>`**: Orchestrates large-scale changes across a codebase in parallel. Provide a description of the change and `/batch` researches the codebase, decomposes the work into 5 to 30 independent units, and presents a plan for your approval.

- **`/debug [description]`**: Troubleshoots your current Claude Code session by reading the session debug log.

- **`/loop [interval] <prompt>`**: Runs a prompt repeatedly on an interval while the session stays open. Useful for polling a deployment, babysitting a PR, or periodically re-running another skill.

- **`/claude-api`**: Loads Claude API reference material for your project's language (Python, TypeScript, Java, Go, Ruby, C#, PHP, or cURL) and Agent SDK reference for Python and TypeScript.

---

## Create Your First Skill

### Step 1: Create the skill directory

```bash
mkdir -p ~/.claude/skills/explain-code
```

### Step 2: Write SKILL.md

Every skill needs a `SKILL.md` file with two parts:
1. **YAML frontmatter** (between `---` markers) - tells Claude when to use the skill
2. **Markdown content** - instructions Claude follows when the skill is invoked

```yaml
---
name: explain-code
description: Explains code with visual diagrams and analogies. Use when explaining how code works, teaching about a codebase, or when the user asks "how does this work?"
---

When explaining code, always include:

1. **Start with an analogy**: Compare the code to something from everyday life
2. **Draw a diagram**: Use ASCII art to show the flow, structure, or relationships
3. **Walk through the code**: Explain step-by-step what happens
4. **Highlight a gotcha**: What's a common mistake or misconception?

Keep explanations conversational. For complex concepts, use multiple analogies.
```

### Step 3: Test the skill

**Let Claude invoke it automatically:**
```
How does this code work?
```

**Or invoke it directly:**
```
/explain-code src/auth/login.ts
```

---

## Where Skills Live

| Location | Path | Applies to |
|----------|------|------------|
| Enterprise | See managed settings | All users in your organization |
| Personal | `~/.claude/skills/<skill-name>/SKILL.md` | All your projects |
| Project | `.claude/skills/<skill-name>/SKILL.md` | This project only |
| Plugin | `<plugin>/skills/<skill-name>/SKILL.md` | Where plugin is enabled |

**Priority:** enterprise > personal > project

Plugin skills use a `plugin-name:skill-name` namespace, so they cannot conflict with other levels.

---

## Skill Directory Structure

Each skill is a directory with `SKILL.md` as the entrypoint:

```
my-skill/
├── SKILL.md           # Main instructions (required)
├── template.md        # Template for Claude to fill in
├── examples/
│   └── sample.md      # Example output showing expected format
└── scripts/
    └── validate.sh    # Script Claude can execute
```

The `SKILL.md` contains the main instructions and is required. Other files are optional.

---

## Frontmatter Reference

Configure skill behavior using YAML frontmatter fields:

```yaml
---
name: my-skill
description: What this skill does
disable-model-invocation: true
allowed-tools: Read, Grep
---
```

| Field | Required | Description |
|-------|----------|-------------|
| `name` | No | Display name for the skill. If omitted, uses the directory name. |
| `description` | Recommended | What the skill does and when to use it. Claude uses this to decide when to apply the skill. |
| `argument-hint` | No | Hint shown during autocomplete. Example: `[issue-number]` or `[filename] [format]`. |
| `disable-model-invocation` | No | Set to `true` to prevent Claude from automatically loading this skill. |
| `user-invocable` | No | Set to `false` to hide from the `/` menu. |
| `allowed-tools` | No | Tools Claude can use without asking permission when this skill is active. |
| `model` | No | Model to use when this skill is active. |
| `context` | No | Set to `fork` to run in a forked subagent context. |
| `agent` | No | Which subagent type to use when `context: fork` is set. |
| `hooks` | No | Hooks scoped to this skill's lifecycle. |

---

## String Substitutions

Skills support string substitution for dynamic values:

| Variable | Description |
|----------|-------------|
| `$ARGUMENTS` | All arguments passed when invoking the skill. |
| `$ARGUMENTS[N]` | Access a specific argument by 0-based index. |
| `$N` | Shorthand for `$ARGUMENTS[N]`. |
| `${CLAUDE_SESSION_ID}` | The current session ID. |
| `${CLAUDE_SKILL_DIR}` | The directory containing the skill's `SKILL.md` file. |

**Example:**
```yaml
---
name: session-logger
description: Log activity for this session
---

Log the following to logs/${CLAUDE_SESSION_ID}.log:

$ARGUMENTS
```

---

## Control Who Invokes a Skill

| Frontmatter | You can invoke | Claude can invoke |
|-------------|----------------|-------------------|
| (default) | Yes | Yes |
| `disable-model-invocation: true` | Yes | No |
| `user-invocable: false` | No | Yes |

---

## Pass Arguments to Skills

```yaml
---
name: fix-issue
description: Fix a GitHub issue
disable-model-invocation: true
---

Fix GitHub issue $ARGUMENTS following our coding standards.

1. Read the issue description
2. Understand the requirements
3. Implement the fix
4. Write tests
5. Create a commit
```

Running `/fix-issue 123` passes `123` as the argument.

---

## Advanced Patterns

### Run skills in a subagent

```yaml
---
name: deep-research
description: Research a topic thoroughly
context: fork
agent: Explore
---

Research $ARGUMENTS thoroughly:

1. Find relevant files using Glob and Grep
2. Read and analyze the code
3. Summarize findings with specific file references
```

### Inject dynamic context

The `!command` syntax runs shell commands before the skill content is sent to Claude:

```yaml
---
name: pr-summary
description: Summarize changes in a pull request
context: fork
agent: Explore
---

## Pull request context
- PR diff: !`gh pr diff`
- PR comments: !`gh pr view --comments`
- Changed files: !`gh pr diff --name-only`

## Your task
Summarize this pull request...
```

---

## Related Resources

- **Subagents**: Delegate tasks to specialized agents
- **Plugins**: Package and distribute skills with other extensions
- **Hooks**: Automate workflows around tool events
- **Memory**: Manage CLAUDE.md files for persistent context
- **Permissions**: Control tool and skill access

---

**For the latest updates and complete documentation, visit:** https://code.claude.com/docs/en/skills
