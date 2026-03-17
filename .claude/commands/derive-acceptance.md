---
name: derive-acceptance
description: Derive acceptance criteria and user stories from business logic
disable-model-invocation: false
allowed-tools:
  - Skill
---

# Derive Acceptance Criteria from Business Logic

Converts trusted business logic into product-owner-grade acceptance criteria and developer-ready tasks using Given/When/Then scenarios. Ideal for sprint planning and requirements definition.

## Usage
`/derive-acceptance <bl-document-path-or-feature>`

## Examples
- `/derive-acceptance business_logic/current/endpoints/user-registration.md`
- `/derive-acceptance order workflow`
- `/derive-acceptance password reset`

---
**Instructions for Claude:**

When invoked, use the Skill tool to call:
`skill: "derive-acceptance-criteria-from-business-logic", args: "<user's input>"`
