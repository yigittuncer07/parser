import java.util.List;

public class Parser {
    private List<String> tokens;
    private int currentToken;

    public void parse(String input) {
        tokens = tokenize(input);
        currentToken = 0;

        program();
    }

    private List<String> tokenize(String input) {
        // Split the input into tokens (assuming space-separated tokens)
        return List.of(input.trim().split("\\s+"));
    }

    private String getNextToken() {
        if (currentToken < tokens.size()) {
            return tokens.get(currentToken++);
        }
        return null;
    }

    private void program() {
        topLevelForm();
        if (getNextToken() != null) {
            program();
        }
    }

    private void topLevelForm() {
        String token = getNextToken();
        if (token != null && token.equals("(")) {
            secondLevelForm();
            token = getNextToken();
            if (token == null || !token.equals(")")) {
                throw new RuntimeException("Missing closing parenthesis.");
            }
        } else {
            throw new RuntimeException("Invalid top-level form.");
        }
    }

    private void secondLevelForm() {
        String token = getNextToken();
        if (token != null && token.equals("DEFINE")) {
            definition();
        } else if (token != null && token.equals("(")) {
            funCall();
        } else {
            throw new RuntimeException("Invalid second-level form.");
        }
    }

    private void definition() {
        definitionRight();
    }

    private void definitionRight() {
        String token = getNextToken();
        if (token != null && token.matches("[a-zA-Z]+")) {
            expression();
        } else if (token != null && token.equals("(")) {
            token = getNextToken();
            if (token != null && token.matches("[a-zA-Z]+")) {
                argList();
                token = getNextToken();
                if (token != null && token.equals(")")) {
                    statements();
                } else {
                    throw new RuntimeException("Missing closing parenthesis.");
                }
            } else {
                throw new RuntimeException("Invalid definition right.");
            }
        } else {
            throw new RuntimeException("Invalid definition right.");
        }
    }

    private void argList() {
        String token = getNextToken();
        if (token != null && token.matches("[a-zA-Z]+")) {
            argList();
        }
    }

    private void statements() {
        String token = getNextToken();
        if (token != null) {
            if (token.equals("(")) {
                expression();
            } else if (token.equals("DEFINE")) {
                definition();
                statements();
            } else {
                throw new RuntimeException("Invalid statement.");
            }
        }
    }

    private void expression() {
        String token = getNextToken();
        if (token != null && token.matches("[a-zA-Z]+")) {
            // IDENTIFIER
        } else if (token != null && token.matches("\\d+")) {
            // NUMBER
        } else if (token != null && token.matches("'[a-zA-Z]'")) {
            // CHAR
        } else if (token != null && (token.equals("#t") || token.equals("#f"))) {
            // BOOLEAN
        } else if (token != null && token.startsWith("\"") && token.endsWith("\"")) {
            // STRING
        } else if (token != null && token.equals("(")) {
            expr();
            token = getNextToken();
            if (token == null || !token.equals(")")) {
                throw new RuntimeException("Missing closing parenthesis.");
            }
        } else {
            throw new RuntimeException("Invalid expression.");
        }
    }

    private void expr() {
        String token = getNextToken();
        if (token != null && token.equals("LET")) {
            letExpression();
        } else if (token != null && token.equals("COND")) {
            condExpression();
        } else if (token != null && token.equals("IF")) {
            ifExpression();
        } else if (token != null && token.equals("BEGIN")) {
            beginExpression();
        } else {
            funCall();
        }
    }

    private void funCall() {
        String token = getNextToken();
        if (token != null && token.matches("[a-zA-Z]+")) {
            expressions();
        } else {
            throw new RuntimeException("Invalid function call.");
        }
    }

    private void expressions() {
        String token = getNextToken();
        if (token != null) {
            if (!token.equals(")")) {
                expression();
                expressions();
            } else {
                currentToken--;
            }
        }
    }

    private void letExpression() {
        String token = getNextToken();
        if (token != null && token.equals("(")) {
            varDefs();
            token = getNextToken();
            if (token != null && token.equals(")")) {
                statements();
            } else {
                throw new RuntimeException("Missing closing parenthesis.");
            }
        } else if (token != null && token.matches("[a-zA-Z]+")) {
            token = getNextToken();
            if (token != null && token.equals("(")) {
                varDefs();
                token = getNextToken();
                if (token != null && token.equals(")")) {
                    statements();
                } else {
                    throw new RuntimeException("Missing closing parenthesis.");
                }
            } else {
                throw new RuntimeException("Invalid let expression.");
            }
        } else {
            throw new RuntimeException("Invalid let expression.");
        }
    }

    private void varDefs() {
        String token = getNextToken();
        if (token != null && token.matches("[a-zA-Z]+")) {
            token = getNextToken();
            if (token != null && token.equals("(")) {
                expression();
                token = getNextToken();
                if (token != null && token.equals(")")) {
                    varDef();
                } else {
                    throw new RuntimeException("Missing closing parenthesis.");
                }
            } else {
                throw new RuntimeException("Invalid variable definition.");
            }
        }
    }

    private void varDef() {
        String token = getNextToken();
        if (token != null) {
            if (token.equals("(")) {
                varDefs();
            } else {
                currentToken--;
            }
        }
    }

    private void condExpression() {
        String token = getNextToken();
        if (token != null && token.equals("(")) {
            condBranches();
        } else {
            throw new RuntimeException("Invalid cond expression.");
        }
    }

    private void condBranches() {
        String token = getNextToken();
        if (token != null && token.equals("(")) {
            expression();
            token = getNextToken();
            if (token != null && token.equals(")")) {
                statements();
                condBranches();
            } else {
                throw new RuntimeException("Missing closing parenthesis.");
            }
        } else {
            currentToken--;
        }
    }

    private void ifExpression() {
        expression();
        expression();
        endExpression();
    }

    private void endExpression() {
        String token = getNextToken();
        if (token != null) {
            if (token.equals(")")) {
                currentToken--;
            } else {
                expression();
            }
        }
    }

    private void beginExpression() {
        statements();
    }
}
