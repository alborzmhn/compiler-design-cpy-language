# Compiler Design Project - Phase 2

This phase focuses on **Name Analysis** and multiple **Code Optimizations** over the AST (Abstract Syntax Tree) generated from the CPY language. The work extends the previous phase and ensures programs are semantically correct while applying dead code elimination techniques to produce more efficient output code.

---

## Name Analysis

### Purpose:

- Detect usage of **undeclared variables** and **functions**.
- Ensure scope correctness and generate appropriate error messages.

### Implementations:

- `NameAnalyzerVisitor.java`: Traverses the AST and builds the symbol table.
- Symbol tables are created for functions and variables.
- Errors such as `Variable not declared` or `Function not declared` are printed with line numbers.

---

## Code Optimizations

These optimizations aim to reduce unused code, improve readability, and minimize execution paths without affecting functionality.

### 1. **Remove Unused Declarations**

- Implemented in `RemoveUnusedDeclarationsVisitor.java`
- Identifies variables or parameters that are declared but never used.
- Modifies function signatures and usages accordingly.

### 2. **Dead Code After Return**

- Implemented in `CleanDeadCode.java`
- Statements after `return` in any block are considered dead and eliminated.

### 3. **No-Effect Statements**

- Implemented in `CleanNoEffectStmts.java`
- Removes expressions/statements that do not alter state or output.

### 4. **Multiple Assignments Optimization**

- Removes previous assignments to a variable if the value is overwritten without being used in-between.

### 5. **Typedef and Constant Replacement** *(for CS students)*

- Replaces `typedef` types and constants with their actual values for simpler static analysis.

---

## Reachability from Main

### Purpose:

- Implemented in `FunctionPruner.java`
- Only functions reachable (directly or indirectly) from `main` are preserved.
- Unused or unreachable functions are pruned from the AST.

---

## Supporting Classes

- `FunctionsReportVisitor.java`: Summarizes and prints metadata about function usage.
- `TypeDefPair.java`: Stores typedef information and mappings.

---

## How to Use

1. Parse the source CPY file using your ANTLR-based parser.
2. Generate the AST.
3. Use the provided visitors in order:
   - `NameAnalyzerVisitor`
   - Optimization visitors (e.g., `CleanDeadCode`, `RemoveUnusedDeclarationsVisitor`)
   - `FunctionPruner`
4. Export or use the final optimized AST for further compilation steps.

---

## Notes

- All sample test files are provided in CPY (C-like Python) language.
- Output should contain only printed errors and cleaned-up code based on optimizations.
