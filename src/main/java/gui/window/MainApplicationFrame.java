
package gui.window;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;


import gui.MenuBarConstructor;
import gui.states.SaveAble;
import gui.states.WindowStore;
import gui.states.WindowsManager;
import log.Logger;

public class MainApplicationFrame extends JFrame implements SaveAble {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowsManager configurationManager = new WindowsManager();

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        WindowStore.add(this);

        createWindows();

        configurationManager.loadConfiguration();


        MenuBarConstructor menuConstructor = new MenuBarConstructor(this);
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
        Logger.debug("Создание окна для логирования");
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        Logger.debug("Создание окна для игры");
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        RobotControllerWindow controllerWindow = new RobotControllerWindow(gameWindow.getVisualizer());
        addWindow(controllerWindow);
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

        if (frame instanceof SaveAble window) {
            WindowStore.add(window);
        }
    }

    public void checkExit() {
        int choice = JOptionPane.showConfirmDialog(
                this, "Вы уверены, что хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            configurationManager.saveConfiguration();
            System.exit(0);
        }
    }

    @Override
    public String getId() {
        return "MainApplicationFrame";
    }
}
