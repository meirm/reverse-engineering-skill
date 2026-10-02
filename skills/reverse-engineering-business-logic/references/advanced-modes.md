# Advanced Analysis Modes

*Last updated: 2026-03-15*

## Overview

This reference document describes three advanced analysis modes for validating, updating, and discovering business logic documentation.

## Mode 1: Diff Mode - BL Docs vs Code

Use this mode to validate that business logic documentation matches actual code implementation. Detect stale docs, unimplemented rules, and hidden behavior.

### When to Use Diff Mode

- Before/after code changes to verify documentation accuracy
- Periodic audits to ensure BL docs stay current
- When troubleshooting "it worked in docs but not in production"
- During code reviews to validate BL impact

### Diff Workflow

#### Step 1: Load Existing BL Documentation

Read the BL file:
```bash
# Example: Check geolocation query creation docs
cat business_logic/endpoints/geolocation-query-creation.md
```

Extract documented rules:
- Decision rules (if/then logic)
- State transitions
- Billing/credit effects
- Preconditions and validations

#### Step 2: Analyze Current Code Implementation

Read the actual source code:
```bash
# Find the view/handler
grep -r "class GeolocationQueryViewSet" backend/

# Read the implementation
# Trace through all code paths
```

Look for:
- All decision branches (if/elif/else)
- Exception handlers
- Background task dispatches
- State changes
- Credit operations
- External service calls

#### Step 3: Compare and Identify Gaps

Create a diff report with these sections:

**1. Matching Rules** (✓ Documented and implemented)
- Rules that work as documented

**2. Missing in Docs** (⚠ Implemented but not documented)
- Code behavior that BL docs don't mention
- New validation rules added later
- Silent fallbacks or edge cases
- Hidden side effects

**3. Missing in Code** (✗ Documented but not implemented)
- Rules described in BL but not in code
- Business requirements not yet implemented
- Stale documentation from removed features

**4. Contradictions** (⚠ Docs say X, code does Y)
- Opposite behavior between docs and code
- Different state transitions than documented
- Billing logic that doesn't match docs

**5. Ambiguities Resolved** (✓ Docs were unclear, code provides answer)
- Questions from BL docs that code answers definitively
- Contradictions within docs themselves

#### Step 4: Update BL Documentation

For each gap:
- **Missing in Docs**: Add to the BL file with "⚠ ADDED: [date]" note
- **Missing in Code**: Create ticket or mark as "TODO: Implement"
- **Contradictions**: Update BL to match code (or vice versa if code is wrong)
- **Ambiguities**: Update BL with clarification from code

**Update the BL file header:**
```markdown
# [Business Capability Name]

*Analysis generated: 2026-03-15*
*Code analyzed: `backend/apps/core/api/views.py:145-189`*
*Last updated: 2026-03-20* ← Update this
*Last validated: 2026-03-20* ← Add this
*Validation status: Stale docs discovered, updated* ← Add this
```

### Example Diff Analysis

User request:
```
Check if the geolocation query creation BL docs still match the current code
```

You would:
1. Load `business_logic/endpoints/geolocation-query-creation.md`
2. Read `backend/apps/core/api/views.py` GeolocationQueryViewSet
3. Trace all code paths in `create()` method
4. Compare documented rules vs actual implementation
5. Generate diff report

