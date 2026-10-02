# Collaboration and Team Workflows

*Last updated: 2026-03-15*

## Overview

This reference covers team collaboration, documentation maintenance, versioning strategies, conflict resolution, and long-term sustainability of business logic documentation.

---

## Versioning BL Documentation

Business logic documentation must track code provenance to detect stale documentation and validate accuracy.

### File Header Format

Every BL document should include version metadata at the top:

```markdown
# [Business Capability Name]

*Analysis generated: 2026-03-15*
*Code analyzed: `backend/pinpoint/apps/core/api/views.py:145-189`*
*Code version: abc123def456* (git commit hash)
*Last updated: 2026-03-20*
*Last validated: 2026-03-20*
*Validation status: DRAFT | REVIEWED | STALE | CURRENT*
*Analyzed by: [optional - analyst name or ID]*
```

### Status Definitions

**DRAFT:** Initial analysis, not yet reviewed
**REVIEWED:** Peer-reviewed, validated against code
**STALE:** Code has changed since analysis, documentation needs update
**CURRENT:** Up-to-date with latest code (recommended status for production use)

### Detecting Stale Documentation

**Method 1: Manual Git Comparison**
```bash
# Get commit hash when BL doc was created
git log --oneline --all | grep "Analysis generated: 2026-03-15"

# Get current commit hash of analyzed code
git rev-parse HEAD

# Compare: if different, doc may be stale
# Get diff between analyzed version and current
git diff abc123def456 HEAD -- backend/pinpoint/apps/core/api/views.py
```

**Method 2: Automated Detection (Future)**
- Use Diff Mode to compare BL doc against current code
- CI check: Verify BL doc freshness on related file changes
- Git hooks: Flag BL docs when code changes

**Method 3: Manual Review Checklist**
- [ ] Read the BL doc's "Code version" hash
- [ ] Check if that commit is an ancestor of HEAD
- [ ] If not, doc is analyzing old code → mark as STALE
- [ ] Review git diff for changes to business logic
- [ ] Update doc if business rules changed

### Versioning Workflow

**Creating New Analysis:**
1. Generate BL documentation from current code
2. Get current git commit hash: `git rev-parse HEAD`
3. Add to file header with status DRAFT
4. Save to `business_logic/` directory

**Updating Existing Analysis:**
1. Read existing BL doc to find "Code version" hash
2. Compare with current code: `git diff <old-hash> HEAD -- <file-path>`
3. Identify changed business logic (not just implementation changes)
4. Update affected sections with "⚠ CHANGED: [date]" notes
5. Update "Last updated" and "Code version" fields
6. Change status to REVIEWED

**Validating Documentation:**
1. Use Diff Mode to compare BL doc against current code
2. Document matches → mark as CURRENT
3. Document differs → update and mark as REVIEWED
4. Code unchanged but never validated → mark as REVIEWED

**Marking as Stale:**
1. Code review reveals business logic changes
2. No time to update documentation immediately
3. Change status to STALE
4. Add note: "⚠ STALE: Code changed on [date], needs review"

### Versioning Best Practices

