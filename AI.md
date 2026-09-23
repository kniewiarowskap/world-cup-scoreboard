# AI Usage Log

This file documents how AI assistance was used during the development of the
World Cup Scoreboard project. It is intended to make the development process
transparent and to distinguish AI suggestions from decisions and verification
performed by the developer.

## Short summary

AI assistance may be used for:

- exploring the repository and identifying relevant files;
- suggesting implementation approaches and edge cases;
- drafting or improving code, tests, and documentation;
- explaining compiler errors, test failures, and unfamiliar APIs;
- reviewing changes for correctness, readability, and maintainability.

The developer remains responsible for the final design, implementation,
security, and correctness of the project. AI-generated suggestions are reviewed,
adapted where necessary, and validated with the project's available checks.

## Context and guiding artifacts

The following materials provide context for the AI-assisted work:

- `task.md` — the text version of the coding exercise requirements;
- `ODDS and Data - JAVA Coding Task.pdf` — the original assignment document;
- `README.md` — project assumptions, reasoning, trade-offs, API boundary,
  thread-safety decision, and the chosen additional scoreboard operation;
- `football-rules.md` — contextual reference for match duration and the
  treatment of extra time and penalty shoot-outs;
- the Java source code, tests, and Maven configuration produced in this
  repository.

The assignment explicitly requires AI assistance and asks for this file to
include a short usage summary, prompt history, contextual information, and any
artifact that guided the implementation.

## Prompt history

This is a concise record of meaningful prompts and their outcomes. It records
the intent and result rather than reproducing potentially sensitive or
irrelevant conversation verbatim.

### 1. 2026-09-23 — Establish AI usage tracking

- **Prompt/goal:** Define a transparent method for recording AI assistance and
  create the assignment-required `AI.md`.
- **AI contribution:** Proposed a disclosure document containing an AI-use
  summary, prompt history, contextual artifacts, an interaction log, a
  reusable entry template, verification details, and guidance not to record
  secrets.
- **Developer decision:** Accepted the proposed structure.
- **Result:** Created the initial `AI.md`.

### 2. 2026-09-23 — Validate AI tracking requirements

- **Prompt/goal:** Compare `AI.md` with the assignment requirements and identify
  any missing AI-tracking information.
- **AI contribution:** Reviewed `task.md` and identified the need to state
  prompt history, contextual inputs, and implementation-guiding artifacts
  explicitly.
- **Developer decision:** Accepted the recommended additions.
- **Verification:** Checked the document against every AI-related requirement
  in `task.md`.
- **Result:** Added explicit context, artifact, and prompt-history sections.

### 3. 2026-09-23 — Define the domain and API decisions

- **Prompt/goal:** Analyse storage, input boundaries, match identity, team
  participation, score updates, exceptions, thread safety, and the scope of
  football rules for a reusable Java library.
- **AI contribution:** Recommended a transport-independent Java API, in-memory
  storage, generated match IDs, one active match per team, incremental
  one-goal updates, explicit lifecycle exceptions, immutable summaries, and a
  single lock to protect compound state changes.
- **Developer decision:** Accepted these recommendations and selected
  thread-safe in-memory state without REST, Kafka, database, or match-clock
  dependencies.
- **Verification:** Compared the decisions with `task.md` and
  `football-rules.md`.
- **Result:** Documented the assumptions, API design, scope, thread-safety
  approach, and trade-offs in `README.md`.

### 4. 2026-09-23 — Select the additional scoreboard operation

- **Prompt/goal:** Select exactly one additional operation that is useful,
  football-related, and appropriate for the assignment scope.
- **AI contribution:** Proposed `getMatchResult(MatchId)`, returning a home win,
  away win, or draw for a finished match.
- **Developer decision:** Accepted the proposal because it provides a useful
  football-domain result without introducing a full match-time engine.
- **Verification:** Confirmed that the feature is documented as the sole
  additional operation and that finished matches must be retained.
- **Result:** Added the feature decision and its trade-offs to `README.md`.

### 5. 2026-09-23 — Validate README and AI documentation

- **Prompt/goal:** Consolidate the assignment requirements and design
  decisions in `README.md`, then verify that `AI.md` satisfies the required
  AI documentation.
- **AI contribution:** Structured the README around requirements, API design,
  assumptions, storage, thread safety, the additional operation, reasoning,
  trade-offs, and verification expectations; reviewed the AI documentation for
  summary, prompt history, context, and artifacts.
- **Developer decision:** Accepted the documentation structure and requested
  professional wording for recorded prompts.
- **Verification:** Reviewed `task.md`, `README.md`, `football-rules.md`, and
  `AI.md` together.
- **Result:** Refined the prompt history and added `football-rules.md` to the
  contextual artifacts.

### 6. 2026-09-23 — Simplify package naming and define TDD

- **Prompt/goal:** Choose a concise package structure suitable for a recruitment
  project and assess whether the planned implementation follows test-driven
  development.
- **AI contribution:** Recommended the package root `com.worldcupscoreboard`,
  separated `api`, `model`, `implementation`, and `exception` packages, and
  defined a Red-Green-Refactor workflow with behavior-focused and separate
  concurrency tests.
- **Developer decision:** Accepted the simplified package naming and TDD
  structure.
- **Verification:** Compared the proposed layout with the Maven library
  requirement and the documented public API.
- **Result:** Added the Maven/package layout and TDD process to `README.md`.

### 7. 2026-09-23 — Establish the Maven project structure

- **Prompt/goal:** Begin implementation by creating the Maven project skeleton
  using the agreed package separation.
