grammar SimpleLang;

@header{
    import main.ast.nodes.*;
    import main.ast.nodes.declaration.*;
    import main.ast.nodes.Stmt.*;
    import main.ast.nodes.expr.*;
    import main.ast.nodes.expr.primitives.*;
    import main.ast.nodes.expr.operator.*;
    import main.ast.nodes.Stmt.iteration.*;
    import main.ast.nodes.Stmt.jump.*;
    import main.ast.nodes.OpResolver;
    import main.ast.nodes.type.*;
    import main.ast.nodes.directDeclarator.*;
    import main.ast.nodes.directAbstractDeclarator.*;
    import main.ast.nodes.initializer.*;
    import main.ast.nodes.Stmt.selection.*;
}

program returns [Program programRet]
    : (t = translationUnit { $programRet = new Program(); $programRet.setTranslationUnit($t.translationunitRet); }) ? NEWLINE* EOF ;

translationUnit returns [TranslationUnit translationunitRet]
    : {$translationunitRet = new TranslationUnit();} (NEWLINE* e = externalDeclaration {if($e.externaldeclarationRet != null){
                                                                                            $translationunitRet.addExternalDec($e.externaldeclarationRet);}})+ ;


externalDeclaration returns [ExternalDeclaration externaldeclarationRet]
    : fd=functionDefinition{$externaldeclarationRet = new ExternalDeclaration();} {$externaldeclarationRet.setContent($fd.functiondefinitionRet);} NEWLINE+ END
    | d=declaration{$externaldeclarationRet = new ExternalDeclaration();} {$externaldeclarationRet.setContent($d.declarationRet);}
    ;

functionDefinition returns [FunctionDefinition functiondefinitionRet]
    : {$functiondefinitionRet = new FunctionDefinition(); boolean is_first = true;}
    (ds=declarationSpecifiers {$functiondefinitionRet.addDeclarationSpecifiers($ds.declarationspecifiersRet);
                               $functiondefinitionRet.setLine($ds.declarationspecifiersRet.get(0).getLine());
                               is_first = false; })?
    d=declarator {$functiondefinitionRet.addDeclarator($d.declaratorRet);
                    if (is_first) {$functiondefinitionRet.setLine($d.declaratorRet.getLine());}}
    (dl=declarationList {$functiondefinitionRet.addDeclarationList($dl.declarationlistRet);})?
    Colon cs=compoundStatement {$functiondefinitionRet.addCompoundStmt($cs.compoundstmtRet);}
    ;


declarationList returns [List<Declaration> declarationlistRet]
    : {$declarationlistRet = new ArrayList<Declaration>();}
     (d=declaration { $declarationlistRet.add($d.declarationRet);} )+
     ;

