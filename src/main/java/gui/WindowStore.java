package gui;

import java.util.ArrayList;
import java.util.List;

public class WindowStore {
    private static final List<SaveAble> windows = new ArrayList<>();

    public static void add(SaveAble window) {
        windows.add(window);
    }

    public static List<SaveAble> getWindows() {
        return windows;
    }
}
