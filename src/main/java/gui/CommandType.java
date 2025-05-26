package gui;

public enum CommandType {
    MOVE_FORWARD {
        @Override
        public void execute(RobotMovement movement, GameVisualizer visualizer) {
            movement.moveStraight(visualizer.getWidth(), visualizer.getHeight());
        }
    },
    MOVE_BACK {
        @Override
        public void execute(RobotMovement movement, GameVisualizer visualizer) {
            movement.moveBack(visualizer.getWidth(), visualizer.getHeight());
        }
    },
    ROTATE_LEFT {
        @Override
        public void execute(RobotMovement movement, GameVisualizer visualizer) {
            movement.rotateLeft(visualizer.getWidth(), visualizer.getHeight());
        }
    },
    ROTATE_RIGHT {
        @Override
        public void execute(RobotMovement movement, GameVisualizer visualizer) {
            movement.rotateRight(visualizer.getWidth(), visualizer.getHeight());
        }
    };

    public static CommandType fromString(String str) {
        try {
            return CommandType.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void execute(RobotMovement robotMovement, GameVisualizer gameVisualizer) {
    }
}
