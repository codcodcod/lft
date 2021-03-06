package part1;

public class Mod3 {

    public static boolean scan(String s) {

        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if (ch == '0') {
                        state = 0;
                    } else if (ch == '1') {
                        state = 1;
                    } else {
                        state = -1;
                    }
                    break;
                case 1:
                    if (ch == '0') {
                        state = 2;
                    } else if (ch == '1') {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;
                case 2:
                    if (ch == '0') {
                        state = 1;
                    } else if (ch == '1') {
                        state = 2;
                    } else {
                        state = -1;
                    }
                    break;
                case 3:
                    if (ch == '0') {
                        state = 3;
                    } else if (ch == '1') {
                        state = 1;
                    } else {
                        state = -1;
                    }
                    break;
            }
        }
        return state == 3;
    }

    public static void main(String[] args) {
        String[] identificatori = {"110", "1001", "10", "111"};
        for (String s : identificatori) {
            System.out.println(Mod3.scan(s) + "   " + s);
        }
    }
}
