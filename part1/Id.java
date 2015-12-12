package part1;

public class Id {

    public static boolean scan(String s) {

        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            
            final char ch = s.charAt(i++);
            
            switch(state) {
                case 0:
                    if (ch == '_') {   
                        state = 1;
                    } else if (Character.isLetter(ch)) {
                        state = 2;
                    } else {
                        state = -1;
                    }
                    break;
                case 1:
                    if (ch == '_') {
                        state = 1;
                    } else if (Character.isLetterOrDigit(ch)) {
                        state = 2;
                    }
                    else {
                        state = -1;
                    }
                    break;
                case 2:
                    if (ch == '_' || Character.isLetterOrDigit(ch)) {
                        state = 2;
                    } else {
                        state = -1;
                    }
                    break;
            }    
        }
        return state == 2;     
    }
    
    public static void main(String[] args) {
        String[] identificatori = {"aaa","_aa","_1a","___","1aa","__A","ok+"};
        for (String s : identificatori) {
            System.out.println(Id.scan(s) +"   "+s);
        }
    }
}
