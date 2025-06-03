package main.visitor;

import main.ast.nodes.*;
import main.ast.nodes.Stmt.*;
import main.ast.nodes.Stmt.iteration.*;
import main.ast.nodes.Stmt.jump.*;
import main.ast.nodes.Stmt.selection.ElseStmt;
import main.ast.nodes.Stmt.selection.*;
import main.ast.nodes.declaration.*;
import main.ast.nodes.directDeclarator.Declarator;
import main.ast.nodes.expr.*;
import main.ast.nodes.initializer.Designator;
import main.ast.nodes.initializer.ExpressionInitializer;
import main.ast.nodes.initializer.InitializerList;
import main.ast.nodes.type.*;
import main.ast.nodes.directDeclarator.*;
import main.ast.nodes.directAbstractDeclarator.*;


import main.ast.nodes.expr.primitives.*;

public interface IVisitor<T> {

    T visit(Program program);
    T visit(TranslationUnit translationUnit);
    T visit(ExternalDeclaration externalDeclaration);
    T visit(Declaration declaration);
    T visit(DeclarationSpecifier declarationSpecifier);
    T visit(ForDeclaration forDeclaration);
    T visit(InitDeclarator initDeclarator);
    T visit(ParameterDeclaration parameterDeclaration);
    T visit(FunctionDefinition functionDefinition);
    T visit(IdentifierList identifierList);
    T visit(Pointer pointer);

    T visit(AbstractDeclarator abstractDeclarator);
    T visit(SpecifierQualifierList specifierQualifierList);
    T visit(TypeName typeName);
    T visit(DADArray dadArray);
    T visit(DADFunction dadFunction);
    T visit(DADNestedArray dadNestedArray);
    T visit(DADNestedFunction dadNestedFunction);

    T visit(Declarator declarator);
    T visit(DirectDeclaratorArray directDeclaratorArray);
    T visit(DirectDeclaratorFunction directDeclaratorFunction);
    T visit(DirectDeclaratorID directDeclaratorID);
    T visit(DirectDeclaratorInner directDeclaratorInner);

    T visit(ArrayAccess arrayAccess);
    T visit(ArgumentListExpr argumentListExpr);
    T visit(Assign assign);
    T visit(UnaryExpr unaryExpr);
    T visit(BinaryExpr binaryExpr);
    T visit(ConditionalExpr conditionalExpr);
    T visit(FuncCallExpr func_call_expr);
    T visit(CastExpr castExpr);
    T visit(CommaExpr commaExpr);
    T visit(ForExpression forExpression);
    T visit(Identifier identifier);
    T visit(InitializerListExpr initializerListExpr);
    T visit(UnaryCastExpression unaryCastExpression);
    T visit(SizeofTypeNameExpression sizeofTypeNameExpression);
    T visit(NestedUnaryExpression nestedUnaryExpression);

    T visit(ConstantNode int_Val);
    T visit(StringVal string_val);
    T visit(BoolVal int_Val);
    T visit(DoubleVal double_val);

    T visit(ExpressionInitializer expressionInitializer);
    T visit(InitializerList initializerList);
    T visit(Designator designator);

    T visit(ExpressionStmt expressionStmt);
    T visit(CompoundStmt compoundStmt);
    T visit(BlockItem blockItem);
    T visit(IfStmt ifStmt);
    T visit(ElseStmt elseStmt);
    T visit(ElseIfStmt elseIfStmt);
    T visit(ForStmt forStmt);
    T visit(WhileStmt whileStmt);
    T visit(DoWhileStmt doWhileStmt);
    T visit(ForCondition forCondition);
    T visit(ContinueStmt continueStmt);
    T visit(BreakStmt breakStmt);
    T visit(ReturnStmt returnStmt);

    T visit(ConstNode constNode);
    T visit(TypeDef typeDef);
    T visit(TypeSpecifier typeSpecifier);
}
