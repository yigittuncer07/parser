import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Lexer {

    private static String keywords[] = { "define", "let", "cond", "if", "begin" };
    private static ArrayList<String> outputStrings = new ArrayList<>();
    private static ArrayList<Token> tokens = new ArrayList<>();
    private static String currentLine;
    private static char character;
    private static int start = 0;
    private static int i = 0;
    private static int j = 0;

    public void lex(File file) throws FileNotFoundException {

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                currentLine = scanner.nextLine();

                for (i = 0; i < currentLine.length(); i++) {
                    character = currentLine.charAt(i);

                    if (character == '~') {// This is a comment so the rest of the line is skipped
                        i = currentLine.length() - 1;

                    } else if (character == '(') {
                        addToken("LEFTPAR", i);

                    } else if (character == ')') {
                        addToken("RIGHTPAR", i);

                    } else if (character == '[') {
                        addToken("LEFTSQUAREB", i);

                    } else if (character == ']') {
                        addToken("RIGHTSQUAREB", i);

                    } else if (character == '{') {
                        addToken("LEFTCURLYB", i);

                    } else if (character == '}') {
                        addToken("RIGHTCURLYB", i);

                    } else if (character == '"') {// This part reads until it finds another ", otherwise anounces an
                                                  // error.
                        start = i;// This is to remember where the string starts

                        if (i == currentLine.length() - 1) {// " cannot be at the end of the line
                            announceError("\"", i, false);
                            return;
                        }
                        do {// This loops until it finds a " or EOL

                            int prev = i;
                            i++;
                            character = currentLine.charAt(i);

                            // This part checks if the " has a / behind it
                            if (character == '\"') {
                                if (currentLine.charAt(prev) == '\\') {
                                    if (!(i == currentLine.length() - 1)) {
                                        i++;
                                        character = currentLine.charAt(i);
                                    } else {
                                        announceError(currentLine.substring(start, i + 1), start, false);
                                        return;
                                    }
                                }
                            }

                        } while (!((i == currentLine.length() - 1) || character == '"'));

                        if (character == '"') {// If the last char read isnt a ", anounce error
                            addToken("STRING", start);
                        } else {
                            announceError(currentLine.substring(start, i + 1), start, false);
                            return;
                        }
                    } else if (character == '\'') {
                        start = i;

                        if (i == currentLine.length() - 1) { // " cannot be at the end of the line
                            announceError("\'", i, false);
                            return;
                        } else if (i == currentLine.length() - 2) {
                            announceError(currentLine.substring(i, i + 2), i, false);
                            return;
                        }

                        i++;
                        character = currentLine.charAt(i);

                        if (character == '\\') {
                            if (i == currentLine.length() - 2) {
                                announceError("\'", i, true);
                                return;
                            }
                            i++;
                            character = currentLine.charAt(i);
                            if (character != '\'') {
                                announceError("\'", i, true);
                                return;
                            }
                            i++;
                            character = currentLine.charAt(i);
                            if (character != '\'') {
                                announceError("\'", i, true);
                                return;
                            }
                            addToken("CHAR", start);

                        } else {
                            i++;
                            character = currentLine.charAt(i);

                            if (character == '\'') {
                                addToken("CHAR", start);

                            } else {
                                announceError(currentLine.substring(start, i), start, false);
                                return;
                            }
                        }
                    } else if (isStartOfIdentifier(character)) {
                        start = i;
                        if (currentLine.length() - 1 == i) {// This prevents error from single characters
                            addToken("IDENTIFIER", start);
                        } else {
                            do {

                                i++;
                                character = currentLine.charAt(i);

                            } while (isRestOfIdentifier(character) && !(i == currentLine.length() - 1));

                            if (isSeperator(character)) {
                                i--;
                                String identifierValue = currentLine.substring(start, i + 1);
                                if (isKeyword(identifierValue)) {
                                    addToken(identifierValue.toUpperCase(), start);// This will add the keyword
                                } else if (isBoolean(identifierValue)){
                                    addToken("BOOLEAN",start);
                                } else {
                                    addToken("IDENTIFIER", start);
                                }

                            } else if (i == currentLine.length() - 1 && isRestOfIdentifier(character)) {
                                String identifierValue = currentLine.substring(start, i + 1);
                                if (isKeyword(identifierValue)) {
                                    addToken(identifierValue.toUpperCase(), start);// This will add the keyword
                                } else {
                                    addToken("IDENTIFIER", start);
                                }
                            } else {
                                announceError(currentLine.substring(start, i + 1), start, false);
                                return;
                            }
                        }
                    } else if (character == '0') {
                        start = i;
                        int temporary = i;
                        if (i == currentLine.length() - 1) {
                            addToken("NUMBER", start);
                        } else if (isSeperator(currentLine.charAt(++temporary))) {
                            addToken("NUMBER", start);
                        } else {

                            i++;
                            character = currentLine.charAt(i);
                            if (character == 'x') {// For hexadecimal numbers
                                if (i == currentLine.length() - 1) {// No such thing as 0x
                                    announceError("0", start, true);
                                    return;
                                }
                                do {

                                    i++;
                                    character = currentLine.charAt(i);

                                } while (isRestOfHex(character) && !(i == currentLine.length() - 1));

                                if (isSeperator(character)) {
                                    i--;
                                    addToken("NUMBER", start);
                                } else if (i == currentLine.length() - 1 && isRestOfHex(character)) {
                                    addToken("NUMBER", start);
                                } else {
                                    announceError(currentLine.substring(start, i + 1), start, false);
                                    return;

                                }

                            } else if (character == 'b') {
                                if (i == currentLine.length() - 1) {// No such thing as 0x
                                    announceError("0", start, true);
                                    return;
                                }
                                do {

                                    i++;
                                    character = currentLine.charAt(i);

                                } while (isBin(character) && !(i == currentLine.length() - 1));

                                if (isSeperator(character)) {
                                    i--;
                                    addToken("NUMBER", start);
                                } else if (i == currentLine.length() - 1 && isBin(character)) {
                                    addToken("NUMBER", start);
                                } else {
                                    announceError(currentLine.substring(start, i + 1), start, false);
                                    return;

                                }
                            } else if (character == '.') {
                                if (i == currentLine.length() - 1) {// No such thing as 0.
                                    announceError(".", start, true);
                                    return;
                                }
                                do {

                                    i++;
                                    character = currentLine.charAt(i);

                                } while (isDecimal(character) && !(i == currentLine.length() - 1));

                                if (character != 'e' && character != 'E') {
                                    if (isSeperator(character)) {
                                        i--;
                                        addToken("NUMBER", start);
                                    } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                        addToken("NUMBER", start);
                                    } else {
                                        announceError(currentLine.substring(start, i + 1), start, false);
                                        return;
                                    }
                                } else {
                                    if (currentLine.length() - 1 == i) {
                                        announceError("e", start, true);
                                        return;
                                    }
                                    i++;
                                    character = currentLine.charAt(i);
                                    if (character == '-' || character == '+') {
                                        if (i != currentLine.length() - 1) {
                                            i++;
                                            character = currentLine.charAt(i);
                                        } else {
                                            announceError("-", start, true);
                                            return;
                                        }

                                    }

                                    if (!isDecimal(character)) {
                                        announceError("x", start, true);
                                        return;
                                    }

                                    while (isDecimal(character) && !(i == currentLine.length() - 1)) {
                                        i++;
                                        character = currentLine.charAt(i);
                                    }

                                    if (isSeperator(character)) {
                                        i--;
                                        addToken("NUMBER", start);
                                    } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                        addToken("NUMBER", start);
                                    } else {
                                        announceError(currentLine.substring(start, i + 1), start, false);
                                        return;

                                    }
                                }
                            } else if (character == 'e' || character == 'E') {
                                if (currentLine.length() - 1 == i) {
                                    announceError("e", start, true);
                                    return;
                                }
                                i++;
                                character = currentLine.charAt(i);
                                if (character == '-' || character == '+') {
                                    if (i != currentLine.length() - 1) {
                                        i++;
                                        character = currentLine.charAt(i);
                                    } else {
                                        announceError("-", start, true);
                                        return;
                                    }

                                }

                                if (!isDecimal(character)) {
                                    announceError("x", start, true);
                                    return;
                                }

                                while (isDecimal(character) && !(i == currentLine.length() - 1)) {
                                    i++;
                                    character = currentLine.charAt(i);
                                }

                                if (isSeperator(character)) {
                                    i--;
                                    addToken("NUMBER", start);
                                } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                    addToken("NUMBER", start);
                                } else {
                                    announceError(currentLine.substring(start, i + 1), start, false);
                                    return;

                                }
                            } else if (isDecimal(character)) {

                                while (isDecimal(character) && !(i == currentLine.length() - 1)) {
                                    i++;
                                    character = currentLine.charAt(i);
                                }

                                if (isSeperator(character)) {
                                    i--;
                                    addToken("NUMBER", start);
                                } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                    addToken("NUMBER", start);
                                } else {
                                    announceError(currentLine.substring(start, i + 1), start, false);
                                    return;

                                }
                            } else {
                                announceError("0", start, true);
                                return;
                            }
                        }
                    } else if (character == '-' || character == '+') {
                        start = i;

                        if (i == currentLine.length() - 1) {
                            addToken("IDENTIFIER", start);
                        } else if (isSeperator(currentLine.charAt(++i))) {
                            i--;
                            addToken("IDENTIFIER", start);
                        } else {
                            i--;
                            character = currentLine.charAt(++i);
                            while (isDecimal(character) && !(i == currentLine.length() - 1)) {
                                i++;
                                character = currentLine.charAt(i);
                            }

                            if (character != 'e' && character != 'E' && character != '.') {
                                if (isSeperator(character)) {
                                    i--;
                                    addToken("NUMBER", start);
                                } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                    addToken("NUMBER", start);
                                } else {
                                    announceError(currentLine.substring(start, i + 1), start, false);
                                    return;

                                }
                            } else if (character == 'e' || character == 'E') {
                                if (currentLine.length() - 1 == i) {
                                    announceError("e", start, true);
                                    return;
                                }
                                i++;
                                character = currentLine.charAt(i);
                                if (character == '-' || character == '+') {
                                    if (i != currentLine.length() - 1) {
                                        i++;
                                        character = currentLine.charAt(i);
                                    } else {
                                        announceError("-", start, true);
                                        return;
                                    }

                                }

                                if (!isDecimal(character)) {
                                    announceError("x", start, true);
                                    return;
                                }

                                while (isDecimal(character) && !(i == currentLine.length() - 1)) {
                                    i++;
                                    character = currentLine.charAt(i);
                                }

                                if (isSeperator(character)) {
                                    i--;
                                    addToken("NUMBER", start);
                                } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                    addToken("NUMBER", start);
                                } else {
                                    announceError(currentLine.substring(start, i + 1), start, false);
                                    return;

                                }
                            } else if (character == '.') {
                                if (i == currentLine.length() - 1) {// No such thing as 0.
                                    announceError(".", start, true);
                                    return;
                                }
                                do {

                                    i++;
                                    character = currentLine.charAt(i);

                                } while (isDecimal(character) && !(i == currentLine.length() - 1));

                                if (character != 'e' && character != 'E') {
                                    if (isSeperator(character)) {
                                        i--;
                                        addToken("NUMBER", start);
                                    } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                        addToken("NUMBER", start);
                                    } else {
                                        announceError(currentLine.substring(start, i + 1), start, false);
                                        return;
                                    }
                                } else {
                                    if (currentLine.length() - 1 == i) {
                                        announceError("e", start, true);
                                        return;
                                    }
                                    i++;
                                    character = currentLine.charAt(i);
                                    if (character == '-' || character == '+') {
                                        if (i != currentLine.length() - 1) {
                                            i++;
                                            character = currentLine.charAt(i);
                                        } else {
                                            announceError("-", start, true);
                                            return;
                                        }

                                    }

                                    if (!isDecimal(character)) {
                                        announceError("x", start, true);
                                        return;
                                    }

                                    while (isDecimal(character) && !(i == currentLine.length() - 1)) {
                                        i++;
                                        character = currentLine.charAt(i);
                                    }

                                    if (isSeperator(character)) {
                                        i--;
                                        addToken("NUMBER", start);
                                    } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                        addToken("NUMBER", start);
                                    } else {
                                        announceError(currentLine.substring(start, i + 1), start, false);
                                        return;

                                    }
                                }
                            }
                        }

                    } else if (isDecimal(character)) {
                        start = i;

                        while (isDecimal(character) && !(i == currentLine.length() - 1)) {
                            i++;
                            character = currentLine.charAt(i);
                        }

                        if (character != 'e' && character != 'E' && character != '.') {

                            if (isSeperator(character)) {
                                i--;
                                addToken("NUMBER", start);
                            } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                addToken("NUMBER", start);
                            } else {
                                announceError(currentLine.substring(start, i + 1), start, false);
                                return;

                            }
                        } else if (character == 'e' || character == 'E') {
                            if (currentLine.length() - 1 == i) {
                                announceError("e", start, true);
                                return;
                            }
                            i++;
                            character = currentLine.charAt(i);
                            if (character == '-' || character == '+') {
                                if (i != currentLine.length() - 1) {
                                    i++;
                                    character = currentLine.charAt(i);
                                } else {
                                    announceError("-", start, true);
                                    return;
                                }

                            }

                            if (!isDecimal(character)) {
                                announceError("x", start, true);
                                return;
                            }

                            while (isDecimal(character) && !(i == currentLine.length() - 1)) {
                                i++;
                                character = currentLine.charAt(i);
                            }

                            if (isSeperator(character)) {
                                i--;
                                addToken("NUMBER", start);
                            } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                addToken("NUMBER", start);
                            } else {
                                announceError(currentLine.substring(start, i + 1), start, false);
                                return;

                            }
                        } else if (character == '.') {
                            if (i == currentLine.length() - 1) {// No such thing as 0.
                                announceError(".", start, true);
                                return;
                            }
                            do {

                                i++;
                                character = currentLine.charAt(i);

                            } while (isDecimal(character) && !(i == currentLine.length() - 1));

                            if (character != 'e' && character != 'E') {
                                if (isSeperator(character)) {
                                    i--;
                                    addToken("NUMBER", start);
                                } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                    addToken("NUMBER", start);
                                } else {
                                    announceError(currentLine.substring(start, i + 1), start, false);
                                    return;
                                }
                            } else {
                                if (currentLine.length() - 1 == i) {
                                    announceError("e", start, true);
                                    return;
                                }
                                i++;
                                character = currentLine.charAt(i);
                                if (character == '-' || character == '+') {
                                    if (i != currentLine.length() - 1) {
                                        i++;
                                        character = currentLine.charAt(i);
                                    } else {
                                        announceError("-", start, true);
                                        return;
                                    }

                                }

                                if (!isDecimal(character)) {
                                    announceError("x", start, true);
                                    return;
                                }

                                while (isDecimal(character) && !(i == currentLine.length() - 1)) {
                                    i++;
                                    character = currentLine.charAt(i);
                                }

                                if (isSeperator(character)) {
                                    i--;
                                    addToken("NUMBER", start);
                                } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                    addToken("NUMBER", start);
                                } else {
                                    announceError(currentLine.substring(start, i + 1), start, false);
                                    return;

                                }
                            }
                        }
                    } else if (character == '.') {
                        start = i;
                        if (i == currentLine.length() - 1) {
                            addToken("IDENTIFIER", start);
                        } else if (isSeperator(currentLine.charAt(++i))) {
                            i--;
                            addToken("IDENTIFIER", start);
                        } else {
                            i--;
                            do {
                                i++;
                                character = currentLine.charAt(i);

                            } while (isDecimal(character) && !(i == currentLine.length() - 1));

                            if (character != 'e' && character != 'E') {
                                if (isSeperator(character)) {
                                    i--;
                                    addToken("NUMBER", start);
                                } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                    addToken("NUMBER", start);
                                } else {
                                    announceError(currentLine.substring(start, i + 1), start, false);
                                    return;
                                }
                            } else {
                                if (currentLine.length() - 1 == i) {
                                    announceError("e", start, true);
                                    return;
                                }
                                i++;
                                character = currentLine.charAt(i);
                                if (character == '-' || character == '+') {
                                    if (i != currentLine.length() - 1) {
                                        i++;
                                        character = currentLine.charAt(i);
                                    } else {
                                        announceError("-", start, true);
                                        return;
                                    }

                                }

                                if (!isDecimal(character)) {
                                    announceError("x", start, true);
                                    return;
                                }

                                while (isDecimal(character) && !(i == currentLine.length() - 1)) {
                                    i++;
                                    character = currentLine.charAt(i);
                                }

                                if (isSeperator(character)) {
                                    i--;
                                    addToken("NUMBER", start);
                                } else if (i == currentLine.length() - 1 && isDecimal(character)) {
                                    addToken("NUMBER", start);
                                } else {
                                    announceError(currentLine.substring(start, i + 1), start, false);
                                    return;

                                }
                            }
                        }

                    } else if (!isSeperator(character)) {
                        String error;
                        error = Character.toString(character);
                        announceError(error, i, false);
                        return;
                    }
                }

                j++;
            }
        }

        printArrayListToFile();
    }

    public Token[] getTokens() {
        Token tokensArray[] = new Token[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            tokensArray[i] = tokens.get(i);
        }
        return tokensArray;
    }

    private static boolean isBoolean(String word) {
        if (word.equals("true") || word.equals("false")) {
            return true;
        }
        return false;

    }

    private static boolean isKeyword(String word) {
        for (String keyword : keywords) {
            if (word.equals(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRestOfHex(char c) {
        int ascii = (int) c;
        if (isDecimal(c)) {
            return true;
        } else if ((ascii >= 97) && (ascii <= 102)) {
            return true;

        } else if ((ascii >= 65) && (ascii <= 70)) {
            return true;

        } else {
            return false;
        }
    }

    private static boolean isDecimal(char c) {
        int ascii = (int) c;
        if ((ascii >= 48) && (ascii <= 57)) {
            return true;
        }
        return false;
    }

    private static boolean isBin(char c) {
        if (c == '1' || c == '0') {
            return true;
        }
        return false;
    }

    private static boolean isStartOfIdentifier(char c) {
        if (c == ' ') {
            return false;
        }
        if ((c == '!') || (c == '*') || (c == '/') || (c == ':') || (c == '<') || (c == '=') || (c == '>')
                || (c == '?')) {
            return true;
        } else if (isLetter(c)) {
            return true;
        }
        return false;
    }

    private static boolean isRestOfIdentifier(char c) {
        int i = (int) c;

        if (c == ' ') {
            return false;
        }
        if (isLetter(c)) {
            return true;
        } else if ((i >= 48) && (i <= 57)) {
            return true;
        } else if ((c == '.') || (c == '+') || (c == '-')) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isLetter(char c) {
        int i = (int) c;
        if ((i >= 97) && (i <= 122)) {
            return true;
        }
        return false;
    }

    private static boolean isSeperator(char c) {
        if ((c == '\'') || (c == '\"') || (c == '(') || (c == ')') || (c == '[') || (c == ']') || (c == '{')
                || (c == '}') || (c == ' ') || (c == '~') || (c == '\t')) {
            return true;
        } else {
            return false;
        }

    }

    private static void addToken(String temp, int index) {
        Token token = new Token();
        int arr[] = { j, index };
        token.setLocation(arr);
        token.setTokenType(temp);
        token.setValue(currentLine.substring(index, i + 1));
        tokens.add(token);
        outputStrings.add(token.getString());
    }

    private static void printArrayListToFile() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("output.txt");
        for (String str : outputStrings) {
            writer.println(str);
        }
        writer.close();
    }

    private static void printArrayListToTerminal() {
        for (String str : outputStrings) {
            System.out.println(str);
        }
    }

    private static void announceError(String lex, int index, boolean state) throws FileNotFoundException {
        int tempI = index;
        if (lex.length() != 1 || state == true) {
            while ((tempI < currentLine.length() - 1) && (currentLine.charAt(tempI) != ' ')) {
                tempI++;
            }
        }
        if (currentLine.charAt(tempI) == ' ') {
            tempI--;
        }
        lex = currentLine.substring(index, tempI + 1);

        PrintWriter writer = new PrintWriter("output.txt");
        for (String str : outputStrings) {
            writer.println(str);
        }
        printArrayListToTerminal();
        writer.println("LEXICAL ERROR [" + (j + 1) + ":" + (index + 1) + "]: Invalid token `" + lex + "`");
        System.out.println("LEXICAL ERROR [" + (j + 1) + ":" + (index + 1) + "]: Invalid token `" + lex + "`");
        writer.close();
        System.exit(0);
    }

}