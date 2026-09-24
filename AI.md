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
- `README.md` — project assumptions, reasoning, trade-offs, API boundary,
  and thread-safety decision;
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

### 11. 2026-09-23 — Remove non-source artifacts from the initial commit

- **Prompt/goal:** Remove unnecessary package metadata files from the initial
  project commit, then verify the remaining structure.
- **AI contribution:** Identified the package metadata files as optional for the
  implementation skeleton and amended the commit to retain only relevant
  project files.
- **Developer decision:** Requested removal of the package metadata files.
- **Verification:** Reviewed the amended commit and confirmed that the
  documentation, Maven configuration, and ignore rules remain included.
- **Result:** The initial commit now contains the project documentation and
  Maven configuration without the optional package metadata.

### 12. 2026-09-23 — Improve task-document formatting

- **Prompt/goal:** Improve the formatting of `task.md` while preserving all
  assignment requirements.
- **AI contribution:** Converted plain text sections into Markdown headings,
  formatted lists and code references, and clarified the example scenario
  layout without changing its meaning.
- **Developer decision:** Requested formatting cleanup and reviewed the
  resulting document.
- **Verification:** Compared the reformatted document with the original
  requirements.
- **Result:** Updated `task.md` for clearer repository documentation.

### 13. 2026-09-23 — Begin TDD implementation

- **Area/files:** `src/main/java/`, `src/test/java/`, `pom.xml`
- **Prompt or goal:** Start the scoreboard implementation using TDD and aim
  for at least 80% code coverage.
- **AI contribution:** Added behavior-focused tests first, then the minimum
  immutable domain model, public API, domain exceptions, thread-safe
  in-memory implementation, concurrency tests, and JaCoCo coverage checks.
- **Developer decisions:** Rejected Lombok after reviewing its limited value
  for records, enums, and small explicit exception constructors.
- **Verification:** The standard test suite reached a green state with 11
  tests. The first coverage verification was blocked because JaCoCo 0.8.12
  did not support Java 25 class files; the plugin was upgraded to 0.8.15 and
  the coverage check subsequently passed.
- **Result:** The initial TDD implementation and measurable 80% coverage gate
  are in place.

### 14. 2026-09-23 — Limit implementation to mandatory operations

- **Area/files:** `README.md`, `src/main/java/`, `src/test/java/`
- **Prompt or goal:** Remove the planned additional result operation and
  replace incremental goal events with direct score updates that can correct a
  disallowed goal.
- **AI contribution:** Changed the public API to the four mandatory operations,
  removed `MatchResult` and `TeamSide`, and added non-negative score replacement
  through `updateScore`.
- **Developer decisions:** Deferred the additional operation and selected score
  replacement so an application can correct a score before play restarts.
- **Verification:** Added tests for score correction and rejection of negative
  scores. `mvn verify` passed with 11 tests and the 80% JaCoCo coverage gate.
- **Result:** The implementation scope now matches the mandatory requirements.

### 15. 2026-09-23 — Review AI usage tracking

- **Area/files:** `AI.md`
- **Prompt or goal:** Check that the implementation work and decisions are
  being recorded in the AI history as requested.
- **AI contribution:** Audited the prompt history, interaction table, context
  section, and verification statements for stale or missing information.
- **Developer decisions:** Requested transparent tracking of implementation
  changes and AI-assisted decisions.
- **Verification:** Compared the recorded history with the current API,
  implementation scope, and successful Maven verification.
- **Result:** Corrected stale references to the deferred additional operation
  and updated the implementation and coverage verification status.

## Interaction log

Record each meaningful AI-assisted task using the following information:

| No. | Date | Area or files | Purpose | AI contribution | Developer contribution and verification |
|-----|------|---------------|---------|-----------------|------------------------------------------|
| 1 | 2026-09-23 | `AI.md` | Establish AI usage tracking | Proposed the disclosure structure and reusable log format | Reviewed and accepted the structure |
| 2 | 2026-09-23 | `AI.md`, `task.md` | Validate AI tracking requirements | Identified the required prompt-history, context, and artifact sections | Compared the document with the assignment requirements |
| 3 | 2026-09-23 | `README.md`, `football-rules.md` | Define domain and API decisions | Recommended transport-independent API, in-memory state, validation, lifecycle rules, incremental goals, and thread safety | Accepted the decisions and documented assumptions and trade-offs |
| 4 | 2026-09-23 | `README.md` | Select the additional operation | Proposed `getMatchResult(MatchId)` | Initially accepted and documented; later deferred to keep the first implementation limited to mandatory operations |
| 5 | 2026-09-23 | `README.md`, `AI.md` | Validate project documentation | Reviewed requirement coverage and professionalized the prompt history | Reviewed the final documentation set |
| 6 | 2026-09-23 | `README.md` | Simplify package naming and define the TDD approach | Recommended a concise package structure and a Red-Green-Refactor workflow with behavior-focused tests | Accepted the recruitment-project scope and documented the Maven layout and TDD process |
| 7 | 2026-09-23 | `pom.xml`, `src/main/java/` | Establish the Maven project structure | Proposed the Maven coordinates, Java version, package directories, and test-first implementation starting point | Accepted the project skeleton and removed the placeholder test so the next test represents real behavior |
| 8 | 2026-09-23 | `pom.xml` | Set the project Java version | Updated the Maven compiler release from Java 17 to Java 25 | Requested Java 25 and accepted the configuration change; local verification is pending a Java 25/Maven environment |
| 9 | 2026-09-23 | `AI.md`, `.gitignore` | Audit AI tracking and generated project files | Checked that documented AI contributions cover the assignment and identified generated Maven output and the current Java environment mismatch | Reviewed the audit and accepted ignoring `target/`; Java 25 configuration remains in `pom.xml` |
| 10 | 2026-09-23 | Java 25 toolchain | Verify the configured Java version | Ran the Java 25 runtime and compiler and compiled the current source tree with `--release 25` | Confirmed Java 25 compilation succeeds; full Maven verification remains blocked because Maven is unavailable |
| 11 | 2026-09-23 | Initial project commit | Remove optional package metadata from the commit | Identified `package-info.java` files as unnecessary for the project skeleton | Requested their removal; amended the initial commit and retained only relevant project files |
| 12 | 2026-09-23 | `task.md` | Improve task-document formatting | Reformatted headings, lists, code formatting, and the example scenario without changing the requirements | Requested formatting cleanup and reviewed the resulting document |
| 13 | 2026-09-23 | `src/main/java/`, `src/test/java/`, `pom.xml` | Begin TDD implementation | Added behavior-first tests, implementation, concurrency coverage, and JaCoCo enforcement | Requested TDD implementation with an 80% coverage target; reviewed the Lombok trade-off and accepted explicit Java code |
| 14 | 2026-09-23 | `README.md`, `src/main/java/`, `src/test/java/` | Limit implementation to mandatory operations | Replaced goal events with score replacement and removed the deferred additional operation | Requested only the four mandatory methods and score correction for disallowed goals |
| 15 | 2026-09-23 | `AI.md` | Review AI usage tracking | Audited and corrected stale implementation and verification records | Requested confirmation that AI-assisted work is recorded accurately |
| 16 | 2026-09-23 | `src/main/java/`, `src/test/java/`, `README.md` | Enforce one-goal score transitions | Added delta validation and tests for one-goal scoring and corrections | Accepted one-goal increases/decreases and rejected score jumps |
| 17 | 2026-09-23 | `README.md` | Document exception strategy | Added rationale for using specific unchecked domain exceptions and explained the checked-exception trade-off | Requested the exception design explanation be recorded in project documentation |
| 18 | 2026-09-23 | `src/main/java/com/worldcupscoreboard/implementation/` | Extract match state from the scoreboard implementation | Moved the mutable match state into a package-private `MatchState` class to reduce `InMemoryScoreboard` size while preserving implementation encapsulation | Requested a human-readable class structure with package-level access rather than a nested private class |
| 19 | 2026-09-23 | `src/main/java/com/worldcupscoreboard/implementation/` | Separate scoreboard validation and lifecycle logic | Extracted one-goal score validation, moved match transitions into `MatchState`, centralized summary ordering, and reused `MatchId.generate()` | Requested the proposed SRP refactoring to make `InMemoryScoreboard` smaller and easier to read |
| 20 | 2026-09-23 | `src/main/java/com/worldcupscoreboard/implementation/` | Extract summary ordering comparator | Moved the summary ordering rule into package-private `MatchStateSummaryComparator` | Requested a dedicated comparator so `InMemoryScoreboard` remains focused on orchestration |
| 21 | 2026-09-23 | `src/main/java/com/worldcupscoreboard/api/`, `src/main/java/com/worldcupscoreboard/implementation/` | Normalize scoreboard type naming | Renamed `ScoreBoard` to `Scoreboard` and `InMemoryScoreBoard` to `InMemoryScoreboard`, including tests and documentation | Requested conventional Java compound-word naming; retained implementation helpers in the implementation package |
| 22 | 2026-09-24 | `src/test/java/com/worldcupscoreboard/implementation/InMemoryScoreboardTest.java` | Review and decide on the `updateTo` test helper | Explained the helper's purpose, trade-offs, and explicit-update alternatives | Requested the recommended approach, then restored the helper-based version after review |
| 23 | 2026-09-24 | `src/test/java/com/worldcupscoreboard/implementation/` | Expand missing scoreboard test coverage | Identified validation and concurrency coverage gaps and implemented focused tests in existing classes | Requested no new test classes and reviewed the passing full test suite |
| 24 | 2026-09-24 | `src/main/java/com/worldcupscoreboard/implementation/`, `src/test/java/com/worldcupscoreboard/implementation/` | Decide whether validator-specific tests are needed | Recommended testing `ScoreUpdateValidator` through the public scoreboard boundary | Accepted the boundary-test approach and did not add a separate validator test class |
| 25 | 2026-09-24 | `src/main/java/`, `src/test/java/`, `README.md` | Check basic functionality requirements | Audited the four mandatory operations and related lifecycle, ordering, validation, immutability, and concurrency behavior | Reviewed the analysis and confirmed no production changes were needed |
| 26 | 2026-09-24 | `AI.md` | Complete recent AI usage tracking | Identified missing records for the latest test analysis, implementation decisions, and requirements audit | Requested this update and reviewed the resulting documentation change |
| 27 | 2026-09-24 | `src/main/java/`, `src/test/java/`, `README.md` | Complete recent quality and documentation updates | Added Unicode-aware team-name normalization, public API Javadocs, import cleanup, and clarified that match-result tests are future work | Requested the changes, reviewed the scope, and verified the test suite |
| 28 | 2026-09-24 | `.github/skills/ai-tracking/SKILL.md`, `AI.md` | Create a reusable AI usage tracking workflow | Added a repository skill with entry templates and consistency checks to reduce missed AI usage records | Requested an automated tracking aid after reviewing recent omissions; reviewed the skill and log update |
| 29 | 2026-09-24 | `README.md`, Git branch `feature/match-result` | Mark the planned match-result feature branch | Added a Future work section identifying the dedicated branch and preserving the current API scope | Requested the new branch and README marker; reviewed the branch and documentation diff |
| 30 | 2026-09-24 | `src/main/java/`, `src/test/java/`, `README.md` | Implement the fifth scoreboard operation with TDD | Added finished-match result retrieval with home win, away win, and draw outcomes, plus red/green tests and documentation | Requested TDD implementation on `feature/match-result`; reviewed the API, domain behavior, and passing test suite |
| 31 | 2026-09-24 | `src/main/java/`, `src/test/java/`, `README.md` | Return the current match state from the result operation | Added `IN_PROGRESS` to `MatchResult`, updated active-match behavior, tests, and documentation | Requested active matches to return their current state instead of throwing; reviewed the focused API change |
| 32 | 2026-09-24 | `README.md`, `src/main/java/com/worldcupscoreboard/api/Scoreboard.java` | Document the score and result API design consultation | Explained why live scores come from `getSummary()` and outcomes/state come from `getMatchResult()` | Requested the design consultation be documented; accepted the separation between score updates and result classification |
| 33 | 2026-09-24 | `src/main/java/com/worldcupscoreboard/implementation/`, `README.md` | Refine result naming and project documentation | Renamed the internal result method to `getResult`, reviewed explicit imports, and corrected the README structure and operation wording | Requested the refactor and documentation audit; accepted keeping the public `getMatchResult` API unchanged |