expression returns [Expr exprRet]
  : ID = Identifier { $exprRet = new Identifier($ID.getText()); $exprRet.setLine($ID.line);}
  | constant=Constant { $exprRet = new ConstantNode($constant.getText()); $exprRet.setLine($constant.line);}
  | {$exprRet = new StringVal(); boolean isFirst = true; } (charSequence = StringLiteral {((StringVal)$exprRet).appendText($charSequence.getText());  if (isFirst) {$exprRet.setLine($charSequence.line);
                                                                                                                                                                    isFirst = false;}})+
  | l=LeftParen e = expression RightParen { $exprRet = $e.exprRet; $exprRet.setLine($l.line);}
  | l=LeftParen tn=typeName RightParen LeftBrace il=initializerList Comma? RightBrace { $exprRet = new InitializerListExpr($tn.typenameRet, $il.initializerlistRet); $exprRet.setLine($l.line);}
  | arr=expression l=LeftBracket idx=expression RightBracket { $exprRet = new ArrayAccess($arr.exprRet, $idx.exprRet); $exprRet.setLine($l.line);} // Array indexing
  | e = expression l=LeftParen {$exprRet = new FuncCallExpr($e.exprRet); $exprRet.setLine($l.line);} (arglist = argumentExpressionList {((FuncCallExpr)$exprRet).setArguments($arglist.argumentlistexprRet);})? RightParen // Function call
  | e = expression p=PlusPlus { $exprRet = new UnaryExpr($e.exprRet, UnaryOperator.PlusPlus); $exprRet.setLine($p.line);} // Postfix increment
  | e = expression m=MinusMinus { $exprRet = new UnaryExpr($e.exprRet, UnaryOperator.MinusMinus); $exprRet.setLine($m.line);} // Postfix decrement
  |{ ArrayList<UnaryOperator> ops = new ArrayList<UnaryOperator>();
         Expr expr = null;
         int line = -1; }
       ( l=PlusPlus { ops.add(UnaryOperator.PlusPlus);}
       | l=MinusMinus { ops.add(UnaryOperator.MinusMinus);}
       | l=Sizeof { ops.add(UnaryOperator.SIZEOF);})*
       ( ID = Identifier { expr = new Identifier($ID.getText()); line = $ID.line;}
       | constant=Constant { expr = new ConstantNode($constant.getText()); line = $constant.line;}
       | { expr = new StringVal(); boolean isFirst = true;} (charSequence = StringLiteral {((StringVal)$exprRet).appendText($charSequence.getText()); if (isFirst) {line = $charSequence.line;
                                                                                                                                                                    isFirst = false;}})+
       | l=LeftParen e=expression RightParen { expr = $e.exprRet; expr.setLine($constant.line); line = $l.line;}
       | l=LeftParen t=typeName RightParen LeftBrace i=initializerList Comma? RightBrace { expr = new InitializerListExpr($tn.typenameRet, $il.initializerlistRet); line = $l.line;}
       | u=unaryOperator c=castExpression { expr = new UnaryCastExpression($u.unaryOperatorRet, $c.castexprRet); line = $c.castexprRet.getLine();}
       | s=Sizeof LeftParen t=typeName RightParen { expr = new SizeofTypeNameExpression($t.typenameRet); line = $s.line;}
       ) { expr.setLine(line);     if (ops.isEmpty()) {
                                       $exprRet = expr;
                                   } else {
                                       $exprRet = new NestedUnaryExpression(ops, expr);
                                   }; $exprRet.setLine(expr.getLine()); }
  | l=LeftParen tn=typeName RightParen ce=castExpression { $exprRet = new CastExpr($tn.typenameRet, $ce.castexprRet); $exprRet.setLine($l.line);}
  | left=expression op=(Star | Div | Mod) right=expression { $exprRet = new BinaryExpr($left.exprRet, $right.exprRet, BinaryOperator.Plus); $exprRet.setLine($left.exprRet.getLine());}
  | left=expression op=(Plus | Minus) right=expression { $exprRet = new BinaryExpr($left.exprRet, $right.exprRet, OpResolver.resolveBinaryOp($op)); $exprRet.setLine($left.exprRet.getLine());}
  | left=expression op=(LeftShift | RightShift) right=expression { $exprRet = new BinaryExpr($left.exprRet, $right.exprRet, OpResolver.resolveBinaryOp($op)); $exprRet.setLine($left.exprRet.getLine());}
  | left=expression op=(Less | Greater | LessEqual | GreaterEqual) right=expression { $exprRet = new BinaryExpr($left.exprRet, $right.exprRet, OpResolver.resolveBinaryOp($op)); $exprRet.setLine($left.exprRet.getLine());}
  | left=expression op=(Equal | NotEqual) right=expression { $exprRet = new BinaryExpr($left.exprRet, $right.exprRet, OpResolver.resolveBinaryOp($op)); $exprRet.setLine($left.exprRet.getLine());}
  | left=expression op=And right=expression { $exprRet = new BinaryExpr($left.exprRet, $right.exprRet, BinaryOperator.And); $exprRet.setLine($left.exprRet.getLine());}
  | left=expression op=Xor right=expression { $exprRet = new BinaryExpr($left.exprRet, $right.exprRet, BinaryOperator.Xor); $exprRet.setLine($left.exprRet.getLine());}
  | left=expression op=Or right=expression { $exprRet = new BinaryExpr($left.exprRet, $right.exprRet, BinaryOperator.Or); $exprRet.setLine($left.exprRet.getLine());}
  | left=expression op=AndAnd right=expression { $exprRet = new BinaryExpr($left.exprRet, $right.exprRet, BinaryOperator.AndAnd); $exprRet.setLine($left.exprRet.getLine());}
  | left=expression op=OrOr right=expression { $exprRet = new BinaryExpr($left.exprRet, $right.exprRet, BinaryOperator.OrOr); $exprRet.setLine($left.exprRet.getLine());} // Logical OR
  | cond=expression Question thenExpr=expression Colon elseExpr=expression { $exprRet = new ConditionalExpr($cond.exprRet, $thenExpr.exprRet, $elseExpr.exprRet); $exprRet.setLine($cond.exprRet.getLine());} // Conditional operator
  | left=expression assignop = assignmentOperator right=expression { $exprRet = new Assign($left.exprRet, $right.exprRet, $assignop.assignmentoperatorRet); $exprRet.setLine($left.exprRet.getLine());}; // Assignment
  //| e1=expression {$exprRet = new CommaExpr($e1.exprRet); $exprRet.setLine($e1.exprRet.getLine());} (Comma e=expression { ((CommaExpr)$exprRet).addExpression($e.exprRet); })+; // Comma operator

