# Coding exercise

You are asked to implement a simple library that manages a Live Football World
Cup Scoreboard. Please read the full requirements before starting.

Using AI to help with design, reasoning, and code generation is required. If
you are selected for an interview, we will walk through your solution
together. We will ask you to explain key decisions, trade-offs, and code
details.

## What we expect

Provide the implementation of the Live Football World Cup Scoreboard as a
simple Java package in a Maven project.

Provide your solution as a Git repository exactly as you would submit it for
code review to your teammates at work. Follow your own standards for high
quality software engineering.

Create a `README.md` documenting:

- your assumptions;
- your reasoning;
- trade-offs made.

Create an `AI.md` that includes:

- a short summary of how AI tools were used;
- prompt history and other contextual information;
- any artifact that guided the implementation.

## Requirements

You work for a sports data company and must implement a scoreboard library that
supports multiple simultaneous matches. The requirements below intentionally
include open questions and design choices; part of the task is deciding how to
handle them and documenting your reasoning.

## Core operations

The library must support:

1. Start a new match.
2. Update the score.
3. Finish a match.
4. Get a summary of matches in progress.

The summary must return matches in progress ordered by:

1. total score, descending;
2. if tied, the most recently started match first.

## Additional operation

Add exactly one additional operation of your choice to the scoreboard. Document
the feature and the reason for choosing it in `README.md`. Introduce the
feature in a distinct Git commit.

## Example scenario

If the following matches are started in the specified order and updated with
these scores:

- Mexico 0 - Canada 5
- Spain 10 - Brazil 2
- Germany 2 - France 2
- Uruguay 6 - Italy 6
- Argentina 3 - Australia 1

The expected summary ordering is:

1. Uruguay 6 - Italy 6
2. Spain 10 - Brazil 2
3. Mexico 0 - Canada 5
4. Argentina 3 - Australia 1
5. Germany 2 - France 2