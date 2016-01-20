/**
 * @author kanovas
 * 09.01.16.
 */

import java.util.ArrayList;
import java.util.HashMap;

public class Rule {
    ArrayList<Symbol> string;

    Rule(String input, HashMap<String, NonTerminal> preGrammar) {
        string = new ArrayList<>();
        parse(new StringBuffer(input), preGrammar);
    }

    private void parse(StringBuffer input, HashMap<String, NonTerminal> preGrammar) {
        int n, l = 0;
        while (l < input.length()) {
            while(input.charAt(l) == ' ') l++;
            if (input.charAt(l) == Terminal.delimiter) {
                l++;
                n = input.indexOf(String.valueOf(Terminal.delimiter), l);
                if (n < 0) {
                    //parse error
                }
                string.add(new Terminal(input.substring(l, n)));
            }
            else {
                n = input.indexOf(" ", l) < 0 ? input.length() : input.indexOf(" ", l);
                string.add(preGrammar.get(input.substring(l, n)));
            }
            l = n + 1;
        }
    }

    void print() {
        for (Symbol symbol : string) {
            symbol.print();
        }
    }
}
