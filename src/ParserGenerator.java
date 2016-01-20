/**
 * @author kanovas
 * 15.11.15.
 */

import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class ParserGenerator {

    private static Scanner in;
    private static Set<NonTerminal> grammar;

    public static void main(String[] args) {

        try {
            in = new Scanner(new File("grammar.in"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, NonTerminal> preGrammar = new HashMap<>(); //assoc string <--> non-terminal
        StringBuffer currentLine;
        String nonTerminalString;
        NonTerminal nonTerminal;
        ArrayList<Pair<String, String>> rules = new ArrayList<>(); //nonTerminalString <--> 1 rule string

        while (in.hasNext()) {
            int i = 0;
            currentLine = new StringBuffer(in.nextLine());
            nonTerminalString = currentLine.substring(i, currentLine.indexOf(":="));
            i += currentLine.indexOf(":=") + 2;
            if (!preGrammar.containsKey(nonTerminalString)) {
                nonTerminal = new NonTerminal(nonTerminalString);
                preGrammar.put(nonTerminalString, nonTerminal);
            }
            while (i < currentLine.length()) {
                int nextDelimiter = currentLine.indexOf(" | ", i) < 0 ? currentLine.length() : currentLine.indexOf(" | ", i);
                rules.add(new Pair<>(nonTerminalString, currentLine.substring(i, nextDelimiter)));
                i += nextDelimiter - i + 3;
            }
        }

        //connecting rules with non-terminals
        for (Pair<String, String> rule : rules) {
            nonTerminal = preGrammar.get(rule.getKey());
            nonTerminal.addRule(new Rule(rule.getValue(), preGrammar));
        }

        grammar = new HashSet<>();
        grammar.addAll(preGrammar.values());

        for (NonTerminal n : grammar) {
            n.printAll();
        }
    }

}
