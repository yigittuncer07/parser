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

    public static void main(String[] args) {

        try {

            Scanner scanner = new Scanner(new File(args[0]));
            while (scanner.hasNextLine()) {
                currentLine = scanner.nextLine();

                for (i = 0; i < currentLine.length(); i++) {
                    asciiCode = (int) currentLine.charAt(i);
                    character = currentLine.charAt(i);

                    if (character == '~') {
                        // System.out.println("Skipped this line");
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

                    } else if (character == '"') {
                        start = i;

                        if (i == currentLine.length() - 1) {
                            System.out.println("ERROR: " + j + " " + i);// Delete This
                            announceError("\"");
                            return;
                        }
                        do {
                            i++;
                            character = currentLine.charAt(i);
                            
                        } while (!((i == currentLine.length() - 1) || character == '"'));

                        if (character == '"') {
                            addToken("STRING", start);
                        } else {
                            announceError(currentLine.substring(start, i + 1));
                            return;
                        }
                    } else if (character == ')') {

                    }
                }
                j++;
            }

            printArrayList();

        } catch (Exception e) {
            System.out.println("INPUT FILE NOT FOUND");
        }

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
