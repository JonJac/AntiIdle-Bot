package idlebot;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

interface User32 extends StdCallLibrary {
    User32 INSTANCE = (User32)Native.loadLibrary("user32", User32.class,
            W32APIOptions.DEFAULT_OPTIONS);

    boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer userData);

    int GetWindowTextA(HWND hWnd, byte[] lpString, int nMaxCount);

    HWND FindWindow(String lpClassName, String lpWindowName);

    int GetWindowRect(HWND handle, int[] rect);

    WinDef.HWND SetFocus(WinDef.HWND hWnd);

    boolean SetForegroundWindow(WinDef.HWND hWnd);
}
