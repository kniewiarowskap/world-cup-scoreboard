---
name: commit
description: Review the current changes, prepare a conventional commit, and create it safely.
---

# Creating a Commit

Use this skill when the user asks to prepare and create a Git commit for the
current repository changes.

## Workflow

1. Inspect `git status`, the complete unstaged and staged diff, and recent
   repository commits.
2. Identify the files that belong to the current change. Do not include
   unrelated user changes.
3. Exclude generated files, IDE metadata, build output, secrets, and other
   artifacts that are not part of the source change. In this project, do not
   commit `.idea/`, `target/`, compiled files, or archives.
4. If there are no meaningful changes, do not create a commit.
5. Suggest a concise conventional commit message before creating the commit.
   Use one of these types as appropriate: `feat`, `fix`, `docs`, `test`,
   `refactor`, or `chore`.
6. Keep the subject action-oriented and concise. Add a short body with one to
   three bullets when it clarifies the change.
7. Stage only the relevant files, then create the commit with this exact
   trailer:

   ```
   Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>
   ```

8. Verify that the commit contains only the intended files and that the working
   tree does not contain accidentally staged artifacts.

## Commit message format

Use a message in this form:

```text
<type>: concise action-oriented subject

- <short description of the meaningful change>
- <optional second description>

Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>
```

Do not amend an existing commit unless the user explicitly requests it.
