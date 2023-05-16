import java.io.PrintWriter;

public class Parser{
    private Token[] tokens;
    private Token currentToken;
    private Token previousToken;
    private String currentTokenType;
    private int index;
    private PrintWriter writer;


    public Parser(Token tokens[], PrintWriter writer){
        this.tokens = tokens;
        this.writer = writer;
        currentToken = tokens[0];
        currentTokenType = tokens[0].getTokenType();
    }

    public void parse(){
        index = 0;
        program(0);

        //After the program ends, close the writer
        writer.close();
    }

    // Depth represents how deep the function is in the call stack. It doesnt actually do anything,
    //it only helps while printing.
    
    private void program(int depth){
        print(depth, "<Program>");

        if (isCurrentToken("LEFTPAR")){
            topLevelForm(depth + 1);
            program(depth + 1);
        } else if (index == tokens.length - 1){//This means code has ended
            print(depth + 1,"___");
        } else {
            announceError("(");
        }
    }

    private void topLevelForm(int depth){
        print(depth,"<TopLevelForm>");

        if (isCurrentToken("LEFTPAR")){
            print(depth + 1,currentTokenType,"(");
            getNextToken();
            secondLevelForm(depth + 1);
            if (isCurrentToken("RIGHTPAR")){
                print(depth + 1, currentTokenType, ")");
                getNextToken();
            } else {
                announceError(")");
            }
        }
    }

    private void secondLevelForm(int depth){
        print(depth, "<SecondLevelForm>");

        if (isCurrentToken("DEFINE")){
            definition(depth + 1);
        } else if (isCurrentToken("LEFTPAR")){
            print(depth + 1, currentTokenType, "(");
            getNextToken();
            funCall(depth + 1);
            if (isCurrentToken("RIGHTPAR")){
                print(depth + 1, currentTokenType,")");
                getNextToken();
            } else {
                announceError(")");
            }
        } else {
            announceError("define or leftpar");
        }
    }

    private void definition(int depth){
        print(depth, "<Definition>");

        if (isCurrentToken("DEFINE")){
            print(depth + 1, currentTokenType, "define");
            getNextToken();
            definitionRight(depth + 1);
        } else {
            announceError("define");
        }
    }

    private void definitionRight(int depth){
        print(depth, "<DefinitionRight>");

        if (isCurrentToken("IDENTIFIER")){
            print(depth + 1, currentTokenType, currentToken.getValue());
            getNextToken();
            expressions(depth + 1);
        } else if (isCurrentToken("LEFTPAR")){
            print(depth + 1, currentTokenType, "(");
            getNextToken();
            if (isCurrentToken("IDENTIFIER")){
                print(depth + 1, currentTokenType, currentToken.getValue());
                getNextToken();
            } else {
                announceError("identifier");
            }
            argList(depth + 1);
            if (isCurrentToken("RIGHTPAR")){
                print(depth + 1, currentTokenType, ")");
                getNextToken();
                statements(depth + 1);

            } else {
                announceError("RIGHTPAR");
            }

        } else {
            announceError("identifier or leftpar");
        }
    }

    private void expressions(int depth){
        print(depth, "<Expressions>");

        if (isCurrentToken("IDENTIFIER") || isCurrentToken("NUMBER") || isCurrentToken("CHAR") || isCurrentToken("BOOLEAN") || isCurrentToken("STRING") || isCurrentToken("LEFTPAR")) {
            expression(depth + 1);
            expressions(depth + 1);
        } else {
            print(depth + 1, "___");
        }

    }

    private void expression(int depth){
        print(depth, "<Expression>");

        if (isCurrentToken("IDENTIFIER") || isCurrentToken("NUMBER") || isCurrentToken("CHAR") || isCurrentToken("BOOLEAN") || isCurrentToken("STRING")) {
            print(depth + 1, currentTokenType, currentToken.getValue());
            getNextToken();
        } else if (isCurrentToken("LEFTPAR")) {
            print(depth + 1, currentTokenType, "(");
            getNextToken();
            expr(depth + 1);
            if (isCurrentToken("RIGHTPAR")){
                print(depth + 1, currentTokenType, ")");
                getNextToken();
            } else {
                announceError(")");
            }
            
        } else {
            announceError("identifier or number or char or boolean or string or (");
        }
    }

