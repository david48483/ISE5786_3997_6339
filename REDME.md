# Project README - Stage 1: Geometric Primitives and Bodies

## 1. Project Overview & Package Structure
This stage builds the mathematical and geometric infrastructure for the rendering engine. Based on the Dependency Inversion Principle (DIP), the project is strictly divided into the following packages: `primitives`, `geometries.api`, `geometries.impl`, and `test`.

## 2. Core Architectural Principles
* **Immutability:** All primitive and geometric objects must be completely immutable. All fields must be declared `final`, no setter methods are allowed at all, and every operation must return a **new** object instead of modifying the existing one.
* **DRY (Don't Repeat Yourself):** Avoid code duplication entirely. Reuse existing methods (e.g., utilize the methods inside `Double3`), and do not copy mathematical formulas between classes.
* **Floating-Point Comparisons:** Never use standard equality or relational operators (like `==`, `<`, `>`) to compare `double` values. To handle floating-point rounding errors, you must use the provided `Util` class methods, such as `Util.isZero(...)` and `Util.alignZero(...)`.

## 3. IDE Configuration & Environment Setup
* **No Java Modules:** The project does not use Java modules. Ensure no `module-info.java` file is created; if your IDE generates one by mistake, delete it and rebuild the project.
* **File Encoding:** Set the global and project text file encoding to **UTF-8**.
* **Auto-Formatting & Imports:** Configure the IDE to automatically format code and organize imports on save. Wildcard imports (e.g., `import java.util.*`) are strictly prohibited; set the IDE threshold to 99 to prevent their creation.
* **Blank Lines:** Configure the IDE to allow exactly **1 blank line** around methods and classes, and 0 blank lines before a method body.

## 4. Coding Conventions & Javadoc
* **Naming Rules:** Use `PascalCase` for classes, `camelCase` for methods, variables, and parameters, `_fieldName` for instance fields, `s_fieldName` for static fields, and `UPPER_CASE` for constants. Use standard Java style for curly braces.
* **Javadoc Documentation:** You must write Javadoc comments **in English only** for every class, field, and method immediately after their declaration. Do not write Javadoc for overridden methods (`@Override`) or interface implementations. The Javadoc should be generated with `private` visibility into a `doc` folder, and you must add `/doc/` to the Git `.gitignore` file.

## 5. Classes to Implement

### The `primitives` Package
* **`Point`:** Represents a 3D point with a `Double3 _xyz` field and a `ZERO` constant. Implement methods: `subtract`, `add`, `distanceSquared`, and `distance` (which must reuse `distanceSquared` to maintain DRY).
* **`Vector`:** Inherits from `Point`. **Creating a zero vector is forbidden.** Its constructors must throw an `IllegalArgumentException` if a zero vector is provided. Implement: `add`, `scale`, `dotProduct`, `crossProduct`, `lengthSquared`, `length`, and `normalize` using only linear algebra.
* **`Ray`:** Represents a half-line using `Point origin` and `Vector direction`. The constructor must **normalize the direction vector** before saving it.

### The `geometries` Packages
* **`Geometry` (api):** An interface or abstract class containing the method `Vector getNormal(Point point)`.
* **`RadialGeometry` (impl):** An abstract base class for bodies with a radius. Contains `radius` and `radiusSquared` fields.
* **Concrete Bodies (impl):** 
    * `Plane`: Contains a `Point` and a `Vector normal`. Its constructor taking a point and a vector must normalize the vector.
    * `Triangle`: Inherits from `Polygon` (no extra fields needed).
    * `Sphere`: Contains `Point center` and `double radius`.
    * `Tube`: Contains `Ray axis` and `double radius`.
    * `Cylinder`: Inherits from `Tube` and adds a `double height` field.
* **`getNormal` Implementations:** For Stage 1, `Plane` must return its normal, `Polygon`/`Triangle` are already fully implemented, and `Sphere`, `Tube`, and `Cylinder` should simply return `null` for now.

## 6. Testing & Submission Requirements
* **Unit Testing:** Run the provided `Main` program. Ensure it does not print any lines starting with "ERROR" and does not throw any exceptions.
* **Pair Programming:** You must work in pairs using Git and GitHub, switching roles (Driver/Navigator) regularly.
* **Repository Format:** Your GitHub repository must be named `ISE5786_XXXX_YYYY` (where XXXX and YYYY are the last 4 digits of each student's ID). It must be private, and the teacher must be added as a collaborator.
* **Submission:** On your final commit, create a tag named `PR01`, push the tag to GitHub, and submit the tag's URL.
