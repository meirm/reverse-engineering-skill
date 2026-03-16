---
name: tailor-bdd-skills-for-project
description: Tailors the universal BDD skills in this directory to a specific project. Use when adopting BDD skills in a new repo, defining where BL docs live, which terminology to use, and how to find entry points (views, models, tasks). Creates or updates project BDD config so other skills (reverse-engineering, validate, derive-acceptance-criteria, etc.) work correctly.
allowed-tools:
  - Read
  - Write
  - Glob
  - Grep
---

# Tailor BDD Skills for a Specific Project

## Purpose

The BDD skills in this directory are **project-agnostic**. They assume:

- A place to write business logic (BL) documents
- Consistent domain terminology (glossary)
- Known code entry points (API views, models, workflows, billing)

**This skill** configures those three things for your project so that:

- **Reverse-engineer business logic** knows where to save output and how to find views/models/tasks
- **Validate BL against code** knows where your code lives and how it’s structured
- **Derive acceptance criteria** and **generate tests** use your domain terms and paths

Run this skill once per project (or when the project structure or domain changes).

---

## Prerequisites

- You have (or will create) a BDD config file for the project (see below)
- You know the project’s tech stack (Django, FastAPI, Celery, etc.)
- You know where source code lives (e.g. `backend/`, `src/`, `apps/`)

---

## Step 1: Locate or Create Project BDD Config

**Preferred location (in the BDD directory):**

- `BDD/project_config.yaml` — project-specific BDD settings

**Alternative (project root):**

- `.cursor/bdd_project_config.yaml` or `bdd_project_config.yaml` at repo root

If the file does not exist, create it from the template below. If it exists, update the sections that need to change.

---

## Step 2: Fill the Config Sections

### 2.1 BL Output (Required)

Define where business logic documents are stored.

```yaml
bl_output:
  # Root directory for all BL docs (relative to repo root or absolute)
  root: "business_logic"

  # Optional: subdirs under root (defaults below)
  subdirs:
    current: "current"           # Production / deployed BL
    proposal: "proposal"         # Proposed changes
    under_development: "under_development"
    historical: "historical"

  # Categories under each state (defaults)
  categories:
    - endpoints
    - models
    - workflows
    - billing

  # Important files to maintain
  index_file: "index.md"
  glossary_file: "glossary.md"
  discovery_file: "DISCOVERY.md"
```

Other BDD skills will **save and read** BL under this root and use these category names.

---

### 2.2 Domain Terminology (Required)

Define how the project talks about the domain. This becomes the **glossary** and keeps BL language consistent.

```yaml
terminology:
  # Canonical term -> short definition (and optional technical alias)
  # Use these terms in BL docs instead of technical or ambiguous words.
  terms:
    - term: "order"
      definition: "A customer purchase request"
      technical_alias: "Order model / API order"
    - term: "tenant"
      definition: "An organization or account in the system"
      technical_alias: "Organization, Account"
    - term: "credit"
      definition: "Prepaid balance used to pay for operations"
      technical_alias: "quota, balance"
  # Add all domain nouns and key concepts your codebase uses.
```

- **term**: Preferred word in BL documents
- **definition**: One-line business meaning
- **technical_alias**: Optional; other names used in code or docs (so the skill can map “quota” → “credit”)

Update this when you discover new domain concepts during reverse-engineering.

---

### 2.3 Entry Points (Required)

Tell the skills **where** and **how** to find API endpoints, models, workflows, and billing logic.

```yaml
entry_points:
  framework: "Django"   # or "FastAPI", "Flask", "Express", etc.

  endpoints:
    description: "API views, handlers, routes"
    path_patterns:
      - "backend/**/api/views.py"
      - "backend/**/views/*.py"
    grep_patterns:
      - "class.*ViewSet"
      - "@api_view"
    bl_category: "endpoints"

  models:
    description: "Domain models, entities"
    path_patterns:
      - "backend/**/models.py"
      - "backend/**/models/*.py"
    grep_patterns:
      - "class.*Model"
    bl_category: "models"

  workflows:
    description: "Background tasks, schedulers, multi-step flows"
    path_patterns:
      - "backend/**/tasks.py"
      - "backend/**/management/commands/*.py"
    grep_patterns:
      - "@shared_task"
      - "class Command"
    bl_category: "workflows"

  billing:
    description: "Charges, refunds, credits, payments"
    path_patterns:
      - "backend/**/services/*credit*.py"
      - "backend/**/billing*.py"
    grep_patterns:
      - "charge"
      - "refund"
      - "CreditService"
    bl_category: "billing"
```

