---
name: refine-bl
description: Refine business logic into deterministic, testable rules
disable-model-invocation: false
allowed-tools:
  - Skill
---

# Refine Business Logic for Implementation

Rewrites vague business logic into deterministic, testable rules by separating policy from mechanism, normalizing terminology, and defining explicit state machines. Makes BL ready for implementation.

## Usage
`/refine-bl <bl-document-path-or-feature>`

## Examples
- `/refine-bl business_logic/proposal/endpoints/pricing-calculator.md`
- `/refine-bl subscription billing`
- `/refine-bl inventory management`

---
**Instructions for Claude:**

When invoked, use the Skill tool to call:
`skill: "refine-business-logic-for-implementation", args: "<user's input>"`
