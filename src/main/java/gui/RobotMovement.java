package gui;

import java.awt.geom.Point2D;
import java.util.LinkedList;


public class RobotMovement {
    private final LinkedList<Point2D.Double> path = new LinkedList<>();
    private final int TAIL_LENGTH = 50;
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private int robotSize = 30;
    private double maxSpeed = 0.1;
    private double maxAngularVelocity = 0.001;


    private void moveRobot(double velocity, double angularVelocity, int duration,  int panelWidth, int panelHeight) {
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

        if (newX >= robotRadius && newX <= panelWidth - robotRadius &&
                newY >= robotRadius && newY <= panelHeight - robotRadius) {
            m_robotPositionX = newX;
            m_robotPositionY = newY;
            m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);

            path.add(new Point2D.Double(m_robotPositionX, m_robotPositionY));
            if (path.size() > TAIL_LENGTH) {
                path.remove(0);
            }
        }
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        else if (value > max)
            return max;
        return value;
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

    public void moveStraight(int panelWidth, int panelHeight) {
        moveRobot(0.1, 0, 10, panelWidth, panelHeight);
    }

    public void moveBack(int panelWidth, int panelHeight) {
        moveRobot(-maxSpeed / 10, 0, 10, panelWidth, panelHeight);
    }

    public void rotateLeft(int panelWidth, int panelHeight) {
        moveRobot(0, -0.01, 10, panelWidth, panelHeight);
    }

    public void rotateRight(int panelWidth, int panelHeight) {
        moveRobot(0, 0.01, 10, panelWidth, panelHeight);
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

    public int getRobotSize() {
        return robotSize;
    }

    public LinkedList<Point2D.Double> getPath() {
        return path;
    }

    public double getM_robotPositionX() {
        return m_robotPositionX;
    }

    public double getM_robotPositionY() {
        return m_robotPositionY;
    }

    public double getM_robotDirection() {
        return m_robotDirection;
    }
}
