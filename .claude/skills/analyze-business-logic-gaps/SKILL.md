---
name: analyzing-business-logic-gaps
description: Identifies missing, vague, underspecified, or contradictory business logic within BL documents. Finds incomplete edge cases, missing state transitions, ambiguous terminology, and weak billing rules. Use when reviewing business logic quality, hardening requirements, or preparing BL for implementation.
---

# Analyzing Business Logic Gaps

**Project-agnostic.** For consistent domain terms when reporting gaps, use `BDD/project_config.yaml` → `terminology` if present (see **tailor-bdd-skills-for-project**).

## Purpose

Find weaknesses in business logic documentation that could lead to implementation bugs, billing errors, or ambiguous behavior. This is a requirements-hardening skill.

## Prerequisites

- A business logic document to analyze
- Understanding of the problem domain
- Access to related documentation (API specs, existing code)

## Instructions

### Step 1: Read and Parse the BL Document

```bash
cat path/to/bl_document.md
```

Extract all stated rules, workflows, and behaviors.

### Step 2: Apply Gap Detection Categories

Systematically check the BL for gaps in these areas:

#### A. Entry and Exit Conditions

**Questions to ask:**
- Are all preconditions for each operation specified?
- Are all possible exit states defined?
- What happens when preconditions are not met?

**Look for:**
- Missing input validation rules
- Undefined behavior for edge cases (null, empty, invalid values)
- Unspecified cleanup or rollback on failure

#### B. State Definitions

**Questions to ask:**
- Are all possible states explicitly listed?
- Is each state's meaning clearly defined?
- Are states mutually exclusive and collectively exhaustive?

**Look for:**
- References to states not in the state definition list
- Ambiguous state names ("processed", "handled", "pending")
- Missing terminal states
- Undefined initial states

#### C. State Transitions

**Questions to ask:**
- For each state, what transitions are allowed?
- What transitions are explicitly forbidden?
- What triggers each transition?

**Look for:**
- States with no defined transitions
- Transitions mentioned in workflows but not in state machine
- Missing error state transitions
- Undefined transition conditions

#### D. Exceptional Paths

**Questions to ask:**
- What happens when external services fail?
- What happens on timeout?
- What happens on invalid input?
- What happens on concurrent operations?

**Look for:**
- Only happy path documentation
- Missing error handling specifications
- Unspecified retry behavior
- Undefined rollback mechanisms

#### E. Billing and Credit Rules

**Questions to ask:**
- When exactly do charges occur?
- Are charges synchronous or asynchronous?
- What triggers refunds?
- How are partial successes billed?

**Look for:**
- Ambiguous timing ("may be charged", "typically")
- Missing refund conditions
- Undefined partial success semantics
- Missing idempotency rules for billing

#### F. Retry and Fallback Logic

**Questions to ask:**
- What conditions trigger retries?
- How many retries are allowed?
- What changes between retries?
- When is fallback to alternative provider used?

**Look for:**
- Vague retry logic ("if needed", "may retry")
- Missing backoff strategies
- Undefined fallback conditions
- Missing circuit breaker specifications

#### G. Concurrent Operations

**Questions to ask:**
- What happens if the same request is submitted twice?
- What happens if conflicting operations occur simultaneously?
- Are there race conditions documented?

**Look for:**
- Missing idempotency guarantees
- Undefined behavior for concurrent state changes
- Missing lock or transaction specifications
- Unspecified duplicate request handling

#### H. Terminology Consistency

**Questions to ask:**
- Are terms used consistently throughout?
- Do multiple terms refer to the same concept?
- Does the same term mean different things in different contexts?

**Look for:**
- "query", "request", "lookup", "investigation" used interchangeably
- "success" meaning different things (HTTP 200 vs business success)
- Inconsistent status or state names

#### I. Actor Responsibilities

**Questions to ask:**
- Who/what initiates each action?
- Which system is responsible for each decision?
- Are external actor responsibilities clear?

**Look for:**
- Passive voice without clear actor ("is processed", "is handled")
- Unclear decision ownership
- Missing external system responsibilities

