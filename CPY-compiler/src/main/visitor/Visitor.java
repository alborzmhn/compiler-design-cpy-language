package main.visitor;

import main.ast.nodes.*;
import main.ast.nodes.Stmt.*;
import main.ast.nodes.Stmt.iteration.*;
import main.ast.nodes.Stmt.jump.*;
import main.ast.nodes.Stmt.selection.*;
import main.ast.nodes.Stmt.selection.IfStmt;
import main.ast.nodes.declaration.*;
import main.ast.nodes.directAbstractDeclarator.*;
import main.ast.nodes.directDeclarator.*;
import main.ast.nodes.expr.*;
import main.ast.nodes.expr.primitives.*;
import main.ast.nodes.initializer.*;
import main.ast.nodes.type.*;

public abstract class Visitor<T> implements IVisitor<T> {
    @Override public T visit(Program program) { return null; }
    @Override public T visit(TranslationUnit translationUnit) { return null; }
    @Override public T visit(ExternalDeclaration externalDeclaration) { return null; }

    @Override public T visit(Declaration declaration) { return null; }
    @Override public T visit(DeclarationSpecifier declarationSpecifier) { return null; }
    @Override public T visit(ForDeclaration forDeclaration) { return null; }
    @Override public T visit(InitDeclarator initDeclarator) { return null; }
    @Override public T visit(ParameterDeclaration parameterDeclaration) { return null; }
    @Override public T visit(FunctionDefinition functionDefinition) { return null; }
    @Override public T visit(IdentifierList identifierList) { return null; }
    @Override public T visit(Pointer pointer) { return null; }

    @Override public T visit(AbstractDeclarator abstractDeclarator) { return null; }
    @Override public T visit(SpecifierQualifierList specifierQualifierList) { return null; }
    @Override public T visit(TypeName typeName) { return null; }
    @Override public T visit(DADArray dadArray) { return null; }
    @Override public T visit(DADFunction dadFunction) { return null; }
    @Override public T visit(DADNestedArray dadNestedArray) { return null; }
    @Override public T visit(DADNestedFunction dadNestedFunction) { return null; }

    @Override public T visit(Declarator declarator) { return null; }
    @Override public T visit(DirectDeclaratorArray directDeclaratorArray) { return null; }
    @Override public T visit(DirectDeclaratorFunction directDeclaratorFunction) { return null; }
    @Override public T visit(DirectDeclaratorID directDeclaratorID) { return null; }
    @Override public T visit(DirectDeclaratorInner directDeclaratorInner) { return null; }

    @Override public T visit(ArrayAccess arrayAccess) { return null; }
    @Override public T visit(ArgumentListExpr argumentListExpr) { return null; }
    @Override public T visit(Assign assign) { return null; }
    @Override public T visit(UnaryExpr unaryExpr) { return null; }
    @Override public T visit(BinaryExpr binaryExpr) { return null; }
    @Override public T visit(ConditionalExpr conditionalExpr) { return null; }
    @Override public T visit(FuncCallExpr func_call_expr) { return null; }
    @Override public T visit(CastExpr castExpr) { return null; }
    @Override public T visit(CommaExpr commaExpr) { return null; }
    @Override public T visit(ForExpression forExpression) { return null; }
    @Override public T visit(Identifier identifier) { return null; }
    @Override public T visit(InitializerListExpr initializerListExpr) { return null; }
    @Override public T visit(UnaryCastExpression unaryCastExpression) {return null; }
    @Override public T visit(SizeofTypeNameExpression sizeofTypeNameExpression) {return null;}
    @Override public T visit(NestedUnaryExpression nestedUnaryExpression) { return null;}

    @Override public T visit(ConstantNode int_Val) { return null; }
    @Override public T visit(StringVal string_val) { return null; }
    @Override public T visit(BoolVal bool_val) { return null; }
    @Override public T visit(DoubleVal double_val) { return null; }

    @Override public T visit(ExpressionInitializer expressionInitializer) { return null; }
    @Override public T visit(InitializerList initializerList) { return null; }
    @Override public T visit(Designator designator) { return null; }

    @Override public T visit(ExpressionStmt expressionStmt) { return null; }
    @Override public T visit(CompoundStmt compoundStmt) { return null; }
    @Override public T visit(BlockItem blockItem) { return null; }
    @Override public T visit(IfStmt ifStmt) {return null;}
    @Override public T visit(ElseStmt elseStmt) {return null;}
    @Override public T visit(ElseIfStmt elseStmt) {return null;}
    @Override public T visit(ForStmt forStmt) { return null; }
    @Override public T visit(WhileStmt whileStmt) { return null; }
    @Override public T visit(DoWhileStmt doWhileStmt) { return null; }
    @Override public T visit(ForCondition forCondition) { return null; }
    @Override public T visit(ContinueStmt continueStmt) { return null; }
    @Override public T visit(BreakStmt breakStmt) { return null; }
    @Override public T visit(ReturnStmt returnStmt) { return null; }

    @Override public T visit(ConstNode constNode) { return null; }
    @Override public T visit(TypeDef typeDef) { return null; }
    @Override public T visit(TypeSpecifier typeSpecifier) { return null; }
}