**Diff Report Output:**
```markdown
# BL Diff Report: Geolocation Query Creation

*Generated: 2026-03-20*
*BL Doc: business_logic/endpoints/geolocation-query-creation.md*
*Code: backend/apps/core/api/views.py:145-189*

## Summary
- ✓ 8 rules match documentation
- ⚠ 2 rules implemented but not documented
- ✗ 0 rules documented but not implemented
- ⚠ 1 contradiction found

## Matching Rules (✓)
1. Target with no active IMSI → reject "Target not active"
2. User quota < cost → reject "Insufficient credits"
3. Provider timeout → fallback to secondary provider
4. Both providers fail → mark FAILED, charge 50%
5-8. [Additional matching rules]

## Missing in Docs (⚠)
1. **Rate limiting added**: Code now checks `UserQueryRateLimit` - max 10 queries/minute
   - Location: `views.py:167-169`
   - Impact: Returns 429 Too Many Requests
   - Action: Add to Decision Rules section

2. **Async credit charge**: Credits are charged by background task, not synchronously
   - Location: `tasks.py:89` (called via `delay()`)
   - Impact: Deduction happens after response, can show temporary negative balance
   - Action: Update Billing/Credit Impact section with async note

## Missing in Code (✗)
None

## Contradictions (⚠)
1. **Fallback behavior**: Docs say "fallback to secondary provider" but code only retries same provider 3x
   - Code: `views.py:178` - `retry_provider(provider, max_retries=3)`
   - Docs: Line 45 says "fallback to secondary provider"
   - Resolution: Update docs to say "retries same provider 3x"
   - Ticket: Should we implement actual fallback to secondary?

## Ambiguities Resolved (✓)
1. **Race condition**: Docs asked "does deduction happen before or after validation?"
   - Code shows: Deduction at line 175, validation at 182 → deduction FIRST
   - Resolution: Update docs with confirmed race condition note

## Recommendations
1. Update BL docs with rate limiting rule (high priority - affects users)
2. Clarify async credit charge in Billing section
3. Fix fallback documentation to match actual retry behavior
4. Consider implementing actual secondary provider fallback
```

---

## Mode 2: Change Impact Analysis - Code → BL

Use this mode to analyze how code changes affect business logic. Generate business impact summaries for pull requests, releases, or stakeholder communication.

### When to Use Change Impact Analysis

- Pull requests - Explain business impact of code changes
- Release notes - Generate user-facing change summaries
- Stakeholder updates - Translate code changes to business language
- Risk assessment - Identify BL changes that need testing
- Compliance audits - Track who changed what business rules and when

### Change Impact Workflow

#### Step 1: Identify Code Changes

Get the diff:
```bash
# Compare branches
git diff main...feature-branch

# Compare commits
git diff abc123 def456

# Specific files
git diff main feature-branch -- backend/apps/core/api/views.py
```

Filter for business-relevant changes:
- Views, handlers, endpoints (user-facing behavior)
- Models, serializers (data rules)
- Services, business logic (domain rules)
- Tasks, workflows (background processes)
- Settings, config (feature flags, limits)

#### Step 2: Map Code Changes to BL Categories

For each changed file, determine:
- **Which BL analysis does this affect?**
  - `business_logic/endpoints/*` for view/handler changes
  - `business_logic/models/*` for model/serializer changes
  - `business_logic/workflows/*` for task/workflow changes
  - `business_logic/billing/*` for credit/quota changes

- **What BL section changed?**
  - Decision rules (new/removed/modified if/then)
  - State transitions (new states, different triggers)
  - Billing/credit (charge amounts, timing, refunds)
  - Preconditions (new validations, relaxed constraints)
  - Exceptions/edge cases (new error cases, removed cases)

#### Step 3: Generate Business Impact Statement

For each BL change, document:

**1. Change Description** (in business language)
- What changed in plain English
- Who is affected (users, admins, systems)

**2. Business Impact**
- Does this add/remove/restrict capability?
- Does this change costs or pricing?
- Does this affect user experience?
- Does this change data retention or privacy?

**3. Risk Assessment**
- Breaking changes (backward incompatible)
- Data migration required
- Performance impact
- Security implications
- Compliance impact

**4. Documentation Updates Required**
- Which BL files need updating
- New sections to add
- Sections to remove or deprecate

**5. Testing Recommendations**
- What business scenarios to test
- Edge cases to verify
- Integration points to check

#### Step 4: Update BL Documentation

After code review/approval:
1. Update affected BL analysis files
2. Add "CHANGED: [date]" notes to modified sections
3. Update file header with new validation date
4. Update `business_logic/index.md` if capabilities changed
5. Update `business_logic/glossary.md` if terms changed

### Example Change Impact Analysis

User request:
```
Analyze the business impact of this PR: https://github.com/org/repo/pull/123
Changes: backend/apps/core/api/views.py (added rate limiting)
```

You would:
1. Get the PR diff
2. Identify the business logic change (rate limiting added)
3. Map to affected BL file (geolocation-query-creation.md)
4. Generate business impact statement