#### J. Time and Timing Constraints

**Questions to ask:**
- Are there timeouts specified?
- Are there SLA requirements?
- Are there timing-dependent behaviors?

**Look for:**
- Missing timeout values
- Unspecified delays or waiting periods
- Undefined expiration rules

### Step 3: Categorize Findings

Organize gaps by severity:

**Critical gaps** (will cause bugs):
- Missing state definitions
- Undefined error handling
- Ambiguous billing rules
- Missing idempotency

**Important gaps** (may cause issues):
- Incomplete edge case coverage
- Partial retry specifications
- Some terminology inconsistencies

**Nice to have** (improves clarity):
- Minor terminology variations
- Missing examples
- Incomplete documentation of non-critical paths

### Step 4: Generate Recommendations

For each gap, provide:

1. **The Gap**: What's missing or ambiguous
2. **Why It Matters**: What problems this could cause
3. **Proposed Fix**: Specific clarification or addition needed

### Step 5: Output the Report

Create a structured markdown report:

```markdown
# Business Logic Gap Analysis: [Domain]

## Summary
- Total gaps found: X
- Critical: Y
- Important: Z

## Critical Gaps

### [Gap Title]
- **Issue**: [Description]
- **Impact**: [What could go wrong]
- **Recommendation**: [Specific fix]

## Important Gaps
[Same structure]

## Terminology Issues
[List all inconsistencies]

## Proposed Clarifications
[List all suggested improvements]
```

## Common Gap Patterns

**Vague words indicating gaps:**
- "may", "might", "typically", "usually" → Need deterministic rules
- "processed", "handled" → Need specific behavior
- "if needed", "when appropriate" → Need conditions
- "valid", "correct" → Need validation rules

**Missing critical areas:**
- State transitions often only document happy path
- Billing rules often lack partial success semantics
- Error handling often lacks specificity
- Concurrent operations rarely specified

**Terminology red flags:**
- Multiple terms for same concept
- Same term with different meanings
- Status names that don't align with enums

## Examples

### Example 1: Analyzing a request/operation BL document

User request:
```
Find gaps in the [feature] business logic documentation
```

You would:
1. Read the BL document thoroughly

2. Analyze each section:
   - Entry conditions: What's required to perform the operation?
   - State transitions: What are all the states?
   - Error handling: What happens on failures?
   - Billing (if any): When exactly are charges applied?

3. Identify gaps:

**Missing Rules:**
- What happens if an external dependency is down?
- What happens if input format is valid but the entity doesn't exist?
- Is there a rate limit per account/user?
- What happens if a balance check passes but balance changes before the operation completes?
- Can the operation be cancelled? If so, from which states?

**Ambiguous Statements:**
- "System may retry on timeout" - How many retries? What timeout threshold?
- "Appropriate error returned to client" - Which error codes for which cases?
- "Processed asynchronously" - How does the client get the result? Polling? Webhook?

**Unhandled States:**
- What if the external service returns "unknown" status?
- What if it times out after partial success?
- What if it returns inconsistent data?

**Billing Ambiguity (if applicable):**
- "Charged on completion" - What counts as completion?
- What about partial results - full or partial charge?

