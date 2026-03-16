# BDD Skills (Universal)

This directory contains **project-agnostic** BDD (behavior-driven development) and business-logic skills that can be used in any codebase.

## Tailoring for your project

Before using the skills in a new project, run:

- **Skill:** [tailor-bdd-skills-for-project](.claude/skills/tailor-bdd-skills-for-project/SKILL.md)

That skill will guide you to create or edit **`BDD/project_config.yaml`** (or `.cursor/bdd_project_config.yaml` at repo root) with:

1. **`bl_output`** — Where to store business logic docs (e.g. `business_logic/`), and categories (endpoints, models, workflows, billing).
2. **`terminology`** — Domain terms and definitions so all BL docs and scenarios use consistent language.
3. **`entry_points`** — Where to find API endpoints, models, workflows, and billing logic in your code (paths and grep patterns).

Once the config exists, the other skills use it for paths and terminology.

**Template:** Copy `project_config.yaml.example` to `project_config.yaml` and fill in your project’s paths and terms.

## Skills in this directory

| Skill | Purpose |
|-------|--------|
| **tailor-bdd-skills-for-project** | Configure BL output dir, terminology, and code entry points for this project. Run first in a new repo. |
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

## Methodology

See **BBD_logic.md** for the overall approach: reverse-engineering business logic from code, then using that BL to drive acceptance criteria, implementation, and tests.