- **path_patterns**: Glob-style paths (relative to repo root) to search
- **grep_patterns**: Regex or substring patterns to find relevant classes/functions
- **bl_category**: Category under `bl_output.categories` where this type of BL is saved

Adjust paths and patterns to match your repo (e.g. `src/`, `apps/`, `packages/`).

---

### 2.4 Optional: Code Paths for Validation

Used by **validate-business-logic-against-code** to know where to search for evidence.

```yaml
validation:
  code_roots:
    - "backend/"
    - "src/"
  # If your validation rules live in specific places, list them:
  validation_locations:
    - "backend/**/validators.py"
  billing_locations:
    - "backend/**/billing*.py"
  state_transition_locations:
    - "backend/**/models/*.py"
```

---

## Step 3: Create or Update Supporting Files

After editing the config:

1. **Ensure BL output directory exists**  
   Create the root and, if you use them, subdirs: e.g. `business_logic/current/`, `business_logic/proposal/`, and category subdirs under each.

2. **Glossary**  
   If the skill or bootstrapping creates a `glossary.md`, seed it from `terminology.terms` so the project has one place for domain language.

3. **Index**  
   Ensure each state directory (e.g. `current`) has an `index.md` that other skills can update when they add new BL files.

---

## Step 4: Use the Config in Other Skills

When running other BDD skills in this directory:

- **Reverse-engineer business logic**: Read `bl_output` and `entry_points` from project config; save new BL under `bl_output.root` and correct category; use `terminology` for domain language.
- **Validate BL against code**: Read `bl_output.root` for BL paths and `entry_points` / `validation` for code paths.
- **Derive acceptance criteria / generate tests**: Use `terminology` and `bl_output.root` so scenarios and tests use the same terms and reference the right BL docs.

If no project config exists, the agent should **run this skill first** (create `project_config.yaml` and optional dirs/glossary/index), then proceed.

---

## Example: Minimal project_config.yaml

```yaml
# BDD/project_config.yaml — minimal example

bl_output:
  root: "business_logic"
  subdirs:
    current: "current"
    proposal: "proposal"
    under_development: "under_development"
    historical: "historical"
  categories: [endpoints, models, workflows, billing]
  index_file: "index.md"
  glossary_file: "glossary.md"
  discovery_file: "DISCOVERY.md"

terminology:
  terms:
    - term: "request"
      definition: "A single user-initiated operation"
    - term: "account"
      definition: "Customer or tenant account"
    - term: "balance"
      definition: "Prepaid balance for operations"

entry_points:
  framework: "FastAPI"
  endpoints:
    path_patterns: ["app/**/routes/*.py", "app/**/api/*.py"]
    grep_patterns: ["@router", "APIRouter"]
    bl_category: "endpoints"
  models:
    path_patterns: ["app/**/models/*.py"]
    grep_patterns: ["class.*Base"]
    bl_category: "models"
  workflows:
    path_patterns: ["app/**/workers/*.py"]
    grep_patterns: ["@task", "def.*task"]
    bl_category: "workflows"
  billing:
    path_patterns: ["app/**/billing/*.py"]
    grep_patterns: ["charge", "refund"]
    bl_category: "billing"
```

---

## Checklist After Tailoring

- [ ] `project_config.yaml` (or equivalent) exists and is committed
- [ ] `bl_output.root` and category subdirs exist on disk
- [ ] `terminology.terms` includes main domain concepts
- [ ] `entry_points` paths and grep patterns match your repo
- [ ] Other BDD skills are instructed (e.g. in README or agent rules) to read this config when running

---

## When to Re-run This Skill

- Adding a new project or repo that will use BDD skills
- Moving or renaming BL directories or code
- Discovering new domain terms that should be standardized
- Changing framework or entry point structure (e.g. new API layer, new task runner)
