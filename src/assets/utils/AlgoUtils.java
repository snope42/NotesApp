package assets.utils;

import java.util.Set;

public class AlgoUtils {

    public static boolean containsCharFromSet(String str, Set<Character> set) {

        for (int i = 0; i < str.length(); i++)
            if (set.contains(str.charAt(i))) return true;

        return false;

    }



}
