package libraries;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
        if (str == null) return null;
        return str.substring(startIndex, str.length() - (useCRC ? 3 : 1));
    }

    public static String removeCRC(int startIndex, StringBuffer str) {
        return str.substring(startIndex, str.length() - (useCRC ? 3 : 1));
    }

    public static String formatTypeTechnicalUnits(String data) {
        return formatTypeTechnicalUnits((float) Integer.parseInt(data));
    }

    public static String formatTypeTechnicalUnits(float data) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("00.000", symbols);
        return decimalFormat.format(data);
    }

    public static String setAnalogOutTechnicalUnits(String id, float result) {
        String str = "#" + id + formatTypeTechnicalUnits(result);
        return filter(str);
    }

    public static String setAnalogInTechnicalUnits(String idModule) {
        String str = "#" + idModule;
        return filter(str);
    }

    public static String setAnalogInTechnicalUnitsSynchronized(String idModule) {
        String str = "$" + idModule + "4";
        return filter(str);
    }

    public static String getSynchronizedSampling() {
        return filter("#**");
    }
}
