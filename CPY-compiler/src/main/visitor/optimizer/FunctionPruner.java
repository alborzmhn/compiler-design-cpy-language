package main.visitor.optimizer;

import main.ast.nodes.Program;
import main.ast.nodes.FunctionDefinition;
import main.visitor.NameAnalyzerVisitor;
import main.ast.nodes.TranslationUnit;
import main.ast.nodes.declaration.ExternalDeclaration;

import java.util.*;

public class FunctionPruner {

    public void pruneUnreachableFunctions(Program program) {
        Map<String, Set<String>> callGraph = new HashMap<>();
        for (CallPair pair : NameAnalyzerVisitor.callList) {
            callGraph.putIfAbsent(pair.caller, new HashSet<>());
            callGraph.get(pair.caller).add(pair.callee);
        }

        // 2. DFS from main_0
        Set<String> reachable = new HashSet<>();
        Stack<String> stack = new Stack<>();
        stack.push("main_0");

        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (!reachable.add(current)) continue;
            for (String callee : callGraph.getOrDefault(current, Set.of())) {
                stack.push(callee);
            }
        }
        TranslationUnit tu = program.getTranslationUnit();
        ArrayList<ExternalDeclaration> filtered = new ArrayList<>();

        for (ExternalDeclaration ext : tu.getExternalDecs()) {
            if (ext.getContent() instanceof FunctionDefinition func) {
                String key = NameAnalyzerVisitor.extractFunctionName(func.getDeclarator().getDirectDeclarator()) + "_" + func.getNumber_of_parameters();
                if (reachable.contains(key)) {
                    //System.out.println(key);
                    filtered.add(ext);
                }
            } else {
                filtered.add(ext);
            }
        }
        tu.setExternalDecs(filtered);
    }
}

