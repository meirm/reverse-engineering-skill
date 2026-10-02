# Bootstrapping Business Logic Documentation

*Last updated: 2026-03-15*

## Overview

When starting with an undocumented or partially documented codebase, use this systematic approach to build out the BL knowledge base. **BL output directory** is set in `BDD/project_config.yaml` → `bl_output.root` (or default `business_logic/`). Use **entry_points** from that config for discovery paths and patterns; below are generic examples.

### Phase 1: Discovery (Map the Landscape)

**Goal:** Identify all entry points and business capabilities. Prefer patterns from `BDD/project_config.yaml` → `entry_points` when present.

**1. Discover API Endpoints:**
```bash
# Examples (adapt to your project's paths and patterns):
# ViewSets / class-based views
grep -r "class.*ViewSet" backend/ --include="*.py"
# Function-based / decorator-based
grep -r "@api_view\|@router\|@app\.post" backend/ --include="*.py"
```

**2. Discover Models/Entities:**
```bash
# ORM models / schemas
grep -r "class.*Model\|class.*Schema" backend/ --include="*.py"
find backend/ -name "*model*.py" -o -name "*schema*.py"
```

**3. Discover Workflows/Background Tasks:**
```bash
# Task decorators / job handlers
grep -r "@shared_task\|@task\|@celery" backend/ --include="*.py"
# CLI / management commands
find backend/ -path "*/management/commands/*.py"
```

**4. Discover Billing/Charging Logic:**
```bash
# Charge / refund / credit / quota
grep -r "charge\|refund\|credit\|quota" backend/ --include="*.py" -l
```

**Output:** Create a discovery list in `[bl_root]/current/DISCOVERY.md` with all found items.

### Phase 2: Prioritization (What to Document First)

Prioritize based on business impact and complexity:

**Tier 1 (Critical Path - Do First):**
- User-facing API endpoints (authentication, CRUD operations)
- Billing/credit operations (money-related, highest risk)
- Core domain models (your main entities)
- Background workflows that modify data

**Tier 2 (Important - Do Second):**
- Admin/management operations
- Scheduled tasks and periodic jobs
- Integration with external services
- Notification/alerting logic

**Tier 3 (Nice to Have - Do Last):**
- Utility endpoints
- Read-only views
- Legacy/deprecated code paths

### Phase 3: Initial Documentation (First Pass)

**Create the state-based directory structure:** Use `bl_output.root` from `BDD/project_config.yaml` if set; otherwise `business_logic`.
```bash
# Example: default root business_logic (replace ROOT with your bl_output.root if different)
ROOT="${BL_ROOT:-business_logic}"
mkdir -p $ROOT/{current,proposal,under_development,historical}
for state in current proposal under_development historical; do
  mkdir -p $ROOT/$state/{endpoints,models,workflows,billing}
done
```

**Create initial index and glossary for current BL:**
```bash
# business_logic/current/index.md
cat > business_logic/current/index.md << 'EOF'
# Business Logic Documentation Index

*Last updated: 2026-03-15*
*State: Current (Production)*
*Total analyses: 0*

## Endpoints
*No analyses yet*

## Models
*No analyses yet*

## Workflows
*No analyses yet*

## Billing
*No analyses yet*

## Glossary
See [glossary.md](glossary.md)
EOF

# business_logic/current/glossary.md
cat > business_logic/current/glossary.md << 'EOF'
# Business Glossary

*Last updated: 2026-03-15*
*State: Current (Production)*

## Terms
*No terms documented yet*
EOF
```

**Create placeholder indexes for other states:**
```bash
# business_logic/proposal/index.md
cat > business_logic/proposal/index.md << 'EOF'
# Proposed Business Logic Documentation Index

*Last updated: 2026-03-15*
*State: Proposed (Not Yet Implemented)*

This index tracks proposed business logic changes that are under design or review but have not yet been implemented in code.

## Proposed Changes
*No proposals yet*

## Purpose
Use this directory to document proposed BL changes before implementation, enabling:
- Comparison of proposed vs current BL (gap analysis)
- Design review and feedback before coding
- Clear documentation of intended business logic changes
EOF

# business_logic/under_development/index.md
cat > business_logic/under_development/index.md << 'EOF'
# Business Logic Under Development

*Last updated: 2026-03-15*
*State: Under Development (Partially Implemented)*

This index tracks business logic changes that are actively being developed in feature branches.

## Under Development
*No items under development yet*

## Purpose
Use this directory to document BL changes during active development, enabling:
- Tracking progress from proposal to implementation
- Comparison of under_development vs current BL (what's changing)
- Parallel development without disrupting current documentation
EOF

# business_logic/historical/index.md
cat > business_logic/historical/index.md << 'EOF'
# Historical Business Logic Documentation

*Last updated: 2026-03-15*
*State: Historical (Deprecated or Removed)

This index tracks business logic that has been deprecated, removed, or replaced.

## Historical BL
*No historical items yet*

## Purpose
Use this directory to preserve old BL for reference, enabling:
- Understanding of historical business decisions
- Audit trail of business logic evolution
- Comparison of historical vs current BL (what changed and why)
EOF
```

