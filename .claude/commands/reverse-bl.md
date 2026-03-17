---
name: reverse-bl
description: Extract business logic from code into structured documentation
disable-model-invocation: false
allowed-tools:
  - Skill
---

# Reverse Engineer Business Logic

Extracts business logic from code into a structured markdown document with 11 sections covering purpose, actors, flows, rules, billing, edge cases, and more.

## Usage
`/reverse-bl <target>`

## Examples
- `/reverse-bl order creation`
- `/reverse-bl payment processing flow`
- `/reverse-bl src/services/auth.js`
- `/reverse-bl User model`

---
**Instructions for Claude:**

When invoked, use the Skill tool to call:
`skill: "reverse-engineering-business-logic", args: "<user's input>"`
