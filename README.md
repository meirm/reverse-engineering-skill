# BDD Skills for pi (Universal)

This is a [pi](https://github.com/earendil-works/pi-coding-agent) package containing **project-agnostic** BDD (behavior-driven development) and business-logic skills that can be used in any codebase.

> **📖 New?** See [QUICK_START.md](QUICK_START.md) for a concise command reference.

## Installation

```bash
# From git
pi install git:github.com/meirm/reverse-engineering-skill

# From a local checkout
pi install ./reverse-engineering-skill

# Try it once without installing
pi -e git:github.com/meirm/reverse-engineering-skill
```

Add `--local` to install into the current project (`.pi/settings.json`) instead of your personal config.

After installation, the skills are advertised to pi automatically (name + description); their full instructions load only when a task matches. Prompt templates appear as `/` commands.

## Quick Start

Choose your workflow:

**For beginners** - Use prompt commands (shortcuts):
```bash
/reverse-bl <feature>              # Extract business logic from code
/validate-bl <document>             # Validate BL against code
/gap-analysis <document>            # Find gaps and ambiguities
/refine-bl <document>               # Refine BL into deterministic rules
/derive-acceptance <document>       # Create acceptance criteria
/generate-tests <document>          # Generate tests from BL
```

**For comprehensive analysis** - Use the orchestrator skill:
```bash
/skill:bdd-orchestrator order creation
# Or just ask: "Understand order creation from code and prepare it for implementation"
# The bdd-orchestrator skill coordinates all skills automatically
```

**For advanced users** - Use skills directly:
```bash
/skill:reverse-engineering-business-logic payment processing
/skill:analyze-business-logic-gaps business_logic/current/workflows/payment-flow.md
```

## Tailoring for your project

Before using the skills in a new project, run:

```bash
/skill:tailor-bdd-skills-for-project
```

That will guide you to create or edit **`BDD/project_config.yaml`** with:

1. **`bl_output`** — Where to store business logic docs (e.g. `business_logic/`), and categories (endpoints, models, workflows, billing).
2. **`terminology`** — Domain terms and definitions so all BL docs and scenarios use consistent language.
3. **`entry_points`** — Where to find API endpoints, models, workflows, and billing logic in your code (paths and grep patterns).

Once the config exists, the other skills use it for paths and terminology.

## Prompt Commands (Shortcuts)

Convenient shortcuts for common BDD workflows:

| Command | Purpose | Example |
|---------|---------|---------|
| `/reverse-bl` | Extract business logic from code | `/reverse-bl order creation` |
| `/validate-bl` | Validate BL against code | `/validate-bl business_logic/current/endpoints/order-creation.md` |
| `/gap-analysis` | Find gaps and ambiguities | `/gap-analysis payment processing` |
| `/refine-bl` | Refine BL into deterministic rules | `/refine-bl user registration` |
| `/derive-acceptance` | Create acceptance criteria | `/derive-acceptance order workflow` |
| `/generate-tests` | Generate tests from BL | `/generate-tests password reset` |

Each command loads the corresponding skill (below) and applies it to your arguments.

## The BDD Orchestrator

The **`bdd-orchestrator`** skill coordinates the full BDD workflow automatically:

```
User: "Understand order creation from code and prepare it for implementation"

The orchestrator:
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

All prompt commands and the orchestrator use these underlying skills:

| Skill | Purpose |
|-------|--------|
| **tailor-bdd-skills-for-project** | Configure BL output dir, terminology, and code entry points. Run first in a new repo. |
| **reverse-engineering-business-logic** | Extract business logic from code into structured BL docs (11-section template). |
| **analyze-business-logic-gaps** | Find missing, vague, or contradictory rules in BL documents. |
| **validate-business-logic-against-code** | Map BL rules to code evidence; report implemented / partial / contradicted / not found. |
| **refine-business-logic-for-implementation** | Rewrite vague BL into deterministic, testable rules and state machines. |
| **derive-acceptance-criteria-from-business-logic** | Turn BL into Given/When/Then scenarios and acceptance criteria. |
| **generate-tests-from-business-logic** | Generate rule, scenario, state-transition, and billing tests from BL. |
| **bdd-orchestrator** | Coordinate the full workflow: extract → validate → gaps → refine → acceptance → tests. |

Skills can be forced with `/skill:<name> [args]` or invoked implicitly when a task matches their description.

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

### Example 3: Using the Orchestrator

```
User: "Understand order fulfillment from code and prepare it for sprint planning"

The bdd-orchestrator skill will:
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

## Prompt Command vs Skill

### Prompt commands (`/reverse-bl`, …)
- **Best for:** Single-step operations
- **Usage:** Quick, direct actions
- **Example:** `/reverse-bl order creation`
- **Pros:** Fast, simple, explicit
- **Cons:** Manual coordination for multi-step workflows

### Skills (`/skill:<name>` or automatic)
- **Best for:** Advanced users, custom workflows, multi-step orchestration
- **Usage:** "Use reverse-engineering-business-logic on payment processing" or `/skill:reverse-engineering-business-logic payment processing`
- **Pros:** Full control, composable; load automatically when a task matches
- **Cons:** Verbose, requires knowledge of skill names

## File Structure

```
.
├── package.json            # pi package manifest (pi-package keyword)
├── prompts/                # Prompt template commands
│   ├── reverse-bl.md
│   ├── validate-bl.md
│   ├── gap-analysis.md
│   ├── refine-bl.md
│   ├── derive-acceptance.md
│   └── generate-tests.md
└── skills/                 # Underlying capabilities
    ├── tailor-bdd-skills-for-project/
    ├── reverse-engineering-business-logic/
    │   ├── references/     # Deep-dive docs
    │   └── examples/       # Worked examples
    ├── validate-business-logic-against-code/
    ├── analyze-business-logic-gaps/
    ├── refine-business-logic-for-implementation/
    ├── derive-acceptance-criteria-from-business-logic/
    ├── generate-tests-from-business-logic/
    └── bdd-orchestrator/   # Full-workflow orchestrator
```

## Documentation

- **Prompt commands:** `prompts/*.md` - Quick reference for each command
- **Skills:** `skills/*/SKILL.md` - Detailed skill documentation
- **Examples:** `skills/reverse-engineering-business-logic/examples/` - Worked examples