### Phase 4: Batch Processing (Systematic Documentation)

**For Endpoints (Endpoint-to-BL):**
```bash
# Get list of all ViewSets
grep -r "class.*ViewSet" backend/apps/*/api/views.py | cut -d: -f1 | sort -u

# For each ViewSet, document to business_logic/current/endpoints/:
# - GET list/create
# - GET retrieve/update/delete
# - Custom actions (@action)
```

**For Models (Model-to-BL):**
```bash
# Get all model classes
grep -rh "^class.*models\.Model" backend/apps/*/models.py | grep -o "class [A-Z][a-zA-Z]*" | sort -u

# For each model, document to business_logic/current/models/:
# - Lifecycle (create → update → delete → archive)
# - State transitions
# - Validation rules
# - Related model side effects
```

**For Workflows (Workflow-to-BL):**
```bash
# Get all Celery tasks
grep -rh "@shared_task\|@task" backend/apps/ --include="*.py" | grep "def " | grep -o "def [a-z_]*" | sort -u

# For each task, document to business_logic/current/workflows/:
# - Trigger condition
# - Main business flow
# - Error handling
# - Side effects
```

**For Billing (Billing-to-BL):**
```bash
# Find all credit operations
grep -r "CreditService\." backend/apps/ --include="*.py" -A 2 | grep -E "(reserve|consume|release|adjust)_credits"

# Document to business_logic/current/billing/:
# - When credits are charged
# - When credits are refunded
# - Partial charge rules
# - Race conditions
```

### Phase 5: Cross-Linking (Connect the Dots)

After first pass, add bidirectional links:

1. **Endpoints → Models:** Link endpoint to the models it creates/modifies
2. **Workflows → Endpoints:** Link background tasks to the endpoints that trigger them
3. **Billing → All:** Link billing logic to every operation that charges credits
4. **State Machines:** Link model state transitions to the endpoints/workflows that cause them

**Update the index:**
```markdown
## Endpoints
- [Order Creation](endpoints/order-creation.md)
  - Code: `backend/api/views.py:145` (example path)
  - Related: [Order Lifecycle](../models/order-lifecycle.md), [Charging](../billing/charging-rules.md)
```

### Bootstrap Command Template

For a complete bootstrap, you can request:

```
Bootstrap business logic documentation for this project:
1. Discover all endpoints, models, and workflows
2. Prioritize by business impact
3. Document Tier 1 items (critical path)
4. Create cross-links between related analyses
5. Build the index and glossary

Focus on: core user-facing flows and billing (if any) first.
```

**This will:**
- Scan project code roots (from config or e.g. `backend/`, `src/`) for entry points
- Create the `[bl_root]/current/` structure
- Generate 10-15 initial analyses for critical path
- Build `[bl_root]/current/index.md` with links
- Extract business terms for `[bl_root]/current/glossary.md` (align with project config terminology if present)

### Phase 6: Comparing Business Logic Across States

**NEW FEATURE:** The state-based organization enables powerful comparison and gap analysis between different versions of business logic.

**Comparison Use Cases:**

**1. Proposal vs Current (Gap Analysis)**
```bash
# Compare proposed BL to current BL
Compare:
- [bl_root]/proposal/endpoints/new-feature.md
- [bl_root]/current/endpoints/existing-feature.md

# Identify gaps:
- What will change?
- What are the business impacts?
- Is the proposal complete?
```

