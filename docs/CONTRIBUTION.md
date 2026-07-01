# CONTRIBUTION.md — Commit Convention

This project follows **Conventional Commits 1.0.0**.

## Format

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

## Types

| Type | Usage |
|------|-------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `style` | Formatting, semicolons, whitespace — no code change |
| `refactor` | Code restructuring without fixing a bug or adding a feature |
| `test` | Adding or updating tests |
| `chore` | Maintenance tasks (deps, build scripts, etc.) |
| `perf` | Performance improvement |
| `ci` | CI/CD changes |
| `build` | Build system or external dependencies |

## Rules

- Description must be **lowercase**.
- No period at the end of the description.
- Use **imperative mood** ("add" not "added" or "adds").
- Keep the first line ≤ 72 characters.
- Scope is optional but recommended for monorepo-style projects.

## Breaking Changes

- Append `!` after the type/scope: `feat(api)!: drop support for v1 tokens`
- Or include `BREAKING CHANGE:` in the footer with a description.

## Examples

```
feat(post): add like and unlike endpoints
fix(auth): handle null authentication in notification service
docs: add CHECKSTYLE_RULES.md
refactor(repo): extract shared post type mappers
test: add NotificationService NPE tests
chore: update liquibase master with task9 changesets
```
