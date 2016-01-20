/**
 * @author kanovas
 * 09.01.16.
 */

import java.io.*;
import java.util.StringTokenizer;

public abstract class Symbol {
    String name;
    Symbol(String name) {
        this.name = clearSpaces(name);
    } //TODO: clear spaces
    String clearSpaces(String string) {
        StringBuffer in = new StringBuffer(string);
        while (in.charAt(0) == ' ') in.deleteCharAt(0);
        while (in.charAt(in.length() - 1) == ' ') in.deleteCharAt(in.length() - 1);
        return in.toString();
    }
    abstract void print();
}
