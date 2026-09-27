# World Cup Scoreboard

Java/Maven library implementing a Live Football World Cup Scoreboard with
match management, live score updates, and ordered match summaries.

## Technology stack

- Java 25
- Maven
- JUnit Jupiter 5.10.2 for tests
- JaCoCo 0.8.15 for test coverage and the 80% line-coverage verification gate
- Git and GitHub for version control and collaboration
- GitHub Copilot for development assistance
- CodeRabbit for automated pull-request reviews in GitHub

## Maven and package structure

This is a small recruitment project, so the package name is intentionally
simple:

```text
com.worldcupscoreboard
```

The Maven coordinates and source layout are:

```text
world-cup-scoreboard/
├── pom.xml
├── README.md
├── AI.md
├── src/
│   ├── main/java/com/worldcupscoreboard/
│   │   ├── api/
│   │   │   └── Scoreboard.java
│   │   ├── model/
│   │   │   ├── MatchId.java
│   │   │   ├── MatchStatus.java
│   │   │   ├── MatchSummary.java
│   │   │   ├── Score.java
│   │   │   └── ...
│   │   ├── implementation/
│   │   │   ├── InMemoryScoreboard.java
│   │   │   ├── MatchState.java
│   │   │   ├── MatchStateSummaryComparator.java
│   │   │   └── ScoreUpdateValidator.java
│   │   └── exception/
│   │       └── ...
│   └── test/java/com/worldcupscoreboard/
│       ├── implementation/
│       │   ├── InMemoryScoreboardTest.java
│       │   └── InMemoryScoreboardConcurrencyTest.java
│       └── model/
│           └── ScoreTest.java
```

The `api` package contains the public interface, `model` contains immutable
domain types, `implementation` contains the in-memory implementation, and
`exception` contains domain-specific failures. Tests are organized around
observable behavior and mirror the relevant production packages.

## Implemented requirements

The library must support multiple matches in progress and provide these core
operations:

1. Start a new match.
2. Update the score.
3. Finish a match.
4. Return a summary of matches in progress.

The summary is ordered by:

1. total score, descending;
2. most recently started match first when total scores are tied.

The implementation contains the four mandatory operations. The fifth
operation, `getMatch`, is the one additional operation selected for this
implementation.

The fifth operation was introduced in a distinct feature commit, as required
by the assignment.

The expected summary ordering is illustrated by the following matches:

```text
Mexico 0 - Canada 5
Spain 10 - Brazil 2
Germany 2 - France 2
Uruguay 6 - Italy 6
Argentina 3 - Australia 1
```

The result is:

```text
Uruguay 6 - Italy 6
Spain 10 - Brazil 2
Mexico 0 - Canada 5
Argentina 3 - Australia 1
Germany 2 - France 2
```

## API design

The library exposes a Java API rather than a REST endpoint, Kafka consumer, or
command-line interface. External applications are responsible for converting
their input into calls to this API.

The intended public operations are:

```java
MatchId startMatch(String homeTeam, String awayTeam);

void updateScore(MatchId matchId, int homeScore, int awayScore);

void finishMatch(MatchId matchId);

MatchSummary getMatch(MatchId matchId);

List<MatchSummary> getSummary();

```

`MatchId`, `MatchSummary`, and `Score` are domain types. The API uses a
generated match ID instead of team names because the same teams may play again
after an earlier match has finished.

### Usage example

```java
Scoreboard scoreboard = new InMemoryScoreboard();

MatchId matchId = scoreboard.startMatch("Mexico", "Canada");
scoreboard.updateScore(matchId, 1, 0);

MatchSummary liveMatch = scoreboard.getMatch(matchId);
List<MatchSummary> activeMatches = scoreboard.getSummary();

scoreboard.finishMatch(matchId);
MatchSummary finishedMatch = scoreboard.getMatch(matchId);
```

`getSummary()` returns all matches that are currently in progress in scoreboard
order. `getMatch(...)` retrieves one known match and can return either an
active or finished snapshot.

## Assumptions and domain rules

- Team names cannot be `null` or blank.
- The home and away teams must be different.
- A team cannot participate in more than one match in progress.
- A team becomes available for another match after its current match finishes.
- Team names are normalized consistently for validation and uniqueness checks.
- A generated `MatchId` uniquely identifies a match.
- Unknown match IDs cause a domain exception.
- A match has the lifecycle `IN_PROGRESS` followed by `FINISHED`.
- Scores can be updated only while a match is in progress.
- A finished match cannot be updated or finished again.
- Each score update must change exactly one team's regular score by exactly one
  goal, either increasing it or decreasing it. Decreases represent corrections
  such as a goal disallowed before play restarts.
- A summary contains only matches that are currently in progress.
- Summary results and domain snapshots are immutable.

## Exception handling

Domain failures are represented by specific unchecked exceptions:
`InvalidMatchException`, `MatchNotFoundException`,
`TeamAlreadyPlayingException`, and `InvalidMatchStateException`.

These failures indicate invalid input or a violated scoreboard lifecycle
contract, similar to `IllegalArgumentException` and `IllegalStateException`
in the Java standard library. Callers should validate inputs and correct their
workflow rather than being forced to catch an exception for every scoreboard
operation. Using unchecked exceptions also keeps the public interface concise
and avoids coupling consumers to checked `throws` declarations.

