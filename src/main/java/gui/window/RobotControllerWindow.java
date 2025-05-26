package gui.window;

import gui.GameVisualizer;
import gui.RobotMovement;
import gui.states.Saveable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class RobotControllerWindow extends JInternalFrame implements Saveable {
    private final RobotMovement robotMovement;
    private final GameVisualizer visualizer;

    public RobotControllerWindow(RobotMovement robotMovement, GameVisualizer visualizer) {
        super("Управление роботом", true, true, true, true);
        this.robotMovement = robotMovement;
        this.visualizer = visualizer;

        setLayout(new BorderLayout());
        add(createControlPanel(), BorderLayout.NORTH);
        add(createSettingsPanel(), BorderLayout.CENTER);

        keyboardControl();

        setSize(RobotConstants.DEFAULT_SIZE_WIDTH, RobotConstants.DEFAULT_SIZE_HEIGHT);
        setLocation(RobotConstants.DEFAULT_POSITION_X, RobotConstants.DEFAULT_POSITION_Y);
        pack();
    }

    private static class RobotConstants {
        public static final int DEFAULT_ROBOT_INIT = 30;
        public static final int MIN_ROBOT_SIZE = 20;
        public static final int MAX_ROBOT_SIZE = 50;
        public static final int DEFAULT_MAJOR_TICK = 10;

        public static final int MIN_SPEED = 0;
        public static final int MAX_SPEED = 100;

        public static final int DEFAULT_MAX_ANGULAR_VELOCITY = 5;
        public static final int MIN_ANGULAR_VELOCITY = 0;
        public static final int MAX_ANGULAR_VELOCITY = 10;
        public static final int DEFAULT_MAJOR_TICK_ANGULAR = 1;

        public static final int DEFAULT_SIZE_WIDTH = 300;
        public static final int DEFAULT_SIZE_HEIGHT = 400;

        public static final int DEFAULT_POSITION_X = 450;
        public static final int DEFAULT_POSITION_Y = 20;

        public static final int DEFAULT_COUNT_COLUMNS = 3;
        public static final int DEFAULT_GAP = 3;

        public static final int SPEED_DIVIDER = 100;
        public static final int ANGULAR_DIVIDER = 1000;

        private static final String ACTION_MOVE_STRAIGHT = "moveStraight";
        private static final String ACTION_MOVE_BACK = "moveBack";
        private static final String ACTION_ROTATE_LEFT = "rotateLeft";
        private static final String ACTION_ROTATE_RIGHT = "rotateRight";
    }


    private JPanel createControlPanel() {
        JButton moveUpButton = new JButton("↑");
        JButton moveDownButton = new JButton("↓");
        JButton rotateLeftButton = new JButton("←");
        JButton rotateRightButton = new JButton("→");

        moveUpButton.addActionListener(e ->
                robotMovement.moveStraight(visualizer.getWidth(), visualizer.getHeight()));
        moveDownButton.addActionListener(e ->
                robotMovement.moveBack(visualizer.getWidth(), visualizer.getHeight()));
        rotateLeftButton.addActionListener(e ->
                robotMovement.rotateLeft(visualizer.getWidth(), visualizer.getHeight()));
        rotateRightButton.addActionListener(e ->
                robotMovement.rotateRight(visualizer.getWidth(), visualizer.getHeight()));
        visualizer.repaint();

        JPanel panel = new JPanel(new GridLayout(RobotConstants.DEFAULT_COUNT_COLUMNS,
                RobotConstants.DEFAULT_COUNT_COLUMNS, RobotConstants.DEFAULT_GAP, RobotConstants.DEFAULT_GAP));
        panel.setBorder(BorderFactory.createTitledBorder("Контроллер"));

        panel.add(new JLabel());
        panel.add(moveUpButton);
        panel.add(new JLabel());

        panel.add(rotateLeftButton);
        panel.add(new JLabel());
        panel.add(rotateRightButton);

        panel.add(new JLabel());
        panel.add(moveDownButton);
        panel.add(new JLabel());

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(RobotConstants.DEFAULT_COUNT_COLUMNS * 2,
                RobotConstants.DEFAULT_COUNT_COLUMNS / 3));
        panel.setBorder(BorderFactory.createTitledBorder("Настройки"));

        JSlider sizeSlider = createSlider(RobotConstants.MIN_ROBOT_SIZE, RobotConstants.MAX_ROBOT_SIZE,
                RobotConstants.DEFAULT_ROBOT_INIT, RobotConstants.DEFAULT_MAJOR_TICK, robotMovement::setRobotSize);
        JSlider speedSlider = createSlider(RobotConstants.MIN_SPEED, RobotConstants.MAX_SPEED,
                RobotConstants.DEFAULT_ROBOT_INIT, RobotConstants.DEFAULT_MAJOR_TICK,
                value -> robotMovement.setMaxSpeed((double) value / RobotConstants.SPEED_DIVIDER));
        JSlider angleSlider = createSlider(RobotConstants.MIN_ANGULAR_VELOCITY, RobotConstants.MAX_ANGULAR_VELOCITY,
                RobotConstants.DEFAULT_MAX_ANGULAR_VELOCITY, RobotConstants.DEFAULT_MAJOR_TICK_ANGULAR,
                value -> robotMovement.setMaxAngularVelocity((double) value / RobotConstants.ANGULAR_DIVIDER));

        panel.add(new JLabel("Размер робота"));
        panel.add(sizeSlider);
        panel.add(new JLabel("Скорость движения"));
        panel.add(speedSlider);
        panel.add(new JLabel("Угловая скорость"));
        panel.add(angleSlider);

        return panel;
    }

    private JSlider createSlider(int min, int max, int initial, int majorTick, java.util.function.IntConsumer func) {
        JSlider slider = new JSlider(min, max, initial);
        slider.setMajorTickSpacing(majorTick);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> func.accept(slider.getValue()));
        return slider;
    }

    private void keyboardControl() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0),
                RobotConstants.ACTION_MOVE_STRAIGHT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0),
                RobotConstants.ACTION_MOVE_BACK);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0),
                RobotConstants.ACTION_ROTATE_LEFT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0),
                RobotConstants.ACTION_ROTATE_RIGHT);

        actionMap.put(RobotConstants.ACTION_MOVE_STRAIGHT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                robotMovement.moveStraight(visualizer.getWidth(), visualizer.getHeight());
            }
        });
        actionMap.put(RobotConstants.ACTION_MOVE_BACK, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                robotMovement.moveBack(visualizer.getWidth(), visualizer.getHeight());
            }
        });
        actionMap.put(RobotConstants.ACTION_ROTATE_LEFT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                robotMovement.rotateLeft(visualizer.getWidth(), visualizer.getHeight());
            }
        });
        actionMap.put(RobotConstants.ACTION_ROTATE_RIGHT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                robotMovement.rotateRight(visualizer.getWidth(), visualizer.getHeight());
            }
        });
    }

    @Override
    public String getId() {
        return "Управление роботом";
    }
}
