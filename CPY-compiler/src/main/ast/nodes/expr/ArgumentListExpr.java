package main.ast.nodes.expr;

import java.util.ArrayList;
import java.util.List;

import main.ast.nodes.expr.primitives.ConstantNode;
import main.ast.nodes.expr.primitives.StringVal;
import main.visitor.IVisitor;
import main.ast.nodes.Node;
import main.visitor.RemoveUnusedDeclarationsVisitor;

public class ArgumentListExpr extends Node {
    private List<Expr> arguments;
    private boolean is_flatten = false;

    public ArgumentListExpr(Expr firstExpr) {
        this.arguments = new ArrayList<>();
        this.arguments.add(firstExpr);
    }

    public void flattenCommaExpressions() {
        if (is_flatten || arguments.isEmpty())
            return;

        if (arguments.get(0) instanceof CommaExpr) {
            List<Expr> flattenArguments = new ArrayList<>();
            Expr expr = arguments.get(0);
            while (expr instanceof CommaExpr commaExpr) {
                flattenArguments.add(commaExpr.getExpressions().get(0));
                expr = commaExpr.getExpressions().get(1);
            }
            flattenArguments.add(expr);

            arguments = flattenArguments;
            is_flatten = true;
        }
    }

    public void delete_parameter(int index){
        //System.out.println(arguments.size());
        Expr removed = arguments.remove(index);
        RemoveUnusedDeclarationsVisitor.print_logs.add("Removed argument at index " + index + " in line " + removed.getLine());
    }

    public int get_number_of_arguments(){
        return arguments.size();
//        int count = 1;
//        Expr expr;
//        if(arguments.get(0) != null) {
//            expr = arguments.get(0);
//        }
//        else{
//            return 1;
//        }
//        while(expr instanceof CommaExpr commaExpr) {
//            expr = commaExpr.getExpressions().get(1);
//            count++;
//        }
//        return count;
    }

    public void addExpression(Expr expr) {
        this.arguments.add(expr);
    }

    public List<Expr> getArguments() {
        return arguments;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}