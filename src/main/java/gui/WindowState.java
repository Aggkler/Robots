package gui;

import java.io.Serializable;

class WindowState implements Serializable {
    String title;
    int x, y, width, height;
    boolean isIcon, isMaximum;
}