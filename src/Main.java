import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {

        Scanner scanFileName = new Scanner(System.in);
        System.out.print("What is the file name: ");
        String fileName = scanFileName.nextLine();
        scanFileName.close();

        File file = new File(fileName);
        Lexer lexer = new Lexer();
        try {
            lexer.lex(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}