# BDD Commands Quick Reference

This guide shows how to use the BDD prompt commands and orchestrator skill (installed as a [pi](https://github.com/earendil-works/pi-coding-agent) package) to extract, validate, and refine business logic from code.

## Prerequisites

Before using any BDD commands, configure your project:

```bash
/skill:tailor-bdd-skills-for-project
```

This creates `BDD/project_config.yaml` with:
- BL output directory
- Domain terminology
- Code entry points

## Commands

### Extract Business Logic

```bash
/reverse-bl <target>
```

Extract business logic from code into a structured document.

**Examples:**
```bash
/reverse-bl order creation
/reverse-bl payment processing flow
/reverse-bl src/services/auth.js
/reverse-bl User model
```

**Output:** `business_logic/current/<category>/<feature>.md`

---

### Validate Business Logic

```bash
/validate-bl <document-or-feature>
```

Validate documented BL against actual code implementation.

**Examples:**
```bash
/validate-bl business_logic/current/endpoints/order-creation.md
/validate-bl payment processing
/validate-bl order-creation
```

**Output:** Validation report with coverage percentage and issues

---

### Find Gaps

```bash
/gap-analysis <document-or-feature>
```

Identify missing, vague, or contradictory business logic.

**Examples:**
```bash
/gap-analysis business_logic/current/workflows/payment-flow.md
/gap-analysis user registration
/gap-analysis order-fulfillment
```

**Output:** Gap analysis report with severity levels

---

### Refine Business Logic

```bash
/refine-bl <document-or-feature>
```

Rewrite vague BL into deterministic, testable rules.

**Examples:**
```bash
/refine-bl business_logic/proposal/endpoints/pricing-calculator.md
/refine-bl subscription billing
/refine-bl inventory management
```

**Output:** Refined BL document with state machines

---

### Derive Acceptance Criteria

```bash
/derive-acceptance <document-or-feature>
```

Convert BL into user stories and Given/When/Then scenarios.

**Examples:**
```bash
/derive-acceptance business_logic/current/endpoints/user-registration.md
/derive-acceptance order workflow
/derive-acceptance password reset
```

**Output:** Acceptance criteria document with user stories

---

### Generate Tests

```bash
/generate-tests <document-or-feature>
```

Generate tests from business logic documentation.

**Examples:**
```bash
/generate-tests business_logic/current/endpoints/cart-checkout.md
/generate-tests payment processing
/generate-tests user authentication
```

**Output:** Test file with scenario, rule, and edge case tests

---

## Orchestrator Skill

For comprehensive analysis, use the bdd-orchestrator skill:

```bash
/skill:bdd-orchestrator order creation
```

Or just ask naturally:

```
"Understand order creation from code and prepare it for implementation"
```

The orchestrator will:
1. Check project configuration
2. Extract business logic
3. Validate against code
4. Analyze gaps
5. Refine into deterministic rules
6. Derive acceptance criteria
7. Generate tests

It will ask for confirmation before each step.

---

## Common Workflows

### Quick Analysis

```bash
/reverse-bl <feature>
/validate-bl <feature>
```

### Sprint Preparation

```bash
/reverse-bl <feature>
/validate-bl <feature>
/gap-analysis <feature>
/refine-bl <feature>
/derive-acceptance <feature>
```

### Test Generation

```bash
/generate-tests <feature>
```

### Quality Check

```bash
/gap-analysis <document>
/refine-bl <document>
```

---

## Output Structure

```
business_logic/
├── current/
│   ├── endpoints/
│   ├── models/
│   ├── workflows/
│   └── billing/
├── proposal/
│   └── (refined BL)
└── historical/
```

---

## Tips

1. **Start with `/skill:tailor-bdd-skills-for-project`** - Configure once per project
2. **Use `/reverse-bl` first** - Extract BL from code before other operations
3. **Validate early** - Catch issues with `/validate-bl` before refinement
4. **Iterate on gaps** - Use `/gap-analysis` and `/refine-bl` together
5. **Generate tests last** - `/generate-tests` works best with refined BL

---

## Getting Help

- **Full documentation:** See `README.md`
- **Skill details:** See `skills/*/SKILL.md`
- **Examples:** See `skills/reverse-engineering-business-logic/examples/`
