/**
 * @author kanovas
 * 09.01.16.
 */

import java.io.*;
import java.util.StringTokenizer;

public abstract class Symbol {
    String name;
    Symbol(String name) {
        this.name = name;
    } //TODO: clear spaces
    abstract void print();
}