### 16. 2026-09-23 — Enforce one-goal score transitions

- **Area/files:** `src/main/java/`, `src/test/java/`, `README.md`
- **Prompt or goal:** Restrict score updates to exactly one goal increase or
  decrease for one team at a time.
- **AI contribution:** Added delta validation and tests for scoring events,
  one-goal corrections, multi-goal changes, and simultaneous team changes.
- **Developer decisions:** Accepted decreases for corrections such as a goal
  disallowed before play restarts, while rejecting all other score jumps.
- **Verification:** Added deterministic and concurrent unit-test coverage.
  `mvn verify` passed with 12 tests and the 80% JaCoCo coverage gate.
- **Result:** Score transitions now model one discrete football goal event or
  one-goal correction.

### 22. 2026-09-24 — Review the `updateTo` test helper

- **Area/files:** `src/test/java/com/worldcupscoreboard/implementation/InMemoryScoreboardTest.java`
- **Prompt or goal:** Analyse whether the `updateTo` helper follows testing
  best practices and explain alternatives.
- **AI contribution:** Explained that the helper builds legal one-goal
  transitions, but hides domain behavior and repeatedly reads from the public
  summary. Recommended explicit updates for behavior-focused tests or a
  clearer helper with explicit starting and target scores.
- **Developer decision:** Requested implementation of the recommended
  approach, then decided to restore the previous helper-based version after
  reviewing the change.
