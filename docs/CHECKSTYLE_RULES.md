# CHECKSTYLE_RULES.md — Allterra Server Code Style

Source: `config/checkstyle/checkstyle.xml` (Google Java Style + project overrides).

Suppressions: `config/checkstyle/suppressions.xml` (tests relaxed, `FinalParameters` disabled project-wide).

---

## Formatting & Whitespace

| Rule | Requirement |
|------|-------------|
| **Line length** | ≤ 140 chars (Java files) |
| **Indentation** | Spaces only. Line-wrapping indent = 0. Array init indent = 8. |
| **Tabs** | NEVER. `FileTabCharacter` enforced. |
| **Braces** | Always required (`NeedBraces`). Left-brace on same line. |
| **Empty blocks** | `{ }` compact form only for try/finally/if/else/switch. |
| **Empty lines** | Separator between members; no multiple consecutive empty lines. |
| **Whitespace** | Around keywords/operators. None before comma/semi/dot/ellipsis. |
| **Method params** | No space between method name and `(`. No padding inside parens. |
| **Generics** | No whitespace after `<` or before `>`. |
| **Dot wrapping** | Newline before dot in chained calls (`.method()` on next line). |
| **Comma wrapping** | Comma at end of line (not start of next). |
| **One statement** | One per line (`OneStatementPerLine`). |
| **Multiple declarations** | NOT allowed — one variable per declaration. |
| **Operator wrap** | Operator on new line. |

## Naming

| Element | Convention | Pattern |
|---------|-----------|---------|
| **Package** | lowercase, dot-separated | `^[a-z]+(\.[a-z][a-z0-9]*)*$` |
| **Type** | PascalCase | Default |
| **Method** | camelCase | `^[a-z][a-z0-9][a-zA-Z0-9_]*$` |
| **Member/Param/Local** | camelCase | Default |
| **Constant** | UPPER_SNAKE_CASE | Default |
| **Lambda param** | camelCase or `__` | `^([a-z][a-zA-Z0-9]*)\|(__)$` |
| **Catch param** | camelCase | Default |
| **Abbreviation** | ≤ 1 char allowed in name | e.g., `Poi` not `POI` |

## Imports

- **No star imports** (`AvoidStarImport`).
- **Order:** third-party → special → standard Java → static.

## Javadoc

| Rule | Requirement |
|------|-------------|
| **Class javadoc** | Required (`MissingJavadocType`). |
| **Public methods** | Required if ≥ 2 lines. Exceptions: `@Override`, `@Test`, `process*`, `initialize`. |
| **Summary** | First sentence must be a valid summary (no `@return the *` or `A {@code Foo} is a`). |
| **`@return` tag** | Can be omitted (`allowMissingReturnTag`). |
| **`@param` tag** | Can be omitted (`allowMissingParamTags`). |
| **Position** | Javadoc must be directly before the element, not floating. |
| **Tags** | Proper continuation indent. Order: `@param` → `@return` → `@throws`. |

## Class Design

| Rule | Requirement |
|------|-------------|
| **Design for extension** | Classes should be `final` or methods designed for override. Ignored: `@Override`, `@Test`. |
| **Final class** | Classes with only private constructors must be `final`. |
| **Utility classes** | Private constructor required (hidden). |
| **Modifier order** | Standard JLS order. |
| **Overload order** | Overloaded methods grouped together. |
| **One top-level class** | One per file. |

## Method Code

| Rule | Requirement |
|------|-------------|
| **Empty catch** | Only if variable named `expected`. |
| **Fall-through** | Must be documented. |
| **Variable distance** | Declare close to usage. |
| **No finalizer** | No `finalize()` methods. |
| **`final` keyword** | Project-wide `FinalParameters` is **disabled** — `final` on params is optional. |

## Literals

- **Escape sequences** — use `\n`, `\t` etc., not octal/Unicode escapes.
- **Unicode escapes** — avoid in strings; use actual characters.
- **Long literals** — uppercase `L` (not lowercase `l`).

## Test Code (Suppressions)

The following checks are **relaxed** for `src/test/java`:
- `LineLength`, `TypeName`, `JavadocType`, `MissingJavadocMethod`, `AbbreviationAsWordInNameCheck`, `VariableDeclarationUsageDistance`, `DesignForExtension`

---

## Quick Checklist for AI Agents

Before committing Java code, verify:

- [ ] No tabs; spaces only
- [ ] Lines ≤ 140 chars
- [ ] Braces on all if/else/for/while/try
- [ ] No `import *`
- [ ] Import order: third-party → special → java → static
- [ ] Class has Javadoc
- [ ] Public methods ≥ 2 lines have Javadoc
- [ ] camelCase for methods/vars, PascalCase for types
- [ ] Dot on newline for chained calls
- [ ] Left-brace on same line
- [ ] No multiple empty lines
- [ ] Variable declared near usage
- [ ] `final` on params is optional (not enforced)
