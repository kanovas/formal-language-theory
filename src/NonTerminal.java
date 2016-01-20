/**
 * @author kanovas
 * 09.01.16.
 */

import java.util.ArrayList;

public class NonTerminal extends Symbol {

    ArrayList<Rule> rules;

    NonTerminal(String name) {
        super(name);
        rules = new ArrayList<Rule>();
    }

    void addRule(Rule rule) {
        rules.add(rule);
    }

    void print() {
        System.out.print(name);
    }

    void printAll() {
        for (Rule rule : rules) {
            print();
            System.out.print(": ");
            rule.print();
            System.out.println();
        }
    }
}