✅ **DO:**
- Always include git commit hash in BL docs
- Update "Last updated" on every change
- Use status labels consistently
- Mark as STALE when code changes (don't mislead)
- Document what changed between versions

❌ **DON'T:**
- Remove old versions (keep history in git)
- Mark as CURRENT if never reviewed
- Skip versioning for "minor" analyses
- Assume doc is still accurate after code changes
- Use ambiguous dates (use YYYY-MM-DD format)

---

## Team Workflows

### Ownership and Maintenance

**Primary Owner:** Team lead or senior developer responsible for BL doc quality
**Contributors:** All developers can create and update BL docs
**Reviewers:** Domain experts review critical analyses (billing, security, core flows)

**Ownership Model:**
- **Endpoint docs:** Owned by backend team
- **Model docs:** Owned by data/backend team
- **Workflow docs:** Owned by feature teams
- **Billing docs:** Owned by backend + finance stakeholder

### Maintenance Schedule

**Weekly:**
- Review BL docs changed in past week
- Validate docs against recent code changes
- Update index.md with new analyses

**Monthly:**
- Run Gap Analysis to find undocumented BL
- Prioritize Tier 1 gaps for documentation
- Review stale docs and update or remove

**Quarterly:**
- Comprehensive audit of all BL documentation
- Update glossary.md with new terms
- Review and refine templates based on usage

**On-Demand:**
- Before major releases (validate BL docs)
- After architectural changes (update related docs)
- When onboarding new developers (review docs for clarity)

### Collaborative Editing

**Single Writer:**
- One person owns the BL doc at a time
- Use Git branches for concurrent edits
- PR process for merging changes

**Concurrent Analysis:**
- Multiple developers analyzing overlapping code
- Coordinate in issue tracker or Slack
- Split by endpoint/model/workflow to avoid conflicts

**Resolving Conflicts:**
1. **Same doc, different sections:** Merge both, reconcile overlaps
2. **Same doc, same section:** Discuss and pick best version
3. **Different docs, overlapping content:** Add cross-references, avoid duplication

### Onboarding New Developers

**Day 1: Read, Don't Write**
- Read 3-5 complete BL analyses to understand format
- Review Quick Start guide
- Skim glossary.md for domain terminology

**Week 1: Simple Analyses**
- Start with read-only endpoints (GET operations)
- Document simple models with clear lifecycles
- Peer review required before merging

**Month 1: Complex Analyses**
- Document POST/PUT/DELETE endpoints
- Analyze workflows and background tasks
- Independent analysis (no peer review required for non-critical docs)

**Month 2+: Critical Analyses**
- Document billing and credit logic
- Analyze state machines and complex workflows
- Review others' analyses

---

## Peer Review Workflow

Peer review ensures business logic documentation is accurate, complete, and maintainable. Not all analyses require review, but critical documentation should always be reviewed.

### When Peer Review is Required

**Always Required (Tier 1 - Critical):**
- ✅ Billing and credit logic (money-related, high risk)
- ✅ State machines and lifecycle rules (core domain logic)
- ✅ Authentication and authorization (security-related)
- ✅ External API integrations (middleware provider contracts)
- ✅ Complex workflows with multiple steps
- ✅ First analysis of a major feature

**Recommended (Tier 2 - Important):**
- ⚠️ User-facing API endpoints
- ⚠️ Background tasks and scheduled jobs
- ⚠️ Data models with business logic
- ⚠️ Notification and alerting systems

**Optional (Tier 3 - Nice to Have):**
- ❌ Read-only endpoints and views
- ❌ Simple CRUD operations
- ❌ Utility functions and helpers
- ❌ Internal tools and debug features

### Review Criteria

#### 1. Completeness Check

**All 11 Sections Present:**
- [ ] 1. Business Purpose
- [ ] 2. Actors
- [ ] 3. Preconditions
- [ ] 4. Main Flow
- [ ] 5. Decision Rules
- [ ] 6. State Transitions
- [ ] 7. Billing/Credit Impact
- [ ] 8. Exceptions/Edge Cases
- [ ] 9. Data Written/Read
- [ ] 10. Ambiguities/Questions
- [ ] 11. Code References

**Each Section Has Content:**
- [ ] No empty sections or "TODO: add" placeholders
- [ ] Section 7 (Billing) is explicit, not "see billing docs"
- [ ] Section 10 (Ambiguities) has content OR explains why none
- [ ] Section 11 (Code References) includes line numbers

#### 2. Quality Check

**Business Language Used:**
- [ ] No "calls function X" or "returns Y" descriptions
- [ ] Technical terms translated to domain language
- [ ] Domain terminology from expertise.yaml used correctly
- [ ] Section 4 describes business flow, not code structure

**Traceability:**
- [ ] Every claim has line number reference
- [ ] References point to actual code locations
- [ ] Related BL docs linked where appropriate
- [ ] Glossary terms linked when first used

**Visual Aids:**
- [ ] State transitions have Mermaid diagram (if applicable)
- [ ] Complex flows have step-by-step breakdown
- [ ] Tables used for clear comparisons (actors, rules, etc.)

#### 3. Accuracy Check

**Verified Against Code:**
- [ ] Decision rules match actual if/elif/else logic
- [ ] State transitions match enum values
- [ ] Billing logic matches CreditService calls
- [ ] Error handling matches exception handlers
- [ ] Not copied from docstrings (verified in actual code)

**No Contradictions:**
- [ ] Sections don't contradict each other
- [ ] Examples match the described flow
- [ ] Related BL docs are consistent

**Edge Cases Covered:**
- [ ] Exception handlers documented
- [ ] Validation rules explicit
- [ ] Silent failures noted
- [ ] Race conditions acknowledged

#### 4. Clarity Check

**Understandable by New Developer:**
- [ ] Acronyms explained or linked
- [ ] Domain terms defined or linked to glossary
- [ ] Examples provided for complex concepts
- [ ] Flow is logical and easy to follow

**Well-Organized:**
- [ ] Sections in correct order
- [ ] Headings are clear and descriptive
- [ ] Lists and tables used effectively
- [ ] No walls of text

**Actionable:**
- [ ] Reader knows what the system does
- [ ] Billing impact is unambiguous
- [ ] Edge cases are documented
- [ ] Ambiguities are honestly noted

### Review Process

#### Step 1: Pre-Review Preparation

**Reviewer:**
1. Read the BL doc completely
2. Open the referenced code files
3. Check git log for recent changes to analyzed code
4. Identify the analysis tier (Tier 1/2/3)

**Author:**
1. Ensure status is DRAFT (not REVIEWED or CURRENT)
2. Include "Code version" git hash in header
3. Add any context or questions in PR description
4. Self-review using the checklist below

#### Step 2: Create Pull Request

**PR Title Format:**
```
[BL Doc] [Category] Business Capability Name

Examples:
- [BL Doc] [Endpoint] Geolocation Query Creation
- [BL Doc] [Model] Target Lifecycle Rules
- [BL Doc] [Billing] Credit Refund Policy
```

**PR Description Template:**
```markdown
## Business Logic Analysis

**Type:** Endpoint | Model | Workflow | Billing
**Tier:** 1 (Critical) | 2 (Important) | 3 (Nice to have)
**Code Analyzed:** `backend/pinpoint/apps/core/views/geolocation.py:69-472`
**Git Commit:** abc123def456

## What This Document Covers
[Brief 1-2 sentence summary]

## Review Focus Areas
- [ ] Billing logic accuracy
- [ ] State transition completeness
- [ ] Edge case coverage
- [ ] Clarity for new developers

## Questions for Reviewer
[Any specific questions or areas of concern]

## Related Changes
- [ ] Index.md updated
- [ ] Glossary.md updated (if new terms)
- [ ] Related BL docs linked
```

#### Step 3: Conduct Review

**Light Review (15-30 minutes):**
- Skim all 11 sections for completeness
- Check billing logic is explicit
- Verify line numbers are present
- State transitions have diagram (if applicable)
- No obvious contradictions

**Thorough Review (45-60 minutes):**
- Complete review checklist (all 30+ items)
- Trace decision rules against actual code
- Verify state transitions match code
- Check billing logic line-by-line
- Test edge cases if code is runnable

**Critical Review (60-90 minutes):**
- Thorough review plus:
- Cross-reference with related BL docs
- Check for consistency across documentation
- Validate against recent code changes
- Suggest improvements to template or process
- Consider security implications

#### Step 4: Provide Feedback

**Good Feedback Practices:**
- ✅ Specific: "Section 5, rule 3 doesn't match code at line 167"
- ✅ Constructive: "Add example of what happens when X occurs"
- ✅ Explained: "This is important because..."
- ✅ Actionable: "Suggest adding Mermaid diagram for clarity"

**Bad Feedback Practices:**
- ❌ Vague: "This section is confusing"
- ❌ Unexplained: "Change this"
- ❌ Subjective: "I don't like this format"
- ❌ Non-actionable: "Rewrite this"

**Feedback Template:**
```markdown
## Review Feedback

### Must Fix (Blocking Merge)
- [ ] **Section 5, Rule 3:** Code at line 167 checks `quota >= cost` but doc says `quota > cost`. Missing equals case.
- [ ] **Section 7:** Billing logic incomplete - doesn't mention what happens if CreditService is unavailable.
- [ ] **Section 6:** State diagram missing PAUSED state that's in the code.

### Should Fix (Recommendations)
- [ ] **Section 4:** Consider breaking this into subsections for readability.
- [ ] **Section 8:** Add example of "empty result" edge case.
- [ ] **Throughout:** Link to glossary for first use of "IMSI" and "MSISDN".

### Nice to Have (Suggestions)
- [ ] Add mermaid diagram for flow in Section 4.
- [ ] Consider table format for Decision Rules in Section 5.
- [ ] Add "Quick Summary" at top for faster skimming.

### Overall Assessment
**Quality:** Excellent | Good | Fair | Poor
**Recommendation:** Approve | Approve with changes | Request changes | Reject

**Comments:**
[Overall impression, what worked well, what needs improvement]
```

#### Step 5: Address Feedback

**Author Actions:**
1. Create a new branch for changes
2. Address all "Must Fix" items
3. Consider "Should Fix" items
4. Document why "Nice to Have" items weren't addressed
5. Update PR with changes
6. Request re-review if substantial changes

**Reviewer Actions:**
1. Review updated BL doc
2. Verify all blocking issues resolved
3. Check for new issues introduced
4. Approve or request additional changes

#### Step 6: Merge and Update

**Pre-Merge Checklist:**
- [ ] All "Must Fix" items addressed
- [ ] Status changed from DRAFT to REVIEWED
- [ ] Index.md updated with new entry
- [ ] Glossary.md updated if new terms added
- [ ] Related BL docs cross-linked
- [ ] PR description includes summary

**Post-Merge Actions:**
1. Delete review branch
2. Notify team of new BL doc
3. Add to team documentation sprint board
4. Schedule follow-up review in 1 month (for Tier 1 docs)

### Review Turnaround Guidelines

| Tier | Target Response | Target Review | Total Time |
|------|----------------|---------------|------------|
| Tier 1 (Critical) | 1 business day | 2-3 business days | 3-4 days |
| Tier 2 (Important) | 2 business days | 3-5 business days | 5-7 days |
| Tier 3 (Nice to Have) | 3 business days | 5-7 business days | 7-10 days |

### Expedited Review Process

**When to Expedite:**
- Blocking a release
- Critical bug fix
- Security issue
- Production incident

**Expedited Process:**
1. Tag PR with `expedite-review`
2. Request specific reviewer in Slack
3. Light review (30 min) focused on accuracy
4. Merge if no blocking issues
5. Schedule thorough review within 1 week post-merge

### Reviewer Assignment

**Automatic Assignment:**
- Billing docs → Finance stakeholder + senior backend dev
- Security docs → Security lead + backend lead
- Core domain → Team lead + domain expert
- Other docs → Any available senior developer

**Voluntary Assignment:**
- Tag PR with `review-needed` skill label
- Reviewers pick up based on expertise/availability
- First to comment claims the review

### Review Quality Metrics

**Track and Report Monthly:**
- Average review turnaround time
- Number of reviews completed per reviewer
- Number of blocking issues found
- Number of "Should Fix" suggestions implemented
- Reviewer participation rate

**Goals:**
- 90% of Tier 1 reviews completed within 4 days
- 100% of Tier 1 docs reviewed before merge
- At least 2 active reviewers in the team

---

## Conflict Resolution

### Overlapping Analyses

**Scenario:** Two developers analyze the same endpoint simultaneously

**Prevention:**
- Claim analysis in issue tracker before starting
- Check business_logic/index.md for existing analyses
- Coordinate in team chat or standup

**Resolution:**
1. Compare both analyses
2. Merge sections (pick best version of each section)
3. Resolve contradictions (check code together)
4. One person owns the merged doc, other reviewer
5. Delete duplicate, keep merged version

### Contradictory Documentation

**Scenario:** BL doc says X, but code does Y

**Diagnosis:**
1. Check "Code version" in BL doc header
2. Compare with current code: `git diff <old-hash> HEAD`
3. Determine if code changed or doc was wrong

**Resolution:**
- **Code changed:** Update BL doc to match new behavior
- **Doc was wrong:** Fix BL doc, add "⚠ CORRECTED: [date]" note
- **Both changed:** Document both old and new behavior in transition period

### Outdated Documentation

**Scenario:** Code refactored, BL docs not updated

**Detection:**
- Peer review catches outdated sections
- Diff Mode reveals inconsistencies
- Developer notices during onboarding

**Resolution:**
1. Mark BL doc as STALE immediately
2. Create issue to update documentation
3. Prioritize by business impact (billing > user-facing > internal)
4. Update and mark as REVIEWED

### Glossary Conflicts

**Scenario:** Term defined differently across BL docs

**Prevention:**
- Single source of truth: glossary.md
- Link to glossary instead of defining inline
- Review glossary in weekly maintenance

**Resolution:**
1. Check glossary.md for canonical definition
2. Update all BL docs to use canonical definition
3. Add note to glossary if term has context-dependent meanings

---

## Continuous Improvement

### Feedback Loops

**From Developers:**
- "I couldn't understand this doc" → Clarify language
- "This section didn't help" → Reorganize or expand
- "I found this doc too late" → Improve discoverability

**From Code Reviews:**
- "BL doc didn't catch this change" → Update validation checklist
- "This BL doc is outdated" → Trigger versioning workflow
- "We need BL doc for feature X" → Add to Gap Analysis

**From Stakeholders:**
- "What does this mean?" → Update glossary
- "Is this accurate?" → Validate against code
- "We need more detail on X" → Expand section

### Metrics and KPIs

**Coverage:**
- Total entry points: X (from DISCOVERY.md)
- Documented entry points: Y
- Coverage %: (Y/X) * 100

**Quality:**
- DRAFT analyses: count
- REVIEWED analyses: count
- CURRENT analyses: count
- STALE analyses: count (should be < 10%)

**Usage:**
- BL docs referenced in code reviews: count/month
- BL docs updated after code changes: %
- New developers onboarded using BL docs: count

**Goals:**
- Phase 1: Critical endpoints (80% coverage, all CURRENT)
- Phase 2: High-risk workflows (60% coverage, all REVIEWED)
- Phase 3: All entry points (40% coverage, < 10% STALE)

### Template Refinement

**When to Update Templates:**
- User feedback reveals missing sections
- New analysis type discovered
- Quality issues found in multiple docs

**Template Change Process:**
1. Propose change in team meeting
2. Get consensus from stakeholders
3. Update output_template.md
4. Re-analyze 2-3 existing docs with new template
5. Roll out to team with migration guide

---

## Related References

- [Quick Start Guide](quick-start.md) - Getting started with business logic extraction
- [Validation](validation.md) - Quality assurance and peer review
- [Advanced Modes](advanced-modes.md) - Diff, Change Impact, and Gap Analysis
- [Bootstrapping](bootstrapping.md) - Initial documentation workflow
