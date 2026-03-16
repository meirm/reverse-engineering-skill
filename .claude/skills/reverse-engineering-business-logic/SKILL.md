---
name: reverse-engineering-business-logic
description: Reverse engineers business logic from source code by extracting operational business truth. Use when user asks to understand what the system actually does, analyze business rules, extract domain logic, infer workflows from code, explain state transitions, or identify decision logic. Works with API views (Django, FastAPI, etc.), domain models, background tasks, and multi-step workflows.
allowed-tools:
  - Read
  - Grep
  - Glob
---

# Reverse Engineer Business Logic from Code

## Project setup

This skill is **project-agnostic**. Before use, ensure the project has a BDD config so output paths and terminology are defined. If `BDD/project_config.yaml` (or `.cursor/bdd_project_config.yaml`) exists, read it for `bl_output.root`, `bl_output.categories`, and `terminology`. If not, run the **tailor-bdd-skills-for-project** skill first to create it.

**Default when no config exists:** write BL under `business_logic/` with categories `endpoints`, `models`, `workflows`, `billing`.

---

## Quick Start (5 minutes)

Extract business logic from code in 3 steps:

**1. Say** "extract business logic from [file/function/feature]"
   - Example: "extract business logic from order creation"
   - Example: "explain what the payment charging flow actually does"

**2. Review** the generated markdown in the BL output directory (from project config, or `business_logic/`)
   - 11 sections covering purpose, actors, flow, rules, billing, edge cases
   - All with line number references to source code

**3. Verify** completeness using the validation checklist (below)

**Where outputs go (organized by state):** Use paths from project config `bl_output` when present; otherwise:
- **Current production code** → `[bl_root]/current/[category]/[name].md`
- **Proposed changes** → `[bl_root]/proposal/[category]/[name].md`
- **Work in progress** → `[bl_root]/under_development/[category]/[name].md`
- **Historical reference** → `[bl_root]/historical/[category]/[name].md`

**Categories:** From config or default: endpoints, models, workflows, billing

**Most common pattern:**
```
User: "Extract business logic from the order creation flow"
Agent: [Reads project config, analyzes code, generates 11-section BL document]
Output: [bl_root]/current/endpoints/order-creation.md
```

---

## When to Use This Skill

Use this skill when you hear phrases like:
- "explain what this code actually does"
- "what are the business rules for [feature]"
- "how does billing work"
- "what happens when [condition] occurs"
- "why did this [state/transaction/failure] happen"
- "what's the flow for [feature]"
- "document the business logic"
- "reverse engineer the business rules"

**Good use cases:**
✅ API endpoints that need documentation
✅ Complex state machines or workflows
✅ Billing/credit logic that must be accurate
✅ Features being refactored (need to understand current behavior)
✅ Onboarding new developers (show them what the system does)

**Not for:**
❌ Code structure or architecture diagrams
❌ Performance analysis
❌ Security audits
❌ Test generation

---

## Core Workflow

### Core Principle

**Never confuse code structure with business structure.**

- Technical structure: `view -> serializer -> service -> provider -> model`
- Business structure: `validate request -> determine eligibility -> choose route -> execute lookup -> normalize result -> update record -> charge credits`

The distinction is the whole value.

### Step 1: Identify the Analysis Scope

Ask the user which mode they need:

1. **Endpoint-to-BL**: API views, handlers, endpoints
   - Best for understanding request/response flows
   - Focus on validation, routing, charging, responses

2. **Model-to-BL**: Domain models, entities, schemas
   - Best for understanding lifecycle rules
   - Focus on state transitions, invariants, side effects

3. **Workflow-to-BL**: Multi-step features spanning files
   - Best for complex business processes
   - Focus on orchestration, retries, fallbacks

4. **Billing-to-BL**: Credit, quota, charging logic
   - Best for understanding when money moves
   - Focus on charges, refunds, adjustments, race conditions

### Step 2: Identify Entry Points

For the chosen scope, locate:
- **Endpoints**: Views, handlers, routes (use Grep for "class.*ViewSet")
- **Commands**: Management commands, CLI operations
- **Schedulers**: Cron jobs, periodic tasks
- **Queue Workers**: Celery tasks, background jobs
- **Models**: Domain entities with business methods

### Step 3: Trace Execution Path

Follow the code from entry point through:
```
controller → service → provider → DB updates → response
```

Look for:
- Method calls and their sequence
- External service integration
- Database operations
- Background task dispatch
- State changes

### Step 4: Extract Business Decisions

Identify and document:
- **Validations**: What gets rejected and why
- **Routing**: How requests are directed to different handlers
- **Charging**: When credits are consumed, refunded, or adjusted
- **Retries**: What gets retried, backoff strategies
- **Fallback logic**: Alternative providers, default values
- **Termination conditions**: When flows stop early
- **Prioritization**: How concurrent requests are ordered

### Step 5: Separate Technical from Business

Rewrite technical mechanics in domain language:

| Technical | Business |
|-----------|----------|
| `calls function X` | `attempts provider A, then provider B if A fails` |
| `updates status field` | `marks investigation as completed` |
| `throws ValidationError` | `rejects invalid target identifier` |
| `check quota >= cost` | `verifies sufficient credits before lookup` |

### Step 6: Use Domain Language

Replace technical terms with business terms. Use the project's **terminology** from `BDD/project_config.yaml` (or tailor skill); if missing, infer from the codebase and document in the BL glossary. Example mappings (adapt to project):
- Prefer the project's canonical term for "request" / "operation" (e.g. "order", "query")
- Prefer the project's term for "background job" (e.g. "job", "task", "investigation")
- Prefer the project's term for "account" (e.g. "tenant", "organization", "customer")
- Prefer the project's term for "balance" or "credits" if the domain uses one

See `expertise.yaml` for a generic mapping template; project config overrides with project-specific terms.

### Step 7: Produce Structured Output

Use the output template in `output_template.md` to structure findings as:

1. Business Purpose
2. Actors
3. Preconditions
4. Main Flow
5. Decision Rules
6. State Transitions
7. Billing / Credit Impact
8. Exceptions / Edge Cases
9. Data Written / Read
10. Ambiguities / Questions
11. Code References

### Step 8: Save to BL Output Directory

**CRITICAL: Always save the analysis to a file in the project's BL output directory** (from `BDD/project_config.yaml` → `bl_output.root`, or default `business_logic/`) to create persistent, linkable documentation.

**Directory structure (organized by state):** Use paths from project config when present. Generic layout:
```
[bl_output.root]/
├── current/                    # Production business logic (currently deployed)
│   ├── index.md                # Master index of current BL analyses
│   ├── endpoints/              # API endpoint business logic
│   ├── models/                 # Domain entity lifecycle rules
│   ├── workflows/              # Multi-step business processes
│   ├── billing/                # Charging / payment logic
│   ├── glossary.md             # Business terms and definitions
│   └── DISCOVERY.md            # Complete inventory of discovered BL
├── proposal/                   # Proposed changes (not yet implemented)
│   ├── index.md                # Index of proposed BL changes
│   └── [same subdirectories as current/]
├── under_development/          # Work in progress (partially implemented)
│   ├── index.md                # Index of BL under development
│   └── [same subdirectories as current/]
└── historical/                 # Old/outdated business logic
    ├── index.md                # Index of historical BL (for reference)
    └── [same subdirectories as current/]
```

**Choosing the right directory:**

**Use `[bl_root]/current/` when:**
- Analyzing production code that's currently deployed
- Documenting existing, stable business logic
- Creating initial BL documentation for live features

**Use `[bl_root]/proposal/` when:**
- Documenting proposed business logic changes
- Analyzing RFCs, design docs, or specifications
- Exploring "what if" scenarios before implementation
- BL is not yet implemented in code

**Use `[bl_root]/under_development/` when:**
- Analyzing code in feature branches
- Documenting partially implemented features
- Tracking BL changes during active development
- Code exists but is not yet in production

**Use `[bl_root]/historical/` when:**
- Documenting deprecated or removed features
- Preserving old BL for reference/audit purposes
- Code has been replaced but understanding is needed

**File naming:**
- Use lowercase with hyphens: e.g. `order-creation.md`, `payment-charging.md`
- Use descriptive names that reflect business capability, not code file names

**Saving workflow:**
1. Write the analysis using the `output_template.md`
2. Choose the appropriate state directory (current/proposal/under_development/historical)
3. Update the `index.md` in that directory with file name, purpose, and code locations
4. Update `glossary.md` if new terms were discovered; align with project config terminology when present

---

## Quality Validation Checklist

After generating BL documentation, verify:

**Completeness (all 11 sections present):**
□ 1. Business Purpose - What capability this provides
□ 2. Actors - Who triggers or uses it
□ 3. Preconditions - What must be true before it runs
□ 4. Main Flow - Step-by-step business flow (not code structure!)
□ 5. Decision Rules - Explicit if/then rules
□ 6. State Transitions - How statuses change (with diagram)
□ 7. Billing/Credit Impact - When credits are charged/refunded
□ 8. Exceptions/Edge Cases - Invalid input, unsupported paths
□ 9. Data Written/Read - Key persistence effects
□ 10. Ambiguities/Questions - What cannot be inferred
□ 11. Code References - Specific files and line numbers

**Quality Checks:**
□ Business language used (not "calls function X")
□ Domain terminology from expertise.yaml
□ Line numbers included for all references
□ State transitions have Mermaid diagrams
□ Billing logic is explicit (no "TODO: verify billing")
□ Ambiguities are honestly documented

**Self-Review Questions:**
- Could a new developer understand what this code does from this doc?
- Are all billing/credit impacts explicit?
- Would this document catch a breaking change?
- Are there "I don't know" sections where appropriate?

---

## Analysis Modes Summary

