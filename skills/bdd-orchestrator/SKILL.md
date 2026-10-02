---
name: bdd-orchestrator
description: Orchestrates the full BDD workflow from code to trustworthy business logic — extraction, validation, gap analysis, refinement, acceptance criteria, and test generation. Use when the user requests comprehensive analysis like "understand [feature] from code", "analyze [domain] end-to-end", or "prepare [feature] for implementation or sprint planning", coordinating the other BDD skills step by step.
allowed-tools: read, write, edit, find, grep, bash
---

# BDD Workflow Orchestrator

You are a BDD workflow orchestrator that coordinates multiple skills to analyze code and produce trustworthy business logic documentation.

## Purpose

When users request comprehensive analysis like "understand [feature] from code" or "analyze [domain] end-to-end," coordinate multiple skills in sequence to:

1. Extract business logic from code
2. Validate BL against implementation
3. Find gaps and ambiguities
4. Refine into deterministic rules
5. Derive acceptance criteria
6. Generate tests

## Workflow

### Step 1: Prerequisites Check

First, check for project configuration:

```yaml
# Check for either:
BDD/project_config.yaml
# or
.cursor/bdd_project_config.yaml
```

If the config exists, read it to understand:
- BL output directory structure (`bl_output.root`)
- BL categories (`bl_output.categories`)
- Domain terminology (`terminology`)
- Code entry points (`entry_points`)

If no config exists:
- Suggest running the `tailor-bdd-skills-for-project` skill first (`/skill:tailor-bdd-skills-for-project`)
- Ask if user wants to proceed with defaults (BL in `business_logic/` with standard categories)
- Verify BL output directory can be created

### Step 2: Extract Business Logic

Run the `reverse-engineering-business-logic` skill: read its SKILL.md and follow it.

```
Target: <user's specified feature, file, or domain>
```

Wait for completion and summarize:
- BL document created with path
- Number of rules extracted
- Code coverage (lines analyzed)
- Key findings

Ask user: "Shall I proceed to validate this business logic against the code?"

### Step 3: Validate Against Code

Run the `validate-business-logic-against-code` skill: read its SKILL.md and follow it.

```
Target: <path to BL document from Step 2>
```

Wait for completion and summarize:
- Coverage percentage
- Rules: implemented / partial / contradicted / not found
- Critical issues (contradictions, missing implementations)

If validation finds issues:
- Explain what was found
- Recommend gap analysis next
- Ask: "Shall I analyze the gaps in this business logic?"

If validation passes (high coverage, no contradictions):
- Ask if user wants to skip to acceptance criteria or tests

### Step 4: Analyze Gaps

Run the `analyze-business-logic-gaps` skill: read its SKILL.md and follow it.

```
Target: <path to BL document>
```

Wait for completion and summarize:
- Number of gaps found by severity (critical, high, medium, low)
- Types of gaps (missing rules, ambiguities, contradictions, edge cases)
- Estimated refinement effort

If gaps are found:
- Explain key gaps
- Recommend refinement
- Ask: "Shall I refine the business logic to address these gaps?"

If no significant gaps:
- Proceed to acceptance criteria or tests

### Step 5: Refine Business Logic

Run the `refine-business-logic-for-implementation` skill: read its SKILL.md and follow it.

```
Target: <path to BL document with gaps>
```

Wait for completion and summarize:
- Refined BL document created
- Number of rules refined
- State machines defined
- Ambiguities resolved

After refinement:
- Explain improvements made
- Ask: "Shall I derive acceptance criteria from this refined business logic?"

### Step 6: Derive Acceptance Criteria (Optional)

Run the `derive-acceptance-criteria-from-business-logic` skill: read its SKILL.md and follow it.

```
Target: <path to refined BL document>
```

Wait for completion and summarize:
- User stories created
- Given/When/Then scenarios written
- Acceptance criteria defined
- Developer tasks identified

After acceptance criteria:
- Show summary of stories and scenarios
- Ask: "Shall I generate tests from this business logic?"

### Step 7: Generate Tests (Optional)

Run the `generate-tests-from-business-logic` skill: read its SKILL.md and follow it.

```
Target: <path to BL document>
```

Wait for completion and summarize:
- Test file created with path
- Test types generated (scenario, rule, edge case, state transition)
- Test coverage achieved
- Instructions for running tests

## Decision Logic

### When BL Document Already Exists

If user specifies a feature and BL document exists:
1. Show existing BL document path
2. Ask: "BL document already exists. Re-extract from code or use existing?"
   - Re-extract: Start from Step 2
   - Use existing: Start from Step 3 (validation)

### When Validation Finds Issues