argumentExpressionList returns [ArgumentListExpr argumentlistexprRet]
  : e1 = expression {$argumentlistexprRet = new ArgumentListExpr($e1.exprRet); $argumentlistexprRet.setLine($e1.exprRet.getLine());}(Comma e2 = expression {$argumentlistexprRet.addExpression($e2.exprRet); })* ;

unaryOperator returns [UnaryOperator unaryOperatorRet]
  : And { $unaryOperatorRet = UnaryOperator.AND; }
  | Star { $unaryOperatorRet = UnaryOperator.STAR; }
  | Plus { $unaryOperatorRet = UnaryOperator.PLUS; }
  | Minus { $unaryOperatorRet = UnaryOperator.MINUS; }
  | Tilde { $unaryOperatorRet = UnaryOperator.TILDE; }
  | Not { $unaryOperatorRet = UnaryOperator.NOT; }
  ;

castExpression returns [Expr castexprRet]
  : l=LeftParen tn=typeName RightParen ce=castExpression {$castexprRet = new CastExpr($tn.typenameRet, $ce.castexprRet); $castexprRet.setLine($l.line);}
  | e=expression {$castexprRet = $e.exprRet; $castexprRet.setLine($e.exprRet.getLine());}
  | ds=DigitSequence {$castexprRet = new ConstantNode($ds.getText()); $castexprRet.setLine($ds.line);}
;

assignmentOperator returns [AssignmentOperator assignmentoperatorRet]
    : Assign            { $assignmentoperatorRet = AssignmentOperator.ASSIGN; }
    | StarAssign        { $assignmentoperatorRet = AssignmentOperator.STAR_ASSIGN; }
    | DivAssign         { $assignmentoperatorRet = AssignmentOperator.DIV_ASSIGN; }
    | ModAssign         { $assignmentoperatorRet = AssignmentOperator.MOD_ASSIGN; }
    | PlusAssign        { $assignmentoperatorRet = AssignmentOperator.PLUS_ASSIGN; }
    | MinusAssign       { $assignmentoperatorRet = AssignmentOperator.MINUS_ASSIGN; }
    | LeftShiftAssign   { $assignmentoperatorRet = AssignmentOperator.LEFT_SHIFT_ASSIGN; }
    | RightShiftAssign  { $assignmentoperatorRet = AssignmentOperator.RIGHT_SHIFT_ASSIGN; }
    | AndAssign         { $assignmentoperatorRet = AssignmentOperator.AND_ASSIGN; }
    | XorAssign         { $assignmentoperatorRet = AssignmentOperator.XOR_ASSIGN; }
    | OrAssign          { $assignmentoperatorRet = AssignmentOperator.OR_ASSIGN; }
    ;

declaration returns [Declaration declarationRet]
    : ds = declarationSpecifiers {$declarationRet = new Declaration($ds.declarationspecifiersRet);
                                  $declarationRet.setLine($ds.declarationspecifiersRet.get(0).getLine());}
    (idl = initDeclaratorList { $declarationRet.setInitDeclarationList($idl.initdeclaratoresRet); })? //Semi
    ;

declarationSpecifiers returns [List<DeclarationSpecifier> declarationspecifiersRet]
    : {$declarationspecifiersRet = new ArrayList<DeclarationSpecifier>();}
    (ds = declarationSpecifier { $declarationspecifiersRet.add($ds.declarationspecifierRet);} )+ ;

declarationSpecifier returns [DeclarationSpecifier declarationspecifierRet]
    : {$declarationspecifierRet = new DeclarationSpecifier();}
    (t1=Typedef     {Type t1typedef = new TypeDef();
                     $declarationspecifierRet.setSpecifier(t1typedef);
                     $declarationspecifierRet.setLine($t1.line);}
    | t2=typeSpecifier {$declarationspecifierRet.setSpecifier($t2.typespecifierRet);
                        $declarationspecifierRet.setLine($t2.typespecifierRet.getLine());}
    | t3=Const     {Type t3const = new ConstNode();
                    $declarationspecifierRet.setSpecifier(t3const);
                    $declarationspecifierRet.setLine($t3.line);})
    ;

