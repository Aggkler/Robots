package gui;

import java.io.Serializable;

public record WindowState(
        String title,
        int x,
        int y,
        int width,
        int height,
        boolean isIcon,
        boolean isMaximum
) implements Serializable {}