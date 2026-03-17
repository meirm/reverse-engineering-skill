# BDD Skills (Universal)

This directory contains **project-agnostic** BDD (behavior-driven development) and business-logic skills that can be used in any codebase.

> **📖 New?** See [QUICK_START.md](QUICK_START.md) for a concise command reference.

## Quick Start

Choose your workflow:

**For beginners** - Use commands (shortcuts):
```bash
/reverse-bl <feature>              # Extract business logic from code
/validate-bl <document>             # Validate BL against code
/gap-analysis <document>            # Find gaps and ambiguities
/refine-bl <document>               # Refine BL into deterministic rules
/derive-acceptance <document>       # Create acceptance criteria
/generate-tests <document>          # Generate tests from BL
```

**For comprehensive analysis** - Use the orchestrator agent:
```bash
# Ask: "Understand order creation from code and prepare it for implementation"
# The bdd-orchestrator will coordinate all skills automatically
```

**For advanced users** - Use skills directly:
```bash
# Invoke any skill by name
# Example: "Use reverse-engineering-business-logic on order creation"
```

## Tailoring for your project

Before using the skills in a new project, run:

- **Command:** `/tailor-bdd-skills-for-project`
- **Or:** Use the skill directly

That will guide you to create or edit **`BDD/project_config.yaml`** (or `.cursor/bdd_project_config.yaml` at repo root) with:

1. **`bl_output`** — Where to store business logic docs (e.g. `business_logic/`), and categories (endpoints, models, workflows, billing).
2. **`terminology`** — Domain terms and definitions so all BL docs and scenarios use consistent language.
3. **`entry_points`** — Where to find API endpoints, models, workflows, and billing logic in your code (paths and grep patterns).

Once the config exists, the other skills/commands use it for paths and terminology.

**Template:** Copy `project_config.yaml.example` to `project_config.yaml` and fill in your project’s paths and terms.

## Commands (Shortcuts)

Convenient shortcuts for common BDD workflows:

| Command | Purpose | Example |
|---------|---------|---------|
| `/reverse-bl` | Extract business logic from code | `/reverse-bl order creation` |
| `/validate-bl` | Validate BL against code | `/validate-bl business_logic/current/endpoints/order-creation.md` |
| `/gap-analysis` | Find gaps and ambiguities | `/gap-analysis payment processing` |
| `/refine-bl` | Refine BL into deterministic rules | `/refine-bl user registration` |
| `/derive-acceptance` | Create acceptance criteria | `/derive-acceptance order workflow` |
| `/generate-tests` | Generate tests from BL | `/generate-tests password reset` |

## Agents (Orchestrators)

### BDD Orchestrator

The **`bdd-orchestrator`** agent coordinates the full BDD workflow automatically:

```
User: "Understand order creation from code and prepare it for implementation"

Agent automatically:
1. Extracts business logic → order-creation.md
2. Validates against code → coverage report
3. Analyzes gaps → issues found
4. Refines BL → deterministic rules
5. Derives acceptance criteria → user stories
6. Generates tests → test suite
```

The orchestrator:
- Checks for project configuration
- Runs skills in sequence
- Confirms before each step
- Summarizes findings
- Offers to skip unnecessary steps
- Handles errors gracefully

## Skills (Underlying Capabilities)

All commands and agents use these underlying skills:

| Skill | Purpose |
|-------|--------|
| **tailor-bdd-skills-for-project** | Configure BL output dir, terminology, and code entry points. Run first in a new repo. |
| **reverse-engineering-business-logic** | Extract business logic from code into structured BL docs (11-section template). |
| **analyze-business-logic-gaps** | Find missing, vague, or contradictory rules in BL documents. |
| **validate-business-logic-against-code** | Map BL rules to code evidence; report implemented / partial / contradicted / not found. |
| **refine-business-logic-for-implementation** | Rewrite vague BL into deterministic, testable rules and state machines. |
| **derive-acceptance-criteria-from-business-logic** | Turn BL into Given/When/Then scenarios and acceptance criteria. |
| **generate-tests-from-business-logic** | Generate rule, scenario, state-transition, and billing tests from BL. |

## Conventions

- BL output is organized by **state**: `current`, `proposal`, `under_development`, `historical`.
- Under each state, use **categories**: `endpoints`, `models`, `workflows`, `billing` (or as defined in config).
- File names: **lowercase-with-hyphens.md** (e.g. `order-creation.md`).
- Keep a **glossary** and **index** per state; update them when adding or changing BL docs.