initDeclaratorList returns [List<InitDeclarator> initdeclaratoresRet]
    : {$initdeclaratoresRet = new ArrayList<InitDeclarator>();}
    i1=initDeclarator {$initdeclaratoresRet.add($i1.initdeclaratorRet);}
    (Comma i=initDeclarator {$initdeclaratoresRet.add($i.initdeclaratorRet);})* ;

initDeclarator returns [InitDeclarator initdeclaratorRet]
    : d=declarator {$initdeclaratorRet = new InitDeclarator($d.declaratorRet);
                    $initdeclaratorRet.setLine($d.declaratorRet.getLine());}
    (Assign i=initializer {$initdeclaratorRet.setInitializer($i.initializerRet); })? ;

typeSpecifier returns [TypeSpecifier typespecifierRet]
    : v=Void { $typespecifierRet = new TypeSpecifier($v.text);
               $typespecifierRet.setLine($v.line); }
    | c=Char { $typespecifierRet = new TypeSpecifier($c.text);
               $typespecifierRet.setLine($c.line); }
    | s=Short { $typespecifierRet = new TypeSpecifier($s.text);
                $typespecifierRet.setLine($s.line); }
    | i=Int { $typespecifierRet = new TypeSpecifier($i.text);
               $typespecifierRet.setLine($i.line); }
    | l=Long { $typespecifierRet = new TypeSpecifier($l.text);
               $typespecifierRet.setLine($l.line); }
    | f=Float { $typespecifierRet = new TypeSpecifier($f.text);
                $typespecifierRet.setLine($f.line); }
    | d=Double { $typespecifierRet = new TypeSpecifier($d.text);
                 $typespecifierRet.setLine($d.line); }
    | s=Signed { $typespecifierRet = new TypeSpecifier($s.text);
                 $typespecifierRet.setLine($s.line); }
    | u=Unsigned { $typespecifierRet = new TypeSpecifier($u.text);
                   $typespecifierRet.setLine($u.line); }
    | b=Bool { $typespecifierRet = new TypeSpecifier($b.text);
               $typespecifierRet.setLine($b.line); }
    | id=Identifier { $typespecifierRet = new TypeSpecifier($id.text, true);
                      $typespecifierRet.setLine($id.line); }
    ;


specifierQualifierList returns [SpecifierQualifierList specifierqualifierRet]
    : {$specifierqualifierRet = new SpecifierQualifierList();}
    (t1=typeSpecifier {$specifierqualifierRet.add($t1.typespecifierRet); $specifierqualifierRet.setLine($t1.typespecifierRet.getLine());}
    | t2=Const {Type t2const=new ConstNode(); $specifierqualifierRet.add(t2const); $specifierqualifierRet.setLine($t2.line);} )
    (sql=specifierQualifierList  {$specifierqualifierRet.addAll($sql.specifierqualifierRet);})?
    ;

declarator returns [Declarator declaratorRet]
    : {$declaratorRet = new Declarator(); boolean isFirst = true;}
    (p=pointer {$declaratorRet.setPointer($p.pointerRet);
                $declaratorRet.setLine($p.pointerRet.getLine());
                isFirst = false; })?
    dd=directDeclarator {$declaratorRet.setDirectDeclarator($dd.directdeclaratorRet);
                         if(isFirst) {$declaratorRet.setLine($dd.directdeclaratorRet.getLine());}}
    ;

directDeclarator returns [DirectDeclarator directdeclaratorRet]
    : ID = Identifier { $directdeclaratorRet = new DirectDeclaratorID(new Identifier($ID.getText())); $directdeclaratorRet.setLine($ID.line); }
    | l=LeftParen d=declarator RightParen { $directdeclaratorRet = new DirectDeclaratorInner($d.declaratorRet); $directdeclaratorRet.setLine($l.line);}
    | dd = directDeclarator {DirectDeclaratorArray temp1 = new DirectDeclaratorArray($dd.directdeclaratorRet);}
      LeftBracket (e=expression {temp1.setExpression($e.exprRet);})? RightBracket {$directdeclaratorRet = temp1; $directdeclaratorRet.setLine($dd.directdeclaratorRet.getLine()); }
    | dd = directDeclarator {DirectDeclaratorFunction temp2 = new DirectDeclaratorFunction($dd.directdeclaratorRet);}
      LeftParen (pl=parameterList {if($pl.parameterlistRet != null){temp2.setParameters($pl.parameterlistRet);}})?
      (il=identifierList {temp2.setIdentifierList($il.identifierlistRet);})? RightParen {$directdeclaratorRet = temp2; $directdeclaratorRet.setLine($dd.directdeclaratorRet.getLine()); }
    ;

