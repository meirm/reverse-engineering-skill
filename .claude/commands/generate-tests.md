---
name: generate-tests
description: Generate tests from business logic documentation
disable-model-invocation: false
allowed-tools:
  - Skill
---

# Generate Tests from Business Logic

Generates scenario tests, rule tests, edge case tests, state transition tests, and billing tests from trusted business logic. Creates comprehensive test coverage from BL documents.

## Usage
`/generate-tests <bl-document-path-or-feature>`

## Examples
- `/generate-tests business_logic/current/endpoints/cart-checkout.md`
- `/generate-tests payment processing`
- `/generate-tests user authentication`

---
**Instructions for Claude:**

When invoked, use the Skill tool to call:
`skill: "generate-tests-from-business-logic", args: "<user's input>"`
