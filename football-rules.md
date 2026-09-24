# Football Rules Relevant to the Scoreboard

## Scope

This document records only football rules that influence the scoreboard
boundaries. It is supporting domain context, not a complete implementation of
the Laws of the Game.

## Match duration

- Regulation time consists of two 45-minute halves.
- Stoppage time compensates for time lost during each half.
- Some competitions use extra time when a winner is required.

The scoreboard does not model a match clock, halves, stoppage time, or extra
time. The consuming application decides when the match is in progress and when
to call `finishMatch`.

## Penalty shoot-outs

- A penalty shoot-out may determine which team advances after a drawn match
  when competition regulations require a winner.
- Shoot-out kicks are not added to the regular match score.
- A match decided by penalties remains a draw in the regular match result.

Penalty shoot-outs and competition progression are outside the scope of this
library. The scoreboard stores regular match scores only.

## Implementation impact

The implementation therefore:

- accepts score updates while a match has `IN_PROGRESS` status;
- retains the regular score when a match is finished;
- does not calculate extra-time or penalty-shoot-out scores;
- leaves competition-specific decisions to the consuming application.

## Sources

- [IFAB Law 7: The Duration of the Match](https://www.theifab.com/laws/latest/the-duration-of-the-match/)
- [The Football Association: Law 7](https://www.thefa.com/football-rules-governance/lawsandrules/laws/football-11-11/law-7---the-duration-of-the-match)
- [Wikipedia: Penalty shoot-out](https://en.wikipedia.org/wiki/Penalty_shoot-out)
