# Contributing / Git Workflow

## Branching
- Default branch: `main`
- Do not commit directly to `main`.
- Branch naming:
  - `feature/name/<short-description>`
  - `bugfix/name/<short-description>`
  - `chore/name/<short-description>`

## Pull Requests (required)
- All merges to `main` happen via Pull Request (PR).
- Every PR must be linked to an Issue (use "Closes #<id>" in PR description).
- PRs require **2+ approvals** from teammates who are not the author.
- Resolve all review comments before merge.

## Reviews
- Reviewers should check:
  - Requirements & acceptance criteria satisfied
  - Code quality / naming / structure
  - No secrets committed
  - Basic testing steps provided

## Merging
- Squash merge is preferred (keeps history clean), unless the team decides otherwise.
- Delete branch after merge.

## Tips
- Keep PRs small when possible.
- If a PR changes behavior, update README or docs.