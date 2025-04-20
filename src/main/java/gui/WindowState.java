package gui;

import java.io.Serializable;

public class WindowState implements Serializable {
    private final String id;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int extendedState;
    private final boolean maximized;
    private final boolean minimized;

    public WindowState(String id,
                       int x,
                       int y,
                       int width,
                       int height,
                       int extendedState,
                       boolean maximized,
                       boolean minimized) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.extendedState = extendedState;
        this.maximized = maximized;
        this.minimized = minimized;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getExtendedState() {
        return extendedState;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public boolean isMinimized() {
        return minimized;
    }
}