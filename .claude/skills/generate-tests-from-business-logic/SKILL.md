---
name: generating-tests-from-business-logic
description: Generates scenario tests, rule tests, edge case tests, state transition tests, and billing tests from trusted business logic. Use when creating test suites from BL, ensuring BL coverage, or turning requirements into verifiable tests.
allowed-tools: Read, Grep, Glob, Write, Edit, Bash
---

# Generating Tests from Business Logic

**Project-agnostic.** Use `BDD/project_config.yaml` for terminology and code paths when present; write tests in the project's test framework and structure (see **tailor-bdd-skills-for-project**).

## Purpose

Transform trusted business logic into a comprehensive test suite that continuously verifies implementation against business behavior. This is the BL enforcement skill.

## Prerequisites

- Refined business logic document (use `refining-business-logic-for-implementation` first)
- Understanding of the project's testing framework (pytest, behave, etc.)
- Knowledge of the project structure

## Instructions

### Step 1: Parse the Business Logic

Read the refined BL document and identify:

```bash
cat path/to/refined_bl_document.md
```

Extract:
- All business rules (validation, billing, workflows)
- State machine definitions (states and transitions)
- Decision tables
- Invariants
- Error handling rules
- Billing/credit rules

### Step 2: Classify Test Types

Organize tests into these categories:

**A. Rule Tests** - Verify individual business rules
**B. Scenario Tests** - Verify end-to-end workflows
**C. State Transition Tests** - Verify state machine correctness
**D. Edge Case Tests** - Verify boundary conditions
**E. Billing Tests** - Verify credit operations
**F. Regression Tests** - Verify past bugs don't recur

### Step 3: Generate Rule Coverage Tests

For each decision rule, generate three tests:

```markdown
For every decision rule:
  - One happy path test (rule should succeed)
  - One negative path test (rule should fail)
  - One edge/boundary test (boundary condition)
```

**Example:**
```markdown
BL Rule: "Request is rejected if identifier is invalid"

Generated Tests:
1. test_request_accepted_with_valid_identifier() - Happy path
2. test_request_rejected_with_invalid_identifier_format() - Negative path
3. test_request_rejected_with_empty_identifier() - Edge case (empty)
4. test_request_rejected_with_too_short_identifier() - Edge case (boundary)
```

### Step 4: Generate State Transition Tests

For each state transition in the state machine:

```markdown
For every valid transition:
  - Test that transition is allowed

For every forbidden transition:
  - Test that transition is blocked
  - Test that appropriate error is raised

For every terminal state:
  - Test that no further transitions are possible
```

**Example:**
```markdown
BL States: PENDING → VALIDATED → SUBMITTED → PROCESSING → COMPLETED

Generated Tests:
1. test_pending_to_validated_on_valid_input() - Valid transition
2. test_cannot_transition_from_completed_to_processing() - Forbidden transition
3. test_query_in_completed_state_is_terminal() - Terminal state check
```

### Step 5: Generate Billing/Credit Tests

For every charge/refund rule:

```markdown
For every charge rule:
  - Test that charge occurs at the right time
  - Test that charge amount is correct
  - Test that charge does NOT occur when conditions not met

For every refund rule:
  - Test refund conditions
  - Test refund amount matches original charge

For partial success:
  - Test billing semantics
  - Test duplicate request semantics
```

**Example:**
```markdown
BL Rule: "Balance deducted before [external] submission"

Generated Tests:
1. test_balance_deducted_on_submission() - Verify deduction
2. test_balance_not_deducted_if_validation_fails() - Verify no deduction
3. test_balance_not_deducted_if_insufficient() - Verify no deduction
4. test_deduction_amount_matches_cost() - Verify amount
```

### Step 6: Generate Scenario Tests

For each workflow, generate end-to-end scenarios:

```markdown
Scenario Format (Given/When/Then):
  Given: Initial state and conditions
  When: Action is taken
  Then: Expected outcome
```

**Example:**
```markdown
BL Workflow: Query submission and processing

Generated Scenario:
Scenario: Successful query processing
  Given a tenant with sufficient credits
  And a valid MSISDN
  When the user submits a geolocation query
  Then the query is created with status PENDING
  And credits are deducted
  And the query is submitted to the provider
  And the query status becomes SUBMITTED
```

### Step 7: Generate Edge Case Tests

Test boundary conditions and unusual inputs:

```markdown
Common edge cases to test:
  - Empty/null values
  - Minimum and maximum values
  - Concurrent operations
  - Race conditions
  - Resource exhaustion
  - Network failures
  - Timeout scenarios
```

**Example:**
```markdown
Generated Edge Case Tests:
1. test_query_with_maximum_length_msisdn() - Boundary test
2. test_concurrent_query_submission_same_msisdn() - Race condition
3. test_query_when_provider_timeout() - Timeout scenario
4. test_query_submission_with_zero_credits() - Boundary condition
```

### Step 8: Output Test Files

Generate test files in the appropriate format:

**For pytest (Python):**
```python
# tests/test_<domain>_bl.py

import pytest
# Import project models (e.g. Request, Account, Ledger)

class TestRequestSubmissionBL:
    """Tests for [Request/Operation] Submission Business Logic"""

    def test_request_accepted_with_valid_input(self):
        # Given
        account = create_account(balance=100)  # project-specific factory
        valid_input = "..."

        # When
        response = api_client.post('/api/requests/', {...})

        # Then
        assert response.status_code == 201
        assert Request.objects.filter(...).exists()

    def test_request_rejected_with_invalid_input(self):
        # ... implementation
```

