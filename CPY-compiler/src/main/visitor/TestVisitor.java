package main.visitor;

import main.ast.nodes.Program;
import main.ast.nodes.Stmt.iteration.ForStmt;
import main.ast.nodes.Stmt.selection.*;
import main.ast.nodes.TranslationUnit;
import main.ast.nodes.Stmt.*;
import main.ast.nodes.declaration.*;
import main.ast.nodes.expr.*;
import main.ast.nodes.type.*;
import main.ast.nodes.expr.primitives.ConstantNode;
import main.ast.nodes.expr.primitives.StringVal;
import main.ast.nodes.*;
import main.ast.nodes.directDeclarator.*;
import main.ast.nodes.directAbstractDeclarator.*;
import main.ast.nodes.Stmt.iteration.*;
import main.ast.nodes.Stmt.jump.*;
import main.ast.nodes.initializer.Designator;
import main.ast.nodes.initializer.ExpressionInitializer;
import main.ast.nodes.initializer.InitializerList;

/*GOALs:
 *   1. print out scope changes each time a new scope starts
 *   2. print the identifier if it is initialized
 *   3. print the identifier if it is used
 *   4. print out the name of the function when it is defined
 *   5. print out the name of the function when it is used
 * */


import java.util.HashSet;
import java.util.Set;


public class TestVisitor extends Visitor<Void>{

    private Set<Integer> printedLines = new HashSet<>();

    @Override
    public Void visit(Program program) {
        if (program.getTranslationUnit() != null)
            program.getTranslationUnit().accept(this);
        return null;
    }

