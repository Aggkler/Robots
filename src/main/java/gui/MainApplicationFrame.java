package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
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

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JOptionPane;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        loadWindowState();

        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                checkExit();
            }
        });
    }

    private WindowState saveWindow(JInternalFrame frame) {
        return new WindowState(
                frame.getTitle(),
                frame.getX(),
                frame.getY(),
                frame.getWidth(),
                frame.getHeight(),
                frame.isIcon(),
                frame.isMaximum());
    }

    private void loadWindowState() {
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

    private void writeWindowState() {
        List<WindowState> states = new ArrayList<>();
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            states.add(saveWindow(frame));
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

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void checkExit() {
        Object[] options = {"Выйти", "Остаться"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Подтвердите выход из приложения",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private JMenu generateLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);
        return lookAndFeelMenu;
    }

    private JMenu generateTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_B);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        return testMenu;
    }

    private JMenu generateExitMenu() {
        JMenu exitMenu = new JMenu("Управление окном");
        exitMenu.setMnemonic(KeyEvent.VK_Q);
        exitMenu.getAccessibleContext().setAccessibleDescription(
                "Закрыть");

        JMenuItem leaveMenuBar = new JMenuItem("Закрыть окно", KeyEvent.VK_L);
        leaveMenuBar.addActionListener((event) -> {
            writeWindowState();
            checkExit();
        });
        exitMenu.add(leaveMenuBar);
        return exitMenu;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(generateLookAndFeelMenu());
        menuBar.add(generateTestMenu());
        menuBar.add(generateExitMenu());
        return menuBar;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
}