**For behave (BDD):**
```gherkin
# features/<domain>_bl.feature

Feature: [Operation] Submission Business Logic

  Scenario: Valid request is accepted
    Given an account with sufficient balance
    And valid input
    When the user submits the [operation]
    Then the request should be created
    And balance should be deducted (if applicable)

  Scenario: Invalid input is rejected
    Given an account with sufficient balance
    And invalid input format
    When the user submits the [operation]
    Then the request should be rejected
    And no balance should be deducted
```

### Step 9: Create Test Coverage Matrix

Document which BL rules are covered by which tests:

```markdown
## Test Coverage Matrix

| BL Rule | Test Type | Test Name | Coverage |
|---------|-----------|-----------|----------|
| Balance deducted on submission | Billing | test_balance_deducted_on_submission | ✅ |
| Invalid input rejected | Rule | test_request_rejected_with_invalid_input | ✅ |
| PENDING → VALIDATED on valid input | State Transition | test_pending_to_validated_on_valid_input | ✅ |
```

### Step 10: Save Generated Tests

```bash
# Create test directory structure
mkdir -p tests/bl
mkdir -p features/bl

# Save generated tests
cat > tests/test_<domain>_bl.py
cat > features/<domain>_bl.feature
```

## Test Generation Templates

### Rule Test Template
```python
def test_{rule_name}_{expected_outcome}():
    """
    BL Rule: {rule_description}

    Expected: {expected_behavior}
    """
    # Given
    {setup_conditions}

    # When
    {action_taken}

    # Then
    {expected_result}
```

### State Transition Test Template
```python
def test_{from_state}_to_{to_state}_{condition}():
    """
    BL Transition: {from_state} → {to_state}

    Condition: {trigger_condition}
    """
    # Given
    entity = create_entity_with_state("{from_state}")

    # When
    entity.{trigger_action}()

    # Then
    assert entity.state == "{to_state}"
```

### Billing Test Template
```python
def test_{billing_event}_credits_{expected_action}():
    """
    BL Billing Rule: {billing_rule}

    Expected: {credits_action} (deducted/not_deducted/refunded)
    """
    # Given
    initial_credits = {amount}
    tenant = create_tenant_with_credits(initial_credits)

    # When
    {billing_event_action}

    # Then
    final_credits = tenant.credits
    assert final_credits == {expected_credits}
```

## Test Coverage Checklist

For each BL rule, verify coverage of:

- [ ] Happy path (successful execution)
- [ ] Negative path (error conditions)
- [ ] Edge cases (boundaries, unusual inputs)
- [ ] State transitions (valid and invalid)
- [ ] Billing operations (charges and refunds)
- [ ] Concurrent operations (race conditions)
- [ ] Idempotency (duplicate operations)
- [ ] Timeout scenarios
- [ ] Error recovery (fallback, retry)

## Examples

### Example 1: Generating Tests for Credit Logic

User request:
```
Generate tests from the credit deduction business logic
```

You would:
1. Read the refined BL for credit rules
2. Extract rules:
   - "Credits deducted before provider submission"
   - "Query rejected if insufficient credits"
   - "No refund for failed queries"
3. Generate tests:
   ```python
   def test_credits_deducted_on_query_submission():
       """Verify credits are deducted when query is submitted"""
       pass

   def test_query_rejected_if_insufficient_credits():
       """Verify query is rejected when tenant lacks credits"""
       pass

   def test_no_refund_for_failed_queries():
       """Verify no credit refund when provider returns error"""
       pass

   def test_credits_deducted_prevents_negative_balance():
       """Verify credits cannot go negative due to race condition"""
       pass

   def test_concurrent_queries_with_exact_balance():
       """Verify concurrent queries respect balance limits"""
       pass
   ```
4. Create test file: `tests/test_credit_bl.py`
5. Generate coverage matrix

### Example 2: Generating Tests for Provider Fallback

User request:
```
Create tests for the provider fallback logic
```

You would:
1. Parse fallback BL rules
2. Generate state transition tests:
   ```python
   def test_fallback_to_secondary_on_primary_timeout():
       """Verify fallback occurs when primary times out"""
       pass

   def test_no_fallback_on_invalid_target_error():
       """Verify no fallback for INVALID_TARGET from primary"""
       pass

   def test_fallback_occurs_once_per_query():
       """Verify fallback happens only once, not multiple times"""
       pass

   def test_query_stalled_after_all_providers_fail():
       """Verify STALLED status after all providers exhausted"""
       pass
   ```
3. Generate edge case tests:
   ```python
   def test_both_primary_and_secondary_timeout():
       """Verify STALLED when both providers timeout"""
       pass

   def test_primary_returns_500_error():
       """Verify fallback for 500 error"""
       pass

   def test_fallback_with_different_cost_provider():
       """Verify correct cost used for fallback provider"""
       pass
   ```

## Output Artifacts

The skill generates:

1. **Test files** in appropriate format (pytest, behave, etc.)
2. **Test coverage matrix** mapping BL rules to tests
3. **Coverage statistics** showing percentage of BL rules tested
4. **Test documentation** explaining test organization and naming conventions
