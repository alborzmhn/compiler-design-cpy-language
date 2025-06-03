# Compiler Design Project – Phase 1 & Phase 2

This repository includes **Phase 1** and **Phase 2** of a compiler design course project. It showcases the development of a compiler frontend from grammar parsing to semantic analysis and optimization using ANTLR and the Visitor pattern in Java.

---

## Phase 1 – AST Construction and CPY Syntax Transformation

This phase focuses on building the Abstract Syntax Tree (AST) from a C-like syntax and transforming it into CPY (a Python-like syntax). It also involves semantic evaluations like scope analysis and expression depth calculation.

### Objectives
- Construct an AST from SimpleLang grammar.
- Define node types using inheritance for expressions and statements.
- Traverse AST to:
  - Count statements in different scopes.
  - Calculate depth and root of expressions.
- Convert C-style code to a Python-like syntax (CPY).
- Generate output in a format suitable for automatic grading.

### Components

#### 1. Grammar & Parser
- **SimpleLang.g4**: Defines syntax via ANTLR grammar.
- ANTLR parser output is used to build the AST.

#### 2. AST & Visitor Classes
- `IVisitor.java`, `Visitor.java`, `ASTBuilderVisitor.java`, `TestVisitor.java`: For constructing and traversing AST.

#### 3. Evaluation Tasks
- **Scope & Statement Counting**:
  - Functions, If/Else, For/While loops.
- **Expression Depth Calculation**:
  - Binary/Unary expressions with depth and root extraction.

#### 4. Syntax Transformation (CPY)
- Removes semicolons.
- Replaces braces with indentation.
- Starts blocks with `:` and ends with `end`.

#### 5. Output Format
Example output:
```
Function -> 3  
If -> 2  
Expression -> * with depth 2  
```

---

## Phase 2 – Name Analysis and Code Optimizations

This phase extends the AST with **semantic validation** and multiple **optimization passes**.

### Name Analysis

#### Purpose:
- Detect undeclared variables/functions.
- Validate scope usage.

#### Key Implementation:
- `NameAnalyzerVisitor.java` builds symbol tables and reports scope errors.

### Code Optimizations

1. **Remove Unused Declarations**  
   - `RemoveUnusedDeclarationsVisitor.java`

2. **Dead Code After Return**  
   - `CleanDeadCode.java`

3. **No-Effect Statements**  
   - `CleanNoEffectStmts.java`

4. **Multiple Assignments Optimization**  
   - Removes overridden assignments.

5. **Typedef and Constant Replacement** *(for CS students)*  
   - Inlines constants and typedefs.

### Function Reachability from Main
- Implemented in `FunctionPruner.java`
- Unused functions are removed if not reachable from `main`.

### Supporting Utilities
- `FunctionsReportVisitor.java`: Metadata on function usage.
- `TypeDefPair.java`: Typedef and constant mappings.

---

## How to Use

1. Open the project in your Java IDE (e.g., IntelliJ, Eclipse).
2. Go to the `sample1` directory.
3. Write your CPY source code into the provided sample input file.
4. Create or update your run configuration to point to the `sample1` input.
5. Run the main class or testing framework:
   - This will:
     - Parse and generate the AST.
     - Perform name analysis.
     - Apply optimizations.
     - Print the required output (errors, statement/expression analysis, or optimized CPY code).

You can create multiple test samples like `sample2`, `sample3`, etc., by duplicating the `sample1` setup and adjusting input files accordingly.
