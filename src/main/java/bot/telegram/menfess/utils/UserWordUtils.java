package bot.telegram.menfess.utils;

public class UserWordUtils {

    public char[] userWordListRequest(String target, String guess) {
        char[] result = new char[guess.length()];
        boolean[] used = new boolean[guess.length()];

        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == target.charAt(i)){
                result[i] = 'Y';
                used[i] = true;
            }
        }
        for (int i = 0; i < guess.length(); i++) {
            if (result[i] != 'Y') {
                for (int j = 0; j < target.length(); j++) {
                    if (!used[j] && guess.charAt(i) == target.charAt(j)) {
                        result[i] = 'B';
                        used[i] = true;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < guess.length(); i++) {
            if (result[i] == 0) {
                result[i] = 'X';
            }
        }
        return result;
    }



}
