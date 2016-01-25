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
            this.expression = new ArrayList<>(expression);
        }
        Expression(Expression expr) {
            this.at = expr.at;
            this.nonTerminal = expr.nonTerminal;
            this.expression = new ArrayList<>(expr.expression);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Expression n = (Expression) obj;
            return n.expression.equals(expression) && n.at == at && n.nonTerminal.equals(nonTerminal);
        }

        @Override
        public int hashCode() {
            int p = 31;
            return p*p*expression.hashCode() + p*nonTerminal.hashCode() + at;
        }
    }

    private static class Condition {
        LinkedHashSet<Expression> active, completed, predicted;
        Condition() {
            active = new LinkedHashSet<>();
            completed = new LinkedHashSet<>();
            predicted = new LinkedHashSet<>();
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
                tmp = new Expression(expr);
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
                tmp = new Expression(expr);
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
        Iterator<Expression> iterator = conditions[p].completed.iterator();  //TODO: concurrent modification
        Expression expr;
        while (iterator.hasNext()) {
            expr = iterator.next();
            scanner(conditions,expr.nonTerminal, expr.at, p);
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

        int added = 1;
        LinkedList<Expression> addedExpr = new LinkedList<>();
        while (added > 0) {
            added = 0;
            Iterator iterator = conditions[p].predicted.iterator();
            Expression expr;
            while (iterator.hasNext()) {
                expr = (Expression) iterator.next();
                int pos = expr.expression.indexOf(DOT);
                Symbol next = expr.expression.get(pos + 1);
                if (next.getClass().equals(NonTerminal.class)) {
                    NonTerminal n = (NonTerminal) next;
                    for (Rule rule : n.rules) {
                        tmp = new Expression(n, rule.string, p + 1);
                        tmp.expression.add(0, DOT);
                        if (!conditions[p].predicted.contains(tmp)) {
                            addedExpr.add(tmp);
                            added++;
                        }
                    }
                }
            }
            iterator = addedExpr.iterator();
            while(iterator.hasNext()) {
                conditions[p].predicted.add((Expression)iterator.next());
                iterator.remove();
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
                    expression = new ArrayList<>(rule.string);
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