    private void expr(int depth){
        print(depth, "<Expr>");

        if (isCurrentToken("LET")){
            letExpression(depth + 1);
        } else if (isCurrentToken("COND")){
            condExpression(depth + 1);
        } else if (isCurrentToken("IF")){
            ifExpression(depth + 1);
        } else if (isCurrentToken("BEGIN")){
            beginExpression(depth + 1);
        } else if (isCurrentToken("IDENTIFIER")){
            funCall(depth + 1);
        } else {
            announceError("let or cond or if or begin or identifier");
        }

    }

    private void condExpression(int depth){
        print(depth,"<condExpression>");

        if (isCurrentToken("COND")){
            print(depth + 1, currentTokenType, "cond");
            getNextToken();

            condBranches(depth + 1);
        } else {
            announceError("cond");
        }
    }

    private void condBranches(int depth){
        print(depth, "<condBranches>");

        if (isCurrentToken("LEFTPAR")){
            print(depth + 1, currentTokenType, "(");
            getNextToken();

            expression(depth + 1);
            statements(depth + 1);

            if (isCurrentToken("RIGHTPAR")){
                print(depth + 1, currentTokenType, ")");
                getNextToken();

                condBranches(depth + 1);
            } else {
                announceError("rightpar");
            }
        } else {
            announceError("leftpar");
        }
    }

    private void letExpression(int depth){
        print(depth,"<LetExpression>");

        if (isCurrentToken("LET")){
            print(depth + 1, currentTokenType, "let");
            getNextToken();
            letExpr(depth + 1);
        } else {
            announceError("let");
        }
    }

    private void letExpr(int depth){
        print(depth, "<LetExpr>");
        if (isCurrentToken("LEFTPAR")){
            print(depth + 1, currentTokenType, currentToken.getValue());
            getNextToken();

            varDefs(depth + 1);

            if (isCurrentToken("RIGHTPAR")){
                print(depth + 1, currentTokenType, currentToken.getValue());
                getNextToken();

                statements(depth + 1);

            } else {
                announceError("rightpar");
            }

        } else if (isCurrentToken("IDENTIFIER")){
            print(depth + 1, currentTokenType, currentToken.getValue());
            getNextToken();

            if (isCurrentToken("LEFTPAR")){
                print(depth + 1, currentTokenType, currentToken.getValue());
                getNextToken();

                varDefs(depth + 1);

                if (isCurrentToken("RIGHTPAR")){
                    print(depth + 1, currentTokenType, currentToken.getValue());
                    getNextToken();

                    statements(depth + 1);

                } else {
                    announceError("rightpar");
                }
            } else {
                announceError("leftpar");
            }
        } else {
            announceError("leftpar or identifier");
        }
    }

    private void varDefs(int depth){
        print(depth,"<VarDefs>");

        if (isCurrentToken("LEFTPAR")){
            print(depth + 1, currentTokenType, "(");
            getNextToken();

            if (isCurrentToken("IDENTIFIER")){
                print(depth + 1, currentTokenType, currentToken.getValue());
                getNextToken();

                expression(depth + 1);

                if (isCurrentToken("RIGHTPAR")){
                    print(depth + 1, currentTokenType, ")");
                    getNextToken();

                    varDef(depth + 1);
                } else {
                    announceError("rightpar");
                }

            } else {
                announceError("identifier");
            }
        } else {
            announceError("leftpar");
        }

    }

    private void varDef(int depth) {
        print(depth, "<VarDef>");
        if (isCurrentToken("LEFTPAR")) {
            varDefs(depth + 1);
        } else {
            print(depth + 1, "___");
        }
    }

    private void ifExpression(int depth) {
        print(depth, "<IfExpression>");

        if (isCurrentToken("IF")) {
            print(depth + 1, currentTokenType, "if");
            getNextToken();
            expression(depth + 1);
            expression(depth + 1);
            endExpression(depth + 1);
        } else {
            announceError("if");
        }
    }

