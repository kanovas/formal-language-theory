/**
 * @author kanovas
 * 09.01.16.
 */

import java.util.ArrayList;

public class NonTerminal extends Symbol {

    ArrayList<Rule> localRules;

    NonTerminal(String name) {
        super(name);
        localRules = new ArrayList<Rule>();
    }

    void addRule(Rule rule) {
        localRules.add(rule);
    }

    void print() {
        System.out.print(name);
    }

    void printAll() {
        for (Rule rule : localRules) {
            print();
            System.out.print(": ");
            rule.print();
            System.out.println();
        }
    }
}