- **Verification:** Inspected the diff after restoration and preserved the
  existing unrelated test changes.
- **Result:** The original `updateTo` helper remains in place by developer
  choice.

### 23. 2026-09-24 — Analyse missing scoreboard tests

- **Area/files:** `src/test/java/com/worldcupscoreboard/implementation/`
- **Prompt or goal:** Review the existing in-memory scoreboard tests and
  identify meaningful coverage gaps without unnecessarily adding test classes.
- **AI contribution:** Identified missing coverage for null team names,
  trimming, duplicate-team checks in both positions, score boundaries, and
  strict exception assertions in concurrency tests.
- **Developer decision:** Requested the missing coverage to be added to the
  existing test classes.
- **Verification:** Ran the complete Maven test suite successfully.
- **Result:** Existing test classes now cover the identified validation and
  concurrency cases.

### 24. 2026-09-24 — Validate implementation-package test boundaries

- **Area/files:** `src/main/java/com/worldcupscoreboard/implementation/`,
  `src/test/java/com/worldcupscoreboard/implementation/`
- **Prompt or goal:** Determine whether `ScoreUpdateValidator` requires a
  dedicated test class.
- **AI contribution:** Recommended testing the package-private validator
  through the public `InMemoryScoreboard.updateScore` API rather than coupling
  tests to an implementation detail. Identified away-team scoring as the one
  useful additional boundary case.
- **Developer decision:** Kept validation tests at the scoreboard boundary and
  did not add a separate validator test class.
- **Verification:** Reviewed validator behavior and existing scoreboard tests.
- **Result:** No new validation test class was introduced.

### 25. 2026-09-24 — Audit basic functionality requirements

- **Area/files:** `src/main/java/`, `src/test/java/`, `README.md`
- **Prompt or goal:** Check whether all basic scoreboard functionality is
  implemented.
- **AI contribution:** Compared the public API and implementation with the
  four mandatory operations: starting, updating, finishing, and summarizing
  matches. Also reviewed lifecycle, ordering, validation, immutability, and
  thread-safety behavior.
- **Developer decision:** Accepted the conclusion that the mandatory
  functionality is implemented and that no production changes are required.
- **Verification:** Reviewed all main source files, tests, and documented
  requirements.
- **Result:** Confirmed that the implementation satisfies the basic scope.

### 26. 2026-09-24 — Verify AI usage tracking

- **Area/files:** `AI.md`
- **Prompt or goal:** Check whether AI-assisted work was being tracked in the
  repository.
- **AI contribution:** Audited the AI usage summary, prompt history,
  interaction table, and commit attribution. Identified that the recent test
  analysis and validation changes were not yet recorded.
- **Developer decision:** Requested that the recent conversations and changes
  be added to `AI.md`.
- **Verification:** Compared the documented history with the recent source
  changes and commits.
- **Result:** Extended this file with the missing recent AI-assisted work.

### 27. 2026-09-24 — Complete recent quality and documentation updates

- **Area/files:** `src/main/java/`, `src/test/java/`, `README.md`
- **Prompt or goal:** Apply the remaining review feedback and ensure recent
  AI-assisted changes are tracked.
- **AI contribution:** Replaced ASCII-only trimming with Unicode-aware
  stripping and added regression coverage, documented the public Java API,
  cleaned up imports, and marked match-result tests as future work.
- **Developer decision:** Requested the focused changes and accepted public
  API documentation without adding noisy documentation to private helpers.