    @Override
    public Void visit(TranslationUnit translationUnit) {
        if(translationUnit.getExternalDecs() != null) {
            for (ExternalDeclaration ext : translationUnit.getExternalDecs()) {
                if (ext != null) {
                    ext.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(ExternalDeclaration ext) {
        if(ext.getContent() != null) {
            if (ext.getContent() instanceof FunctionDefinition) {
                ((FunctionDefinition) ext.getContent()).accept(this);
            }
            else if (ext.getContent() instanceof Declaration) {
                ((Declaration) ext.getContent()).accept(this);
            }
        }
        return null;
    }

    private String extractFunctionName(DirectDeclarator dd) {
        if (dd instanceof DirectDeclaratorID)
            return ((DirectDeclaratorID)dd).getIdentifier().getName();
        else if (dd instanceof DirectDeclaratorFunction)
            return extractFunctionName(((DirectDeclaratorFunction)dd).getBase());
        else if (dd instanceof DirectDeclaratorArray)
            return extractFunctionName(((DirectDeclaratorArray)dd).getBase());
        else if (dd instanceof DirectDeclaratorInner)
            return extractFunctionName(((DirectDeclaratorInner)dd).getDeclarator().getDirectDeclarator());

        return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
        if(functionDefinition.getDeclarationSpecifiers() != null) {
            for (DeclarationSpecifier declarationSpecifier : functionDefinition.getDeclarationSpecifiers()) {
                if (declarationSpecifier != null) {
                    declarationSpecifier.accept(this);
                }
            }
        }
        if(functionDefinition.getDeclarator() != null){
            functionDefinition.getDeclarator().accept(this);
        }
        if(functionDefinition.getDeclarationList() != null) {
            for (Declaration declaration : functionDefinition.getDeclarationList()) {
                if (declaration != null) {
                    declaration.accept(this);
                }
            }
        }
        if (functionDefinition.getCompoundStmt() != null) {
            int count = functionDefinition.getOneIndentCompoundStmt().countStatements();
            if (printedLines.add(functionDefinition.getLine()))
                System.out.println("Line " + functionDefinition.getLine() + ": Stmt function " + extractFunctionName(functionDefinition.getDeclarator().getDirectDeclarator()) + " = " + count);
            functionDefinition.getCompoundStmt().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DeclarationSpecifier declarationSpecifier) {
        if(declarationSpecifier.getSpecifier() != null){
            declarationSpecifier.getSpecifier().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ExpressionStmt expressionStmt) {
        if(expressionStmt.getExpression() != null){
            expressionStmt.getExpression().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(CompoundStmt compoundStmt) {
        if(compoundStmt.getBlocks() != null) {
            for (BlockItem item : compoundStmt.getBlocks()) {
                if (item != null) {
                    item.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(BlockItem item) {
        if (item.isStatement()) {
            if(item.getStatement() != null) {
                item.getStatement().accept(this);
            }
        }
        else if (item.isDeclaration()) {
            if(item.getDeclaration() != null) {
                item.getDeclaration().accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(Declaration declaration) {
        if(declaration.getSpecifiers() != null) {
            for (DeclarationSpecifier declarationSpecifier : declaration.getSpecifiers()) {
                if (declarationSpecifier != null) {
                    declarationSpecifier.accept(this);
                }
            }
        }
        if(declaration.getInitDeclarators() != null) {
            for (InitDeclarator initDeclarator : declaration.getInitDeclarators()) {
                if (initDeclarator != null) {
                    initDeclarator.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(ParameterDeclaration parameterDeclaration) {
        if(parameterDeclaration.getSpecifiers() != null) {
            for (DeclarationSpecifier declarationSpecifier : parameterDeclaration.getSpecifiers()) {
                if (declarationSpecifier != null) {
                    declarationSpecifier.accept(this);
                }
            }
        }
        if(parameterDeclaration.getDeclarator() != null){
            parameterDeclaration.getDeclarator().accept(this);
        }
        if(parameterDeclaration.getAbstractDeclarator() != null){
            parameterDeclaration.getAbstractDeclarator().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(AbstractDeclarator abstractDeclarator) {
        if(abstractDeclarator.getDirectDeclarator() != null){
            abstractDeclarator.getDirectDeclarator().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DADArray dadArray) {
        if(dadArray.getSize() != null){
            dadArray.getSize().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DADNestedFunction dadNestedFunction) {
        if(dadNestedFunction.getInner() != null){
            dadNestedFunction.getInner().accept(this);
        }
        if(dadNestedFunction.getParameters() != null) {
            for (ParameterDeclaration parameterDeclaration : dadNestedFunction.getParameters()) {
                if (parameterDeclaration != null) {
                    parameterDeclaration.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(DADFunction dadFunction) {
        if(dadFunction.getAbstractDeclarator() != null){
            dadFunction.getAbstractDeclarator().accept(this);
        }
        if(dadFunction.getParameters() != null){
            for (ParameterDeclaration parameterDeclaration: dadFunction.getParameters()){
                if(parameterDeclaration != null){
                    parameterDeclaration.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(DADNestedArray dadNestedArray) {
        if(dadNestedArray.getInner() != null){
            dadNestedArray.getInner().accept(this);
        }
        if(dadNestedArray.getSize() != null){
            dadNestedArray.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ExpressionInitializer exp) {
        if(exp.getExpression() != null){
            exp.getExpression().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(InitializerList init) {
        if(init.getEntries() != null) {
            for (InitializerList.InitEntry initEntry : init.getEntries()) {
                if(initEntry.getDesignation() != null) {
                    for (Designator designator : initEntry.getDesignation()) {
                        if (designator != null) {
                            designator.accept(this);
                        }
                    }
                }
                if (initEntry.getInitializer() != null) {
                    initEntry.getInitializer().accept(this);
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(Designator designator) {
        if(designator.getExpr() != null){
            designator.getExpr().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(InitDeclarator initDeclarator) {
        if (initDeclarator.getDeclarator() != null)
            initDeclarator.getDeclarator().accept(this);
        if(initDeclarator.getInitializer() != null)
            initDeclarator.getInitializer().accept(this);

        return null;
    }

    @Override
    public Void visit(Declarator declarator) {
        if (declarator.getDirectDeclarator() != null)
            declarator.getDirectDeclarator().accept(this);

        return null;
    }

    @Override
    public Void visit(DirectDeclaratorID directDeclaratorID) {
        return null;
    }

    @Override
    public Void visit(DirectDeclaratorFunction directDeclaratorFunction) {
        if(directDeclaratorFunction.getBase() != null) {
            directDeclaratorFunction.getBase().accept(this);
        }
        if(directDeclaratorFunction.getParameters() != null) {
            for (ParameterDeclaration parameterDeclaration : directDeclaratorFunction.getParameters()) {
                if (parameterDeclaration != null) {
                    parameterDeclaration.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(DirectDeclaratorInner directDeclaratorInner) {
        if(directDeclaratorInner.getDeclarator() != null) {
            directDeclaratorInner.getDeclarator().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DirectDeclaratorArray directDeclaratorArray) {
        if(directDeclaratorArray.getBase() != null) {
            directDeclaratorArray.getBase().accept(this);
        }
        if(directDeclaratorArray.getSizeExpr() != null){
            directDeclaratorArray.getSizeExpr().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(IfStmt ifStmt) {
        if (ifStmt.getBody() != null){
            System.out.println("Line " + ifStmt.getLine() + ": Stmt selection = " + (ifStmt.getBody().countStatements()));
            ifStmt.getBody().accept(this);
        }
        if(ifStmt.getCondition() != null){
            ifStmt.getCondition().accept(this);
        }

        if(ifStmt.getElseIfStmts() != null){
            for(ElseIfStmt elseIfStmt : ifStmt.getElseIfStmts()){
                if(elseIfStmt != null){
                    elseIfStmt.accept(this);
                }
            }
        }

        if(ifStmt.getElseStmt() != null){
            ifStmt.getElseStmt().accept(this);
        }

        return null;
    }

    @Override
    public Void visit(ElseIfStmt elseIfStmt) {
        if (elseIfStmt.getBody() != null){
            System.out.println("Line " + elseIfStmt.getLine() + ": Stmt selection = " + (elseIfStmt.getBody().countStatements())); ;
            elseIfStmt.getBody().accept(this);
        }
        if(elseIfStmt.getCondition() != null){
            elseIfStmt.getCondition().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ElseStmt elseStmt) {
        if (elseStmt.getBody() != null) {
            System.out.println("Line " + elseStmt.getLine() + ": Stmt selection = " + (elseStmt.getBody().countStatements()));
            elseStmt.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt) {
        if (forStmt.getBody() != null) {
            System.out.println("Line " + forStmt.getLine() + ": Stmt for = " + (forStmt.getBody().countStatements()));
            forStmt.getBody().accept(this);
        }

        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt) {
        if (whileStmt.getBody() != null)
            System.out.println("Line " + whileStmt.getLine() + ": Stmt while = " + (whileStmt.getBody().countStatements()));
            whileStmt.getBody().accept(this);
        if(whileStmt.getCondition() != null){
            whileStmt.getCondition().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DoWhileStmt doWhileStmt) {
        if (doWhileStmt.getBody() != null){
            doWhileStmt.getBody().accept(this);
            System.out.println("Line " + doWhileStmt.getLine() + ": Stmt doWhile = " + (doWhileStmt.getBody().countStatements()));
        }
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt) {
        if (returnStmt.getReturnValue() != null)
            returnStmt.getReturnValue().accept(this);
        return null;
    }

    @Override
    public Void visit(BreakStmt breakStmt) {
        return null;
    }

    @Override
    public Void visit(ContinueStmt continueStmt) {
        return null;
    }

    @Override
    public Void visit(ArrayAccess arrayAccess) {
        if(arrayAccess.getArray() != null) {
            arrayAccess.getArray().accept(this);
        }
        if(arrayAccess.getIndex()!= null){
            arrayAccess.getIndex().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        if (printedLines.add(assign.getLine()))
            System.out.println("Line " + assign.getLine() + ": Expr " + assign.getAssignmentOperator().getSymbol());
        return null;
    }

    @Override
    public Void visit(BinaryExpr binaryExpr) {
        if (printedLines.add(binaryExpr.getLine()))
            System.out.println("Line " + binaryExpr.getLine() + ": Expr " + binaryExpr.getOperator().getSymbol());
        return null;
    }


    @Override
    public Void visit(CommaExpr commaExpr) {
        if (printedLines.add(commaExpr.getLine()))
            System.out.println("Line " + commaExpr.getLine() + ": Expr ,");
        return null;
    }

    @Override
    public Void visit(ConditionalExpr conditionalExpr) {
        conditionalExpr.getCondition().accept(this);
        return null;
    }

    @Override
    public Void visit(FuncCallExpr funcCallExpr) {
        funcCallExpr.getFunction().accept(this);
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        if (printedLines.add(identifier.getLine()))
            System.out.println("Line " + identifier.getLine() + ": Expr " + identifier.getName());
        return null;
    }

    @Override
    public Void visit(ConstantNode int_Val) {
        if(int_Val.getStringVal() != null){
            if (printedLines.add(int_Val.getLine()))
                System.out.println("Line " + int_Val.getLine() + ": Expr " + int_Val.getStringVal());
        }
        return null;
    }

    @Override
    public Void visit(StringVal string_val) {
        if(string_val.get_string_val() != null) {
            if (printedLines.add(string_val.getLine()))
                System.out.println("Line " + string_val.getLine() + ": Expr " + string_val.get_string_val());
        }
        return null;
    }


    @Override
    public Void visit(InitializerListExpr initializerListExpr) {
        if(initializerListExpr.getType() != null) {
            initializerListExpr.getType().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(CastExpr castExpr) {
        if(castExpr.getCastType() != null) {
            castExpr.getCastType().accept(this);
        }
        if(castExpr.getExpr()!= null){
            castExpr.getExpr().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(NestedUnaryExpression nestedUnaryExpression) {
        if (!nestedUnaryExpression.getOperators().isEmpty()) {
            if (printedLines.add(nestedUnaryExpression.getLine())) {
                System.out.println("Line " + nestedUnaryExpression.getLine() + ": Expr " + nestedUnaryExpression.getOperators().get(0).getSymbol());
            }
        }
        else {
            nestedUnaryExpression.getOperand().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(SizeofTypeNameExpression sizeofTypeNameExpression) {
        if(sizeofTypeNameExpression.getTypeName() != null){
            sizeofTypeNameExpression.getTypeName().accept(this);
        }
        return null;
    }


    @Override
    public Void visit(TypeName typeName) {
        if(typeName.getSpecifiers() != null){
            typeName.getSpecifiers().accept(this);
        }
        if(typeName.getAbstractDeclarator() != null){
            typeName.getAbstractDeclarator().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(SpecifierQualifierList specifierQualifierList) {
        if(specifierQualifierList.getSpecifiers() != null){
            for (Type type: specifierQualifierList.getSpecifiers()){
                if(type != null){
                    type.accept(this);
                }
            }
        }
        return null;
    }



//    @Override
//    public Void visit(ConstNode constNode) {
//        if (printedLines.add(constNode.getLine()))
//            System.out.println("Line " + constNode.getLine() + ":" + " Expr Const");
//        return null;
//    }
//
//    @Override
//    public Void visit(TypeSpecifier typeSpecifier) {
//        if (printedLines.add(typeSpecifier.getLine()))
//            System.out.println("Line " + typeSpecifier.getLine() + ":" + " Expr " + typeSpecifier.getTypeName());
//        return null;
//    }
//
//    @Override
//    public Void visit(TypeDef typeDef) {
//        if (printedLines.add(typeDef.getLine()))
//            System.out.println("Line " + typeDef.getLine() + ":" + " Expr Typedef");
//        return null;
//    }

    @Override
    public Void visit(UnaryCastExpression unaryCastExpression) {
        if (printedLines.add(unaryCastExpression.getLine()))
            System.out.println("Line " + unaryCastExpression.getLine() + ":" + " Expr " + unaryCastExpression.getOperator().getSymbol());
        return null;
    }

    @Override
    public Void visit(UnaryExpr unaryExpr) {
        if (printedLines.add(unaryExpr.getLine()))
            System.out.println("Line " + unaryExpr.getLine() + ":" + " Expr " + unaryExpr.getOperator().getSymbol());
        return null;
    }
}