pointer returns [Pointer pointerRet]
    : { $pointerRet = new Pointer(); boolean is_first = true;}
      (s=Star c+=Const* {if(is_first){
                            $pointerRet.setLine($s.line); is_first = false; }
                       $pointerRet.addLevel(!$c.isEmpty()); })+
    ;

parameterList returns [List<ParameterDeclaration> parameterlistRet]
    : { $parameterlistRet = new ArrayList<ParameterDeclaration>(); }
      p=parameterDeclaration { $parameterlistRet.add($p.parameterdeclarationRet); }
      (Comma p2=parameterDeclaration { $parameterlistRet.add($p2.parameterdeclarationRet); })*
    ;

parameterDeclaration returns [ParameterDeclaration parameterdeclarationRet]
    : ds=declarationSpecifiers{$parameterdeclarationRet = new ParameterDeclaration();
                               $parameterdeclarationRet.setSpecifiers($ds.declarationspecifiersRet);
                               $parameterdeclarationRet.setLine($ds.declarationspecifiersRet.get(0).getLine());}
    (d=declarator { $parameterdeclarationRet.setDeclarator($d.declaratorRet); }
    | (ad=abstractDeclarator { $parameterdeclarationRet.setAbstractDeclarator($ad.abstractdeclaratorRet); } )?)
    ;

identifierList returns [IdentifierList identifierlistRet]
    : { $identifierlistRet = new IdentifierList(); }
      id1=Identifier { $identifierlistRet.addID(new Identifier($id1.getText())); $identifierlistRet.setLine($id1.line); }
      (Comma id2=Identifier { $identifierlistRet.addID(new Identifier($id2.getText())); })*
    ;

typeName returns [TypeName typenameRet]
    : sq=specifierQualifierList { $typenameRet = new TypeName($sq.specifierqualifierRet); $typenameRet.setLine($sq.specifierqualifierRet.getLine());}
    (ad=abstractDeclarator {$typenameRet.setAbstractDeclarator($ad.abstractdeclaratorRet);})?
    ;

abstractDeclarator returns [AbstractDeclarator abstractdeclaratorRet]
    :  p=pointer { $abstractdeclaratorRet = new AbstractDeclarator(); $abstractdeclaratorRet.setPointer($p.pointerRet); $abstractdeclaratorRet.setLine($p.pointerRet.getLine());}
    | {boolean is_make = false;}(p=pointer { $abstractdeclaratorRet = new AbstractDeclarator(); $abstractdeclaratorRet.setPointer($p.pointerRet); is_make = true;})?
       dad = directAbstractDeclarator {if(!is_make){
                                              $abstractdeclaratorRet = new AbstractDeclarator();}
                                        $abstractdeclaratorRet.setDirectDeclarator($dad.directabstractdeclaratorRet);
                                        $abstractdeclaratorRet.setLine($dad.directabstractdeclaratorRet.getLine());}
    ;

directAbstractDeclarator returns [DirectAbstractDeclarator directabstractdeclaratorRet]
    : l=LeftBracket  (e=expression {DADArray node = new DADArray(); node.setSize($e.exprRet); $directabstractdeclaratorRet = node; $directabstractdeclaratorRet.setLine($l.line);})? RightBracket
    | l=LeftParen ( a=abstractDeclarator {DADFunction node = new DADFunction(); node.setAbstractDeclarator($a.abstractdeclaratorRet); $directabstractdeclaratorRet = node;}
        | (pl=parameterList {DADFunction node = new DADFunction(); node.setParameters($pl.parameterlistRet); $directabstractdeclaratorRet = node;})?) RightParen { $directabstractdeclaratorRet.setLine($l.line);}
    | inner1=directAbstractDeclarator {DADNestedArray node = new DADNestedArray($inner1.directabstractdeclaratorRet);}
        LeftBracket (e=expression {node.setSize($e.exprRet);})? RightBracket {$directabstractdeclaratorRet = node; $directabstractdeclaratorRet.setLine($inner1.directabstractdeclaratorRet.getLine());}
    | inner2=directAbstractDeclarator {DADNestedFunction node = new DADNestedFunction($inner2.directabstractdeclaratorRet);}
        LeftParen (pl=parameterList {node.setParameters($pl.parameterlistRet);})? RightParen {$directabstractdeclaratorRet = node; $directabstractdeclaratorRet.setLine($inner2.directabstractdeclaratorRet.getLine());}
    ;

