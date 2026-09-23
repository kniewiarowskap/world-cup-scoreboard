---
name: ai-tracking
description: Record meaningful AI-assisted work in AI.md and verify the usage history is complete.
---

# Tracking AI-Assisted Work

Use this skill whenever AI materially contributes to the repository through
code, tests, documentation, analysis that changes a decision, or validation
of a change.

## Workflow

1. Inspect `AI.md` before editing it.
2. Identify the meaningful AI-assisted outcome, affected files, developer
   decision, and verification performed.
3. Do not record sensitive prompts, credentials, private data, or irrelevant
   conversational details.
4. Add one numbered prompt-history entry under `## Prompt history`.
5. Add the matching row to the interaction table.
6. Keep the numbering, date, wording, and affected files consistent between
   both sections.
7. Record the developer's decision and verification, not only the AI output.
8. Review the diff and confirm the new entry describes the actual repository
   change.

## Entry format

Use this format for the detailed prompt-history section:

```markdown
### N. YYYY-MM-DD — Short description

- **Area/files:** `path/to/file`, feature, or commit
- **Prompt or goal:** What problem was given to the AI
- **AI contribution:** Suggestions, code, tests, analysis, or documentation
  provided
- **Developer decision:** What was accepted, changed, or rejected and why
- **Verification:** Tests, builds, manual checks, or review performed
- **Result:** Summary of the final change
```

Add a matching row to the interaction table:

```markdown
| N | YYYY-MM-DD | `path/to/file` | Short purpose | AI contribution | Developer decision and verification |
```

## Completion checks

Before finishing:

- Confirm `AI.md` is modified when AI materially changed the repository.
- Confirm the detailed entry and table row use the same number and date.
- Confirm affected paths and verification statements are accurate.
- Run `git diff --check` when the log was edited.
- Do not create duplicate entries for routine follow-up messages.

If the work was only a conversational explanation and did not affect a
repository decision or artifact, no `AI.md` entry is required.
