package libraries;

public class I7000 {

    public static boolean useCRC = false;

    public static String getCRC(char[] str) {
        int crc = 0;
        for (char ch : str) crc += ch;
        String reStr = String.format("%02X", crc);
        return reStr.substring(reStr.length() - 2);
    }

    public static String filter(String str) {
        return  str + (useCRC ? getCRC(str.toCharArray()) + "\r" : "\r");
    }

    public static String setModuleName(String id, String newName) {
        String str = "~" + id + "O" + newName;
        return filter(str);
    }

    public static String getModuleName(String id) {
        String str = "$" + id + "M";
        return filter(str);
    }

    public static String removeCRC(int startIndex, String str) {
        return str.substring(startIndex, str.length() - (useCRC ? 3 : 1));
    }

    public static String removeCRC(int startIndex, StringBuffer str) {
        return str.substring(startIndex, str.length() - (useCRC ? 3 : 1));
    }

}
