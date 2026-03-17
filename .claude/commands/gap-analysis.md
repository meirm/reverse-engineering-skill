---
name: gap-analysis
description: Identify gaps, ambiguities, and contradictions in business logic documentation
disable-model-invocation: false
allowed-tools:
  - Skill
---

# Analyze Business Logic Gaps

Identifies missing, vague, underspecified, or contradictory business logic within BL documents. Finds incomplete edge cases, missing state transitions, ambiguous terminology, and weak rules.

## Usage
`/gap-analysis <bl-document-path-or-feature>`

## Examples
- `/gap-analysis business_logic/current/workflows/payment-flow.md`
- `/gap-analysis user registration`
- `/gap-analysis order-fulfillment`

---
**Instructions for Claude:**

When invoked, use the Skill tool to call:
`skill: "analyze-business-logic-gaps", args: "<user's input>"`
