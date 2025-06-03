package main.visitor;

import main.ast.nodes.FunctionDefinition;
import main.ast.nodes.Program;
import main.ast.nodes.Stmt.BlockItem;
import main.ast.nodes.Stmt.CompoundStmt;
import main.ast.nodes.Stmt.ExpressionStmt;
import main.ast.nodes.Stmt.iteration.*;
import main.ast.nodes.Stmt.selection.ElseIfStmt;
import main.ast.nodes.Stmt.selection.ElseStmt;
import main.ast.nodes.Stmt.selection.IfStmt;
import main.ast.nodes.TranslationUnit;
import main.ast.nodes.declaration.*;
import main.ast.nodes.directAbstractDeclarator.*;
import main.ast.nodes.directDeclarator.*;
import main.ast.nodes.expr.*;
import main.ast.nodes.initializer.Designator;
import main.ast.nodes.initializer.ExpressionInitializer;
import main.ast.nodes.initializer.InitializerList;
import main.visitor.optimizer.CallPair;

import java.util.ArrayList;
import java.util.List;

public class RemoveUnusedDeclarationsVisitor extends Visitor<Void> {

    FunctionDefinition currentFunction;
    public static List<String> print_logs = new ArrayList<>();

    @Override
    public Void visit(Program program) {
        if (program.getTranslationUnit() != null)
            program.getTranslationUnit().accept(this);
        return null;
    }

