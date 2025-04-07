package gui;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class WindowStateController {
    private final JDesktopPane desktopPane;

    WindowStateController(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
    }

    private WindowState createWindowState(JInternalFrame frame) {
        return new WindowState(
                frame.getTitle(),
                frame.getX(),
                frame.getY(),
                frame.getWidth(),
                frame.getHeight(),
                frame.isIcon(),
                frame.isMaximum());
    }

    public void loadWindowState() {
        try {
            FileInputStream fileInputStream = new FileInputStream("save.bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            Object deserialized = objectInputStream.readObject();
            if (!(deserialized instanceof List<?> currentList)) {
                return;
            }

            for (Object element : currentList) {
                if (!(element instanceof WindowState)) {
                    return;
                }
            }

            for (JInternalFrame frame : desktopPane.getAllFrames()) {
                for (Object element : currentList) {
                    Class<?> windowState = element.getClass();
                    String title = (String) windowState.getMethod("title").invoke(element);
                    if (frame.getTitle().equals(title)) {
                        int x = (int) windowState.getMethod("x").invoke(element);
                        int y = (int) windowState.getMethod("y").invoke(element);
                        int width = (int) windowState.getMethod("width").invoke(element);
                        int height = (int) windowState.getMethod("height").invoke(element);
                        boolean isIcon = (boolean) windowState.getMethod("isIcon").invoke(element);
                        boolean isMaximum = (boolean) windowState.getMethod("isMaximum").invoke(element);
                        frame.setMaximum(isMaximum);
                        if (isMaximum) {
                            frame.setBounds(0, 0, 400, 400);
                        } else {
                            frame.setBounds(x, y, width, height);
                        }
                        frame.setIcon(isIcon);
                    }
                }
            }
        } catch (PropertyVetoException e) {
            throw new RuntimeException("Исключение в настройке свернутости или развернутости");
        } catch (FileNotFoundException e) {
            return;
        } catch (ClassNotFoundException |
                 IllegalAccessException |
                 InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException("Другая ошибка");
        }
    }

    public void saveWindowState() {
        List<WindowState> states = new ArrayList<>();
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            states.add(createWindowState(frame));
        }
        try {
            FileOutputStream outputStream = new FileOutputStream("save.bin");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(states);
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении");
        }
    }

}