4. Generate report:
```markdown
## [Feature] BL Gap Analysis

### Critical Missing Rules
| Area | Missing Rule | Impact | Suggested Addition |
|------|--------------|--------|-------------------|
| Error handling | Provider unavailable behavior | API hangs forever | "Return 503 after 3 failed provider attempts" |
| State machine | Query cancellation | No way to stop running queries | "Add cancelled state; allow cancel from pending/processing" |
| Race conditions | Concurrent quota deduction | Overspending possible | "Use atomic quota decrement with retry" |

### Ambiguous Language
| Quote | Section | Issue | Proposed Clarification |
|-------|---------|-------|----------------------|
| "may retry on timeout" | 4.2 Error Handling | Unclear condition | "Retries exactly 2 times when provider timeout > 30s" |
| "processed asynchronously" | 2.1 Overview | No result delivery | "Client polls status endpoint or receives webhook" |
| "appropriate error" | 5.1 Error Codes | Which error? | "Map provider errors to: 400, 404, 502, 503" |

### Billing Semantics
| Issue | Current | Proposed Fix |
|-------|---------|--------------|
| Charge timing undefined | "on completion" | "Charge when provider returns success or failure, not on timeout" |
| Partial success billing | Not specified | "Charge full credits for any provider response (including partial)" |

### Edge Cases
| Case | Missing Behavior | Risk | Suggested Rule |
|------|------------------|------|----------------|
| MSISDN valid but non-existent | Not documented | Confusion for users | "Return 404 with 'target not found' message" |
| Empty provider response | Not documented | Null pointer errors | "Treat empty as not found; return 404" |
| Duplicate identical queries | Not specified | Double charging | "Deduplicate within 5min window; return cached result" |
```

### Example 2: Analyzing an export/workflow BL document

User request:
```
Check the [export/workflow] business logic for gaps and ambiguities
```

You would:
1. Extract stated rules from documentation

2. Identify gaps:

**Missing Entry Conditions:**
- Must the source entity be in a specific state?
- Is validation (e.g. consent, eligibility) required or optional?
- What permissions are needed?

**Missing Exit Conditions:**
- How does the client know the operation is complete?
- What if the operation fails midway?
- How long are outputs retained?

**Ambiguous Statements:**
- "Large [operations] are processed asynchronously" - What is "large"? Define threshold.
- "Appropriate format selected" - How is format chosen? By user? By system?
- "Rules are applied" - Which rules? All? Some?

**Missing Edge Cases:**
- What if there are zero items to process?
- What if required metadata is missing?
- What if output exceeds size limits?

3. Generate report:
```markdown
## [Export/Workflow] BL Gap Analysis

### Critical Gaps
| Category | Missing Specification | Impact | Suggested Addition |
|----------|----------------------|--------|-------------------|
| Entry conditions | Investigation state requirement | May export incomplete data | "Require investigation.status == completed" |
| Thresholds | "Large export" definition | Unclear behavior | "Exports > 1000 CDRs use async processing" |
| Error handling | Mid-export failure behavior | Data inconsistency | "On failure: delete partial file, log error, notify user" |
| Retention | Export file lifetime | Storage leaks | "Export files deleted after 24 hours" |

### Ambiguous Language
| Quote | Issue | Clarification |
|-------|-------|--------------|
| "Consent rules are applied" | Which rules? How? | "Only CDRs with consent_status=true are exported" |
| "Appropriate format" | How chosen? | "User specifies format: CSV, JSON, or XML" |
| "May include metadata" | When is it included? | "Always include metadata: investigation_id, timestamp, requester" |

### Edge Cases Not Specified
| Case | Risk | Suggested Behavior |
|------|------|-------------------|
| 0 CDRs in investigation | Empty file confusion | "Return 204 No Content, no file created" |
| Missing consent fields | Export violations | "Skip CDRs without consent_field; log warning" |
| File size exceeded | Export failure | "Return 413 Payload Too Large; suggest pagination" |

### Terminology Inconsistencies
| Term | Inconsistent Usage | Standardize To |
|------|-------------------|----------------|
| (use project terms) | Also called "X" in some places | Always use "[canonical term]" |

Align with `BDD/project_config.yaml` → `terminology` when present.
```

## Checklist for Quick Analysis

Use this checklist for rapid gap detection:

- [ ] All states explicitly defined
- [ ] All transitions documented (including error transitions)
- [ ] All entry/exit conditions specified
- [ ] All error cases handled
- [ ] Billing/credit rules are deterministic (no "may", "typically")
- [ ] Idempotency specified for relevant operations
- [ ] Timeout values specified
- [ ] Retry conditions and counts specified
- [ ] Fallback conditions specified
- [ ] Terminology used consistently
- [ ] Actor responsibilities clear
- [ ] Concurrent operation behavior specified
- [ ] Partial success semantics specified
- [ ] Rollback behavior on failure specified