- **Critical issues** (contradictions, missing implementations): Strongly recommend gap analysis
- **Medium issues** (partial implementations): Suggest gap analysis
- **Minor issues** (low coverage): Ask user preference

### When Gaps Are Found

- **Critical/high gaps**: Automatically recommend refinement
- **Medium gaps**: Suggest refinement, let user decide
- **Low gaps**: Ask if refinement needed

### After Refinement

Always ask about acceptance criteria and tests, but make them optional:
- "Shall I derive acceptance criteria?" (yes/no)
- "Shall I generate tests?" (yes/no)

## User Interaction

### After Each Major Step

1. **Show what was accomplished**: Artifact created, file path
2. **Summarize key findings**: Metrics, issues, improvements
3. **Ask for confirmation**: Proceed to next step?
4. **Offer to skip**: Goal might be achieved early

### Example Interactions

**After extraction:**
```
✓ Extracted business logic for order creation
  Document: business_logic/current/endpoints/order-creation.md
  Rules: 24 business rules extracted
  Code coverage: 450 lines analyzed

Key findings:
- Order validation has 8 rules
- Payment flow has 12 rules
- Inventory checks have 4 rules

Shall I proceed to validate this business logic against the code?
```

**After validation (with issues):**
```
⚠ Validation found issues
  Coverage: 78% (18/23 rules fully implemented)
  Issues:
  - 2 rules contradicted by code
  - 3 rules not found in implementation
  - 2 rules partially implemented

Recommendation: Run gap analysis to identify and fix these issues.
Shall I analyze the gaps in this business logic?
```

**After refinement:**
```
✓ Business logic refined
  Document: business_logic/proposal/endpoints/order-creation-refined.md
  Improvements:
  - Resolved 5 ambiguities
  - Defined 3 state machines
  - Made 12 rules deterministic

Next options:
1. Derive acceptance criteria for sprint planning
2. Generate tests for implementation
3. Both
4. Done

What would you like?
```

## Error Handling

### Skill Failure

If a skill fails:

1. **Explain what went wrong**: Error type and context
2. **Offer recovery options**:
   - Retry (same step)
   - Skip step and continue
   - Abort workflow
3. **Save progress**: Note which steps completed successfully

### Missing Configuration

If config file is missing:

1. **Inform user**: No BDD project config found
2. **Explain options**:
   - Run `tailor-bdd-skills-for-project` to configure
   - Proceed with defaults (BL in `business_logic/`)
3. **Ask preference**: What does user want to do?

### File Access Issues

If BL document can't be created:

1. **Check directory**: Does output directory exist?
2. **Create if needed**: Ask user permission
3. **Handle permissions**: Warn if directory is read-only

## Output

### Final Summary

After workflow completes (or user exits early), produce a summary:

```markdown
# BDD Workflow Summary

## Artifacts Created

| Artifact | Path | Description |
|----------|------|-------------|
| Business Logic | `business_logic/current/...` | Extracted rules |
| Validation Report | `reports/validation/...` | Coverage analysis |
| Gap Analysis | `reports/gaps/...` | Issues found |
| Refined BL | `business_logic/proposal/...` | Deterministic rules |
| Acceptance Criteria | `acceptance-criteria/...` | User stories |
| Tests | `tests/...` | Test suite |

## Issues Found and Addressed

- Validation: 5 rules contradicted → Fixed in refinement
- Gaps: 12 ambiguities → Resolved
- Coverage: 78% → 95% after refinement

## Recommended Next Steps

1. Review refined business logic: `business_logic/proposal/...`
2. Review acceptance criteria with product owner
3. Run generated tests: `pytest tests/...`
4. Implement missing rules (if any)

## Configuration

Project config: `BDD/project_config.yaml`
BL output: `business_logic/`
Categories: endpoints, models, workflows, billing
```

## Shortcuts

### Common User Requests

**"Understand [feature] and create tests"**
- Run: Extract → Validate → (if good) → Generate tests
- Skip: Gap analysis, refinement, acceptance criteria

**"Prepare [feature] for sprint"**
- Run: Extract → Validate → Gap analysis → Refine → Acceptance criteria
- Skip: Tests (save for developer)

**"Check if BL matches code"**
- Run: Validate only (if BL exists)
- Skip: Extraction, refinement, tests

**"Find problems in this BL"**
- Run: Gap analysis → Refine
- Skip: Extraction, validation, tests

### Partial Workflows

You don't always need to run the full workflow. Based on user request:

- **"Validate BL"**: Just validation
- **"Find gaps"**: Just gap analysis
- **"Make BL testable"**: Gap analysis → Refine
- **"Create tests from BL"**: Just test generation

Always confirm with user before running multi-step workflows.
