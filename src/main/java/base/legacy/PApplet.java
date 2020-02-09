package base.legacy;

import base.BaseInit;
import base.helpers.OSUtils;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.zip.GZIPOutputStream;

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

    /**
     * Integer number formatter.
     */
    static private NumberFormat int_nf;
    static private int int_nf_digits;
    static private boolean int_nf_commas;

    static public String[] nf(int[] num, int digits) {
        String[] formatted = new String[num.length];
        for (int i = 0; i < formatted.length; i++) {
            formatted[i] = nf(num[i], digits);
        }
        return formatted;
    }

    static public String nf(int num, int digits) {
        if ((int_nf != null) &&
                (int_nf_digits == digits) &&
                !int_nf_commas) {
            return int_nf.format(num);
        }

        int_nf = NumberFormat.getInstance();
        int_nf.setGroupingUsed(false); // no commas
        int_nf_commas = false;
        int_nf.setMinimumIntegerDigits(digits);
        int_nf_digits = digits;
        return int_nf.format(num);
    }

    static public String[] str(int x[]) {
        String[] s = new String[x.length];
        for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
        return s;
    }

    static public int[] parseInt(String[] what) {
        return parseInt(what, 0);
    }

    static public int[] parseInt(String[] what, int missing) {
        int[] output = new int[what.length];
        for (int i = 0; i < what.length; i++) {
            try {
                output[i] = Integer.parseInt(what[i]);
            } catch (NumberFormatException e) {
                output[i] = missing;
            }
        }
        return output;
    }

    static public PrintWriter createWriter(OutputStream output) {
        OutputStreamWriter osw = new OutputStreamWriter(output, StandardCharsets.UTF_8);
        return new PrintWriter(osw);
    }

    static public PrintWriter createWriter(File file) throws IOException {
        return createWriter(file, false);
    }

    static public PrintWriter createWriter(File file, boolean append) throws IOException {
        createPath(file);
        OutputStream output = new FileOutputStream(file, append);
        try {
            if (file.getName().toLowerCase().endsWith(".gz")) {
                output = new GZIPOutputStream(output);
            }
        } catch (IOException e) {
            output.close();
            throw e;
        }
        return createWriter(output);
    }

    static public void createPath(String path) {
        createPath(new File(path));
    }

    static public void createPath(File file) {
        try {
            String parent = file.getParent();
            if (parent != null) {
                File unit = new File(parent);
                if (!file.exists()) unit.mkdirs();
            }
        } catch (SecurityException se) {
            BaseInit.showWarning(null, "У вас немає дозволів на створення " + file.getAbsolutePath(), se);
        }
    }

    static public String[] loadStrings(InputStream input) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

            String[] lines = new String[100];
            int lineCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (lineCount == lines.length) {
                    String[] temp = new String[lineCount << 1];
                    System.arraycopy(lines, 0, temp, 0, lineCount);
                    lines = temp;
                }
                lines[lineCount++] = line;
            }

            if (lineCount == lines.length) {
                return lines;
            }

            // resize array to appropriate amount for these lines
            String[] output = new String[lineCount];
            System.arraycopy(lines, 0, output, 0, lineCount);
            return output;

        } catch (IOException e) {
            BaseInit.showWarning(null, "Помилка всередині loadStrings ()", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return null;
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
