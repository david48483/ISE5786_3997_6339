# Copilot Instructions for This Repository

Apply these rules in every code change unless the user explicitly asks otherwise.

## Mandatory documentation

- Always write JavaDoc for every class and every member declaration (field or method).
- Do not write JavaDoc for methods that implement interface methods.
- Do not write JavaDoc for methods annotated with `@Override`.
- Write JavaDoc immediately after each class declaration, field declaration, and method declaration (after declaration planning and before implementation).
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
Keep proper indentation and spaces around operators.

Do not use multiple consecutive blank lines.

Do not leave TODO placeholders at this stage.

Do not keep unused fields, methods, or local variables.

Introduce local variables only when they improve DRY or readability.

Do not hardcode configurable or repeated values; extract them to named constants.

Use the IDE auto-formatter to enforce indentation and spacing.

Mandatory TDD workflow for this stage
Stage goals:

Continue developing the project according to Extreme Programming principles, with special emphasis on TDD.

Continue practicing structured unit test design and writing.

Add computational behavior to the geometric model by implementing intersection calculations.

Implement the Composite design pattern and manage collections using List and enhanced for loops.

This stage extends the existing geometric model by adding behavior; do not redesign the model structure unless the stage instructions explicitly require it.

Follow this exact work order for every required task in this stage:

Define the class and method signatures.

Write the JavaDoc.

Write the unit tests.

Implement incrementally while running all tests until they pass.

Refactor.

Run all tests again.

Do not deviate from this order.

Stage-specific project rules (Previous Stages)
The following files are supplied for this stage and must not be modified unless the user explicitly asks to do so:

src/primitives/Double3.java

src/primitives/Util.java

src/test/Main.java

src/geometries/impl/Polygon.java

Do not define retrieval methods (getters) at this stage, except in Ray.

Do not define setter/update methods.

Keep constructors and methods public unless explicitly stated otherwise.

Keep instance fields private by default unless a rule explicitly requires a different access level; constants remain public when required by the project API.

For primitives.Point, keep _xyz as protected.

For primitives.Vector:

Do not add new fields.

Keep constants AXIS_X, AXIS_Y, and AXIS_Z.

Implement vector arithmetic according to existing Double3/direct-formula guidance.

add and scale must use Double3 methods.

dotProduct and crossProduct must compute directly (no intermediate objects).

For primitives.Vector.normalize(), return a new unit vector using _xyz.divide(length()). Do not use trigonometric functions; use linear algebra only.

For primitives.Ray:

Keep fields _origin and _direction.

Keep getters origin() and direction().

The constructor must normalize the direction vector before storing it.

For geometries.api.Geometry, do not define a constructor at this stage.

For geometries.impl.RadialGeometry, keep protected fields _radius and _radiusSquared. Do not add a radius field in subclasses — radius is inherited.

For geometries.impl.Plane constructor (Point p1, Point p2, Point p3), at this stage store one point and set _normal to null.

For geometries.impl.Triangle, do not add fields and constructor should only delegate to Polygon.

For geometries.impl.Sphere, add only the field _center of type Point. Do not redeclare a radius field.

For geometries.impl.Tube, add only the field _axis of type Ray with protected access. Do not redeclare a radius field.

For geometries.impl.Cylinder, add only the field _height of type double.

At this stage only Plane and Polygon return a real normal from getNormal; all other geometries return null.

Stage 4: Camera and Ray Construction Rules
Add a new package renderer and create the Camera class inside it.

Camera class constraints:

Must implement the Cloneable interface (marker interface).

Contains fields for location (Point), direction vectors (vTo, vUp, vRight), View Plane data (width, height, distance), and resolution (nX, nY defaulted to 1). Add helper fields like vpCenter, pixelWidth, and pixelHeight to avoid redundant calculations.

The constructor must be private and take no parameters. Do not create any other constructors.

Implement public static Builder getBuilder() that returns a new Builder instance.

Implement public Ray constructRay(int xIndex, int yIndex).

Builder nested class constraints:

Must be a public static class Builder inside Camera.

Must hold a private final Camera _camera field.

Provide the following builder methods that return Builder (i.e., return this):

setLocation(Point location)

setDirection(Vector to, Vector up)

setDirection(Point target, Vector up)

setDirection(Point target)

setVpSize(double width, double height)

setVpDistance(double distance)

setResolution(int nX, int nY)

No validation or calculation should be performed inside the set... methods; they only update fields.

Provide a build() method with the exact sequence:

checkResolution()

checkLocationAndDirection()

checkViewPlane()

Clone and return _camera (catching CloneNotSupportedException).

Validation methods (check...) must throw IllegalArgumentException for invalid parameters (e.g., non-positive view plane sizes or parallel direction vectors) and MissingResourceException for missing required fields.

(Optional/Bonus) Implement rotate(double angle) to rotate the camera around its viewing direction.

Testing:

Do not modify the provided CameraTests module.

Create CameraIntersectionIntegration for integration testing in unittests.renderer.

Write integration tests for Sphere, Plane, and Triangle.

Implement a private helper method: assertIntersectionsCount(Camera camera, Intersectable body, int expected, String testName) to iterate through all pixels of a 3x3 resolution view plane, construct rays, compute intersections, and assert the total count.