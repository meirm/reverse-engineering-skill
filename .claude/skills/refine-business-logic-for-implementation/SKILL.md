---
name: refining-business-logic-for-implementation
description: Rewrites vague business logic into deterministic, testable rules by separating policy from mechanism, normalizing terminology, and defining explicit state machines. Use when preparing BL for code implementation, making BL executable, or normalizing ambiguous requirements.
---

# Refining Business Logic for Implementation

**Project-agnostic.** Use `BDD/project_config.yaml` → `terminology` when normalizing terms (see **tailor-bdd-skills-for-project**).

## Purpose

Transform descriptive business logic into deterministic, testable, implementable rules. Not all BL is written in a form that can drive software—this skill makes it executable.

## Prerequisites

- A business logic document to refine
- Understanding of the target implementation domain
- Access to the `analyzing-business-logic-gaps` skill (to identify issues first)

## Instructions

### Step 1: Analyze the Original BL

First, use gap analysis to identify issues:

```bash
# Use the gap analysis skill
# Identify vague statements, missing rules, inconsistencies
```

Document all areas that need refinement.

### Step 2: Classify BL Content

Separate the BL into these categories:

**A. Business Policy** (What, not how)
- Business rules and constraints
- Actor responsibilities
- Billing and credit rules
- Compliance requirements

**B. Workflow** (Sequences and orchestration)
- Step-by-step processes
- Decision points
- Parallel vs sequential operations

**C. Data Rules** (Validation and structure)
- Input validation rules
- Data format requirements
- Field constraints

**D. Billing Rules** (Financial operations)
- When charges occur
- Charge amounts
- Refund conditions
- Partial success semantics

**E. Exception Handling** (Error cases)
- Error conditions
- Recovery strategies
- Fallback behavior

### Step 3: Rewrite Vague Statements

Transform ambiguous statements into deterministic rules.

**Pattern 1: Conditional vagueness → Explicit conditions**

Before:
```
The system tries another provider when needed.
```

After:
```
If the primary provider returns TIMEOUT or CONNECTION_ERROR:
  - The system retries once with the secondary provider
  - The secondary provider is selected based on priority routing rules

If the primary provider returns INVALID_TARGET:
  - No fallback is attempted
  - The query immediately transitions to FAILED
```

**Pattern 2: Timing vagueness → Explicit timeouts**

Before:
```
The system polls for status until complete.
```

After:
```
Status polling behavior:
  - Initial poll: immediately after submission
  - Subsequent polls: every 30 seconds
  - Maximum poll duration: 5 minutes (10 polls)
  - After 10 incomplete polls: transition to STALLED
  - STALLED queries can be resumed with CONTINUE operation
```

**Pattern 3: Billing vagueness → Deterministic rules**

Before:
```
Credits are deducted for queries.
```

After:
```
Credit deduction rules:
  - Credits are deducted synchronously BEFORE provider submission
  - Deduction amount: Provider.query_cost value
  - If deduction fails (insufficient credits): query is rejected, no provider call made
  - Refunds: No automatic refunds for failed queries
  - Partial success: Full credit amount deducted even if only partial data returned
```

**Pattern 4: State vagueness → Explicit state machines**

Before:
```
Queries can be pending, processing, or done.
```

After:
```
Query state machine:

States:
  - PENDING: Initial state, awaiting validation
  - VALIDATED: Passed validation, awaiting credit check
  - SUBMITTED: Credits deducted, provider call made
  - PROCESSING: Provider is processing the query
  - COMPLETED: Provider returned results successfully
  - FAILED: Query failed (validation, credit, or provider error)
  - STALLED: Provider timeout, can be retried
  - CANCELLED: Query cancelled by user

Allowed transitions:
  - PENDING → VALIDATED: Input validation passes
  - VALIDATED → SUBMITTED: Sufficient credits, deduction successful
  - SUBMITTED → PROCESSING: Provider acknowledges request
  - PROCESSING → COMPLETED: Provider returns successful result
  - PROCESSING → STALLED: Provider timeout (no response within 60s)
  - STALLED → SUBMITTED: User triggers CONTINUE operation
  - (any active state) → CANCELLED: User requests cancellation
  - (any state) → FAILED: Validation fails, insufficient credits, or provider returns error

Forbidden transitions:
  - COMPLETED → any other state (terminal)
  - FAILED → any state other than CANCELLED
```

### Step 4: Normalize Terminology

Create a terminology dictionary and enforce consistency:

```markdown
## Terminology

| Term | Definition | Use |
|------|------------|-----|
| [Canonical term for operation] | Definition | Use consistently; not "request", "lookup", etc. |
| [Canonical term for account] | Definition | Not "customer", "client", or "account" (unless that is canonical) |
| [Canonical term for balance] | Prepaid/balance for operations | Not "quota", "tokens" unless standardized |
| [External service term] | Definition | Use consistently for third-party integration |
```

Align with `BDD/project_config.yaml` → `terminology` when present.

Replace all instances with the standardized term.

### Step 5: Extract Decision Tables

For complex conditional logic, create explicit decision tables:

**Before:**
```
Different providers are selected based on query type and location.
```