Checked exceptions would be appropriate if these failures were expected
recoverable business outcomes that every caller had to handle explicitly. For
this in-memory library, specific runtime exceptions provide clear failure
types without adding that obligation.

The score update operation supports one-goal events and corrections to the
current regular score:

```text
initial score: 0 - 0
goal scored: 1 - 0
goal disallowed: 0 - 0
```

The library does not model the complete Laws of the Game. The relevant football
rules and their sources are summarized in [`football-rules.md`](football-rules.md).
In particular, this implementation does not include a match clock, halves,
stoppage time, extra time, or penalty shoot-outs. A score update is accepted while the match is
`IN_PROGRESS`; the consuming application decides when to call `finishMatch`.
This keeps the API focused on scoreboard state and lifecycle.

## Match retrieval

The additional `getMatch(MatchId)` operation returns a complete immutable
snapshot for either an active or finished match, including its ID, teams,
score, and lifecycle status. This is more useful than returning only a result
enum because callers can inspect a historical match without needing to
reconstruct its state or derive its identifier from the active summary.

The operation is intentionally separate from `getSummary()`: `getSummary()`
returns all active matches in scoreboard order, while `getMatch(...)` retrieves
one known match, including finished matches. Callers still use `getSummary()`
to read an active score before calling `updateScore(...)`. This design was
chosen for the recruitment task because it demonstrates a useful query
boundary, reuses the existing immutable `MatchSummary` type, and adds exactly
one operation without duplicating result-calculation logic.

## Storage and integration boundary

The reference implementation stores state in memory. This is appropriate for
the exercise because the library should not own a database, REST framework,
Kafka integration, or other application infrastructure.

Persistence, recovery after a process restart, and coordination between
multiple application instances are outside the scope of this library. An
application may place the library behind a REST controller, Kafka consumer,
scheduled job, or another adapter without coupling the scoreboard domain to
that transport.

Finished matches are retained with `FINISHED` status and excluded from the
active summary. Administrative forfeits and post-match disciplinary decisions
are outside the current scoreboard API.

## Thread safety

The in-memory scoreboard is thread-safe within one JVM instance. A fair
`ReentrantReadWriteLock` allows `getMatch` and `getSummary` to run concurrently,
while `startMatch`, `updateScore`, and `finishMatch` take the exclusive write
lock. The write lock protects compound changes such as checking team
availability, creating a match, registering its teams, and assigning its start
sequence. `getSummary()` captures immutable match snapshots and their start
sequences under the read lock, then sorts them after releasing it. This keeps
each result consistent while avoiding holding the lock during sorting.

Fair mode prevents newly arriving readers from continually overtaking a
queued writer, but it does not interrupt readers that already hold the lock or
guarantee strict priority over every reader already queued. Writes to different
matches also serialize because the scoreboard's maps and invariants are shared.

Thread safety does not provide persistence or distributed coordination between
separate scoreboard instances.

## Reasoning and trade-offs

### In-memory state versus a database

In-memory storage keeps the implementation small, fast, dependency-free, and
easy to test. A database would add schema, migrations, transactions,
configuration, and deployment concerns that are not required by the task.
The trade-off is that state is lost when the process stops.

### Generated IDs versus team-based lookup

Generated IDs prevent ambiguity when teams play more than once. Team names are
still stored as display data and used for participation validation, but they
are not match identity.

### Score replacement versus incremental goals

Replacing the regular score allows a consuming application to correct a goal
that is disallowed before play restarts, while still rejecting negative scores.
The trade-off is that the library does not record the goal event history or
reason for a correction.

### Fair read/write lock versus a single exclusive lock

A fair read/write lock allows concurrent snapshot reads for a read-heavy
workload and prevents later-arriving readers from indefinitely bypassing a
waiting writer. Its trade-off is greater locking complexity, and writes still
serialize across the scoreboard. A single exclusive lock would be simpler but
would also serialize independent reads.

### Scoreboard scope versus full football rules

The task asks for a scoreboard, not a match-clock or football-rules engine.
Avoiding time and competition rules keeps the public API clear and focused.
The application remains responsible for deciding when the match is active and
when it should be finished.

## Verification expectations

The implementation should be developed using test-driven development (TDD).
Each behavior should follow the cycle:

1. **Red:** write one focused test that describes the required behavior and
   confirm that it fails for the expected reason.
2. **Green:** implement the smallest change that makes the test pass.
3. **Refactor:** improve names, structure, and duplication while keeping all
   tests passing.

Start with the public `Scoreboard` behavior rather than internal collections.
Use a small test increment for each rule, then refactor shared validation or
snapshot logic only when duplication appears. Keep concurrency tests separate
from deterministic behavior tests and run them after the basic implementation
is working.

Tests should cover:

- starting valid and invalid matches;
- rejecting duplicate active team participation;
- adding home and away goals;
- rejecting unknown IDs and updates after finishing;
- finishing matches and excluding them from the summary;
- the required summary ordering and tie-breaking;
- retrieving active and finished match snapshots;
- immutable summary results;
- concurrent operations preserving the domain invariants.
