package com.wutian.utils;

public class ReplaceSpecialCharUtils {
    public static String replaceSpecialChar(String line) {
        line = checkAndReplaceSpecialChar(line, "\u2028", " ","2028"); //行分隔符	行结束符
        line = checkAndReplaceSpecialChar(line, "\u2029", " ", "2029"); //段落分隔符	行结束符
        line = checkAndReplaceSpecialChar(line, "\uFEFF", " ", "FEFF");//字节顺序标记	空白
        line = checkAndReplaceSpecialChar(line, "\u00A0", " ", "00A0");
//                line = checkAndReplaceSpecialChar(line, "%", "%");
        line = checkAndReplaceSpecialChar2(line); // '-------> \'

        return line;
    }

    private static  String checkAndReplaceSpecialChar(String line, String replaceTarget, String replaceStr, String tag) {
        while (line.indexOf(replaceTarget) != -1) {
            line = line.replace(replaceTarget, replaceStr);
        }
        return line;
    }

    // '  ------------------>   \'
    private static String checkAndReplaceSpecialChar2(String line) {
        boolean hasChanged = false;
        if (line.indexOf("'") != -1) {
            char[] chars = line.toCharArray();
            char lastChar = '1';
            StringBuffer sb = new StringBuffer();
            for (char aChar : chars) {
                if ('\'' == aChar) {
                    if ('\\' != lastChar) {
                        sb.append("\\");
                        hasChanged = true;
                    }
                }
                sb.append(aChar);
                lastChar = aChar;
            }
            if (hasChanged) {
                line = sb.toString();
                sb.setLength(0);
            }
        }
        return line;
    }
}
