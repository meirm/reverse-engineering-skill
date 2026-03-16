---
name: deriving-acceptance-criteria-from-business-logic
description: Converts trusted business logic into product-owner-grade acceptance criteria and developer-ready tasks using Given/When/Then scenarios. Use when creating user stories, preparing for sprints, or turning BL into executable requirements.
---

# Deriving Acceptance Criteria from Business Logic

**Project-agnostic.** Use `BDD/project_config.yaml` → `terminology` for domain terms in scenarios when present (see **tailor-bdd-skills-for-project**).

## Purpose

Transform trusted business logic into clear, testable acceptance criteria that can guide development and verify implementation. This is how BL starts leading software development.

## Prerequisites

- Refined business logic document (use `refining-business-logic-for-implementation` first)
- Understanding of the target audience (product owners, developers, QA)
- Access to project templates or standards for acceptance criteria

## Instructions

### Step 1: Parse the Business Logic Document

Read the refined BL and identify:

```bash
cat path/to/refined_bl_document.md
```

Extract:
- User-facing capabilities and features
- Business policies and constraints
- Workflows and user journeys
- Error conditions and edge cases
- Billing and financial rules

### Step 2: Identify User Stories

For each capability, frame it as a user story:

```markdown
Format:
  As a <actor>,
  I want to <capability>,
  So that <business_value>.
```

**Examples:**
```markdown
As a [user role],
I want to [perform core action],
So that [business value].

As an administrator,
I want to monitor [account/balance] state,
So that I can ensure service continuity.
```

### Step 3: Generate Given/When/Then Scenarios

For each user story, create specific scenarios using the Gherkin format:

```gherkin
Scenario: <Descriptive title>
  Given <initial context>
  When <action occurs>
  Then <expected outcome>
  And <additional outcomes>
```

**Scenario Categories:**
1. **Happy path** - Successful operation
2. **Sad path** - Error conditions
3. **Edge cases** - Boundary conditions
4. **Business rules** - Specific policy enforcement

### Step 4: Create Acceptance Criteria

For each user story, list concrete acceptance criteria:

```markdown
Acceptance Criteria:
  1. [Specific, testable requirement]
  2. [Specific, testable requirement]
  ...
```

**Good acceptance criteria are:**
- Specific and unambiguous
- Testable (can be verified)
- Independent (not dependent on other stories)
- Negotiable (can be discussed and clarified)

### Step 5: Add Implementation Checklist

Add technical checklist items for developers:

```markdown
Implementation Checklist:
  - [ ] Database migration if needed
  - [ ] API endpoint changes
  - [ ] Background task modifications
  - [ ] Validation logic
  - [ ] Error handling
  - [ ] Logging requirements
  - [ ] Test coverage
```

### Step 6: Define Non-Functional Requirements

Add relevant non-functional requirements:

```markdown
Non-Functional Requirements:
  - Performance: <specific metrics>
  - Security: <specific requirements>
  - Reliability: <specific guarantees>
  - Scalability: <specific constraints>
```

### Step 7: Define "Done" Criteria

Specify what "done" means for this story:

```markdown
Done Means:
  - Code reviewed and approved
  - Unit tests written and passing
  - Integration tests passing
  - API documentation updated
  - Business logic document updated
  - No critical bugs outstanding
```

### Step 8: Output the Acceptance Criteria Document

Structure the document:

```markdown
# Acceptance Criteria: [Feature Name]

## User Stories
[List of user stories]

## Scenarios
[Given/When/Then scenarios for each story]

## Acceptance Criteria
[Testable requirements for each story]

## Implementation Checklist
[Developer checklist]

## Non-Functional Requirements
[Performance, security, etc.]

## Done Criteria
[Definition of done]
```

## Best Practices

**Given/When/Then Guidelines:**
- **Given**: Set up the initial state (avoid test setup details)
- **When**: Describe the action or event
- **Then**: Describe the observable outcome
- Use business language, not technical implementation
- Be specific with values (use actual numbers, not "valid value")

**Acceptance Criteria Guidelines:**
- Start each criterion with a verb
- Make it measurable (can be verified as pass/fail)
- Focus on what, not how
- Include business rules explicitly
- Cover error cases

