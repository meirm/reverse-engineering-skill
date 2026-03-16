# From Code to Business Logic — and Back Again

**A Practical Method to Let Business Logic Lead Software Development**

Most engineers agree with a simple principle:

**Software should be developed from business logic to code.**

The idea is straightforward:

```
Business Logic → Implementation → Tests
```

- The **business logic (BL)** describes what the system must do.
- The **code** implements those rules.
- The **tests** verify that the implementation behaves according to those rules.

In this model, business logic is the source of truth, and the testing framework ensures that the code does not drift away from it.

In theory, this is how software development works.

In reality, most systems evolve in the opposite direction.

---

## Code → Behavior → Guessing the Business Logic

- Documentation becomes outdated.
- Original design decisions are lost.
- Business rules are scattered across controllers, services, database queries, background tasks, and configuration flags.

Eventually, when someone asks:

- "What are the actual rules for this feature?"
- "What exactly happens when this condition occurs?"
- "Why did the system behave this way?"

the only reliable answer is:

> "Let's read the code."

At that point, the intended development model breaks down.

You cannot develop from business logic to code if the business logic itself is unknown.

So the first problem becomes:

**How do we reconstruct the business logic of a system that already exists?**

---

## The Missing Capability: Reverse-Engineering Business Logic

Most engineering practices focus on writing new systems. Very few address the problem of understanding old ones.

What is needed is a specific capability:

**Reverse engineering business logic from code.**

This does not mean documenting functions or describing modules.

It means extracting the **operational truth** of the system.

For example, a code structure might look like this:

```
controller → service → provider → model
```

But the real business flow might be:

1. validate request
2. verify eligibility
3. select processing strategy
4. execute operation
5. normalize results
6. update system state
7. record outcome
8. return response

The technical structure is not the business structure.

Reverse engineering business logic means translating implementation details into domain rules that describe what the system actually does.

A proper BL extraction typically identifies:

- **Actors** — Who triggers the process.
- **Preconditions** — What must be true before execution begins.
- **Decision rules** — The conditions that change system behavior.
- **State transitions** — How objects move between states.
- **Operational workflows** — The sequence of business actions.
- **Exception paths** — What happens when things fail.
- **Data effects** — What is stored or modified.
- **Ambiguities** — Behaviors that cannot be inferred with certainty.

This produces something extremely valuable:

**A reliable description of what the system actually does.**

Not what the documentation says. Not what developers remember.

**What the system really does.**

But extracting BL is only the first step.

---

## Turning Business Logic Into the Source of Truth

Once the business logic has been reconstructed, the goal is not simply documentation.

The goal is to restore the correct development model:

```
Business Logic → Code → Tests
```

For that to work, the BL must become:

- trustworthy
- complete
- precise
- testable

Achieving that requires additional capabilities.

Below are five skills that transform extracted business logic into something that can guide and control software development.

---

## Skill 1 — Reverse Engineer Business Logic

This is the starting point.

When documentation is missing or unreliable, the only way to recover the system's rules is by analyzing the implementation itself.

This skill extracts business logic from code by identifying:

- workflows
- rules
- states
- actors
- edge cases

The output is structured documentation that describes the system in business terms rather than technical ones.

**Instead of:**

> The system calls function A, then B, then C.

**The documentation describes:**

> The system validates input, determines eligibility, selects a processing strategy, performs the operation, and records the outcome.

This process creates the first trustworthy description of the system's behavior.

Without it, teams remain trapped in code-driven development.

---

## Skill 2 — Validate Business Logic Against Code

Once BL documentation exists, a new problem appears.

**Is the documentation accurate?**

Even freshly written BL can contain misunderstandings.

This skill compares the documented rules with the implementation and determines whether each rule is:

- implemented
- partially implemented
- contradicted by code
- missing in the implementation

The result is a coverage map that connects business rules to concrete code locations.

This step transforms BL from speculative documentation into verified knowledge.

---

## Skill 3 — Analyze Business Logic Gaps

Even when BL matches the code, it may still be incomplete.

Typical problems include:

- missing edge cases
- undefined state transitions
- vague rules
- ambiguous language
- incomplete workflows

For example, a statement like:

> The system retries when necessary.

is not a rule. It is an interpretation.

A gap analysis identifies such weaknesses and forces the logic to become explicit.

Clear business logic must answer questions like:

- Under what exact conditions does a retry occur?
- How many retries are allowed?
- What happens after retries fail?
- Which states are considered terminal?

This process turns descriptive logic into deterministic logic.

---

## Skill 4 — Refine Business Logic for Implementation

Once ambiguities are discovered, the BL itself must be improved.

This step restructures business logic so that it becomes easier to implement and verify.

Typical improvements include:

- converting narrative descriptions into explicit rules
- defining state machines
- creating decision tables
- separating policies from workflows
- defining invariants and constraints

**For example:**

**Before:**

> The system may attempt an alternative process if the first attempt fails.

**After:**

```
If the first attempt returns a timeout
    retry using the alternative process

If the first attempt returns an invalid request error
    do not retry

If both attempts fail
    mark the operation as failed
```

Refined BL removes interpretation and produces clear operational rules.

---

## Skill 5 — Generate Tests from Business Logic

Once the BL is precise and trusted, it becomes possible to generate tests directly from it.

This is where the development model finally becomes complete.

```
Business Logic → Tests → Implementation
```

Tests derived from BL include:

- **rule tests** — verifying decision logic
- **state transition tests** — verifying allowed and forbidden transitions
- **edge-case tests** — verifying exceptional scenarios
- **invariant tests** — verifying system constraints

These tests ensure that the implementation continues to respect the business logic.

This reframes the purpose of testing.

Tests are not checking whether the code runs.

**They are verifying that the code faithfully implements the BL.**

---

## Skill 6 — Derive Acceptance Criteria from Business Logic

Once business logic is structured and verified, it becomes possible to use it directly in planning and development.

This skill converts BL into:

- acceptance criteria
- scenario descriptions
- implementation checklists
- development tasks

Instead of writing vague feature descriptions, teams can rely on explicit business rules that define the expected behavior.

This ensures that everyone—architects, developers, testers, and product owners—works from the same foundation.

---

## The Full Loop

Once these skills exist, the development process becomes a continuous cycle:

```
Code → Extract BL → Validate BL → Refine BL
             ↓
        Generate Tests
             ↓
BL → Acceptance Criteria → New Code
```

The system evolves, but the business logic remains the center of gravity.

---

## Why This Matters

Most long-lived software systems suffer from the same condition:

- the code is the only reliable source of truth
- documentation no longer reflects reality
- business rules are fragmented
- system behavior becomes difficult to predict

Reverse engineering business logic provides a way to restore clarity.

Once extracted, validated, refined, and tested, business logic can finally reclaim its original role:

**the blueprint that software follows.**

Not the other way around.