initializer returns [Initializer initializerRet]
    : e=expression {$initializerRet = new ExpressionInitializer($e.exprRet); $initializerRet.setLine($e.exprRet.getLine());}
    | l=LeftBrace il=initializerList {$initializerRet = $il.initializerlistRet; $initializerRet.setLine($l.line);} Comma? RightBrace ;

initializerList returns [InitializerList initializerlistRet]
    : { $initializerlistRet = new InitializerList(); InitializerList.InitEntry temp = new InitializerList.InitEntry();}
      (d1=designation {temp.setD($d1.designationRet);})? i1=initializer {temp.setI($i1.initializerRet);
        $initializerlistRet.addEntry(temp); $initializerlistRet.setLine($i1.initializerRet.getLine());}
      (Comma {InitializerList.InitEntry temp1 = new InitializerList.InitEntry();}
        (d2=designation {temp1.setD($d2.designationRet);})? i2=initializer {temp1.setI($i2.initializerRet);
        $initializerlistRet.addEntry(temp1);})*
    ;

designation returns [List<Designator> designationRet]
    : {$designationRet = new ArrayList<Designator>();}
    (d=designator {$designationRet.add($d.designatorRet);})+ Assign ;

designator returns [Designator designatorRet]
    : {$designatorRet = new Designator();}
    l=LeftBracket e=expression {$designatorRet.setExpr($e.exprRet); $designatorRet.setLine($l.line);} RightBracket
    | d=Dot ID=Identifier {Identifier dotid = new Identifier($ID.getText());
                         $designatorRet.setExpr(dotid); $designatorRet.setLine($d.line);};

statement returns [Stmt statementRet]
    : //cs=compoundStatement        { $statementRet = $cs.compoundstmtRet; }
    | es=expressionStatement      { $statementRet = $es.expressionstmtRet; }
    | ss=selectionStatement       { $statementRet = $ss.selectionstmtRet; }
    | is=iterationStatement       { $statementRet = $is.iterationstmtRet; }
    | js=jumpStatement            { $statementRet = $js.jumpstmtRet; }
    ;

compoundStatement returns [CompoundStmt compoundstmtRet]
    :
      {boolean is_first = true;}
      ((NEWLINE)+ i=INDENT b=blockItem { if(is_first){
                                           $compoundstmtRet = new CompoundStmt();
                                            is_first = false;}
                                         if($b.blockitemRet != null){
                                           $b.blockitemRet.setIndentLevel($i.getText().length() / 4);
                                           $compoundstmtRet.addBlock($b.blockitemRet);}})+
    ;

blockItem returns [BlockItem blockitemRet]
    :
    s=statement {
        if ($s.statementRet != null) {
            $blockitemRet = new BlockItem($s.statementRet);
            $blockitemRet.setLine($s.statementRet.getLine());
        } else {
            $blockitemRet = null;
        }}
    | d=declaration {$blockitemRet = new BlockItem($d.declarationRet); $blockitemRet.setLine($d.declarationRet.getLine());}
    ;

expressionStatement returns [ExpressionStmt expressionstmtRet]
    : {$expressionstmtRet = new ExpressionStmt();}
    (expr = expression {$expressionstmtRet.setExpression($expr.exprRet); $expressionstmtRet.setLine($expr.exprRet.getLine());})
    ;

selectionStatement returns [SelectionStmt selectionstmtRet]
    : i=If LeftParen condition=expression RightParen Colon cs=statement {$selectionstmtRet = new IfStmt($condition.exprRet); $selectionstmtRet.setLine($i.line);}
      | e=Else Colon ec=statement { $selectionstmtRet = new ElseStmt(); $selectionstmtRet.setLine($e.line);}
      | ef=ElseIf LeftParen condition2=expression RightParen Colon eis=statement {$selectionstmtRet = new ElseIfStmt($condition2.exprRet); $selectionstmtRet.setLine($ef.line);}
    ;

iterationStatement returns [IterationStmt iterationstmtRet]
    : w=While LeftParen condition = expression RightParen Colon body = statement
    {$iterationstmtRet = new WhileStmt($condition.exprRet); $iterationstmtRet.setLine($w.line);}
    | d=Do body = statement While LeftParen condition = expression RightParen //Semi
    {$iterationstmtRet = new DoWhileStmt($condition.exprRet); $iterationstmtRet.setLine($d.line);}
    | f=For LeftParen cond=forCondition RightParen Colon body = statement
    {$iterationstmtRet = new ForStmt($cond.forconditionRet);$iterationstmtRet.setLine($f.line);}
    ;

