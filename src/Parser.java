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
        program();
        
    }

    private void program(){
        print(0, "<Program>");

        if (isCurrentToken("LEFTPAR")){
            topLevelForm();
            program();
        } else if (index == tokens.length - 1){//This means code has ended
            print(1,"___");
        } else {
            announceError("(");
        }
    }

    private void topLevelForm(){
        print(1,"<TopLevelForm>");

        if (isCurrentToken("LEFTPAR")){
            print(2,currentTokenType,"(");
            getNextToken();
            secondLevelForm();
            if (isCurrentToken("RIGHTPAR")){
                print(2, currentTokenType, ")");
                getNextToken();
            } else {
                announceError(")");
            }
        }
    }

    private void secondLevelForm(){
        print(2, "<SecondLevelForm>");

        if (isCurrentToken("DEFINE")){
            definition();
        } else if (isCurrentToken("LEFTPAR")){
            print(3, currentTokenType, "(");
            getNextToken();
            funCall();
            if (isCurrentToken("RIGHTPAR")){
                print(3, currentTokenType,")");
                getNextToken();
            } else {
                announceError(")");
            }
        } else {
            announceError("DEFINE or LEFTPAR");
        }
    }

    private void definition(){
        print(3, "<Definition>");

        if (isCurrentToken("DEFINE")){
            print(4, currentTokenType, "define");
            getNextToken();
            definitionRight();
        } else {
            announceError("define");
        }
    }

    private void definitionRight(){
        print(4, "<DefinitionRight>");

        if (isCurrentToken("IDENTIFIER")){
            print(5, currentTokenType, currentToken.getValue());
            getNextToken();
            expressions();
        } else if (isCurrentToken("LEFTPAR")){
            print(5, currentTokenType, "(");
            getNextToken();
            if (isCurrentToken("IDENTIFIER")){
                print(5, currentTokenType, currentToken.getValue());
                getNextToken();
            } else {
                announceError("identifier");
            }
            argList();
            if (isCurrentToken("RIGHTPAR")){
                print(5, currentTokenType, ")");
                getNextToken();
                statements();

            } else {
                announceError("RIGHTPAR");
            }

        } else {
            announceError("identifier or (");
        }
    }

    private void expressions(){
        print(5, "<Expressions>");

        if (isCurrentToken("IDENTIFIER") || isCurrentToken("NUMBER") || isCurrentToken("CHAR") || isCurrentToken("BOOLEAN") || isCurrentToken("STRING") || isCurrentToken("LEFTPAR")) {
            expression();
            expressions();
        } else {

        }

    }

    private void expression(){
        print(6, "<Expression>");

        if (isCurrentToken("IDENTIFIER") || isCurrentToken("NUMBER") || isCurrentToken("CHAR") || isCurrentToken("BOOLEAN") || isCurrentToken("STRING")) {

        } else if (isCurrentToken("LEFTPAR")) {
            print(7, currentTokenType, "(");
            getNextToken();
            expr();
            if (isCurrentToken("RIGHTPAR")){
                print(7, currentTokenType, ")");
                getNextToken();
            } else {
                announceError(")");
            }
            
        } else {
            announceError("identifier or number or char or boolean or string or (");
        }
    }

    private void expr(){
        // print(7, "<Expr>");

    }

    private void argList(){
        // print(5, "<ArgList>");

    }

    private void statements(){
        // print(5, "<Statements>");

    }

    private void funCall(){
        // print(3, "<FunCall>");

    }
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