- **AI contribution:** Added the root Maven configuration, Java 17 compiler
  settings, JUnit 5 test dependency, and package documentation files for
  `api`, `model`, `implementation`, and `exception`.
- **Developer decision:** Accepted the project skeleton and kept the test
  source tree ready for the first behavior-driven test rather than retaining a
  placeholder assertion.
- **Verification:** Confirmed the Maven layout and package names match
  `README.md`.
- **Result:** Created the initial Maven project structure.

### 8. 2026-09-23 — Set the project Java version

- **Prompt/goal:** Update the Maven project to use Java 25.
- **AI contribution:** Changed the Maven compiler release from Java 17 to
  Java 25.
- **Developer decision:** Requested Java 25 as the project language level.
- **Verification:** Confirmed the `maven.compiler.release` property is set to
  `25`; compilation requires a local Java 25 and Maven installation.
- **Result:** Updated `pom.xml` to target Java 25.

### 9. 2026-09-23 — Audit AI tracking and generated project files

- **Prompt/goal:** Verify that the AI-assisted work is fully recorded and
  determine whether the environment needs refreshing after the Java update.
- **AI contribution:** Audited `AI.md` against the documented work, confirmed
  the Java 25 Maven configuration, identified the current shell's Java 21
  environment, and added `target/` to `.gitignore`.
- **Developer decision:** Accepted the audit and the generated-output ignore
  rule.
- **Verification:** Reviewed `AI.md`, `README.md`, `pom.xml`, `.gitignore`, and
  the repository status.
- **Result:** AI tracking now includes the audit, and generated Maven output is
  excluded from version control.

### 10. 2026-09-23 — Verify the Java 25 toolchain

- **Prompt/goal:** Verify that Java 25 works with the current project
  configuration.
- **AI contribution:** Ran the Java 25 runtime and compiler, then compiled the
  current Java source tree with `javac --release 25`.
- **Developer decision:** Requested direct toolchain verification.
- **Verification:** Java 25.0.4.1 and `javac 25.0.4.1` were detected, and
  compilation succeeded. Full Maven verification remains unavailable because
  Maven is not installed or on `PATH`.
- **Result:** Confirmed that the current sources compile successfully with
  Java 25.

## Interaction log

Record each meaningful AI-assisted task using the following information:

| No. | Date | Area or files | Purpose | AI contribution | Developer contribution and verification |
|-----|------|---------------|---------|-----------------|------------------------------------------|
| 1 | 2026-09-23 | `AI.md` | Establish AI usage tracking | Proposed the disclosure structure and reusable log format | Reviewed and accepted the structure |
| 2 | 2026-09-23 | `AI.md`, `task.md` | Validate AI tracking requirements | Identified the required prompt-history, context, and artifact sections | Compared the document with the assignment requirements |
| 3 | 2026-09-23 | `README.md`, `football-rules.md` | Define domain and API decisions | Recommended transport-independent API, in-memory state, validation, lifecycle rules, incremental goals, and thread safety | Accepted the decisions and documented assumptions and trade-offs |
| 4 | 2026-09-23 | `README.md` | Select the additional operation | Proposed `getMatchResult(MatchId)` | Accepted and documented the feature and its scope |
| 5 | 2026-09-23 | `README.md`, `AI.md` | Validate project documentation | Reviewed requirement coverage and professionalized the prompt history | Reviewed the final documentation set |
| 6 | 2026-09-23 | `README.md` | Simplify package naming and define the TDD approach | Recommended a concise package structure and a Red-Green-Refactor workflow with behavior-focused tests | Accepted the recruitment-project scope and documented the Maven layout and TDD process |
| 7 | 2026-09-23 | `pom.xml`, `src/main/java/` | Establish the Maven project structure | Proposed the Maven coordinates, Java version, package directories, and test-first implementation starting point | Accepted the project skeleton and removed the placeholder test so the next test represents real behavior |
| 8 | 2026-09-23 | `pom.xml` | Set the project Java version | Updated the Maven compiler release from Java 17 to Java 25 | Requested Java 25 and accepted the configuration change; local verification is pending a Java 25/Maven environment |
| 9 | 2026-09-23 | `AI.md`, `.gitignore` | Audit AI tracking and generated project files | Checked that documented AI contributions cover the assignment and identified generated Maven output and the current Java environment mismatch | Reviewed the audit and accepted ignoring `target/`; Java 25 configuration remains in `pom.xml` |
| 10 | 2026-09-23 | Java 25 toolchain | Verify the configured Java version | Ran the Java 25 runtime and compiler and compiled the current source tree with `--release 25` | Confirmed Java 25 compilation succeeds; full Maven verification remains blocked because Maven is unavailable |

### Entry template

Copy this template for each subsequent interaction:

```text
### YYYY-MM-DD — Short description

- **Area/files:** `path/to/file`, feature, or commit
- **Prompt or goal:** What problem was given to the AI
- **AI contribution:** Suggestions, code, tests, analysis, or documentation provided
- **Developer decisions:** What was accepted, changed, or rejected and why
- **Verification:** Tests, builds, manual checks, or review performed
- **Result:** Summary of the final change
```

## Development principles

- Do not include passwords, tokens, private data, or other secrets in prompts or
  in this log.
- Log meaningful contributions rather than every short conversational exchange.
- Reference the affected files or feature so an interaction can be audited later.
- Record the developer's review and verification, not only the AI's suggestion.
- Record prompts by intent and outcome, together with the relevant context and
  artifacts that informed the work.
- Update this file as part of the same change when AI materially contributes to
  implementation, tests, or documentation.
