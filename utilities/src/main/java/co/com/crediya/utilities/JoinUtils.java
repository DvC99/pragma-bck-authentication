package co.com.crediya.utilities;

import java.util.LinkedList;

public class JoinUtils {
    public static String join(LinkedList<String> source) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < source.size(); ++i) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(source.get(i));
        }

        return result.toString();
    }
}