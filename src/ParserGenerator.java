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
    private static NonTerminal DOT;
    private static final String STARTING_SYMBOL = "S";

    private static void initGrammar() {
        HashMap<String, NonTerminal> preGrammar = new HashMap<>(); //assoc string <--> non-terminal
        StringBuffer currentLine;
        String nonTerminalString;
        NonTerminal nonTerminal;
        ArrayList<Pair<String, String>> rules = new ArrayList<>(); //nonTerminalString <--> 1 rule string

        while (in.hasNext()) {
            int i = 0;
            currentLine = new StringBuffer(in.nextLine());
            nonTerminalString = currentLine.substring(i, currentLine.indexOf(":="));
            i = currentLine.indexOf(":=") + 2;
            if (!preGrammar.containsKey(nonTerminalString)) {
                nonTerminal = new NonTerminal(nonTerminalString);
                preGrammar.put(nonTerminalString, nonTerminal);
            }
            while (i < currentLine.length()) {
                int nextDelimiter = currentLine.indexOf(" | ", i) < 0 ? currentLine.length() : currentLine.indexOf(" | ", i);
                rules.add(new Pair<>(nonTerminalString, currentLine.substring(i, nextDelimiter)));
                i = nextDelimiter + 3;
            }
        }

        //connecting rules with non-terminals
        for (Pair<String, String> rule : rules) {
            nonTerminal = preGrammar.get(rule.getKey());
            nonTerminal.addRule(new Rule(rule.getValue(), preGrammar));
        }

        grammar = new HashSet<>();
        grammar.addAll(preGrammar.values());
    }

    private static class Expression {
        int at;
        NonTerminal nonTerminal;
        ArrayList<Symbol> expression;
        Expression(NonTerminal nonTerminal, int at) {
            this.at = at;
            this.nonTerminal = nonTerminal;
        }
        Expression(NonTerminal nonTerminal, ArrayList<Symbol> expression, int at) {
            this.at = at;
            this.nonTerminal = nonTerminal;
            this.expression = expression;
        }
    }

    private static class Condition {
        HashSet<Expression> active, completed, predicted;
        Condition() {
            active = new HashSet<>();
            completed = new HashSet<>();
            predicted = new HashSet<>();
        }
    }

    static private <T> void swap(ArrayList<T> array, int i, int j) {
        T o = array.get(i);
        array.set(i, array.get(j));
        array.set(j, o);
    }


    /* Scans itemset[m-1] for .../DOT/current... and copy it to active[p] or completed[p]
    *  returns true if added something to completed
    */
    //yes it is copypasted code oh but only once
    private static void scanner(Condition[] conditions, Symbol current, int m, int p) {
        Expression tmp;

        for (Expression expr : conditions[m - 1].active) {
            int n = expr.expression.indexOf(DOT);
            if (expr.expression.get(n + 1).equals(current)) {
                tmp = expr;
                swap(tmp.expression, n, n + 1);
                if (tmp.expression.size() == n + 2) { //expr completed
                    conditions[p].completed.add(tmp);
                }
                else { //expr active
                    conditions[p].active.add(tmp);
                }
            }
        }
        for (Expression expr : conditions[m - 1].predicted) {
            int n = expr.expression.indexOf(DOT);
            if (expr.expression.get(n + 1).equals(current)) {
                tmp = expr;
                swap(tmp.expression, n, n + 1);
                if (tmp.expression.size() == n + 2) { //expr completed
                    conditions[p].completed.add(tmp);
                }
                else { //expr active
                    conditions[p].active.add(tmp);
                }
            }
        }
    }

    private static void completer(Condition[] conditions, int p) {
        int checked = 0;
        while (checked < conditions[p].completed.size()) { //написать по-другому как то а то ашипка нельзя добавлять и перебирать
                                                            // ошибка параллельности крч TODO
            checked = conditions[p].completed.size();
            for (Expression expr : conditions[p].completed) {
                scanner(conditions, expr.nonTerminal, expr.at, p);
            }
        }
    }

    private static void predictor(Condition[] conditions, int p) {
        Expression tmp;
        for (Expression expr : conditions[p].active) {     //firstly, scan active
            int pos = expr.expression.indexOf(DOT);
            Symbol next = expr.expression.get(pos + 1);
            if (next.getClass().equals(NonTerminal.class)){
                NonTerminal n = (NonTerminal) next;
                for (Rule rule : n.rules) {
                    tmp = new Expression(n, rule.string, p + 1);
                    tmp.expression.add(0, DOT);
                    conditions[p].predicted.add(tmp);
                }
            }
        }
        int checked = 0;
        while (checked < conditions[p].predicted.size()) {     //TODO см выше
            checked = conditions[p].predicted.size();
            for (Expression expr : conditions[p].predicted) {
                int pos = expr.expression.indexOf(DOT);
                Symbol next = expr.expression.get(pos + 1);
                if (next.getClass().equals(NonTerminal.class)){
                    NonTerminal n = (NonTerminal) next;
                    for (Rule rule : n.rules) {
                        tmp = new Expression(n, rule.string, p + 1);
                        tmp.expression.add(0, DOT);
                        conditions[p].predicted.add(tmp);
                    }
                }
            }
        }
    }

    private static boolean parse(String word) {
        int p = word.length() + 1;
        Condition[] conditions = new Condition[p];
        ArrayList<Symbol> expression;
        conditions[0] = new Condition();  //initialize itemSet[0], adding rules for starting symbol
        for (NonTerminal n : grammar) {
            if (n.name.equals(STARTING_SYMBOL)) {
                for (Rule rule : n.rules) {
                    expression = rule.string;
                    expression.add(0, DOT);
                    conditions[0].active.add(new Expression(n, expression, 1));
                }
            }
        }
        predictor(conditions, 0);
        for (int i = 1; i < p; i++) {
            conditions[i] = new Condition();
            char current = word.charAt(i - 1);
            scanner(conditions, new Terminal(current), i, i);
            completer(conditions, i);
            predictor(conditions, i);
        }
        for (Expression expr : conditions[p - 1].completed) {
            if (expr.nonTerminal.name.equals(STARTING_SYMBOL) && expr.at == 1) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            in = new Scanner(new File("grammar.in"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initGrammar();
        DOT = new NonTerminal("/DOT/");
        for (NonTerminal n : grammar) {
            n.printAll();
        }
        try {
            in = new Scanner(new File("word.in"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String word = in.next();
        if (parse(word)) System.out.println(word + " is a word of this formal language");
    }
}