**Example:**
```markdown
## Gap Analysis: New Feature Proposal

**Proposal:** [bl_root]/proposal/endpoints/advanced-feature.md
**Current:** [bl_root]/current/endpoints/core-feature.md

**What Changes:**
- NEW: Real-time geolocation tracking
- NEW: WebSocket-based updates
- MODIFIED: Credit charging (now per-minute instead of per-query)
- REMOVED: Fallback to secondary provider

**Business Impact:**
- Users get real-time results (better UX)
- Credit costs may increase (per-minute billing)
- No fallback increases failure risk
- Need to update client to use WebSocket

**Gap Analysis:**
- Proposal doesn't address error handling for WebSocket failures
- Current BL doesn't document how to handle long-running queries
- Migration path unclear (how do existing queries transition?)
```

**2. Under Development vs Current (Change Impact)**
```bash
# Compare under_development BL to current BL
Compare:
- [bl_root]/under_development/endpoints/updated-feature.md
- [bl_root]/current/endpoints/old-feature.md

# Identify what's changing:
- What's different in the new implementation?
- Are there breaking changes?
- Do we need to update documentation?
```

**3. Historical vs Current (Evolution Tracking)**
```bash
# Compare historical BL to current BL
Compare:
- [bl_root]/historical/endpoints/deprecated-feature.md
- [bl_root]/current/endpoints/replacement-feature.md

# Understand evolution:
- Why was it changed?
- What business problems did the old version have?
- What did we learn from the old implementation?
```

**4. Migration Planning**
```bash
# When implementing a proposal:
1. Copy proposal → under_development when implementation starts
2. Compare under_development vs current during implementation
3. Move under_development → historical (old) when deploying
4. Move under_development → current when deployed
```

**Comparison Workflow:**

**Step 1: Identify Files to Compare**
```bash
# Find BL docs in different states for the same feature
find [bl_root]/proposal -name "*<feature>*"
find [bl_root]/current -name "*<feature>*"
find [bl_root]/under_development -name "*<feature>*"
find [bl_root]/historical -name "*<feature>*"
```

**Step 2: Read Both Documents**
```bash
# Read proposal BL
cat [bl_root]/proposal/endpoints/feature-v2.md

# Read current BL
cat [bl_root]/current/endpoints/core-feature.md
```

**Step 3: Section-by-Section Comparison**
Compare each of the 11 sections:
- **Business Purpose:** Did the purpose change?
- **Actors:** Are new actors involved?
- **Preconditions:** Are new preconditions added?
- **Main Flow:** How did the flow change?
- **Decision Rules:** What rules changed/added/removed?
- **State Transitions:** Are new states introduced?
- **Billing:** How does billing change?
- **Exceptions:** New edge cases?
- **Data:** New data fields or persistence changes?

**Step 4: Document the Differences**
Create a comparison document:
```markdown
# BL Comparison: [Feature Name]

**States Compared:** [State 1] vs [State 2]
**Date:** [Comparison date]

## Summary of Changes
[Brief overview of what changed]

## Section-by-Section Differences

### 1. Business Purpose
- **[State 1]:** [Original purpose]
- **[State 2]:** [New purpose]
- **Change:** [What changed and why]

### 2. Decision Rules
- **Added:** [New rules]
- **Removed:** [Old rules]
- **Modified:** [Changed rules]

### ... [Continue for all relevant sections]

## Business Impact Analysis
- **Breaking Changes:** [List]
- **User Impact:** [Describe]
- **Data Migration:** [Needed?]
- **Documentation Updates:** [What needs updating]

## Recommendations
- [Actionable recommendations]
```

**Step 5: Identify Gaps**
When comparing proposal vs current:
- **Missing in proposal:** Essential BL from current that proposal doesn't address
- **Missing in current:** New requirements proposal addresses
- **Inconsistencies:** Contradictions between states
- **Completeness:** Is proposal production-ready?

**Use Cases for Comparison:**

**Before Implementation:**
- Review proposal BL for completeness
- Identify gaps before coding starts
- Validate business requirements
- Get stakeholder alignment

**During Implementation:**
- Track implementation progress vs proposal
- Ensure all changes are implemented
- Catch scope creep
- Validate BL matches actual code

**After Implementation:**
- Verify implementation matches proposal
- Move to current when complete
- Archive old BL to historical
- Update training documentation

## Related References

- [Quick Start Guide](quick-start.md) - Getting started with single analyses
- [Advanced Modes](advanced-modes.md) - Diff, Change Impact, and Gap Analysis modes
- [Collaboration](collaboration.md) - Team workflows and maintenance
- [Validation](validation.md) - Quality assurance and review processes
