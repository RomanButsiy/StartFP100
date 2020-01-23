package base.legacy;

import base.helpers.OSUtils;

public class PApplet {

    static public int platform;

    static {
        if (OSUtils.isWindows()) {
            platform = PConstants.WINDOWS;
        } else if (OSUtils.isLinux()) {
            platform = PConstants.LINUX;
        } else {
            platform = PConstants.OTHER;
        }
    }

    static public String[] subset(String[] list, int start, int count) {
        String[] output = new String[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }

    static public String join(String[] str, char separator) {
        return join(str, String.valueOf(separator));
    }

    static public String join(String[] str, String separator) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            if (i != 0) buffer.append(separator);
            buffer.append(str[i]);
        }
        return buffer.toString();
    }

    static public String[] split(String what, char delimiter) {
        if (what == null)
            return null;
        char[] chars = what.toCharArray();
        int splitCount = 0;
        for (char aChar : chars) {
            if (aChar == delimiter)
                splitCount++;
        }
        if (splitCount == 0) {
            String[] splits = new String[1];
            splits[0] = what;
            return splits;
        }
        String[] splits = new String[splitCount + 1];
        int splitIndex = 0;
        int startIndex = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == delimiter) {
                splits[splitIndex++] = new String(chars, startIndex, i - startIndex);
                startIndex = i + 1;
            }
        }
        splits[splitIndex] = new String(chars, startIndex, chars.length - startIndex);
        return splits;
    }
    
}