    @Override
    public Void visit(TranslationUnit translationUnit) {
        if (translationUnit.getExternalDecs() != null) {
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
        if (ext.getContent() != null) {
            if (ext.getContent() instanceof FunctionDefinition) {
                ((FunctionDefinition) ext.getContent()).accept(this);
            } else if (ext.getContent() instanceof Declaration) {
                if(!((Declaration) ext.getContent()).isIs_used()){
                    ext.delete_declaration();
                }
                else {
                    ((Declaration) ext.getContent()).accept(this);
                }
            }
        }
        return null;
    }

    private String extractFunctionName(DirectDeclarator dd) {
        if (dd instanceof DirectDeclaratorID)
            return ((DirectDeclaratorID) dd).getIdentifier().getName();
        else if (dd instanceof DirectDeclaratorFunction)
            return extractFunctionName(((DirectDeclaratorFunction) dd).getBase());
        else if (dd instanceof DirectDeclaratorArray)
            return extractFunctionName(((DirectDeclaratorArray) dd).getBase());
        else if (dd instanceof DirectDeclaratorInner)
            return extractFunctionName(((DirectDeclaratorInner) dd).getDeclarator().getDirectDeclarator());

        return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {

        currentFunction = functionDefinition;

        if (functionDefinition.getDeclarationSpecifiers() != null) {
            for (DeclarationSpecifier declarationSpecifier : functionDefinition.getDeclarationSpecifiers()) {
                if (declarationSpecifier != null) {
                    declarationSpecifier.accept(this);
                }
            }
        }
        if (functionDefinition.getDeclarator() != null) {
            functionDefinition.getDeclarator().accept(this);
        }
        if (functionDefinition.getDeclarationList() != null) {
            for (Declaration declaration : functionDefinition.getDeclarationList()) {
                if (declaration != null) {
                    declaration.accept(this);
                }
            }
        }
        if (functionDefinition.getCompoundStmt() != null) {
            functionDefinition.getCompoundStmt().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DeclarationSpecifier declarationSpecifier) {
        if (declarationSpecifier.getSpecifier() != null) {
            declarationSpecifier.getSpecifier().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ExpressionStmt expressionStmt) {
        if (expressionStmt.getExpression() != null) {
            expressionStmt.getExpression().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(CompoundStmt compoundStmt) {
        if (compoundStmt.getBlocks() != null) {
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
            if (item.getStatement() != null) {
                item.getStatement().accept(this);
            }
        } else if (item.isDeclaration()) {
            if (item.getDeclaration() != null) {
                if(!item.getDeclaration().isIs_used()){
                    item.delete_declaration();
                }
                else {
                    item.getDeclaration().accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(Declaration declaration) {
        if (declaration.getSpecifiers() != null) {
            for (DeclarationSpecifier declarationSpecifier : declaration.getSpecifiers()) {
                if (declarationSpecifier != null) {
                    declarationSpecifier.accept(this);
                }
            }
        }
        if (declaration.getInitDeclarators() != null) {
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
        if (parameterDeclaration.getSpecifiers() != null) {
            for (DeclarationSpecifier declarationSpecifier : parameterDeclaration.getSpecifiers()) {
                if (declarationSpecifier != null) {
                    declarationSpecifier.accept(this);
                }
            }
        }
        if (parameterDeclaration.getDeclarator() != null) {
            parameterDeclaration.getDeclarator().accept(this);
        }
        if (parameterDeclaration.getAbstractDeclarator() != null) {
            parameterDeclaration.getAbstractDeclarator().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(AbstractDeclarator abstractDeclarator) {
        if (abstractDeclarator.getDirectDeclarator() != null) {
            abstractDeclarator.getDirectDeclarator().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DADArray dadArray) {
        if (dadArray.getSize() != null) {
            dadArray.getSize().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DADNestedFunction dadNestedFunction) {
        List<ParameterDeclaration> parameters_to_remove = new ArrayList<>();
        if (dadNestedFunction.getInner() != null) {
            dadNestedFunction.getInner().accept(this);
        }
        if (dadNestedFunction.getParameters() != null) {
            for (ParameterDeclaration parameterDeclaration : dadNestedFunction.getParameters()) {
                if (parameterDeclaration != null) {
                    if(!parameterDeclaration.isIs_used()){
                        parameters_to_remove.add(parameterDeclaration);
                    }
                    else {
                        parameterDeclaration.accept(this);
                    }
                }
            }
        }

        //System.out.println(parameters_to_remove.size());

        for(ParameterDeclaration parameterDeclaration: parameters_to_remove){
            int number = dadNestedFunction.delete_parameter(parameterDeclaration);
            //System.out.println(parameterDeclaration.getLine() + "hi");
            //System.out.println(NameAnalyzerVisitor.callList.size() + "hiii");
            for(CallPair callPair : NameAnalyzerVisitor.callList)
            {
                //System.out.println(callPair.getCallee() + " alborz " + callPair.getCaller());
                //System.out.println((callPair.getCallee() + " alborz " + NameAnalyzerVisitor.extractFunctionName(currentFunction.getDeclarator().getDirectDeclarator()) + "_" + currentFunction.getNumber_of_parameters()));
                if(callPair.getCallee().equals(NameAnalyzerVisitor.extractFunctionName(currentFunction.getDeclarator().getDirectDeclarator()) + "_" + currentFunction.getNumber_of_parameters())){
                    FuncCallExpr func_call = callPair.getFunction_caller();
                    //System.out.println(func_call.getLine() + " and " + number);
                    func_call.getArguments().delete_parameter(number);
                }
            }
            //String parameter_name = "VarDec_" + NameAnalyzerVisitor.extractParameterName(parameterDeclaration);


        }


        return null;
    }

    @Override
    public Void visit(DADFunction dadFunction) {
        List<ParameterDeclaration> parameters_to_remove = new ArrayList<>();
        if (dadFunction.getAbstractDeclarator() != null) {
            dadFunction.getAbstractDeclarator().accept(this);
        }
        if (dadFunction.getParameters() != null) {
            for (ParameterDeclaration parameterDeclaration : dadFunction.getParameters()) {
                if (parameterDeclaration != null) {
                    if(!parameterDeclaration.isIs_used()){
                        parameters_to_remove.add(parameterDeclaration);
                    }
                    else {
                        parameterDeclaration.accept(this);
                    }
                }
            }
        }

        for(ParameterDeclaration parameterDeclaration: parameters_to_remove){
            int number = dadFunction.delete_parameter(parameterDeclaration);
            //System.out.println(parameterDeclaration.getLine() + "hii");
            //System.out.println(NameAnalyzerVisitor.callList.size() + "hiii");
            for(CallPair callPair : NameAnalyzerVisitor.callList)
            {
                //System.out.println(callPair.getCallee() + " alborz " + callPair.getCaller());
                //System.out.println((callPair.getCallee() + " alborz " + NameAnalyzerVisitor.extractFunctionName(currentFunction.getDeclarator().getDirectDeclarator()) + "_" + currentFunction.getNumber_of_parameters()));
                if(callPair.getCallee().equals(NameAnalyzerVisitor.extractFunctionName(currentFunction.getDeclarator().getDirectDeclarator()) + "_" + currentFunction.getNumber_of_parameters())){
                    FuncCallExpr func_call = callPair.getFunction_caller();
                    //System.out.println(func_call.getLine() + " and " + number);
                    func_call.getArguments().delete_parameter(number);
                }
            }
        }

        return null;
    }

    @Override
    public Void visit(DADNestedArray dadNestedArray) {
        if (dadNestedArray.getInner() != null) {
            dadNestedArray.getInner().accept(this);
        }
        if (dadNestedArray.getSize() != null) {
            dadNestedArray.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ExpressionInitializer exp) {
        if (exp.getExpression() != null) {
            exp.getExpression().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(InitializerList init) {
        if (init.getEntries() != null) {
            for (InitializerList.InitEntry initEntry : init.getEntries()) {
                if (initEntry.getDesignation() != null) {
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
        if (designator.getExpr() != null) {
            designator.getExpr().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(InitDeclarator initDeclarator) {
        if (initDeclarator.getDeclarator() != null)
            initDeclarator.getDeclarator().accept(this);
        if (initDeclarator.getInitializer() != null)
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
        List<ParameterDeclaration> parameters_to_remove = new ArrayList<>();
        if (directDeclaratorFunction.getBase() != null) {
            directDeclaratorFunction.getBase().accept(this);
        }
        if (directDeclaratorFunction.getParameters() != null) {
            for (ParameterDeclaration parameterDeclaration : directDeclaratorFunction.getParameters()) {
                if (parameterDeclaration != null) {
                    if(!parameterDeclaration.isIs_used()){
                        parameters_to_remove.add(parameterDeclaration);
                    }
                    else {
                        parameterDeclaration.accept(this);
                    }
                }
            }
        }

        for(ParameterDeclaration parameterDeclaration: parameters_to_remove){
            int number = directDeclaratorFunction.delete_parameter(parameterDeclaration);
            //System.out.println(NameAnalyzerVisitor.callList.size() + "hiii");

            for(CallPair callPair : NameAnalyzerVisitor.callList)
            {
                //System.out.println(callPair.getCallee() + " alborz " + callPair.getCaller());
                //System.out.println((callPair.getCallee() + " niloufar " + NameAnalyzerVisitor.extractFunctionName(currentFunction.getDeclarator().getDirectDeclarator()) + "_" + currentFunction.getNumber_of_parameters()));
                if(callPair.getCallee().equals(NameAnalyzerVisitor.extractFunctionName(currentFunction.getDeclarator().getDirectDeclarator()) + "_" + currentFunction.getNumber_of_parameters())){
                    FuncCallExpr func_call = callPair.getFunction_caller();
                    //System.out.println(func_call.getLine() + " and " + number);
                    func_call.getArguments().delete_parameter(number);
                    //print_logs.add("al:" +  func_call.getArguments().getArguments().size());
                }
            }
        }

        return null;
    }

    @Override
    public Void visit(DirectDeclaratorInner directDeclaratorInner) {
        if (directDeclaratorInner.getDeclarator() != null) {
            directDeclaratorInner.getDeclarator().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DirectDeclaratorArray directDeclaratorArray) {
        if (directDeclaratorArray.getBase() != null) {
            directDeclaratorArray.getBase().accept(this);
        }
        if (directDeclaratorArray.getSizeExpr() != null) {
            directDeclaratorArray.getSizeExpr().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(IfStmt ifStmt) {
        if (ifStmt.getBody() != null) {
            ifStmt.getBody().accept(this);
        }
        if (ifStmt.getCondition() != null) {
            ifStmt.getCondition().accept(this);
        }

        if (ifStmt.getElseIfStmts() != null) {
            for (ElseIfStmt elseIfStmt : ifStmt.getElseIfStmts()) {
                if (elseIfStmt != null) {
                    elseIfStmt.accept(this);
                }
            }
        }

        if (ifStmt.getElseStmt() != null) {
            ifStmt.getElseStmt().accept(this);
        }

        return null;
    }

    @Override
    public Void visit(ElseIfStmt elseIfStmt) {
        if (elseIfStmt.getBody() != null) {
            elseIfStmt.getBody().accept(this);
        }
        if (elseIfStmt.getCondition() != null) {
            elseIfStmt.getCondition().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ElseStmt elseStmt) {
        if (elseStmt.getBody() != null) {
            elseStmt.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt) {

        if(forStmt.getForCondition() != null){

            forStmt.getForCondition().accept(this);
        }

        if(forStmt.getBody() != null){
            forStmt.getBody().accept(this);
        }

        return null;
    }

    @Override
    public Void visit(ForCondition forCondition) {
        if(forCondition.getInit() != null){
            if(forCondition.getInit() instanceof ForDeclaration forDeclaration){
                if(!forDeclaration.isIs_used()){
                    forCondition.delete_for_declaration();
                }
                else {
                    forDeclaration.accept(this);
                }
            }else{
                ((Expr)forCondition.getInit()).accept(this);
            }
        }
        if(forCondition.getCondition() != null){
            forCondition.getCondition().accept(this);
        }
        if(forCondition.getUpdate() != null){
            forCondition.getUpdate().accept(this);
        }

        return null;
    }

    @Override
    public Void visit(ForExpression forExpression) {
        if(forExpression.getExpressions()!= null){
            for(Expr expr:forExpression.getExpressions()){
                if(expr != null){
                    expr.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(ForDeclaration forDeclaration) {
        if(forDeclaration.getSpecifiers() != null) {
            for (DeclarationSpecifier declarationSpecifier : forDeclaration.getSpecifiers()) {
                if (declarationSpecifier != null) {
                    declarationSpecifier.accept(this);
                }
            }
        }
        if(forDeclaration.getInitDeclarators() != null) {
            for (InitDeclarator initDeclarator : forDeclaration.getInitDeclarators()) {
                if (initDeclarator != null) {
                    initDeclarator.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt) {
        if (whileStmt.getBody() != null)
            whileStmt.getBody().accept(this);
        if (whileStmt.getCondition() != null) {
            whileStmt.getCondition().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DoWhileStmt doWhileStmt) {
        if (doWhileStmt.getBody() != null) {
            doWhileStmt.getBody().accept(this);}
        return null;
    }
}