**After:**
```markdown
## Provider Selection Decision Table

| Query Type | Target Country | Has MCC/MNC | Primary Provider | Fallback Provider |
|------------|----------------|-------------|------------------|-------------------|
| GEO | US | Yes | ProviderA | ProviderB |
| GEO | US | No | ProviderC | ProviderA |
| GEO | Non-US | Yes | ProviderA | ProviderD |
| GEO | Non-US | No | ProviderC | ProviderD |
| CDR | Any | N/A | ProviderE | ProviderF |

Notes:
- MCC/MNC from HLR lookup is required for routing
- If primary provider fails with INVALID_TARGET, no fallback attempted
- If primary provider fails with TIMEOUT or CONNECTION_ERROR, fallback used
```

### Step 6: Define Data Invariants

Specify what must always be true:

```markdown
## Invariants

- [Account].balance >= 0 (or defined minimum; cannot go negative)
- [Request].external_id is NULL before submission, set after
- Ledger/transaction entries are immutable once created
- [Request].status transitions are one-way (no backwards transitions)
- For any COMPLETED [request], exactly one Ledger entry exists for the charge (if billing applies)
```

### Step 7: Separate Mechanism from Policy

Document business rules separately from implementation details:

**Before (mixed):**
```
We validate the MSISDN format using a regex and then store it in the database.
```

**After (separated):**

**Business Policy:**
```
[Identifier] Validation Rules:
  - Must match format (e.g. length, character set)
  - Must satisfy domain constraints (e.g. country, type)
  - Must be from allowed set if applicable
```

**Implementation Notes:**
```
Implementation:
  - Validation performed by [module].[function]
  - Pattern or rules: [specify]
  - Allowed values: [config or constant]
```

### Step 8: Generate the Refined BL Document

Structure the refined document:

```markdown
# Refined Business Logic: [Domain]

## Terminology
[Standardized terms and definitions]

## Business Policies
[The "what" - rules, constraints, requirements]

## Data Rules
[Validation, format, constraints]

## Billing Rules
[Credit charges, refunds, partial success]

## Workflows
[Step-by-step processes]

## State Machine
[All states and allowed transitions]

## Decision Tables
[Complex conditional logic]

## Error Handling
[Exception cases and recovery]

## Invariants
[What must always be true]

## Implementation Guidance
[Notes on how policies could be implemented]
```

## Before and After Transformations

| Before (Vague) | After (Deterministic) |
|----------------|----------------------|
| "checks credits" | "Validates tenant.credits >= Provider.query_cost" |
| "processes the request" | "Validates input, checks credits, deducts credits, submits to provider" |
| "may retry" | "Retries up to 3 times with exponential backoff (1s, 2s, 4s)" |
| "if there's an error" | "If provider returns 500, 502, 503, or 504" |
| "successful response" | "HTTP 200-299 or provider-specific success codes" |
| "typically" | [Remove word, make rule unconditional or specify conditions] |
| "handled" | "Transitions to FAILED state and creates error log" |
| "as needed" | [Specify exact condition] |

## Output Quality Checklist

After refinement, the BL should pass these checks:

- [ ] No vague words (may, might, typically, usually)
- [ ] All timeouts have explicit values
- [ ] All conditions are explicit (no "if needed")
- [ ] All states are defined
- [ ] All transitions are explicit
- [ ] All billing rules are deterministic
- [ ] Terminology is consistent throughout
- [ ] Policy is separated from mechanism
- [ ] Decision tables exist for complex logic
- [ ] Invariants are specified
- [ ] Each rule can be turned into a test
- [ ] Each rule can be implemented directly

## Examples

### Example 1: Refining request/operation submission BL

User request:
```
Refine the [request/operation] submission business logic for implementation
```

You would:
1. Identify vague statements (e.g. "Request is validated", "Balance is checked", "External service is called").
2. Rewrite into deterministic rules:
   - Step 1: Input validation (pattern, type, allowed values; on failure: return 400, create no records).
   - Step 2: Balance/eligibility check (required amount, current >= required; if insufficient: return 402, create no records).
   - Step 3: Deduction (create Ledger entry; use concurrency-safe update; on failure: return 500, no external call).
   - Step 4: External submission (select handler by routing rules, create Request with status=SUBMITTED, call API, set external_id).
3. Create decision table for routing/selection if applicable.
4. Define state machine for Request status.

### Example 2: Refining async job / status polling

User request:
```
Make the [async job] polling logic executable
```

You would:
1. Extract the vague polling description.
2. Define explicit state machine: states (e.g. PENDING → POLLING → COMPLETED/FAILED/STALLED), poll interval, max polls, max duration, timeout → STALLED; STALLED resume via CONTINUE, max CONTINUEs, terminal state after max.
3. Create decision table for status handling.
4. Define invariant: "Each [job] has exactly one final terminal state".

### Example 3: Refining refund/release logic

User request:
```
Clarify the [credit/balance] refund rules
```

You would:
1. Find vague refund/release statements.
2. Rewrite as deterministic rules: NO REFUND cases (validation failure, insufficient balance, external error, partial result, timeout, user cancel); REFUND cases (platform error before external call, idempotency/duplicate). Refund implementation: Ledger entry with positive amount, amount = original deduction, link to original entry.
3. Define invariant: "Sum of all Ledger entries for [account] = current balance".
