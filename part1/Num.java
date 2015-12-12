package part1;

public class Num {

    public static boolean scan(String s) {

        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if (ch == ' ') {
                        state = 0;
                    } else if (Character.isDigit(ch)) {
                        state = 2;
                    } else if (ch == '+' || ch == '-') {
                        state = 1;
                    } else if (ch == '.') {
                        state = 4;
                    } else {
                        state = -1;
                    }
                    break;
                case 1:
                    if (Character.isDigit(ch)) {
                        state = 2;
                    } else {
                        state = -1;
                    }
                    break;
                case 2:
                    if (Character.isDigit(ch)) {
                        state = 2;
                    } else if (ch == ' ') {
                        state = 3;
                    } else if (ch == '.') {
                        state = 4;
                    } else if (ch == 'e') {
                        state = 5;
                    } else {
                        state = -1;
                    }
                    break;
                case 3:
                    if (ch == ' ') {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;
                case 4:
                    if (Character.isDigit(ch)) {
                        state = 6;
                    } else {
                        state = -1;
                    }
                    break;
                case 5:
                    if (Character.isDigit(ch)) {
                        state = 8;
                    } else if (ch == '+') {
                        state = 7;
                    } else if (ch == '-') {
                        state = 9;
                    } else {
                        state = -1;
                    }
                    break;
                case 6:
                    if (Character.isDigit(ch)) {
                        state = 6;
                    } else if (ch == ' ') {
                        state = 3;
                    } else if (ch == 'e') {
                        state = 5;
                    } else {
                        state = -1;
                    }
                    break;
                case 7:
                    if (Character.isDigit(ch)) {
                        state = 8;
                    } else {
                        state = -1;
                    }
                    break;
                case 8:
                    if (Character.isDigit(ch)) {
                        state = 8;
                    } else if (ch == ' ') {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;
                case 9:
                    if (Character.isDigit(ch) && Character.getNumericValue(ch) != 0) {
                        state = 8;
                    } else {
                        state = -1;
                    }
                    break;
            }
        }
        return state == 2 || state == 3 || state == 6 || state == 8;
    }

    public static void main(String[] args) {
        String[] identificatori = {"123", "123.5", ".567", "+7.5", "67e10", "1e-2", "-.7e2",
            "2e-0", "2e+0"};
        for (String s : identificatori) {
            System.out.println(Num.scan(s) + "   " + s);
        }
    }
}