forCondition returns [ForCondition forconditionRet]
    : {$forconditionRet = new ForCondition();}
    (fd=forDeclaration {$forconditionRet.setInit($fd.fordeclarationRet);}
    | (e=expression {$forconditionRet.setInit($e.exprRet);})? )
    s=Semi (fe1=forExpression {$forconditionRet.setCondition($fe1.forexpressionRet);})?
    Semi (fe2=forExpression {$forconditionRet.setUpdate($fe2.forexpressionRet);})? {$forconditionRet.setLine($s.line);}
    ;

forDeclaration returns [ForDeclaration fordeclarationRet]
    : ds=declarationSpecifiers {$fordeclarationRet = new ForDeclaration($ds.declarationspecifiersRet);
                                $fordeclarationRet.setLine($ds.declarationspecifiersRet.get(0).getLine());}
     (id=initDeclaratorList {$fordeclarationRet.setInit($id.initdeclaratoresRet);})?
    ;

forExpression returns [ForExpression forexpressionRet]
    : {$forexpressionRet = new ForExpression();}
     e1=expression {$forexpressionRet.addExpr($e1.exprRet); $forexpressionRet.setLine($e1.exprRet.getLine());}
     (Comma e=expression {$forexpressionRet.addExpr($e.exprRet); })*
     ;

jumpStatement returns [JumpStmt jumpstmtRet]
    : c=Continue { $jumpstmtRet = new ContinueStmt(); $jumpstmtRet.setLine($c.line);}
    | b=Break { $jumpstmtRet = new BreakStmt(); $jumpstmtRet.setLine($b.line);}
    | r=Return { $jumpstmtRet = new ReturnStmt(); $jumpstmtRet.setLine($r.line);} (e=expression { ((ReturnStmt)$jumpstmtRet).setValue($e.exprRet); })?
    ;

Break                 : 'break'                 ;
Char                  : 'char'                  ;
Const                 : 'const'                 ;
Continue              : 'continue'              ;
Do                    : 'do'                    ;
Double                : 'double'                ;
Else                  : 'else'                  ;
Float                 : 'float'                 ;
For                   : 'for'                   ;
If                    : 'if'                    ;
Int                   : 'int'                   ;
Long                  : 'long'                  ;
Return                : 'return'                ;
Short                 : 'short'                 ;
Signed                : 'signed'                ;
Sizeof                : 'sizeof'                ;
Switch                : 'switch'                ;
Typedef               : 'typedef'               ;
Unsigned              : 'unsigned'              ;
Void                  : 'void'                  ;
While                 : 'while'                 ;
Bool                  : 'bool'                  ;
LeftParen             : '('                     ;
RightParen            : ')'                     ;
LeftBracket           : '['                     ;
RightBracket          : ']'                     ;
LeftBrace             : '{'                     ;
RightBrace            : '}'                     ;
Less                  : '<'                     ;
LessEqual             : '<='                    ;
Greater               : '>'                     ;
GreaterEqual          : '>='                    ;
LeftShift             : '<<'                    ;
RightShift            : '>>'                    ;
Plus                  : '+'                     ;
PlusPlus              : '++'                    ;
Minus                 : '-'                     ;
MinusMinus            : '--'                    ;
Star                  : '*'                     ;
Div                   : '/'                     ;
Mod                   : '%'                     ;
And                   : '&'                     ;
Or                    : '|'                     ;
AndAnd                : '&&'                    ;
OrOr                  : '||'                    ;
Xor                   : '^'                     ;
Not                   : '!'                     ;
Tilde                 : '~'                     ;
Question              : '?'                     ;
Colon                 : ':'                     ;
Semi                  : ';'                     ;
Comma                 : ','                     ;
Assign                : '='                     ;
StarAssign            : '*='                    ;
DivAssign             : '/='                    ;
ModAssign             : '%='                    ;
PlusAssign            : '+='                    ;
MinusAssign           : '-='                    ;
LeftShiftAssign       : '<<='                   ;
RightShiftAssign      : '>>='                   ;
AndAssign             : '&='                    ;
XorAssign             : '^='                    ;
OrAssign              : '|='                    ;
Equal                 : '=='                    ;
NotEqual              : '!='                    ;
Arrow                 : '->'                    ;
Dot                   : '.'                     ;
END                   : 'end'                   ;
ElseIf                : 'else if'               ;


