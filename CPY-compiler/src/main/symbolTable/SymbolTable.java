package main.symbolTable;

import main.symbolTable.exceptions.ItemAlreadyExists;
import main.symbolTable.exceptions.ItemNotFound;
import main.symbolTable.items.DeclarationSymbolItem;
import main.symbolTable.items.FunctionDefinitionSymbolItem;
import main.symbolTable.items.ParameterDeclarationSymbolItem;
import main.symbolTable.items.SymbolTableItem;
import main.utils.Stack;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public static SymbolTable top;
    public static SymbolTable root;
    public SymbolTable pre;
    private static Stack<SymbolTable> stack = new Stack<>();
    private Map<String, SymbolTableItem> items;


    public static void push(SymbolTable symbolTable) {
        if (top != null)
            stack.push(top);
        top = symbolTable;
    }
    public static void pop() {
        top = stack.pop();
    }

    public SymbolTable() {
        this.items = new HashMap<>();
    }
    public SymbolTable(SymbolTable pre) {
        this.pre = pre;
        this.items = new HashMap<>();
    }

    public void put(SymbolTableItem item) throws ItemAlreadyExists {
        if (items.containsKey(item.getKey()))

            throw new ItemAlreadyExists();
        items.put(item.getKey(), item);
    }


    public SymbolTableItem getItem(String key) throws ItemNotFound {
        SymbolTable currentSymbolTable = this;

        while(currentSymbolTable != null) {
            SymbolTableItem symbolTableItem = currentSymbolTable.items.get(key);
            if( symbolTableItem != null ) {
                symbolTableItem.setIs_used(true);
                return symbolTableItem;
            }
            currentSymbolTable = currentSymbolTable.pre;
        }
        throw new ItemNotFound();
    }

//    public void delete_unused_declarations(){
//        for(SymbolTableItem item : items.values()){
//            if(!item.getIs_used()){
//                if(item instanceof ParameterDeclarationSymbolItem){
//                    ((ParameterDeclarationSymbolItem) item).getParameterDeclaration();
//                }
//                else if(item instanceof DeclarationSymbolItem){
//                    ((DeclarationSymbolItem) item).getVarDec();
//                }
//            }
//        }
//    }
}
