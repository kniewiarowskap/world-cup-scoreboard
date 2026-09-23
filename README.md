# World Cup Scoreboard

Java/Maven library implementing a Live Football World Cup Scoreboard with
match management, live score updates, and ordered match summaries.

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
│   │   │   └── ScoreBoard.java
│   │   ├── model/
│   │   │   ├── MatchId.java
│   │   │   ├── MatchResult.java
│   │   │   ├── MatchStatus.java
│   │   │   ├── MatchSummary.java
│   │   │   ├── Score.java
│   │   │   └── TeamSide.java
│   │   ├── implementation/
│   │   │   └── InMemoryScoreBoard.java
│   │   └── exception/
│   │       └── ...
│   └── test/java/com/worldcupscoreboard/
│       ├── implementation/
│       │   ├── InMemoryScoreBoardTest.java
│       │   └── InMemoryScoreBoardConcurrencyTest.java
│       └── model/
│           └── ScoreTest.java
```

The `api` package contains the public interface, `model` contains immutable
domain types, `implementation` contains the in-memory implementation, and
`exception` contains domain-specific failures. Tests are organized around
observable behavior and mirror the relevant production packages.

## Assignment requirements

The library must support multiple matches in progress and provide these core
operations:

1. Start a new match.
2. Update the score.
3. Finish a match.
4. Return a summary of matches in progress.

The summary is ordered by:

1. total score, descending;
2. most recently started match first when total scores are tied.

The implementation also provides exactly one additional operation:
`getMatchResult`, which returns the result of a finished match.

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

void addGoal(MatchId matchId, TeamSide teamSide);

void finishMatch(MatchId matchId);

List<MatchSummary> getSummary();

MatchResult getMatchResult(MatchId matchId);
```

`MatchId`, `TeamSide`, `MatchSummary`, and `MatchResult` are domain types. The API
uses a generated match ID instead of team names because the same teams may
play again after an earlier match has finished.

## Assumptions and domain rules

- Team names cannot be `null` or blank.
- The home and away teams must be different.
- A team cannot participate in more than one match in progress.
- A team becomes available for another match after its current match finishes.
- Team names are normalized consistently for validation and uniqueness checks.
- A generated `MatchId` uniquely identifies a match.
- Unknown match IDs cause a domain exception.
- A match has the lifecycle `IN_PROGRESS` followed by `FINISHED`.
- Goals can be added only while a match is in progress.
- A finished match cannot be updated or finished again.
- Each score operation adds exactly one goal to the selected team.
- Scores cannot become negative because arbitrary score replacement is not
  supported.
- A summary contains only matches that are currently in progress.
- Summary results and domain snapshots are immutable.

The incremental goal operation models football scoring as discrete goal events:

```text
initial score: 0 - 0
add a home goal: 1 - 0
add an away goal: 1 - 1
```

The library does not model the complete Laws of the Game. In particular, it
does not implement a match clock, halves, stoppage time, extra time, or
penalty shoot-outs. A score update is accepted while the match is
`IN_PROGRESS`; the consuming application decides when to call `finishMatch`.
This keeps the API focused on scoreboard state and lifecycle.

## Storage and integration boundary

The reference implementation stores state in memory. This is appropriate for
the exercise because the library should not own a database, REST framework,
Kafka integration, or other application infrastructure.

Persistence, recovery after a process restart, and coordination between
multiple application instances are outside the scope of this library. An
application may place the library behind a REST controller, Kafka consumer,
scheduled job, or another adapter without coupling the scoreboard domain to
that transport.

Finished matches are retained with `FINISHED` status so that
`getMatchResult` can be called after a match ends. Finished matches are excluded
from the active summary.

## Thread safety

The in-memory scoreboard is thread-safe within one JVM instance. A single
private lock protects all public operations, including compound operations such
as checking team availability, creating a match, registering its teams, and
assigning its start sequence.

The same protection applies to score updates, finishing matches, and creating
the immutable snapshot returned by `getSummary`. This prevents concurrent
callers from violating team participation, match lifecycle, score, or ordering
invariants.

Thread safety does not provide persistence or distributed coordination between
separate scoreboard instances.

## Additional operation: match result

The additional operation is:

```java
MatchResult getMatchResult(MatchId matchId);
```

It returns:

- `HOME_WIN` when the home team has more goals;
- `AWAY_WIN` when the away team has more goals;
- `DRAW` when both teams have the same score.

The operation is available only for a finished match. It does not determine a
winner from a penalty shoot-out because penalty shoot-outs are outside the
regular match score model.

This feature was selected because it is a useful football-domain operation,
requires finished-match state to be retained, and adds clear value without
introducing an unnecessary time engine or external infrastructure.

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

### Incremental goals versus score replacement

Adding one goal per operation reflects football scoring and prevents invalid
negative or arbitrary score changes. The trade-off is that callers cannot
replace a complete score in one call; they must submit individual goal events.

### Single lock versus unsynchronized access

A single lock is easy to reason about and protects related state changes as one
atomic operation. The trade-off is that concurrent operations are serialized.
The operations are short and in-memory, so this is preferable to exposing
inconsistent state or relying on callers to synchronize access.

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

Start with the public `ScoreBoard` behavior rather than internal collections.
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
- retrieving home wins, away wins, and draws;
- immutable summary results;
- concurrent operations preserving the domain invariants.
