import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

class Main {

    static ArrayList<String> tokens = new ArrayList<>();
    static String currentLine;
    static int asciiCode;
    static char character;
    static int state = 0;
    static int start = 0;
    static int tempS = 0;
    static int i = 0;
    static int j = 0;

    public static void main(String[] args) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(args[0]));//Initialize scanner using args

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();

            for (i = 0; i < currentLine.length(); i++) {
                asciiCode = (int) currentLine.charAt(i);
                character = currentLine.charAt(i);

                if (character == '~') {//This is a comment so the rest of the line is skipped
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

                } else if (character == '"') {//This part reads until it finds another ", otherwise anounces an error.
                    start = i;//This is to remember where the string starts

                    if (i == currentLine.length() - 1) {//" cannot be at the end of the line
                        announceError("\"");
                        return;
                    }
                    do {//This loops until it finds a " or EOL

                        int temp = i;
                        i++;
                        character = currentLine.charAt(i);

                        //This part checks if the " has a / behind it
                        if (character == '\"'){
                            if (currentLine.charAt(temp) == '\\'){
                                if (!(i == currentLine.length() - 1)){
                                    i++;
                                } else {
                                    announceError(currentLine.substring(start, i + 1));
                                    return;
                                }
                            }
                        }

                    } while (!((i == currentLine.length() - 1) || character == '"'));

                    System.out.println(character);
                    if (character == '"') {//If the last char read isnt a ", anounce error
                        addToken("STRING", start);
                    } else {
                        announceError(currentLine.substring(start, i + 1));
                        return;
                    }
                } else if (character == '\'') {

                }
            }
            j++;//Incriments which line we are on
        }

        //Prints the token if the program didnt stop due to errors.
        printArrayList();

    }

    public static void addToken(String token, int index) {
        tokens.add(token + " " + (j + 1) + ":" + (index + 1));
    }

    public static void printArrayList() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("output.txt");
        for (String str : tokens) {
            writer.println(str);
        }
        writer.close();
    }

    public static void announceError(String lex) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("output.txt");
        writer.println("LEXICAL ERROR [" + (j + 1) + ":" + (i + 1) + "]: Invalid token `" + lex + "`");
        writer.close();
    }

}