Identifier
    : IdentifierNondigit (IdentifierNondigit | Digit)* ;

fragment IdentifierNondigit
    : Nondigit | UniversalCharacterName ;

fragment Nondigit
    : [a-zA-Z_] ;

fragment Digit
    : [0-9] ;

fragment UniversalCharacterName
    : '\\u' HexQuad | '\\U' HexQuad HexQuad ;

fragment HexQuad
    : HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit ;

Constant
    : IntegerConstant | FloatingConstant | CharacterConstant ;

fragment IntegerConstant
    : DecimalConstant IntegerSuffix?
    | OctalConstant IntegerSuffix?
    | HexadecimalConstant IntegerSuffix?
    | BinaryConstant ;

fragment BinaryConstant
    : '0' [bB] [0-1]+ ;

fragment DecimalConstant
    : NonzeroDigit Digit* ;

fragment OctalConstant
    : '0' OctalDigit* ;

fragment HexadecimalConstant
    : HexadecimalPrefix HexadecimalDigit+ ;

fragment HexadecimalPrefix
    : '0' [xX] ;

fragment NonzeroDigit
    : [1-9] ;

fragment OctalDigit
    : [0-7] ;

fragment HexadecimalDigit
    : [0-9a-fA-F] ;

fragment IntegerSuffix
    : UnsignedSuffix LongSuffix? | UnsignedSuffix LongLongSuffix | LongSuffix UnsignedSuffix? | LongLongSuffix UnsignedSuffix? ;

fragment UnsignedSuffix
    : [uU] ;

fragment LongSuffix
    : [lL] ;

fragment LongLongSuffix
    : 'll' | 'LL' ;

fragment FloatingConstant
    : DecimalFloatingConstant | HexadecimalFloatingConstant ;

fragment DecimalFloatingConstant
    : FractionalConstant ExponentPart? FloatingSuffix? | DigitSequence ExponentPart FloatingSuffix? ;

fragment HexadecimalFloatingConstant
    : HexadecimalPrefix (HexadecimalFractionalConstant | HexadecimalDigitSequence) BinaryExponentPart FloatingSuffix? ;

fragment FractionalConstant
    : DigitSequence? Dot DigitSequence | DigitSequence Dot ;

fragment ExponentPart
    : [eE] Sign? DigitSequence ;

fragment Sign
    : [+-] ;

DigitSequence
    : Digit+ ;

fragment HexadecimalFractionalConstant
    : HexadecimalDigitSequence? Dot HexadecimalDigitSequence | HexadecimalDigitSequence Dot ;

fragment BinaryExponentPart
    : [pP] Sign? DigitSequence ;

fragment HexadecimalDigitSequence
    : HexadecimalDigit+ ;

fragment FloatingSuffix
    : [flFL] ;

fragment CharacterConstant
    : '\'' CCharSequence '\'' | 'L\'' CCharSequence '\''| 'u\'' CCharSequence '\'' | 'U\'' CCharSequence '\''
    ;

fragment CCharSequence
    : CChar+ ;

fragment CChar
    : ~['\\\r\n] | EscapeSequence ;

fragment EscapeSequence
    : SimpleEscapeSequence | OctalEscapeSequence | HexadecimalEscapeSequence | UniversalCharacterName ;

fragment SimpleEscapeSequence
    : '\\' ['"?abfnrtv\\] ;

fragment OctalEscapeSequence
    : '\\' OctalDigit OctalDigit? OctalDigit? ;

fragment HexadecimalEscapeSequence
    : '\\x' HexadecimalDigit+ ;

StringLiteral
    : EncodingPrefix? '"' SCharSequence? '"' ;

fragment EncodingPrefix
    : 'u8' | 'u' | 'U' | 'L' ;

fragment SCharSequence
    : SChar+ ;

fragment SChar
    : ~["\\\r\n] | EscapeSequence | '\\\n' | '\\\r\n' ;

MultiLineMacro
    : '#' (~[\n]*? '\\' '\r'? '\n')+ ~ [\n]+ -> channel(HIDDEN) ;

Directive
    : '#' ~[\n]* -> channel(HIDDEN) ;

INDENT : TAB+ ;

TAB
    : ('    ');

NEWLINE
    : ('\r' '\n'? | '\n') ;

Whitespace
    : [ \t]+ -> channel(HIDDEN) ;

BlockComment
    : TAB* '/*' .*? '*/' -> channel(HIDDEN) ;

LineComment
    : TAB* '//' ~[\r\n]* -> channel(HIDDEN) ;