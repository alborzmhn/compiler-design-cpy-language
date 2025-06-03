package main.visitor.optimizer;

import main.ast.nodes.expr.FuncCallExpr;

public class CallPair {
    public String caller;
    public String callee;
    public FuncCallExpr function_caller;

    public FuncCallExpr getFunction_caller() {
        return function_caller;
    }

    public CallPair(String caller, String callee, FuncCallExpr funcCallExpr) {
        this.caller = caller;
        this.callee = callee;
        this.function_caller = funcCallExpr;
    }

    public String getCaller() {
        return caller;
    }

    public String getCallee() {
        return callee;
    }
}