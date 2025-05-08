package gui.states;

import java.util.ArrayList;
import java.util.List;

public class WindowStore {
    private static final List<Saveable> windows = new ArrayList<>();

    public static void add(Saveable window) {
        windows.add(window);
    }

    public static List<Saveable> getWindows() {
        return windows;
    }
}
