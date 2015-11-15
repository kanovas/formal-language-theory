/**
 * @author kanovas
 * 15.11.15.
 */

import java.io.*;
import java.util.StringTokenizer;

public class ParserGenerator {

    static class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner(File f) {
            try {
                br = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        boolean hasNext() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    if (br.ready()) {
                        st = new StringTokenizer(br.readLine());
                    } else {
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.hasMoreTokens();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }

    private static FastScanner in;

    public static void main(String[] args) {
        in = new FastScanner(new File("grammar.in"));
    }

}
