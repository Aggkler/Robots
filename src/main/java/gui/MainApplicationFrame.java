package gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowStateController windowStateController;

    public MainApplicationFrame() {
        int inset = 50;
        windowStateController = new WindowStateController(desktopPane);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        createWindows();

        windowStateController.loadWindowState();

        MenuConstructor menuConstructor = new MenuConstructor(this);
        setJMenuBar(menuConstructor.createMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                checkExit();
            }
        });
    }

    private void createWindows() {
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);
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

    void checkExit() {
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
            windowStateController.saveWindowState();
            System.exit(0);
        }
    }
}
