import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NQueensSolverVisualizer extends JPanel {
    private int[] queens;
    private int n;
    private static final int CELL_SIZE = 150;
    private static final int DELAY = 255; // Delay in milliseconds
    private List<int[]> solutions = new ArrayList<>();

    public NQueensSolverVisualizer(int n) {
        this.n = n;
        queens = new int[n];
        for (int i = 0; i < n; i++) {
            queens[i] = -1;
        }
        setPreferredSize(new Dimension(n * CELL_SIZE, n * CELL_SIZE));
    }

    public void findAllSolutions() {
        solve(0);
    }

    private boolean solve(int row) {
        if (row == n) {
            int[] solution = queens.clone();
            solutions.add(solution);
            repaintAndSleep();
            return true;
        }
        boolean found = false;
        for (int col = 0; col < n; col++) {
            if (isSafe(row, col)) {
                queens[row] = col;
                repaintAndSleep();
                if (solve(row + 1)) {
                    found = true;
                }
                queens[row] = -1; // Reset the row when backtracking
                repaintAndSleep();
            }
        }
        return found;
    }

    private boolean isSafe(int row, int col) {
        for (int i = 0; i < row; i++) {
            if (queens[i] == col || Math.abs(queens[i] - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }

    private void repaintAndSleep() {
        repaint();
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            e.printStackTrace();
        }
    }

    private void printAllSolutions() {
        for (int[] solution : solutions) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (solution[i] == j) {
                        System.out.print("Q ");
                    } else {
                        System.out.print(". ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
        System.out.println("Total solutions: " + solutions.size());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                Color color1 = (row + col) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY;
                Color color2 = (row + col) % 2 == 0 ? Color.WHITE : Color.BLACK;
                GradientPaint gp = new GradientPaint(col * CELL_SIZE, row * CELL_SIZE, color1, (col + 1) * CELL_SIZE, (row + 1) * CELL_SIZE, color2);
                g2d.setPaint(gp);
                g2d.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                if (queens[row] == col) {
                    drawCrown(g2d, col * CELL_SIZE, row * CELL_SIZE);
                }
            }
        }
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(0, 0, n * CELL_SIZE, n * CELL_SIZE);
    }

    private void drawCrown(Graphics2D g2d, int x, int y) {
        int[] xPoints = { x + CELL_SIZE / 4, x + CELL_SIZE / 2, x + 3 * CELL_SIZE / 4, x + CELL_SIZE / 2 };
        int[] yPoints = { y + CELL_SIZE / 2, y + CELL_SIZE / 4, y + CELL_SIZE / 2, y + 3 * CELL_SIZE / 4 };
        g2d.setColor(Color.YELLOW);
        g2d.fillPolygon(xPoints, yPoints, xPoints.length);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(xPoints, yPoints, xPoints.length);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String input = JOptionPane.showInputDialog("Enter the size of the board (n):");
            int n;
            try {
                n = Integer.parseInt(input);
                if (n <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a positive integer.");
                return;
            }

            NQueensSolverVisualizer solverVisualizer = new NQueensSolverVisualizer(n);
            JFrame frame = new JFrame("N-Queens Solver");
            frame.add(solverVisualizer);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            new Thread(() -> {
                solverVisualizer.findAllSolutions();
                solverVisualizer.printAllSolutions();
            }).start();
        });
    }
}
