package gui;

import log.Logger;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();
    private int robotSize = 30;
    private final List<Point2D.Double> path = new ArrayList<>();
    private final List<String> commandQueue = new ArrayList<>();
    private final String FILE_NAME_COMMANDS = "Commands.txt";

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;


    private static double maxSpeed = 0.1;
    private static double maxAngularVelocity = 0.001;


    public GameVisualizer() {
        setDoubleBuffered(true);
        setFocusable(true);
        requestFocusInWindow();

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                loadCommandsFromFile();
                executeNextCommand();
                onRedrawEvent();
            }
        }, 0, 50);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        else if (value > max)
            return max;
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, int duration) {
        velocity = applyLimits(velocity, -maxSpeed, maxSpeed);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        int robotRadius = robotSize / 2;
        int width = getWidth();
        int height = getHeight();

        if (newX >= robotRadius && newX <= width - robotRadius &&
                newY >= robotRadius && newY <= height - robotRadius) {
            m_robotPositionX = newX;
            m_robotPositionY = newY;
            m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);

            path.add(new Point2D.Double(m_robotPositionX, m_robotPositionY));
            if (path.size() > 1000) {
                path.remove(0);
            }
            repaint();
        }
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 1; i < path.size(); i++) {
            Point2D.Double p1 = path.get(i - 1);
            Point2D.Double p2 = path.get(i);
            g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
        }

        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform oldTransform = g.getTransform();
        g.translate(x, y);
        g.rotate(direction);

        g.setColor(Color.MAGENTA);
        fillOval(g, 0, 0, robotSize, robotSize / 3);
        g.setColor(Color.BLACK);
        drawOval(g, 0, 0, robotSize, robotSize / 3);
        g.setColor(Color.WHITE);
        fillOval(g, robotSize / 2 - 5, 0, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotSize / 2 - 5, 0, 5, 5);
        g.setTransform(oldTransform);
    }

    private void loadCommandsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME_COMMANDS))) {
            commandQueue.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                commandQueue.add(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeFirstCommandFromFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME_COMMANDS));
            if (!lines.isEmpty()) {
                lines.remove(0);
                Files.write(Paths.get(FILE_NAME_COMMANDS), lines);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeNextCommand() {
        if (commandQueue.isEmpty()) {
            return;
        }

        String command = commandQueue.remove(0);
        switch (command) {
            case "MOVE_FORWARD":
                Logger.debug("Сommands from file: " + command);
                moveStraight();
                break;
            case "MOVE_BACK":
                Logger.debug("Сommands from file: " + command);
                moveBack();
                break;
            case "ROTATE_LEFT":
                Logger.debug("Сommands from file: " + command);
                rotateLeft();
                break;
            case "ROTATE_RIGHT":
                Logger.debug("Сommands from file: " + command);
                rotateRight();
                break;
            default:
                Logger.debug("Unknown command: " + command);
        }
        removeFirstCommandFromFile();
    }


    public void moveStraight() {
        moveRobot(0.1, 0, 10);
    }

    public void moveBack() {
        moveRobot(-maxSpeed / 10, 0, 10);
    }

    public void rotateLeft() {
        moveRobot(0, -0.01, 10);
    }

    public void rotateRight() {
        moveRobot(0, 0.01, 10);
    }

    public void setRobotSize(int size) {
        this.robotSize = size;
    }

    public void setMaxSpeed(double value) {
        maxSpeed = Math.max(0.01, Math.min(value, 1.0));
    }

    public void setMaxAngularVelocity(double angle) {
        maxAngularVelocity = Math.max(0.0001, Math.min(angle, 0.1));
    }
}
