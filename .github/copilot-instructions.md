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

## Mandatory TDD workflow for this stage

- Stage goals:
    - Continue developing the project according to Extreme Programming principles, with special emphasis on TDD.
    - Continue practicing structured unit test design and writing.
    - Add computational behavior to the geometric model by implementing intersection calculations.
    - Implement the Composite design pattern and manage collections using `List` and enhanced `for` loops.
- This stage extends the existing geometric model by adding behavior; do not redesign the model structure unless the
  stage instructions explicitly require it.
- Follow this exact work order for every required task in this stage:
    1. Define the class and method signatures.
    2. Write the JavaDoc.
    3. Write the unit tests.
    4. Implement incrementally while running all tests until they pass.
    5. Refactor.
    6. Run all tests again.
- Do not deviate from this order.

## Stage-specific project rules

- The following files are supplied for this stage and must not be modified unless the user explicitly asks to do so:
    - `src/primitives/Double3.java`
    - `src/primitives/Util.java`
    - `src/test/Main.java`
    - `src/geometries/impl/Polygon.java`
- Do not define retrieval methods (getters) at this stage, except in `Ray`.
- Do not define setter/update methods.
- Keep constructors and methods `public` unless explicitly stated otherwise.
- Keep instance fields `private` by default unless a rule explicitly requires a different access level; constants remain
  `public` when required by the project API.
- For `primitives.Point`, keep `_xyz` as `protected`.
- For `primitives.Vector`:
    - Do not add new fields.
    - Keep constants `AXIS_X`, `AXIS_Y`, and `AXIS_Z`.
    - Implement vector arithmetic according to existing `Double3`/direct-formula guidance.
    - `add` and `scale` must use `Double3` methods.
    - `dotProduct` and `crossProduct` must compute directly (no intermediate objects).
- For `primitives.Vector.normalize()`, return a new unit vector using `_xyz.divide(length())`. Do not use trigonometric
  functions; use linear algebra only.
- For `primitives.Ray`:
    - Keep fields `_origin` and `_direction`.
    - Keep getters `origin()` and `direction()`.
    - The constructor must normalize the direction vector before storing it.
- For `geometries.api.Geometry`, do not define a constructor at this stage.
- For `geometries.impl.RadialGeometry`, keep protected fields `_radius` and `_radiusSquared`. Do not add a `radius`
  field in subclasses — radius is inherited.
- For `geometries.impl.Plane` constructor `(Point p1, Point p2, Point p3)`, at this stage store one point and set
  `_normal` to `null`.
- For `geometries.impl.Triangle`, do not add fields and constructor should only delegate to `Polygon`.
- For `geometries.impl.Sphere`, add only the field `_center` of type `Point`. Do not redeclare a radius field.
- For `geometries.impl.Tube`, add only the field `_axis` of type `Ray` with `protected` access. Do not redeclare a
  radius field.
- For `geometries.impl.Cylinder`, add only the field `_height` of type `double`.
- At this stage only `Plane` and `Polygon` return a real normal from `getNormal`; all other geometries return `null`.

## Object methods and geometry safety

- For value-like classes, implement `equals` and `toString` according to project conventions.
- Keep `toString` concise but useful for debugging.
- Use `Util.isZero(...)` and `Util.alignZero(...)` for floating-point zero handling; do not compare doubles to zero
  directly with `==`, `<`, `>`, or similar operators.
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
    - Do not use `Math.pow` for squaring — use direct multiplication (`x * x`).
    - Do not create temporary objects for calculations (e.g., do not create a `Vector` just to compute a distance).