**Implementation Checklist Guidelines:**
- Include all technical tasks
- Consider database changes, API changes, background tasks
- Don't forget tests and documentation
- Include monitoring and logging if relevant

## Common Anti-Patterns to Avoid

**❌ Vague scenarios:**
```gherkin
Scenario: Query is processed
  Given a valid request
  When the request is processed
  Then it should work
```

**✅ Specific scenarios:**
```gherkin
Scenario: Request submitted with sufficient balance
  Given an account with 100 [balance units]
  And the operation cost is 10 [units]
  When the user submits the [operation]
  Then the request is created with status "SUBMITTED"
  And the account balance is 90 [units]
```

**❌ Implementation details in scenarios:**
```gherkin
Scenario: Database transaction works
  Given the database is available
  When the transaction starts
  Then the lock is acquired
```

**✅ Business-focused scenarios:**
```gherkin
Scenario: Concurrent operations handled correctly
  Given an account with exactly 10 [balance units]
  When two users submit 10-unit operations simultaneously
  Then only one operation succeeds
  And the other is rejected with insufficient balance error
```

## Examples

### Example 1: Request/operation submission acceptance criteria

User request:
```
Create acceptance criteria for [request/operation] submission
```

You would:

**User Story:** As a [role], I want to [action], So that [value].

**Scenarios:** Write Given/When/Then for:
- Successful submission with sufficient balance (include balance deduction and ledger/audit if applicable)
- Rejection due to insufficient balance (correct status code, no deduction)
- Rejection due to invalid input (validation error, no charge)
- Concurrent submissions with exact balance (one succeeds, one rejected; balance never negative)

**Acceptance Criteria:** List testable conditions for: when the operation is accepted, when and how balance is deducted, when it is rejected and with which codes, and what the response includes.

**Implementation Checklist:** Validation, balance check, deduction (with concurrency safety if needed), ledger/audit, error codes, tests for concurrency. Use project terminology from config.

### Example 2: Async job / status polling acceptance criteria

User request:
```
Create acceptance criteria for [async job] status polling
```

You would:

**User Story:** As a [role], I want to check [job/file] status so that I know when the result is ready.

**Scenarios:** Given/When/Then for: initial poll returns PROCESSING (with submission_time, estimated_completion); poll returns COMPLETED (with download/result URL and metadata); timeout leads to STALLED and user can request continue polling.

**Acceptance Criteria:** What the status API returns (status enum, timestamps, for COMPLETED: result URL/metadata, for STALLED: continue option); polling interval and max duration; STALLED and CONTINUE rules (reset counter, max CONTINUEs).

**Non-Functional Requirements:** Performance (e.g. API response time), reliability (retries), monitoring if applicable.

## Acceptance Criteria Template

```markdown
# Acceptance Criteria: [Feature Name]

## User Story
As a <actor>,
I want to <capability>,
So that <business_value>.

## Scenarios

### Happy Path
Scenario: [Descriptive title]
  Given [context]
  When [action]
  Then [outcome]

### Sad Path
Scenario: [Descriptive title]
  Given [context]
  When [action]
  Then [outcome]

### Edge Cases
Scenario: [Descriptive title]
  Given [context]
  When [action]
  Then [outcome]

## Acceptance Criteria

1. [Requirement 1]
   - [Sub-requirement or detail]
   - [Sub-requirement or detail]

2. [Requirement 2]
   - [Sub-requirement or detail]

## Implementation Checklist
- [ ] [Task 1]
- [ ] [Task 2]
- [ ] [Task 3]

## Non-Functional Requirements
- **Performance**: [Specific requirement]
- **Security**: [Specific requirement]
- **Reliability**: [Specific requirement]

## Done Means
- [ ] [Criteria 1]
- [ ] [Criteria 2]
- [ ] [Criteria 3]
```

## Output Artifacts

The skill generates:

1. **User stories** with clear value propositions
2. **Given/When/Then scenarios** for BDD testing
3. **Acceptance criteria** checklist for verification
4. **Implementation checklist** for developers
5. **Non-functional requirements** where applicable
6. **Done criteria** for sprint completion
