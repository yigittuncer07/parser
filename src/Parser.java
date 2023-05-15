public class Parser{
    private Token[] tokens;
    private Token currentToken;
    private String currentTokenType;
    private int index;

    public Parser(Token tokens[]){
        this.tokens = tokens;
        currentToken = tokens[0];
        currentTokenType = tokens[0].getTokenType();
    }

    public void parse(){
        index = 0;
        program(0);
        
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
            announceError("DEFINE or LEFTPAR");
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
            announceError("identifier or (");
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

        if (isCurrentToken("(")){
            print(depth + 1, currentTokenType, "(");
            getNextToken();

            expression(depth + 1);
            statements(depth + 1);

            if (isCurrentToken(")")){
                print(depth + 1, currentTokenType, ")");
                getNextToken();

                condBranches(depth);
            } else {
                announceError(")");
            }
        } else {
            announceError("(");
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
    }

    private void varDefs(int depth){

    }

    private void ifExpression(int depth){
        print(depth,"<IfExpression>");
    }

    private void beginExpression(int depth){
        print(depth,"<BeginExpression>");
    }

    private void funCall(int depth){
        print(depth,"<FunCall>");

        if (isCurrentToken("IDENTIFIER")){
            print(depth + 1, currentTokenType, currentToken.getValue());
            getNextToken();
            expressions(depth);
        } else {
            announceError("identifier");
        }

    }

    private void argList(int depth){
        print(depth, "<ArgList>");

        if (isCurrentToken("IDENTIFIER")){
            print(depth + 1, currentTokenType, currentToken.getValue());
            getNextToken();
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
        for (int i = 0; i < tabs; i++){
            System.out.print("\t");
        }
        System.out.print(string + " (" + value + ")\n");
    }
    
    private void print(int tabs, String string){
        for (int i = 0; i < tabs; i++){
            System.out.print("\t");
        }
        System.out.print(string + "\n");
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
        int location[] = currentToken.getLocation();
        System.out.println("SYNTAX ERROR [" + (location[0] + 1) + ":" + (location[1] + 1) + "]: '" + expected + "' is expected");
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
            currentToken = null;
            currentTokenType = null;
        } else {
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