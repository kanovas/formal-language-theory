/**
 * @author kanovas
 * 09.01.16.
 */

import java.util.ArrayList;
import java.util.HashMap;

public class Rule {
    ArrayList<Symbol> string;
    String in;

    Rule(String input, HashMap<String, NonTerminal> preGrammar) {
        //parse(new StringBuffer(input), preGrammar);
        in = input;
    }

    private void parse(StringBuffer input, HashMap<String, NonTerminal> preGrammar) {
        int n, l = 0;
        while (l < input.length()) {
            while(input.charAt(l) == ' ') l++;
            if (input.charAt(l) == Terminal.delimiter) {
                l++;
                n = input.indexOf(String.valueOf(Terminal.delimiter), l);
                string.add(new Terminal(input.substring(0, n)));
            }
            else {
                n = input.indexOf(" ");
                string.add(preGrammar.get(input.substring(l, n)));
            }
            l += n + 1;
        }
    }

    void print() {
        /*for (Symbol symbol : string) {
            symbol.print();
        }*/
        System.out.print(in);
    }
}
