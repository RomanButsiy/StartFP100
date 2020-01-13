package libraries;

public class I7000 {

    private boolean useCRC;

    public I7000(boolean useCRC) {
        this.useCRC = useCRC;
    }

    public String getCRC(char[] str) {
        int crc = 0;
        for (char ch : str) crc += ch;
        String reStr = String.format("%02X", crc);
        return reStr.substring(reStr.length() - 2);
    }

    public String filter(String str) {
        return  str + (useCRC ? getCRC(str.toCharArray()) + "\r" : "\r");
    }

    public void enabledCRC(boolean enabledCRC) {
        this.useCRC = enabledCRC;
    }

    public String setModuleName(String[] newData) {
        String str = "~" + newData[0] + "O" + newData[1];
        return  filter(str);
    }
}
