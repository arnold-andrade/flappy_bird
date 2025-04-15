import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBirdV1 extends JPanel implements ActionListener, KeyListener {
    // Game constants
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int GROUND_HEIGHT = 100;
    private static final int PIPE_WIDTH = 60;
    private static final int PIPE_GAP = 150;
    private static final int BIRD_SIZE = 30;
    private static final int GRAVITY = 1;
    private static final int FLAP_STRENGTH = -12;
    private static final int PIPE_SPEED = 4;

    // Game state
    private int birdY = HEIGHT / 2;
    private int birdVelocity = 0;
    private ArrayList<Rectangle> pipes = new ArrayList<>();
    private int score = 0;
    private boolean gameOver = false;
    private boolean started = false;
    private Timer timer;
    private Random rand = new Random();
    private int titleY = 50;
    private int titleDirection = 1;
    private int titleFloatRange = 15;
    private int titleBaseY = 50;

    public FlappyBirdV1() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.cyan);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(20, this);
        timer.start();
        resetGame();
    }

    private void resetGame() {
        birdY = HEIGHT / 2;
        birdVelocity = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        started = false;
        // Add initial pipes
        for (int i = 0; i < 3; i++) {
            addPipe(true);
        }
    }

    private void addPipe(boolean start) {
        int space = PIPE_GAP;
        int pipeHeight = 50 + rand.nextInt(HEIGHT - GROUND_HEIGHT - space - 100);
        int x = start ? WIDTH + pipes.size() * 200 : pipes.get(pipes.size() - 1).x + 200;
        pipes.add(new Rectangle(x, 0, PIPE_WIDTH, pipeHeight)); // Top pipe
        pipes.add(new Rectangle(x, pipeHeight + space, PIPE_WIDTH, HEIGHT - pipeHeight - space - GROUND_HEIGHT)); // Bottom pipe
    }

    private void paintTitle(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(Color.YELLOW);
        // Draw shadow
        g2.drawString("Flappy bird V2", 61, titleY + 6);
        g2.setColor(Color.ORANGE);
        g2.drawString("Flappy bird V2", 60, titleY + 5);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // Draw ground
        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT - GROUND_HEIGHT, WIDTH, GROUND_HEIGHT);
        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - GROUND_HEIGHT, WIDTH, 20);
        // Draw pipes
        g.setColor(Color.green.darker());
        for (Rectangle pipe : pipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }
        // Draw bird
        g.setColor(Color.red);
        g.fillOval(80, birdY, BIRD_SIZE, BIRD_SIZE);
        // Draw floating title
        paintTitle(g);
        // Draw score
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 40);
        // Draw game over
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.RED);
            g.drawString("Game Over!", 100, HEIGHT / 2 - 30);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.BLACK);
            g.drawString("Press SPACE to restart", 100, HEIGHT / 2 + 10);
        } else if (!started) {
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.BLACK);
            g.drawString("Press SPACE to start", 110, HEIGHT / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (started && !gameOver) {
            // Bird physics
            birdVelocity += GRAVITY;
            birdY += birdVelocity;
            // Move pipes
            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);
                pipe.x -= PIPE_SPEED;
            }
            // Remove pipes off screen, add new pipes
            if (!pipes.isEmpty() && pipes.get(0).x + PIPE_WIDTH < 0) {
                pipes.remove(0);
                pipes.remove(0);
                addPipe(false);
            }
            // Check collisions
            for (Rectangle pipe : pipes) {
                if (pipe.intersects(new Rectangle(80, birdY, BIRD_SIZE, BIRD_SIZE))) {
                    gameOver = true;
                }
            }
            // Check if bird hits ground or goes above screen
            if (birdY > HEIGHT - GROUND_HEIGHT - BIRD_SIZE || birdY < 0) {
                gameOver = true;
            }
            // Score
            for (int i = 0; i < pipes.size(); i += 2) {
                Rectangle pipe = pipes.get(i);
                if (pipe.x + PIPE_WIDTH < 80 && !pipe.contains(-1, -1)) {
                    score++;
                    // Mark as scored by setting impossible location
                    pipe.setLocation(-1000, pipe.y);
                }
            }
        }
        // Floating title animation
        titleY += titleDirection;
        if (titleY > titleBaseY + titleFloatRange || titleY < titleBaseY - titleFloatRange) {
            titleDirection *= -1;
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!started) {
                started = true;
                birdVelocity = FLAP_STRENGTH;
            } else if (!gameOver) {
                birdVelocity = FLAP_STRENGTH;
            } else {
                resetGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy bird V3");
        FlappyBirdV1 game = new FlappyBirdV1();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
