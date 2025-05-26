package gui;

import log.Logger;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel {
    private final RobotMovement robotMovement;
    private final CommandReader commandReader = new CommandReader("Commands.txt");
    private final Timer m_timer = initTimer();
    private List<String> commandQueue = new ArrayList<>();


    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public GameVisualizer(RobotMovement robotMovement) {
        this.robotMovement = robotMovement;
        setDoubleBuffered(true);
        setFocusable(true);
        requestFocusInWindow();

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                commandQueue = commandReader.loadCommandsFromFile();
                executeNextCommand();
                onRedrawEvent();
            }
        }, 0, 50);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 1; i < robotMovement.getPath().size(); i++) {
            Point2D.Double p1 = robotMovement.getPath().get(i - 1);
            Point2D.Double p2 = robotMovement.getPath().get(i);
            g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
        }
        drawRobot(g2d,
                round(robotMovement.getM_robotPositionX()),
                round(robotMovement.getM_robotPositionY()),
                robotMovement.getM_robotDirection());
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform t = g.getTransform();
        g.translate(x, y);
        g.rotate(direction);

        g.setColor(Color.MAGENTA);
        fillOval(g, 0, 0, robotMovement.getRobotSize(), robotMovement.getRobotSize() / 3);
        g.setColor(Color.BLACK);
        drawOval(g, 0, 0, robotMovement.getRobotSize(), robotMovement.getRobotSize() / 3);
        g.setColor(Color.WHITE);
        fillOval(g, robotMovement.getRobotSize() / 2 - 5, 0, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotMovement.getRobotSize() / 2 - 5, 0, 5, 5);
        g.setTransform(t);
    }

    private void executeNextCommand() {
        if (commandQueue.isEmpty()) return;

        String commandStr = commandQueue.remove(0);
        CommandType command = CommandType.fromString(commandStr);
        if (command != null) {
            Logger.debug("Command: " + command);
            command.execute(robotMovement, this);
        } else {
            Logger.debug("Unknown command: " + commandStr);
        }
        commandReader.removeFirstCommandFromFile();
    }

}