**Impact Analysis Output:**
```markdown
# Business Impact Analysis: PR #123 - Add Query Rate Limiting

*Generated: 2026-03-20*
*PR: https://github.com/org/repo/pull/123*
*Files changed: backend/apps/core/api/views.py (+45 lines)*

## Summary
Adds rate limiting to geolocation query creation to prevent abuse and manage middleware load.

## BL Change Type
- **Category**: Endpoint (geolocation-query-creation.md)
- **Section**: Decision Rules (new validation rule)
- **Change Type**: Restriction (adds new constraint)

## Change Description (Business Language)
**Before**: Users could create unlimited geolocation queries per minute.
**After**: Users are limited to 10 queries per minute. Exceeding this limit returns HTTP 429 "Too Many Requests" for 60 seconds.

**Who is affected**:
- All API users (automated scripts may hit limit)
- Power users doing bulk queries
- Integration partners

## Business Impact

**Capability Impact**: RESTRICTS existing capability
- Prevents rapid-fire queries that could overwhelm middleware
- Protects against accidental script loops
- May break existing integrations that poll frequently

**Cost Impact**: None (credit charging unchanged)

**User Experience Impact**:
- Legitimate high-volume users will see 429 errors
- Clear error message explains limit and retry time
- Existing automated workflows may need adjustment

**Risk Assessment**:
- **Breaking Change**: YES (API behavior changes)
- **Data Migration**: None required
- **Performance Impact**: Positive (reduces middleware load)
- **Security Impact**: Positive (prevents abuse)
- **Compliance Impact**: None

## BL Documentation Updates Required

1. **Update**: `business_logic/endpoints/geolocation-query-creation.md`
   - Add to Decision Rules section:
     ```markdown
     - If user exceeded rate limit (10 queries/minute) → reject with HTTP 429
       - Retry after: 60 seconds
       - Affects: All users, per-user limit
     ```
   - Add to Preconditions section:
     ```markdown
     - User must be under query rate limit (10/minute sliding window)
     ```
   - Add to Exceptions section:
     ```markdown
     - **Rate limit exceeded**: Returns 429, request blocked for 60 seconds
       - Retry-After header indicates seconds until reset
     ```

2. **Update**: `business_logic/index.md`
   - Add note to geolocation entry: "Rate limited: 10 queries/minute"

3. **Update**: `business_logic/glossary.md`
   - Add term: "Rate limiting - Per-user limit of 10 geolocation queries per minute"

## Testing Recommendations

**Business Scenarios to Test**:
1. Normal query under limit (should succeed)
2. 11th query within 1 minute (should return 429)
3. Query after 60 second wait (should succeed)
4. Different users have independent limits (user A's limit doesn't affect user B)
5. Existing API clients handle 429 gracefully
6. Power user with unlimited credits still hits rate limit

**Edge Cases**:
- Burst of 10 queries exactly at limit boundary
- Query exactly at 60 second reset time
- Concurrent requests from same user

**Integration Points**:
- Frontend error handling for 429 responses
- API client retry logic (exponential backoff recommended)
- Monitoring/alerting for rate limit hits

## Recommendations

1. **Communicate to users**: Announce rate limiting before deployment
2. **Grace period**: Consider logging only for 1 week before enforcing
3. **Admin override**: Add option for supervisors to bypass limit
4. **Metrics**: Track how many users hit the limit
5. **Documentation**: Update API docs with rate limit info
```

---

## Mode 3: Gap Analysis - Finding Undocumented Business Logic

Use this mode to identify business logic that exists in code but lacks documentation. Gap analysis answers "What are we missing?" and helps prioritize documentation efforts.

### When to Use Gap Analysis

- Initial documentation assessment - What needs to be documented?
- Periodic reviews - What new code hasn't been documented?
- Before audits - Ensure completeness of BL documentation
- Team handoffs - Identify knowledge gaps
- Risk assessment - Find undocumented critical paths

### Gap Analysis Workflow

#### Step 1: Scan Codebase for Entry Points

Discover all potential business logic entry points:

```bash
# Find all API endpoints
find backend/ -name "views.py" -o -name "api.py" | xargs grep -l "class\|def\|@api_view\|@router"

# Find all models
find backend/ -name "models.py" | xargs grep -l "class.*Model"

# Find all background tasks
find backend/ -name "tasks.py" -o -name "*task*.py" | xargs grep -l "@task\|@shared_task"

# Find all management commands
find backend/ -path "*/management/commands/*.py"

# Find all billing/credit operations
grep -r "CreditService\|QuotaService" backend/ --include="*.py" -l
```