## Usage Examples

### Example 1: Extract and Validate Business Logic

```bash
# Extract business logic from code
/reverse-bl payment processing

# Output: business_logic/current/workflows/payment-processing.md

# Validate the extracted BL
/validate-bl payment processing

# Output: Validation report showing coverage and issues
```

### Example 2: Full Workflow with Commands

```bash
# 1. Extract
/reverse-bl user registration

# 2. Validate
/validate-bl user registration

# 3. Find gaps
/gap-analysis user registration

# 4. Refine
/refine-bl user registration

# 5. Create acceptance criteria
/derive-acceptance user registration

# 6. Generate tests
/generate-tests user registration
```

### Example 3: Using the Orchestrator Agent

```
User: "Understand order fulfillment from code and prepare it for sprint planning"

The bdd-orchestrator will:
✓ Check project configuration
✓ Extract business logic
✓ Validate against code
✓ Analyze gaps
✓ Refine into deterministic rules
✓ Derive acceptance criteria
✓ Ask if tests needed

Final output:
- BL document: business_logic/proposal/workflows/order-fulfillment.md
- Acceptance criteria: acceptance-criteria/order-fulfillment.md
- Validation report: reports/validation/order-fulfillment.md
- Gap analysis: reports/gaps/order-fulfillment.md
```

### Example 4: Quick Validation

```bash
# Validate existing BL document
/validate-bl business_logic/current/endpoints/cart-checkout.md

# Output shows:
# - Coverage: 85%
# - Issues: 2 rules contradicted, 1 not found
```

## Common Workflows

### Workflow: New Feature Analysis

Goal: Understand a new feature from code

```bash
/reverse-bl <feature>
/validate-bl <feature>
```

### Workflow: Sprint Preparation

Goal: Prepare BL for implementation

```bash
/reverse-bl <feature>
/validate-bl <feature>
/gap-analysis <feature>
/refine-bl <feature>
/derive-acceptance <feature>
```

### Workflow: Test Generation

Goal: Generate comprehensive tests

```bash
/generate-tests <feature>
```

### Workflow: Quality Check

Goal: Verify BL quality and completeness

```bash
/gap-analysis <document>
/refine-bl <document>
```

## Command vs Agent vs Skill

### Commands (`/command`)
- **Best for:** Single-step operations
- **Usage:** Quick, direct actions
- **Example:** `/reverse-bl order creation`
- **Pros:** Fast, simple, explicit
- **Cons:** Manual coordination for multi-step workflows

### Agents (orchestrator)
- **Best for:** Multi-step, comprehensive analysis
- **Usage:** Natural language requests
- **Example:** "Understand order creation from code and prepare it for implementation"
- **Pros:** Automatic coordination, smart decisions, interactive
- **Cons:** More interactive, less explicit control

### Skills (direct invocation)
- **Best for:** Advanced users, custom workflows
- **Usage:** "Use <skill-name> on <target>"
- **Example:** "Use reverse-engineering-business-logic on payment processing"
- **Pros:** Full control, composable
- **Cons:** Verbose, requires knowledge of skill names

## Methodology

See **BBD_logic.md** for the overall approach: reverse-engineering business logic from code, then using that BL to drive acceptance criteria, implementation, and tests.

## File Structure

```
.claude/
├── commands/              # User-facing shortcuts
│   ├── reverse-bl.md
│   ├── validate-bl.md
│   ├── gap-analysis.md
│   ├── refine-bl.md
│   ├── derive-acceptance.md
│   └── generate-tests.md
├── agents/                # Workflow orchestrators
│   └── bdd-orchestrator.md
└── skills/                # Underlying capabilities
    ├── reverse-engineering-business-logic/
    ├── validate-business-logic-against-code/
    ├── analyze-business-logic-gaps/
    ├── refine-business-logic-for-implementation/
    ├── derive-acceptance-criteria-from-business-logic/
    ├── generate-tests-from-business-logic/
    └── tailor-bdd-skills-for-project/
```

## Documentation

- **Commands:** `.claude/commands/*.md` - Quick reference for each command
- **Agents:** `.claude/agents/*.md` - Orchestrator workflows and behavior
- **Skills:** `.claude/skills/*/SKILL.md` - Detailed skill documentation
- **Examples:** `.claude/skills/reverse-engineering-business-logic/examples/` - Worked examples
