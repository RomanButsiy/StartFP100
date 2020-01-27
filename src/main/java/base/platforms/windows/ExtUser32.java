package base.platforms.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface ExtUser32 extends StdCallLibrary, com.sun.jna.platform.win32.User32 {

    ExtUser32 INSTANCE = (ExtUser32) Native.loadLibrary("user32", ExtUser32.class, W32APIOptions.DEFAULT_OPTIONS);

    public int GetDpiForSystem();

    public int SetProcessDpiAwareness(int value);

    public final int DPI_AWARENESS_INVALID = -1;
    public final int DPI_AWARENESS_UNAWARE = 0;
    public final int DPI_AWARENESS_SYSTEM_AWARE = 1;
    public final int DPI_AWARENESS_PER_MONITOR_AWARE = 2;

    public Pointer SetThreadDpiAwarenessContext(Pointer dpiContext);

    public final Pointer DPI_AWARENESS_CONTEXT_UNAWARE = new Pointer(-1);
    public final Pointer DPI_AWARENESS_CONTEXT_SYSTEM_AWARE = new Pointer(-2);
    public final Pointer DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE = new Pointer(-3);

}
