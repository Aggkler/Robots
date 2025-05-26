package gui.window;

import gui.GameVisualizer;
import gui.RobotMovement;
import gui.states.Saveable;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame implements Saveable
{
    private final GameVisualizer m_visualizer;
    private final RobotMovement robotMovement;
    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        robotMovement = new RobotMovement();
        m_visualizer = new GameVisualizer(robotMovement);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public String getId() {
        return "GameWindow";
    }

    public GameVisualizer getVisualizer() {
        return this.m_visualizer;
    }

    public RobotMovement getRobotMovement() {
        return robotMovement;
    }
}
