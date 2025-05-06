package gui.window;

import gui.GameVisualizer;
import gui.states.SaveAble;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RobotControllerWindow extends JInternalFrame implements SaveAble {
    private final GameVisualizer visualizer;

    public RobotControllerWindow(GameVisualizer visualizer) {
        super("Управление роботом", true, true, true, true);
        this.visualizer = visualizer;

        setLayout(new BorderLayout());
        add(createControlPanel(), BorderLayout.NORTH);
        add(createSettingsPanel(), BorderLayout.CENTER);

        keyboardControl();

        setSize(300, 400);
        setLocation(450, 20);
        pack();
    }

    private JPanel createControlPanel() {
        JButton moveUpButton = new JButton("↑");
        JButton moveDownButton = new JButton("↓");
        JButton rotateLeftButton = new JButton("←");
        JButton rotateRightButton = new JButton("→");

        moveUpButton.addActionListener(e -> visualizer.moveStraight());
        moveDownButton.addActionListener(e -> visualizer.moveBack());
        rotateLeftButton.addActionListener(e -> visualizer.rotateLeft());
        rotateRightButton.addActionListener(e -> visualizer.rotateRight());

        JPanel panel = new JPanel(new GridLayout(3, 3, 5, 5));
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
        JPanel panel = new JPanel(new GridLayout(6, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Настройки"));

        JSlider sizeSlider = createSizeSlider();
        JSlider speedSlider = createSpeedSlider();
        JSlider angleSlider = createAngleSlider();

        panel.add(new JLabel("Размер робота"));
        panel.add(sizeSlider);
        panel.add(new JLabel("Скорость движения"));
        panel.add(speedSlider);
        panel.add(new JLabel("Угловая скорость"));
        panel.add(angleSlider);

        return panel;
    }

    private JSlider createSizeSlider() {
        JSlider slider = new JSlider(20, 50, 30);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> visualizer.setRobotSize(slider.getValue()));
        return slider;
    }

    private JSlider createSpeedSlider() {
        JSlider slider = new JSlider(0, 100, 10);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> visualizer.setMaxSpeed(slider.getValue() / 100.0));
        return slider;
    }

    private JSlider createAngleSlider() {
        JSlider slider = new JSlider(0, 100, 1);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> visualizer.setMaxAngularVelocity(slider.getValue() / 1000.0));
        return slider;
    }

    private void keyboardControl() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("W"), "moveStraight");
        inputMap.put(KeyStroke.getKeyStroke("S"), "moveBack");
        inputMap.put(KeyStroke.getKeyStroke("A"), "rotateLeft");
        inputMap.put(KeyStroke.getKeyStroke("D"), "rotateRight");

        actionMap.put("moveStraight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizer.moveStraight();
            }
        });
        actionMap.put("moveBack", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizer.moveBack();
            }
        });
        actionMap.put("rotateLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizer.rotateLeft();
            }
        });
        actionMap.put("rotateRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizer.rotateRight();
            }
        });
    }

    @Override
    public String getId() {
        return "Управление роботом";
    }
}
