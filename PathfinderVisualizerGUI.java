import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PathfinderVisualizerGUI extends JFrame {
    private static final int ROWS = 20;
    private static final int COLS = 20;
    private static final int CELL_SIZE = 30;

    private static final Color EMPTY_COLOR = Color.WHITE;
    private static final Color OBSTACLE_COLOR = Color.BLACK;
    private static final Color START_COLOR = Color.GREEN;
    private static final Color END_COLOR = Color.RED;
    private static final Color PATH_COLOR = Color.CYAN;
    private static final Color VISITED_COLOR = Color.LIGHT_GRAY;

    private JPanel gridPanel;
    private Cell[][] grid;
    private Cell startCell = null;
    private Cell endCell = null;

    public PathfinderVisualizerGUI() {
        setTitle("Pathfinder Visualizer");
        setSize(COLS * CELL_SIZE + 50, ROWS * CELL_SIZE + 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the grid
        gridPanel = new JPanel(new GridLayout(ROWS, COLS));
        grid = new Cell[ROWS][COLS];

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Cell cell = new Cell(row, col);
                grid[row][col] = cell;
                gridPanel.add(cell);

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            if (startCell == null) {
                                startCell = cell;
                                cell.setAsStart();
                            } else if (endCell == null) {
                                endCell = cell;
                                cell.setAsEnd();
                            } else {
                                cell.toggleObstacle();
                            }
                        }
                    }
                });
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        // Add controls
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start Pathfinding");
        JButton resetButton = new JButton("Reset");

        startButton.addActionListener(e -> {
            if (startCell == null || endCell == null) {
                JOptionPane.showMessageDialog(this, "Set both start and end points.");
                return;
            }
            findPath();
        });

        resetButton.addActionListener(e -> resetGrid());

        controlPanel.add(startButton);
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void findPath() {
        // Perform BFS
        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> parentMap = new HashMap<>();
        boolean[][] visited = new boolean[ROWS][COLS];

        queue.add(startCell);
        visited[startCell.row][startCell.col] = true;

        boolean pathFound = false;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            current.setVisited();

            if (current == endCell) {
                pathFound = true;
                break;
            }

            for (int[] direction : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                int newRow = current.row + direction[0];
                int newCol = current.col + direction[1];

                if (isValidMove(newRow, newCol, visited)) {
                    Cell neighbor = grid[newRow][newCol];
                    queue.add(neighbor);
                    visited[newRow][newCol] = true;
                    parentMap.put(neighbor, current);
                }
            }

            try {
                Thread.sleep(50); // Slow down for visualization
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (pathFound) {
            drawPath(parentMap);
        } else {
            JOptionPane.showMessageDialog(this, "No path found.");
        }
    }

    private boolean isValidMove(int row, int col, boolean[][] visited) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS
                && !grid[row][col].isObstacle && !visited[row][col];
    }

    private void drawPath(Map<Cell, Cell> parentMap) {
        Cell current = endCell;

        while (current != null && current != startCell) {
            current.setPath();
            current = parentMap.get(current);
        }
    }

    private void resetGrid() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col].reset();
            }
        }
        startCell = null;
        endCell = null;
    }

    private class Cell extends JPanel {
        private final int row, col;
        private boolean isStart = false, isEnd = false, isObstacle = false, isPath = false;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
            setBackground(EMPTY_COLOR);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        public void setAsStart() {
            isStart = true;
            setBackground(START_COLOR);
        }

        public void setAsEnd() {
            isEnd = true;
            setBackground(END_COLOR);
        }

        public void toggleObstacle() {
            isObstacle = !isObstacle;
            setBackground(isObstacle ? OBSTACLE_COLOR : EMPTY_COLOR);
        }

        public void setVisited() {
            if (!isStart && !isEnd) {
                setBackground(VISITED_COLOR);
            }
        }

        public void setPath() {
            if (!isStart && !isEnd) {
                isPath = true;
                setBackground(PATH_COLOR);
            }
        }

        public void reset() {
            isStart = false;
            isEnd = false;
            isObstacle = false;
            isPath = false;
            setBackground(EMPTY_COLOR);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PathfinderVisualizerGUI frame = new PathfinderVisualizerGUI();
            frame.setVisible(true);
        });
    }
}
