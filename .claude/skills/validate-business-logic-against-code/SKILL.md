---
name: validating-business-logic-against-code
description: Verifies whether documented business logic is actually implemented by mapping BL rules to code evidence. Flags rules as implemented, partially implemented, contradicted, or not found. Use when validating business logic documents, checking code coverage against requirements, or verifying BL-to-code alignment.
allowed-tools: Read, Grep, Glob
---

# Validating Business Logic Against Code

**Project-agnostic.** Use `BDD/project_config.yaml` → `bl_output.root` and `validation` (or `entry_points`) for BL and code paths when present (see **tailor-bdd-skills-for-project**).

## Purpose

Build trust between documented business logic and actual implementation by systematically mapping each BL rule to concrete evidence in the codebase.

## Prerequisites

- A business logic document (markdown, YAML, or structured text)
- Access to the relevant code files
- Understanding of the project structure

## Instructions

### Step 1: Parse Business Logic Document

Read the BL document and extract all explicit rules:

```bash
# Read the BL document
cat path/to/bl_document.md
```

Extract and categorize rules:
- **Validation rules**: What conditions are checked
- **State transition rules**: How state changes
- **Billing/credit rules**: When charges occur
- **Error handling rules**: How failures are processed
- **Workflow rules**: Sequential/parallel operations
- **Routing/fallback rules**: Provider selection logic

### Step 2: Identify Relevant Code Files

For each rule category, identify the code files that would implement it:

```bash
# Search for relevant code
grep -r "keyword" django_app/
```

**Common mapping (adapt to your project):**
- Validation rules → views, serializers, validators
- State transitions → models, state handlers
- Billing rules → billing/ledger services, models
- Error handling → views, task handlers
- Workflow → entry points, background tasks
- Routing → route handlers, middleware

Use `BDD/project_config.yaml` → `entry_points` and `validation` for actual paths when present.

### Step 3: Map Each Rule to Code Evidence

For each extracted rule, search for implementation evidence:

**Status Categories:**

| Status | Definition |
|--------|------------|
| **Implemented** | Clear, direct implementation matching BL |
| **Partially Implemented** | Some cases covered, edge cases missing |
| **Contradicted** | Code does opposite of what BL states |
| **Not Found** | No evidence of implementation |
| **Hidden Behavior** | Code does something not documented in BL |

**Evidence to collect:**
- File path and line numbers
- Function/method names
- Relevant code snippets
- Model fields or constants

### Step 4: Generate Coverage Matrix

Create a markdown table with this structure:

```markdown
| BL Rule | Status | Evidence | Notes |
|---------|--------|----------|-------|
| [Rule summary] | [Status] | [file:line] | [Details] |
```

### Step 5: Generate Summary Report

After mapping all rules, create a summary with:

1. **Coverage Statistics:**
   - Total rules documented
   - Rules implemented (count and %)
   - Rules partially implemented (count and %)
   - Rules contradicted (count and %)
   - Rules not found (count and %)

2. **Critical Issues:**
   - All contradicted rules
   - All not-found rules
   - Hidden behaviors that could impact billing or security

3. **Recommendations:**
   - Which BL rules need clarification
   - Which code needs updates to match BL
   - Which tests are needed to verify alignment

### Step 6: Save the Report

Save the coverage matrix and report to a file (e.g. under project root or `[bl_root]`):

```bash
# Example: reports or BL output directory
mkdir -p reports/bl-validation
# or use BDD/project_config.yaml bl_output.root if desired
cat > reports/bl-validation/<domain>-validation.md
```

## Best Practices

- **Be specific with evidence**: Always include file:line references
- **Distinguish intent from bug**: Code may implement correctly but have a bug
- **Check both happy and sad paths**: Validate error handling too
- **Look for side effects**: Some rules have hidden implementation side effects
- **Verify model constraints**: Some rules are enforced at the database level
- **Check for multiple implementations**: Same rule may be implemented in multiple places
- **Be thorough**: Search multiple terms for each rule (e.g., "timeout", "retry", "fallback")
- **Read actual code**: Don't rely on function names alone

## Examples

### Example 1: Validating request/operation BL

User request:
```
Validate the [feature] business logic against the backend code
```

You would:
1. Read the BL document extracting rules, e.g.:
   - "Reject requests with invalid identifier format"
   - "Charge only on successful completion"
   - "Fallback to secondary handler after 30 second timeout"
   - "Reject when account has insufficient balance"

2. Search code for evidence (use project config paths or typical locations):
   ```bash
   grep -r "identifier\|validate" <code_root>/
   grep -r "charge\|credit\|balance" <code_root>/
   grep -r "timeout\|fallback\|secondary" <code_root>/
   ```

3. Read implementation files to verify and note file:line.

4. Generate coverage matrix:
   ```markdown
   | BL Rule | Status | Evidence | Notes |
   |--------|--------|----------|------|
   | Reject invalid identifier | Implemented | path/to/views.py:88-97 | Validation matches BL |
   | Charge on success only | Contradicted | path/to/billing.py:145-160 | Charges before completion |
   | Fallback on timeout | Partially implemented | path/to/service.py:210-235 | Only for timeout, not 5xx |
   | Reject insufficient balance | Implemented | path/to/views.py:102-115 | Check before operation |
   ```

5. Report findings with coverage %, critical issues, and gaps.

### Example 2: Validating export/workflow BL

User request:
```
Check if the [export/workflow] feature matches the documented business rules
```

You would:
1. Extract BL rules from documentation (e.g. state requirements, filters, limits, audit).

2. Search codebase using project paths (from config or standard layout).

3. Read relevant files and map each rule to evidence.

4. Generate matrix with Status (Implemented / Partially / Contradicted / Not found) and file:line Evidence.

5. Report coverage %, critical gaps (e.g. missing filter, wrong limit), and any hidden behavior not in BL.

### Example 3: Validating billing/admin workflow

User request:
```
Verify the [replenishment/adjustment] workflow against business logic
```

You would:
1. Extract BL rules (minimum amount, who can perform, ledger entry, quota update, audit).

2. Search and read code using project config or typical admin/billing paths.

3. Generate matrix mapping each rule to Status and Evidence (file:line).

4. Report coverage and any contradictions or hidden behavior.

## Output Format

The validation produces:

1. **Coverage Matrix** (table format)
2. **Summary Statistics** (counts and percentages)
3. **Critical Issues List** (contradictions and missing implementations)
4. **Recommendations** (actionable next steps)