#### Step 2: Build Inventory of Documented BL

Read the existing `business_logic/index.md` and build a checklist:

```markdown
## Documented Endpoints
- [x] Geolocation Query Creation
- [x] CDR Query List
- [ ] CDR Query Create  ← MISSING
- [ ] Target Update     ← MISSING

## Documented Models
- [x] Geolocation Lifecycle
- [ ] Target Assignment Rules  ← MISSING
- [ ] User Credit Rules        ← MISSING

## Documented Workflows
- [x] Proximity Monitoring
- [ ] Geofence Violations      ← MISSING
- [ ] Scheduled Queries        ← MISSING

## Documented Billing
- [x] Geolocation Credit Charging
- [ ] CDR Credit Pricing       ← MISSING
- [ ] Refund Policy            ← MISSING
```

#### Step 3: Cross-Reference Code vs Documentation

For each discovered entry point, check if BL docs exist:

**For Endpoints:**
```bash
# Endpoint: GeolocationQueryViewSet.create
# Check: business_logic/endpoints/geolocation-query-creation.md
# Status: ✓ Documented

# Endpoint: CdrQueryViewSet.create
# Check: business_logic/endpoints/cdr-query-creation.md
# Status: ✗ NOT DOCUMENTED
```

**For Models:**
```bash
# Model: Target
# Check: business_logic/models/target-lifecycle.md
# Status: ✗ NOT DOCUMENTED
```

**For Workflows:**
```bash
# Task: recreate_geolocation (scheduled queries)
# Check: business_logic/workflows/scheduled-queries.md
# Status: ✗ NOT DOCUMENTED
```

#### Step 4: Prioritize Gaps by Business Impact

Categorize undocumented items by priority:

**Tier 1 - Critical (Document Immediately)**
- User-facing API endpoints
- Billing/credit operations
- Authentication/authorization
- Core domain entity lifecycles
- External integrations

**Tier 2 - Important (Document Soon)**
- Background workflows
- Notification systems
- Admin operations
- Scheduled tasks
- Data export/import

**Tier 3 - Nice to Have (Document When Time)**
- Utility endpoints
- Read-only views
- Internal tools
- Legacy/deprecated code

#### Step 5: Generate Gap Analysis Report

Create a structured report with:

**1. Coverage Summary**
- Total entry points found: X
- Documented: Y (Z%)
- Undocumented: X-Y
- Coverage by category (endpoints, models, workflows, billing)

**2. Undocumented Items by Priority**
- Tier 1 gaps (with business impact justification)
- Tier 2 gaps
- Tier 3 gaps

**3. Documentation Debt Estimate**
- Estimated hours to complete Tier 1
- Estimated hours to complete Tier 2
- Recommended documentation schedule

**4. Risk Assessment**
- What undocumented code could cause production issues?
- What undocumented logic affects billing/credits?
- What undocumented functionality has compliance implications?

**5. Recommended Action Plan**
- Which gaps to address first
- Suggested order of documentation
- Quick wins (high impact, low effort)

### Example Gap Analysis

User request:
```
Run a gap analysis on our business logic documentation
```

You would:
1. Scan the codebase for all entry points
2. Check `business_logic/` for existing documentation
3. Cross-reference and identify gaps
4. Prioritize by business impact
5. Generate report

