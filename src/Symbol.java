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
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Symbol o = (Symbol) obj;
        return o.name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    String clearSpaces(String string) {
        StringBuilder in = new StringBuilder(string);
        while (in.charAt(0) == ' ') in.deleteCharAt(0);
        while (in.charAt(in.length() - 1) == ' ') in.deleteCharAt(in.length() - 1);
        return in.toString();
    }

    abstract void print();
}