    private void condBranch(int depth) {
        print(depth, "<CondBranch>");
        if (isCurrentToken("LEFTPAR")) {
            print(depth + 1, currentTokenType, "(");
            getNextToken();
            expression(depth + 1);
            statements(depth + 1);
            if (isCurrentToken("RIGHTPAR")) {
                print(depth + 1, currentTokenType, ")");
                getNextToken();
            } else {
                announceError("rightpar");
            }
        } else {
            print(depth + 1, "___");
        }
    }

    private void beginExpression(int depth) {
        print(depth, "<BeginExpression>");
        if (isCurrentToken("BEGIN")) {
            print(depth + 1, currentTokenType, "begin");
            getNextToken();
            statements(depth + 1);
        } else {
            announceError("begin");
        }
    }

    private void endExpression(int depth) {
        print(depth, "<EndExpression>");
        if (isCurrentToken("IDENTIFIER") || isCurrentToken("NUMBER") || isCurrentToken("CHAR") || isCurrentToken("BOOLEAN") || isCurrentToken("STRING") || isCurrentToken("LEFTPAR")) {
            expression(depth + 1);
        } else {
            print(depth + 1, "___");
        }
    }

    private void funCall(int depth){
        print(depth,"<FunCall>");

        if (isCurrentToken("IDENTIFIER")){
            print(depth + 1, currentTokenType, currentToken.getValue());
            getNextToken();
            expressions(depth + 1);
        } else {
            announceError("identifier");
        }

    }

    private void argList(int depth){
        print(depth, "<ArgList>");

        if (isCurrentToken("IDENTIFIER")){
            print(depth + 1, currentTokenType, currentToken.getValue());
            getNextToken();

            argList(depth + 1);
        } else {
            print(depth + 1, "___");
        }

    }

    private void statements(int depth){
        print(depth, "<Statements>");

        if (isCurrentToken("IDENTIFIER") || isCurrentToken("NUMBER") || isCurrentToken("CHAR") || isCurrentToken("BOOLEAN") || isCurrentToken("STRING") || isCurrentToken("LEFTPAR")){
            expression(depth + 1);
        } else if (isCurrentToken("DEFINE")){
            definition(depth + 1);
            statements(depth + 1);
        } else {
            announceError("identifier or number or char or boolean or string or leftpar or define");
        }

    }


    //----------------------------------------------------------------------

    private void print(int tabs, String string, String value){
        String str = "";
        for (int i = 0; i < tabs; i++){
            str += "  ";
        }
        str = str + (string + " (" + value + ")\n");
        // System.out.print(string + " (" + value + ")\n");
        System.out.print(str);
        writer.write(str);
        
        
    }
    
    private void print(int tabs, String string){
        String str = "";
        for (int i = 0; i < tabs; i++){
            str += "  ";
        }
        str = str + string + "\n";
        System.out.print(str);
        writer.write(str);
        
    }

    private boolean isCurrentToken(String type){
        if (currentToken == null ){
            return false;
        }
        if (currentTokenType.equals(type) ){
            return true;
        }
        return false;
    }

    public void announceError(String expected){
        int location[] = new int[2];
        if (currentToken == null) {
            location = previousToken.getLocation();
        } else {
            location = currentToken.getLocation();
        }
        String str = "SYNTAX ERROR [" + (location[0] + 1) + ":" + (location[1] + 1) + "]: '" + expected + "' is expected";
        System.out.println(str);
        writer.write(str);
        writer.close();
        System.exit(0);
    }

    public boolean checkAhead(String tokenType){
        if (isEndOfArray()){
            return false;
        } else if (tokenType.equals(currentTokenType)){
            // System.out.println(tokenType + " == " + currentTokenType);
            return true;
        }
        return false;

    }

    private void getNextToken(){
        if (isEndOfArray()){
            previousToken = currentToken;
            currentToken = null;
            currentTokenType = null;
        } else {
            previousToken = currentToken;
            currentToken = tokens[++index];
            currentTokenType = currentToken.getTokenType();
        }  
    }

    private boolean isEndOfArray(){
        if (index < tokens.length - 1) 
            return false;
        else
            return true;
    }
}