**Gap Analysis Output:**
```markdown
# Business Logic Gap Analysis Report

*Generated: 2026-03-20*
*Scope: backend/apps/core/*
*Documentation Root: business_logic/*

## Executive Summary

| Category | Total Found | Documented | Coverage |
|----------|-------------|------------|----------|
| Endpoints | 24 | 8 | 33% |
| Models | 12 | 2 | 17% |
| Workflows | 8 | 1 | 13% |
| Billing | 6 | 1 | 17% |
| **TOTAL** | **50** | **12** | **24%** |

**Documentation Debt:**
- Tier 1 (Critical): 14 items (~28 hours)
- Tier 2 (Important): 16 items (~24 hours)
- Tier 3 (Nice to Have): 8 items (~12 hours)
- **Total: 64 hours of documentation work**

## Critical Gaps (Tier 1) - Document Immediately

### Endpoints

#### 1. CDR Query Creation
- **Location**: `backend/apps/core/api/views.py:220-280` (CdrQueryViewSet.create)
- **Business Impact**: Users create CDR queries - primary revenue-generating feature
- **Risk**: No documented validation rules, credit charging, or error handling
- **Priority**: HIGH - Money-related, user-facing
- **Estimate**: 2 hours

#### 2. Target Management (CRUD)
- **Location**: `backend/apps/core/api/targets.py` (TargetViewSet)
- **Business Impact**: Core entity management - affects all queries
- **Risk**: Undocumented RBAC rules, assignment logic, validation
- **Priority**: HIGH - Affects everything downstream
- **Estimate**: 3 hours

#### 3. User Authentication & API Key Management
- **Location**: `backend/apps/core/api/auth.py`
- **Business Impact**: Security entry point, affects all API access
- **Risk**: Undocumented rate limits, key rotation, lockout rules
- **Priority**: CRITICAL - Security-related
- **Estimate**: 2 hours

#### 4. Geolocation Query Cancellation
- **Location**: `backend/apps/core/api/views.py:310-340` (GeolocationQueryViewSet.cancel)
- **Business Impact**: User can stop in-progress queries, affects credit refunds
- **Risk**: Undocumented refund policy, timing restrictions
- **Priority**: HIGH - Credits involved
- **Estimate**: 1 hour

### Models

#### 5. Target Entity Lifecycle
- **Location**: `backend/apps/core/models/target.py`
- **Business Impact**: Core domain entity, links to users, MSISDNs, operations
- **Risk**: Undocumented state transitions, validation rules, cascade effects
- **Priority**: HIGH - Core domain
- **Estimate**: 2 hours

#### 6. User Credit/Quota Model
- **Location**: `backend/apps/core/models/user.py` (credit_limit, credit_usage, credit_reserved)
- **Business Impact**: Billing foundation, determines who can do what
- **Risk**: Undocumented credit calculation, negative balance handling
- **Priority**: CRITICAL - Money-related
- **Estimate**: 3 hours

### Billing

#### 7. CDR Credit Pricing
- **Location**: `backend/apps/core/services/billing.py:90-130`
- **Business Impact**: Determines cost of CDR queries (revenue)
- **Risk**: Undocumented pricing tiers, volume discounts, special cases
- **Priority**: CRITICAL - Direct revenue impact
- **Estimate**: 2 hours

#### 8. Credit Refund Policy
- **Location**: `backend/apps/core/services/billing.py:200-250`
- **Business Impact**: When users get credits back (cost reduction)
- **Risk**: Undocumented refund triggers, partial refunds, time limits
- **Priority**: HIGH - Affects revenue
- **Estimate**: 2 hours

### Workflows

#### 9. Scheduled Query Recurrence
- **Location**: `backend/apps/core/tasks.py:45-120` (recreate_geolocation)
- **Business Impact**: Users can schedule recurring geolocation lookups
- **Risk**: Undocumented recurrence rules, TTL handling, error recovery
- **Priority**: MEDIUM - Automated costs, user-facing feature
- **Estimate**: 3 hours

#### 10. Geofence Violation Detection
- **Location**: `backend/apps/core/tasks.py:300-380` (check_geofence)
- **Business Impact**: Automated alerts when targets enter/leave areas
- **Risk**: Undocumented boundary calculation, notification logic
- **Priority**: MEDIUM - Alerting is key product feature
- **Estimate**: 2 hours

## Important Gaps (Tier 2) - Document Soon

[16 items including:]
- Bulk query operations (endpoints)
- Investigation workflows (models)
- Notification system (workflows)
- Quota enforcement (billing)
- Admin user management (endpoints)
- Data export functionality (workflows)
... [full list in report]

## Nice to Have (Tier 3) - Document When Time

[8 items including:]
- System health endpoints
- Debug/development endpoints
- Legacy v1 API endpoints
- Internal admin tools
... [full list in report]

## Risk Assessment

### High Risk Undocumented Code

1. **Credit Refund Logic** (`billing.py:200-250`)
   - Risk: Users may be overcharged or undercharged without clear policy
   - Impact: Direct financial loss/gain
   - Recommendation: Document IMMEDIATELY

2. **User Credit Calculation** (`models/user.py`)
   - Risk: `available_credits` computed property has complex fallback logic
   - Impact: Users may be incorrectly blocked from queries
   - Recommendation: Document before next billing cycle

3. **Authentication Rate Limiting** (`api/auth.py`)
   - Risk: undocumented rate limits may lock out legitimate users
   - Impact: User access issues, support burden
   - Recommendation: Document and verify limits are reasonable

4. **Geolocation Cancellation Refund** (`views.py:310-340`)
   - Risk: Inconsistent refund timing (before vs after processing)
   - Impact: User disputes, unclear policy
   - Recommendation: Document and align refund policy

### Compliance Implications

- **Audit Trail**: User credit adjustments lack documented BL for auditors
- **Data Retention**: Query archival policy not documented (90 days?)
- **Access Control**: Target assignment RBAC rules undocumented

## Recommended Action Plan

### Week 1: Critical Path (20 hours)
1. CDR Query Creation (2h) - Revenue-generating
2. User Credit Model (3h) - Billing foundation
3. CDR Credit Pricing (2h) - Revenue
4. Credit Refund Policy (2h) - Revenue protection
5. Target Lifecycle (2h) - Core domain
6. Authentication (2h) - Security
7. Geolocation Cancellation (1h) - Credits
8. Target Management (3h) - Core operations
9. Scheduled Queries (3h) - Automated revenue
10. Update index/glossary (0h) - Continuous

### Week 2: Important Features (20 hours)
[Document Tier 2 gaps]

### Week 3: Complete Coverage (24 hours)
[Document remaining Tier 2 and Tier 3 gaps]

## Quick Wins (High Impact, Low Effort)

1. **Geofence State Transitions** (1h)
   - Single file, clear state machine
   - High-value product feature
   - Low complexity

2. **Query Validation Rules** (1h)
   - Consolidated in validators.py
   - Affects all query types
   - Clear if/then rules

3. **Notification Categories** (1h)
   - Single enum + mapping
   - User-facing feature
   - Simple structure

## Next Steps

1. **Immediate**: Start with Tier 1 gaps (billing and authentication)
2. **This Sprint**: Complete all Tier 1 endpoints and models
3. **Next Sprint**: Document Tier 1 workflows, start Tier 2
4. **Ongoing**: Update index.md and glossary.md after each analysis
5. **Review**: Re-run gap analysis in 2 weeks to measure progress
```