- **Verification:** Ran the test suite and reviewed the resulting diffs and
  repository scope.
- **Result:** Recent implementation, test, and documentation improvements are
  now recorded in the AI usage history.

### 28. 2026-09-24 — Create a reusable AI usage tracking workflow

- **Area/files:** `.github/skills/ai-tracking/SKILL.md`, `AI.md`
- **Prompt or goal:** Create a repository skill that makes AI-assisted changes
  easier to track without requiring a separate manual reminder.
- **AI contribution:** Added a reusable workflow with entry templates,
  synchronization checks, privacy guidance, and completion checks for the
  prompt history and interaction table.
- **Developer decision:** Requested an automated tracking aid after reviewing
  recent omissions and accepted the skill-based approach.
- **Verification:** Reviewed the skill instructions, updated both AI.md
  tracking sections consistently, and checked the documentation diff.
- **Result:** Future meaningful AI-assisted changes have a documented workflow
  for updating `AI.md`.

### 29. 2026-09-24 — Mark the planned match-result feature branch

- **Area/files:** `README.md`, Git branch `feature/match-result`
- **Prompt or goal:** Create a dedicated branch for the planned additional
  match-result operation and identify it in the project documentation.
- **AI contribution:** Created `feature/match-result` and added a README
  section distinguishing the future operation from the current API.
- **Developer decision:** Requested the branch and documentation marker.
- **Verification:** Confirmed the active branch and reviewed the README diff.
- **Result:** Future match-result work has a named branch and documented scope.

### 30. 2026-09-24 — Implement the fifth scoreboard operation with TDD

- **Area/files:** `src/main/java/`, `src/test/java/`, `README.md`
- **Prompt or goal:** Implement the one additional operation required by the
  assignment on the dedicated `feature/match-result` branch using TDD.
- **AI contribution:** Added failing tests first, then implemented
  `MatchResult`, `getMatchResult`, finished-match result calculation, API
  documentation, and README updates identifying it as the fifth operation.
- **Developer decision:** Requested home-win, away-win, and draw results only
  for finished matches and accepted the selected enum-based API.
- **Verification:** Confirmed the red phase failed before implementation, then
  ran the full Maven test suite successfully after implementation.
- **Result:** The fifth scoreboard operation is implemented and documented.

### 31. 2026-09-24 — Return the current match state from the result operation

- **Area/files:** `src/main/java/`, `src/test/java/`, `README.md`
- **Prompt or goal:** Allow `getMatchResult` to report that an active match is
  still in progress instead of rejecting the request.
- **AI contribution:** Added `IN_PROGRESS` to `MatchResult`, returned it for
  active matches, updated the public contract and README, and changed the
  regression test to assert the new behavior.
- **Developer decision:** Requested current-state responses for active matches
  while retaining `MatchNotFoundException` for unknown matches.
- **Verification:** Reviewed the focused implementation, test, and documentation
  changes.
- **Result:** `getMatchResult` now reports both active and finished match states.

### 32. 2026-09-24 — Document the score and result API design consultation

- **Area/files:** `README.md`,
  `src/main/java/com/worldcupscoreboard/api/Scoreboard.java`
- **Prompt or goal:** Record how callers update a live score without using the
  match result operation.
- **AI contribution:** Explained that `getSummary()` provides the current
  active score, `updateScore(...)` applies the new score, and
  `getMatchResult(...)` reports state or final outcome only.
- **Developer decision:** Requested this design consultation to be added to
  the README and accepted the separation of score data from result
  classification.
- **Verification:** Reviewed the public API behavior and documented the
  intended caller flow and rationale.
- **Result:** The README now explains how the score and result operations work
  together.

### 33. 2026-09-24 — Refine result naming and project documentation

- **Area/files:** `src/main/java/com/worldcupscoreboard/implementation/`,
  `README.md`
- **Prompt or goal:** Rename the internal result method and audit project
  structure, imports, and README completeness.
- **AI contribution:** Renamed `MatchState.result()` to `getResult()`,
  confirmed production imports are explicit, and updated the README tree and
  operation wording to include the fifth operation and implementation helpers.
- **Developer decision:** Requested the internal refactor while retaining the
  public `getMatchResult()` API because it clearly describes the public
  operation.
- **Verification:** Reviewed source imports, project structure, README
  consistency, and the resulting diff.
- **Result:** Naming and project documentation now match the implemented
  structure and API.

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
