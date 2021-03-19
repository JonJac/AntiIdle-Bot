package idlebot;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoopWindowsPOC {
    public static void main(String[] args) {
/*        List<String> winNameList = getAllWindowNames();
        for (String winName : winNameList) {
            System.out.println(winName);
        }

        String windowName = "Adobe Flash Player 9";
        int[] rect;
        try {
            rect = getRect(windowName);
            System.out.printf("The corner locations for the window \"%s\" are %s",
                    windowName, Arrays.toString(rect));
        } catch (WindowNotFoundException e) {
            e.printStackTrace();
        } catch (GetWindowRectException e) {
            e.printStackTrace();
        }*/

    }

    public static int[] getRect(String windowName) throws WindowNotFoundException,
            GetWindowRectException {
        HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
        if (hwnd == null) {
            throw new WindowNotFoundException("", windowName);
        }

        int[] rect = {0, 0, 0, 0};
        int result = User32.INSTANCE.GetWindowRect(hwnd, rect);
        if (result == 0) {
            throw new GetWindowRectException(windowName);
        }
        return rect;
    }

/*    public static List<String> getAllWindowNames() {
        final List<String> windowNames = new ArrayList<String>();
        final User32 user32 = User32.INSTANCE;
        user32 .EnumWindows(new User32.WNDENUMPROC() {

            @Override
            public boolean callback(Pointer hWnd, Pointer arg) {
                byte[] windowText = new byte[512];
                user32.GetWindowTextA(hWnd, windowText, 512);
                String wText = Native.toString(windowText).trim();
                if (!wText.isEmpty()) {
                    windowNames.add(wText);
                }
                return true;
            }
        }, null);

        return windowNames;
    }*/

    @SuppressWarnings("serial")
    public static class WindowNotFoundException extends Exception {
        public WindowNotFoundException(String className, String windowName) {
            super(String.format("Window null for className: %s; windowName: %s",
                    className, windowName));
        }
    }

    @SuppressWarnings("serial")
    public static class GetWindowRectException extends Exception {
        public GetWindowRectException(String windowName) {
            super("Window Rect not found for " + windowName);
        }
    }
}