**Endpoint-to-BL:** API views, handlers, endpoints → `[bl_root]/endpoints/` (or config category)
- Focus: Request/response flows, validation, routing, charging, responses

**Model-to-BL:** Domain models, entities, schemas → `[bl_root]/models/`
- Focus: Lifecycle rules, state transitions, invariants, side effects

**Workflow-to-BL:** Multi-step features → `[bl_root]/workflows/`
- Focus: Orchestration, retries, fallbacks, background tasks

**Billing-to-BL:** Charges, refunds, payments → `[bl_root]/billing/`
- Focus: When charges occur, refunds, adjustments, partial charges

---

## Best Practices

**Do:**
- Start from entry points and trace forward
- Look for hidden logic in model methods, property decorators, signals
- Check config flags and environment variables for behavioral switches
- Examine exception handlers for silent fallbacks
- Document what you cannot safely infer

**Don't:**
- Describe code structure instead of business flow
- Ignore background tasks and async operations
- Skip billing/credit logic (often the most critical)
- Assume docstrings match implementation
- Omit "impossible" states the code actually handles

---

## Red Flags to Investigate

- Empty except blocks (silent failures)
- TODO comments with business implications
- Commented-out code with "removed" logic
- Multiple return paths with different semantics
- State checks that don't match enum definitions
- Billing logic without clear audit trail
- Fallback providers with different semantics
- Partial success states

---

## Advanced Analysis (See References)

This skill includes three advanced analysis modes for validation, discovery, and impact analysis:

**📖 [Diff Mode: BL Docs vs Code](references/advanced-modes.md#mode-1-diff-mode---bl-docs-vs-code)**
- Validate that business logic documentation matches actual code implementation
- Detect stale docs, unimplemented rules, and hidden behavior
- Use before/after code changes or during code reviews

**📖 [Change Impact Analysis: Code → BL](references/advanced-modes.md#mode-2-change-impact-analysis---code--bl)**
- Analyze how code changes affect business logic
- Generate business impact summaries for pull requests and releases
- Translate code changes to business language for stakeholders

**📖 [Gap Analysis: Finding Undocumented BL](references/advanced-modes.md#mode-3-gap-analysis---finding-undocumented-business-logic)**
- Identify business logic that exists in code but lacks documentation
- Answer "What are we missing?" and prioritize documentation efforts
- Use for periodic reviews or before audits

---

## Bootstrapping (See Reference)

**📖 [Bootstrapping Guide](references/bootstrapping.md)** - Systematic approach for documenting an entire codebase:

- **Phase 1: Discovery** - Map all entry points (endpoints, models, workflows, billing)
- **Phase 2: Prioritization** - Tier 1 (critical), Tier 2 (important), Tier 3 (nice to have)
- **Phase 3: Initial Documentation** - Set up directory structure and index
- **Phase 4: Batch Processing** - Systematic documentation of prioritized items
- **Phase 5: Cross-Linking** - Connect related analyses

Use this for initial documentation projects or comprehensive knowledge base builds.

---

## Examples

### Example 1: Endpoint-to-BL (API view)

User request:
```
What does the OrderCreateView actually do when someone creates an order?
```

You would:
1. Find the entry point using project config `entry_points.endpoints` (e.g. `grep -r "class OrderCreateView"` or the configured path/grep patterns)
2. Read the view/handler file
3. Trace the create flow through validation → service → persistence → response
4. Extract business logic into the structured template
5. **Save analysis to file:** `[bl_root]/current/endpoints/order-creation.md`
6. **Update the index:** Add entry to that state's `index.md`

**Output file:** `[bl_root]/current/endpoints/order-creation.md`

```markdown
## 1. Business Purpose
Creates a new order from validated request data and reserves inventory.

## 5. Decision Rules
- If item out of stock → reject with "Item unavailable"
- If account balance < order total → reject with "Insufficient balance"
- If payment gateway timeout → retry once, then fail with "Payment unavailable"
- If both payment attempts fail → mark order as FAILED, release reservation

## Related Business Logic
- [Charging Rules](../billing/charging-rules.md) - When and how charges are applied
- [Order Lifecycle](../models/order-lifecycle.md) - State transitions
```

---

## References

**📖 [Quick Start Guide](references/quick-start.md)** - Detailed getting started guide with 3-5 complete walkthrough examples

**📖 [Bootstrapping](references/bootstrapping.md)** - Systematic codebase documentation workflow for large-scale projects

**📖 [Advanced Modes](references/advanced-modes.md)** - Diff, Change Impact, and Gap Analysis modes with workflows and examples

**📖 [Validation](references/validation.md)** - Quality assurance, common issues, peer review process, and ambiguity handling

**📖 [Collaboration](references/collaboration.md)** - Team workflows, versioning, conflict resolution, and maintenance guidelines

**📖 [Domain Expertise](expertise.yaml)** - Template for business language mappings; use project config `terminology` for project-specific terms

**📖 [Output Template](output_template.md)** - 11-section template for generating BL documentation
