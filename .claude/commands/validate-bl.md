---
name: validate-bl
description: Validate business logic documentation against actual code implementation
disable-model-invocation: false
allowed-tools:
  - Skill
---

# Validate Business Logic Against Code

Validates documented business logic by mapping each rule to code evidence. Reports coverage percentage and flags rules as implemented, partially implemented, contradicted, or not found.

## Usage
`/validate-bl <bl-document-path-or-feature>`

## Examples
- `/validate-bl business_logic/current/endpoints/order-creation.md`
- `/validate-bl payment processing`
- `/validate-bl order-creation`

---
**Instructions for Claude:**

When invoked, use the Skill tool to call:
`skill: "validate-business-logic-against-code", args: "<user's input>"`
