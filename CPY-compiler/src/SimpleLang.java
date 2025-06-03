import main.ast.nodes.Program;
import main.grammar.SimpleLangLexer;
import main.grammar.SimpleLangParser;
import main.visitor.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import main.visitor.optimizer.*;

import java.io.IOException;

public class SimpleLang {
    public static void main(String[] args) throws IOException {
        CharStream reader = CharStreams.fromFileName(args[0]);
        SimpleLangLexer simpleLangLexer = new SimpleLangLexer(reader);
        CommonTokenStream tokens = new CommonTokenStream(simpleLangLexer);
        SimpleLangParser flParser = new SimpleLangParser(tokens);
        Program program = flParser.program().programRet;
        System.out.println();

        ASTBuilderVisitor my_visitor_AST_builder = new ASTBuilderVisitor();
        my_visitor_AST_builder.visit(program);

//        TestVisitor my_visitor = new TestVisitor();
//        my_visitor.visit(program);

        NameAnalyzerVisitor my_visitor_name_analyzer = new NameAnalyzerVisitor();
        RemoveUnusedDeclarationsVisitor my_visitor_remove_declarations = new RemoveUnusedDeclarationsVisitor();
        CleanDeadCode my_visitor_clean_dead_code = new CleanDeadCode();
        CleanNoEffectStmts my_visitor_clean_no_effect_stmts = new CleanNoEffectStmts();
        FunctionPruner pruner = new FunctionPruner();

        NameAnalyzerVisitor.print_logs.add("temp");
        RemoveUnusedDeclarationsVisitor.print_logs.add("temp");

        int i = 0;

        while (!NameAnalyzerVisitor.print_logs.isEmpty() || !RemoveUnusedDeclarationsVisitor.print_logs.isEmpty()) {

            NameAnalyzerVisitor.print_logs.clear();
            NameAnalyzerVisitor.callList.clear();
            RemoveUnusedDeclarationsVisitor.print_logs.clear();

            my_visitor_name_analyzer.visit(program);

//            for(String print: NameAnalyzerVisitor.print_logs){
//                System.out.println(print);
//            }

            if(i == 0){
                if(!NameAnalyzerVisitor.print_logs.isEmpty()) {
                    for (String print : NameAnalyzerVisitor.print_logs) {
                        System.out.println(print);
                    }
                    break;
                }

                my_visitor_clean_dead_code.visit(program);
                my_visitor_clean_no_effect_stmts.visit(program);

                NameAnalyzerVisitor.print_logs.clear();
                NameAnalyzerVisitor.callList.clear();

                my_visitor_name_analyzer.visit(program);

            }

            my_visitor_remove_declarations.visit(program);

            for(String print: RemoveUnusedDeclarationsVisitor.print_logs){
                System.out.println(print);
            }

            pruner.pruneUnreachableFunctions(program);

            i++;

            //System.out.println("______________________________________________________");
        }

        if(i != 0) {
            FunctionsReportVisitor my_visitor_functions_report = new FunctionsReportVisitor();
            my_visitor_functions_report.visit(program);

            for (String print : FunctionsReportVisitor.print_logs) {
                System.out.println(print);
            }
        }
    }

}

