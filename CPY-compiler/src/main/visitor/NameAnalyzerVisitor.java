package main.visitor;

import main.ast.nodes.FunctionDefinition;
import main.ast.nodes.Program;
import main.ast.nodes.Stmt.BlockItem;
import main.ast.nodes.Stmt.CompoundStmt;
import main.ast.nodes.Stmt.ExpressionStmt;
import main.ast.nodes.Stmt.iteration.*;
import main.ast.nodes.Stmt.jump.BreakStmt;
import main.ast.nodes.Stmt.jump.ContinueStmt;
import main.ast.nodes.Stmt.jump.ReturnStmt;
import main.ast.nodes.Stmt.selection.ElseIfStmt;
import main.ast.nodes.Stmt.selection.ElseStmt;
import main.ast.nodes.Stmt.selection.IfStmt;
import main.ast.nodes.TranslationUnit;
import main.ast.nodes.declaration.*;
import main.ast.nodes.directAbstractDeclarator.*;
import main.ast.nodes.directDeclarator.*;
import main.ast.nodes.expr.*;
import main.ast.nodes.expr.primitives.ConstantNode;
import main.ast.nodes.expr.primitives.StringVal;
import main.ast.nodes.initializer.Designator;
import main.ast.nodes.initializer.ExpressionInitializer;
import main.ast.nodes.initializer.InitializerList;
import main.ast.nodes.type.Type;
import main.visitor.optimizer.CallPair;
import main.symbolTable.SymbolTable;
import main.symbolTable.exceptions.ItemAlreadyExists;
import main.symbolTable.exceptions.ItemNotFound;
import main.symbolTable.items.*;
import main.symbolTable.items.FunctionDefinitionSymbolItem;
import main.ast.nodes.type.*;
import main.visitor.optimizer.TypeDefPair;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class NameAnalyzerVisitor extends Visitor<Void>{

    private Boolean is_declaration_redeclared = false;
    private Boolean is_parameter_redeclared = false;
    private Boolean is_function_redeclared = false;
    private Boolean is_for_declaration_redeclared = false;

    //public static Boolean have_name_analysis_error = false;
    public static List<String> print_logs = new ArrayList<>();

    private Boolean in_function_call = false;
    private int number_of_arguments = -1 ;
    List<String> primitiveIdentifiers = Arrays.asList("printf", "scanf", "print", "scan", "exit");
    private FunctionDefinition currentFunction;
    private FuncCallExpr currentCaller = null;
    public static List<CallPair> callList = new ArrayList<>();

    @Override
    public Void visit(Program program) {
        if (program.getTranslationUnit() != null)
            program.getTranslationUnit().accept(this);
        return null;
    }

    @Override
    public Void visit(TranslationUnit translationUnit) {

        SymbolTable.top = new SymbolTable();
        SymbolTable.root = SymbolTable.top;

        translationUnit.set_symbol_table(SymbolTable.top);

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
            if (ext.getContent() instanceof FunctionDefinition functionDefinition) {
                functionDefinition.accept(this);
                if(is_function_redeclared) {
                    //ext.delete_function();
                    is_function_redeclared = false;
                }
            }
            else if (ext.getContent() instanceof Declaration declaration) {
                declaration.accept(this);
                if(is_declaration_redeclared){
                    //ext.delete_declaration();
                    is_declaration_redeclared = false;
                }
            }
        }
        return null;
    }

    public static String extractFunctionName(DirectDeclarator dd) {
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

    public static String extractDeclarationName(Declaration d){
        if(d.getSpecifiers().size() > 1) {
            if(d.getSpecifiers().get(1).getSpecifier() instanceof TypeSpecifier){
                if(((TypeSpecifier) d.getSpecifiers().get(1).getSpecifier()).getIs_identifier()){
                    //System.out.println(((TypeSpecifier) d.getSpecifiers().get(1).getSpecifier()).getTypeName());
                    return ((TypeSpecifier) d.getSpecifiers().get(1).getSpecifier()).getTypeName();
                }
            }
        }
        //System.out.println(extractFunctionName(d.getInitDeclarators().get(0).getDeclarator().getDirectDeclarator()) + " " +  d.getSpecifiers().size());
        return extractFunctionName(d.getInitDeclarators().get(0).getDeclarator().getDirectDeclarator());
    }

    public static String extractForDeclarationName(ForDeclaration d){
        if(d.getSpecifiers().size() > 1) {
            if(d.getSpecifiers().get(1).getSpecifier() instanceof TypeSpecifier){
                if(((TypeSpecifier) d.getSpecifiers().get(1).getSpecifier()).getIs_identifier()){
                    return ((TypeSpecifier) d.getSpecifiers().get(1).getSpecifier()).getTypeName();
                }
            }
        }
        return extractFunctionName(d.getInitDeclarators().get(0).getDeclarator().getDirectDeclarator());
    }

    public static String extractParameterName(ParameterDeclaration d){
        if(d.getSpecifiers().size() > 1) {
            if(d.getSpecifiers().get(1).getSpecifier() instanceof TypeSpecifier){
                if(((TypeSpecifier) d.getSpecifiers().get(1).getSpecifier()).getIs_identifier()){
                    return ((TypeSpecifier) d.getSpecifiers().get(1).getSpecifier()).getTypeName();
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
        functionDefinition.setIs_used(false);
        currentFunction = functionDefinition;

        FunctionDefinitionSymbolItem func_dec_item = new FunctionDefinitionSymbolItem(functionDefinition);

        try {
            SymbolTable.top.put(func_dec_item);
        } catch (ItemAlreadyExists e) {
            //System.out.println("Redeclaration of function \"" + extractFunctionName(functionDefinition.getDeclarator().getDirectDeclarator()) +"\" in line " + functionDefinition.getLine());
            is_function_redeclared = true;
        }

        SymbolTable func_dec_symbol_table = new SymbolTable(SymbolTable.top);
        functionDefinition.set_symbol_table(func_dec_symbol_table);
        SymbolTable.push(func_dec_symbol_table);

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
            functionDefinition.getCompoundStmt().accept(this);
        }

        //func_dec_symbol_table.delete_unused_declarations();
        SymbolTable.pop();

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
                if(is_declaration_redeclared){
                    //item.delete_declaration();
                    is_declaration_redeclared = false;
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(Declaration declaration) {
        declaration.setIs_used(false);
        DeclarationSymbolItem var_dec_item = new DeclarationSymbolItem(declaration);
        try {
            SymbolTable.top.put(var_dec_item);
        } catch (ItemAlreadyExists e) {
            //System.out.println("Redeclaration of variable \"" + extractDeclarationName(declaration) +"\" in line " + declaration.getLine());
            is_declaration_redeclared = true;
        }


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
        parameterDeclaration.setIs_used(false);
        ParameterDeclarationSymbolItem parameterDeclarationSymbolItem = new ParameterDeclarationSymbolItem(parameterDeclaration);
        try {
            SymbolTable.top.put(parameterDeclarationSymbolItem);
        } catch (ItemAlreadyExists e) {
            //System.out.println("Redeclaration of parameter \"" + extractParameterName(parameterDeclaration) + "\" in line " + parameterDeclaration.getLine());
            is_parameter_redeclared = true;
        }

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
        List<ParameterDeclaration> parameters_to_remove = new ArrayList<>();

        if(dadNestedFunction.getInner() != null){
            dadNestedFunction.getInner().accept(this);
        }
        if(dadNestedFunction.getParameters() != null) {
            for (ParameterDeclaration parameterDeclaration : dadNestedFunction.getParameters()) {
                if (parameterDeclaration != null) {
                    parameterDeclaration.accept(this);
                    if(is_parameter_redeclared){
                        //parameters_to_remove.add(parameterDeclaration);
                        is_parameter_redeclared = false;
                    }
                }
            }
        }
        for(ParameterDeclaration parameterDeclaration: parameters_to_remove){
            //dadNestedFunction.delete_parameter(parameterDeclaration);
        }

        return null;
    }

    @Override
    public Void visit(DADFunction dadFunction) {
        List<ParameterDeclaration> parameters_to_remove = new ArrayList<>();

        if(dadFunction.getAbstractDeclarator() != null){
            dadFunction.getAbstractDeclarator().accept(this);
        }
        if(dadFunction.getParameters() != null){
            for (ParameterDeclaration parameterDeclaration: dadFunction.getParameters()){
                if(parameterDeclaration != null){
                    parameterDeclaration.accept(this);
                    if(is_parameter_redeclared){
                        //parameters_to_remove.add(parameterDeclaration);
                        is_parameter_redeclared = false;
                    }
                }
            }
        }

        for(ParameterDeclaration parameterDeclaration: parameters_to_remove){
            //dadFunction.delete_parameter(parameterDeclaration);
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
        List<ParameterDeclaration> parameters_to_remove = new ArrayList<>();
        if(directDeclaratorFunction.getBase() != null) {
            directDeclaratorFunction.getBase().accept(this);
        }
        if(directDeclaratorFunction.getParameters() != null) {
            for (ParameterDeclaration parameterDeclaration : directDeclaratorFunction.getParameters()) {
                if (parameterDeclaration != null) {
                    parameterDeclaration.accept(this);
                    if(is_parameter_redeclared){
                        //parameters_to_remove.add(parameterDeclaration);
                        is_parameter_redeclared = false;
                    }
                }
            }
        }

        for(ParameterDeclaration parameterDeclaration: parameters_to_remove){
            //directDeclaratorFunction.delete_parameter(parameterDeclaration);
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

        SymbolTable symbolTable = new SymbolTable(SymbolTable.top);
        ifStmt.set_symbol_table(symbolTable);
        SymbolTable.push(symbolTable);

        if(ifStmt.getCondition() != null){
            ifStmt.getCondition().accept(this);
        }

        if(ifStmt.getBody() != null){
            ifStmt.getBody().accept(this);
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

        SymbolTable.pop();

        return null;
    }

    @Override
    public Void visit(ElseIfStmt elseIfStmt) {

        SymbolTable symbolTable = new SymbolTable(SymbolTable.top);
        elseIfStmt.set_symbol_table(symbolTable);
        SymbolTable.push(symbolTable);

        if(elseIfStmt.getCondition() != null){
            elseIfStmt.getCondition().accept(this);
        }

        if(elseIfStmt.getBody() != null){
            elseIfStmt.getBody().accept(this);
        }

        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(ElseStmt elseStmt) {
        SymbolTable symbolTable = new SymbolTable(SymbolTable.top);
        elseStmt.set_symbol_table(symbolTable);
        SymbolTable.push(symbolTable);

        if(elseStmt.getBody() != null){
            elseStmt.getBody().accept(this);
        }

        SymbolTable.pop();

        return null;
    }

    @Override
    public Void visit(ForStmt forStmt) {

        SymbolTable symbolTable = new SymbolTable(SymbolTable.top);
        forStmt.set_symbol_table(symbolTable);
        SymbolTable.push(symbolTable);

        if(forStmt.getForCondition() != null){
            forStmt.getForCondition().accept(this);
        }

        if(forStmt.getBody() != null){
            forStmt.getBody().accept(this);
        }

        SymbolTable.pop();

        return null;
    }

    @Override
    public Void visit(ForCondition forCondition) {
        if(forCondition.getInit() != null){
            if(forCondition.getInit() instanceof ForDeclaration forDeclaration){
                forDeclaration.accept(this);
                if(is_for_declaration_redeclared){
                    //forCondition.delete_for_declaration();
                    is_for_declaration_redeclared = false;
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
        forDeclaration.setIs_used(false);
        ForDeclarationSymbolItem var_dec_item = new ForDeclarationSymbolItem(forDeclaration);
        try {
            SymbolTable.top.put(var_dec_item);
        } catch (ItemAlreadyExists e) {
            //System.out.println("Redeclaration of variable \"" + extractForDeclarationName(forDeclaration) +"\" in line " + forDeclaration.getLine());
            is_for_declaration_redeclared = true;
        }

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

        SymbolTable symbolTable = new SymbolTable(SymbolTable.top);
        whileStmt.set_symbol_table(symbolTable);
        SymbolTable.push(symbolTable);

        if(whileStmt.getCondition() != null){
            whileStmt.getCondition().accept(this);
        }
        if(whileStmt.getBody() != null){
            whileStmt.getBody().accept(this);
        }

        SymbolTable.pop();

        return null;
    }

    @Override
    public Void visit(DoWhileStmt doWhileStmt) {

        SymbolTable symbolTable = new SymbolTable(SymbolTable.top);
        doWhileStmt.set_symbol_table(symbolTable);
        SymbolTable.push(symbolTable);

        if(doWhileStmt.getCondition() != null){
            doWhileStmt.getCondition().accept(this);
        }
        if(doWhileStmt.getBody() != null){
            doWhileStmt.getBody().accept(this);
        }

        SymbolTable.pop();

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
    public Void visit(ArgumentListExpr argumentListExpr) {
        if(argumentListExpr.getArguments() != null){
            for(Expr expr : argumentListExpr.getArguments()){
                expr.accept(this);
            }
        }
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
        if(assign.getLeft() != null){
            assign.getLeft().accept(this);
        }
        if(assign.getRight() != null){
            assign.getRight().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(BinaryExpr binaryExpr) {

        if(binaryExpr.getFirstOperand() != null){
            binaryExpr.getFirstOperand().accept(this);
        }
        if(binaryExpr.getSecondOperand() != null){
            binaryExpr.getSecondOperand().accept(this);
        }
        return null;
    }


    @Override
    public Void visit(CommaExpr commaExpr) {
        if(commaExpr.getExpressions() != null) {
            for (Expr expr : commaExpr.getExpressions()) {
                if (expr != null) {
                    expr.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(ConditionalExpr conditionalExpr) {
        if(conditionalExpr.getCondition() != null) {
            conditionalExpr.getCondition().accept(this);
        }
        if(conditionalExpr.getTrueExpr() != null){
            conditionalExpr.getTrueExpr().accept(this);
        }
        if(conditionalExpr.getFalseExpr() != null) {
            conditionalExpr.getFalseExpr().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(FuncCallExpr funcCallExpr) {
        in_function_call = true;
        currentCaller = funcCallExpr;
        if(funcCallExpr.getArguments() != null) {
            number_of_arguments = funcCallExpr.set_number_of_parameters();
            //number_of_arguments = (funcCallExpr.getNumber_of_arguments() == -1) ? funcCallExpr.set_number_of_parameters() : funcCallExpr.getNumber_of_arguments();
        }
        else{
            number_of_arguments = 0;
        }
        if(funcCallExpr.getFunction() != null) {
            funcCallExpr.getFunction().accept(this);
        }
        in_function_call = false;
        currentCaller = null;
        number_of_arguments = -1;


        if(funcCallExpr.getArguments() != null){
            if(funcCallExpr.getArguments().getArguments() != null){
                for (Expr expr: funcCallExpr.getArguments().getArguments()){
                    if(expr != null) {
                        expr.accept(this);
                    }
                }
            }

        }
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        if(!primitiveIdentifiers.contains(identifier.getName())) {
            if (in_function_call) {
                try {
                    SymbolTable.top.getItem(FunctionDefinitionSymbolItem.START_KEY + identifier.getName() + "_" + number_of_arguments);
                    CallPair temp = new CallPair(extractFunctionName(currentFunction.getDeclarator().getDirectDeclarator()) + "_" + currentFunction.get_number_of_parameters(), identifier.getName() + "_" + number_of_arguments, currentCaller);
                    callList.add(temp);
                } catch (ItemNotFound e) {
                    print_logs.add("Line:" + identifier.getLine() + "-> " + identifier.getName() + " not declared");
                }
            } else {
                //System.out.println("estefade " + identifier.getName() + " " + identifier.getLine());
                try {
                    SymbolTableItem declaration = SymbolTable.top.getItem(DeclarationSymbolItem.START_KEY + identifier.getName());
//                    if(declaration instanceof DeclarationSymbolItem declarationSymbolItem){
//                        if(declarationSymbolItem.is_const()){
//                            identifi
//                        }
//                    }
                } catch (ItemNotFound e) {
                    print_logs.add("Line:" + identifier.getLine() + "-> " + identifier.getName() + " not declared");
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(ConstantNode int_Val) {
        return null;
    }

    @Override
    public Void visit(StringVal string_val) {
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
        if(nestedUnaryExpression.getOperand() != null){
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
//        return null;
//    }
//
    @Override
    public Void visit(TypeSpecifier typeSpecifier) {
        if(typeSpecifier.getTypeName() != null) {
            if (typeSpecifier.getIs_identifier()) {
                for(TypeDefPair typeDefPair: ASTBuilderVisitor.typeDefPairs)
                    if(typeSpecifier.getTypeName().equals(typeDefPair.getT2())){
                        typeSpecifier.set_name(typeDefPair.getT1());
                    }
            }
        }
        return null;
    }
//
//    @Override
//    public Void visit(TypeDef typeDef) {
//        return null;
//    }

    @Override
    public Void visit(UnaryCastExpression unaryCastExpression) {
        if(unaryCastExpression.getCastExpr() != null){
            unaryCastExpression.getCastExpr().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(UnaryExpr unaryExpr) {
        if(unaryExpr.getOperand() != null){
            unaryExpr.getOperand().accept(this);
        }
        return null;
    }
}
