/**
 * @author kanovas
 * 09.01.16.
 */


public class Terminal extends Symbol {

    static final char delimiter = '"';

    Terminal(String name) {
        super(name);
    }
    Terminal(char c) {
        super(Character.toString(c));
    }

    @Override
    void print() {
        System.out.print(" " + delimiter + name + delimiter + " ");
    }
}
