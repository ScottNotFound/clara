package net.scottnotfound.clara;

import net.scottnotfound.clara.interpret.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Clara {

    public static final String APP_NAME = "Clara";
    public static final String APP_VERSION = "0.0.002";
    public static final boolean isSNAPSHOT = true;

    private static Clara instance;

    private Clara(){}


    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String input = reader.readLine();
            if (input.equals("exit")) {
                shutdown(0);
            }
            Lexer lexer = new Lexer(input);
            lexer.lex();
            Parser parser = new Parser(lexer.lex());
            Expression expression = parser.parse();
            System.out.println(new PrintAST().print(expression));
            Interpreter interpreter = new Interpreter();
            interpreter.interpret(expression);
        }

    }

    public static void shutdown() {
        System.exit(0);
    }

    public static void shutdown(int status) {
        System.exit(status);
    }

    public static Clara getInstance() {
        if (instance == null) {
            instance = new Clara();
        }
        return instance;
    }
}
