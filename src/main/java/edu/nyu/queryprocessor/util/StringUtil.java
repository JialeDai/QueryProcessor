package edu.nyu.queryprocessor.util;

import org.apache.commons.lang.StringUtils;

public class StringUtil {
    public static boolean isEnglish(String word) {
        if (StringUtils.isEmpty(word)) {
            return false;
        }

        for (int i = 0; i < word.length(); i++) {
            if (!(word.charAt(i) >= 'A' && word.charAt(i) <= 'Z') && !(word.charAt(i) >= 'a' && word.charAt(i) <= 'z')) {
                return false;
            }
        }
        return true;
    }
}
