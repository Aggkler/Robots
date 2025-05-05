package gui.states;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WindowsManager {

    private static final String CONFIG_PATH = System.getProperty("user.home") + File.separator + "window_state.bin";

    public void saveConfiguration() {
        List<WindowState> states = new ArrayList<>();
        for (SaveAble window : WindowStore.getWindows()) {
            if (window instanceof Component comp) {
                states.add(collectWindowState(comp, window.getId()));
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONFIG_PATH))) {
            oos.writeObject(states);
        } catch (IOException e) {
            Logger.error("Failed to save a window state: " + e.getMessage());
        }
    }

    public void loadConfiguration() {
        File file = new File(CONFIG_PATH);
        if (!file.exists()) {
            return;
        }

        List<WindowState> states;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object object = ois.readObject();
            if (!(object instanceof List<?> list)) return;

            states = new ArrayList<>();
            for (Object obj : list) {
                if (obj instanceof WindowState state) {
                    states.add(state);
                }
            }
        } catch (ClassNotFoundException e) {
            Logger.error("Failed to load window state: " + e.getMessage());
            return;
        } catch (IOException e) {
            Logger.error("Failed: " + e.getMessage());
            return;
        }

        for (SaveAble window : WindowStore.getWindows()) {
            if (window instanceof Component comp) {
                for (WindowState state : states) {
                    if (state.getId().equals(window.getId())) {
                        applyWindowState(comp, state);
                    }
                }
            }
        }
    }

    private WindowState collectWindowState(Component comp, String id) {
        int extendedState = 0;
        boolean maximized = false;
        boolean minimized = false;

        if (comp instanceof JFrame jFrame) {
            extendedState = jFrame.getExtendedState();
        }

        if (comp instanceof JInternalFrame jInternalFrame) {
            maximized = jInternalFrame.isMaximum();
            minimized = jInternalFrame.isIcon();
        }

        return new WindowState(
                id,
                comp.getX(),
                comp.getY(),
                comp.getWidth(),
                comp.getHeight(),
                extendedState,
                maximized,
                minimized
        );
    }

    private void applyWindowState(Component comp, WindowState state) {
        comp.setBounds(state.getX(), state.getY(), state.getWidth(), state.getHeight());

        if (comp instanceof JFrame jFrame) {
            jFrame.setExtendedState(state.getExtendedState());
        }

        if (comp instanceof JInternalFrame jInternalFrame) {
            try {
                jInternalFrame.setMaximum(state.isMaximized());
                if (state.isMaximized()) {
                    comp.setBounds(0, 0, 400, 400);
                }
                jInternalFrame.setIcon(state.isMinimized());
            } catch (PropertyVetoException e) {
                Logger.error("Failed apply a window state: " + e.getMessage());
            }
        }
    }
}
