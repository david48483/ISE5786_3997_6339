# Copilot Instructions for This Repository

Apply these rules in every code change unless the user explicitly asks otherwise.

## Mandatory documentation

- Always write JavaDoc for every class and every member declaration (field or method).
- Do not write JavaDoc for methods that implement interface methods.
- Do not write JavaDoc for methods annotated with `@Override`.
- Write JavaDoc immediately after each class declaration, field declaration, and method declaration (after declaration
  planning and before implementation).
- Write all comments in English only.
- Keep JavaDoc external-facing: explain what the component is/does, not how it is implemented.

## Coding standards

- Use Java style formatting with standard braces and clean spacing.
- Use meaningful names:
    - Classes/types: `PascalCase`
    - Methods: `camelCase`
    - Variables/fields/parameters: `camelCase`
    - Instance fields: `_fieldName`
    - Static fields: `s_fieldName`
    - Constants: `UPPER_CASE`
- Keep instance fields `private` unless explicitly required otherwise.
- Follow Java brace style, for example:
  ```java
  if (condition) {
      doSomething();
  } else {
      doSomethingElse();
  }
  ```
- Keep proper indentation and spaces around operators.
- Do not use multiple consecutive blank lines.
- Do not leave `TODO` placeholders at this stage.
- Do not keep unused fields, methods, or local variables.
- Introduce local variables only when they improve DRY or readability.
- Do not hardcode configurable or repeated values; extract them to named constants.
- Use the IDE auto-formatter to enforce indentation and spacing.

## Object methods and geometry safety

- For value-like classes, implement `equals` and `toString` according to project conventions.
- Keep `toString` concise but useful for debugging.
- Validate geometric edge cases (zero vectors, division by zero, parallel rays, and similar conditions).

## Core design principles

- Prefer immutability:
    - Make fields `final` whenever possible.
    - Do not add setters unless explicitly required.
    - Prefer operations that return new objects instead of mutating existing state.
- Enforce DRY:
    - Avoid duplicated code.
    - Reuse existing methods when possible.
- Keep efficiency in mind:
    - Avoid unnecessary temporary object creation.