### Gap Analysis Commands

**Quick gap check:**
```bash
# Find all ViewSets
grep -r "class.*ViewSet" backend/ --include="*.py" | wc -l

# Count documented endpoints
ls business_logic/endpoints/ | wc -l

# Simple coverage calculation
echo "Endpoints: $(grep -r 'class.*ViewSet' backend/ --include='*.py' | wc -l) found, $(ls business_logic/endpoints/ 2>/dev/null | wc -l) documented"
```

**Comprehensive gap scan:**
```bash
# Full inventory command
cat > /tmp/gap_scan.sh << 'EOF'
#!/bin/bash
echo "=== Endpoints ==="
grep -rh "class.*ViewSet" backend/ --include="*.py" | sed 's/.*class //' | sed 's/(.*//' | sort
echo ""
echo "=== Models ==="
grep -rh "^class.*models\.Model" backend/ --include="*.py" | sed 's/.*class //' | sed 's(.*' | sort
echo ""
echo "=== Tasks ==="
grep -rh "@shared_task\|@task" backend/ --include="*.py" -A 1 | grep "def " | sed 's/.*def //' | sed 's(.*' | sort
EOF

chmod +x /tmp/gap_scan.sh
/tmp/gap_scan.sh
```

---

## When to Use Each Mode

| Mode | Use When... | Output |
|------|-------------|--------|
| Diff Mode | Validating docs against code | Diff report with gaps |
| Change Impact | Analyzing PR/business impact | Impact statement |
| Gap Analysis | Finding undocumented BL | Prioritized gap list |

## Related References

- [Quick Start Guide](quick-start.md) - Getting started with basic analysis
- [Bootstrapping](bootstrapping.md) - Initial documentation workflow
- [Validation](validation.md) - Quality assurance and review
