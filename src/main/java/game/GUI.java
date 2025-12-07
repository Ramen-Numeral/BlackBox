package game;

import game.tasks.TextOutTask;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GUI {

    private JFrame frame;
    private JTextArea textArea;
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    private String currentMessage = "";
    private int charIndex = 0;

    private static final int LEFT = 100, RIGHT = 150, TOP = 100, BOTTOM = 200;
    private static final int WINDOW_SIZE = 800;

    public GUI(BlockingQueue<String> guiQueue) {
        this(); // call the existing no-arg constructor to set up the GUI components

        // Start the TextOutTask for this queue
        TextOutTask textTask = new TextOutTask(guiQueue, textArea, 80); // 80ms per character
        new Thread(textTask).start();
    }

    public GUI() {
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(WINDOW_SIZE, WINDOW_SIZE);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        // ---------------- LAYERED PANE ----------------
        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));

        // ---------------- BLACK BACKGROUND ----------------
        JPanel blackBg = new JPanel();
        blackBg.setBackground(Color.BLACK);
        blackBg.setBounds(0, 0, WINDOW_SIZE, WINDOW_SIZE);
        layered.add(blackBg, Integer.valueOf(0));

        // ---------------- CRT TERMINAL ----------------
        int terminalWidth = WINDOW_SIZE - LEFT - RIGHT;
        int terminalHeight = WINDOW_SIZE - TOP - BOTTOM;

        CRTPanel crt = new CRTPanel();
        crt.setLayout(new BorderLayout());
        crt.setBounds(LEFT, TOP, terminalWidth, terminalHeight);

        // ---------------- TEXT AREA ----------------
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setForeground(new Color(50, 180, 120, 190)); // slightly dimmed green
        textArea.setMargin(new Insets(30, 30, 30, 30));

        // Load custom font
        try {
            InputStream is = getClass().getResourceAsStream("/pixelmix.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
            Map<TextAttribute, Object> attrs = (Map<TextAttribute,Object>) font.getAttributes();
            attrs.put(TextAttribute.TRACKING, 0.05);
            textArea.setFont(font.deriveFont(attrs));
        } catch (Exception e) {
            textArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        }

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        crt.add(scroll, BorderLayout.CENTER);
        layered.add(crt, Integer.valueOf(1));

        // ---------------- PNG OVERLAY ----------------
        try {
            InputStream is = getClass().getResourceAsStream("/pixback.png");
            BufferedImage png = ImageIO.read(is);
            JPanel overlay = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(png, 0, 0, getWidth(), getHeight(), null);
                }
            };
            overlay.setOpaque(false);
            overlay.setBounds(0, 0, WINDOW_SIZE, WINDOW_SIZE);
            layered.add(overlay, Integer.valueOf(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ---------------- TOP TOOLBAR ----------------
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        toolbar.setBackground(new Color(0, 0, 0, 180));
        toolbar.setBounds(0, 0, WINDOW_SIZE, 30);
        toolbar.setVisible(true);

        JButton closeBtn = new JButton("X");
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.addActionListener(e -> System.exit(0));
        toolbar.add(closeBtn);
        layered.add(toolbar, Integer.valueOf(3));

        // ---------------- CLOSE ON CLICK OUTSIDE ----------------
        layered.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!crt.getBounds().contains(e.getPoint())) {
                    System.exit(0);
                }
            }
        });

        frame.setContentPane(layered);

        // ---------------- CARET ----------------
        FalloutCaret caret = new FalloutCaret(500);
        textArea.setCaret(caret);
        textArea.setCaretColor(new Color(50, 200, 150));

        frame.setVisible(true);
        // ---------------- START TEXT OUTPUT TASK ----------------
        TextOutTask textTask = new TextOutTask(messageQueue, textArea, 80); // 80ms per character
        new Thread(textTask).start();



        // ---------------- DEMO MESSAGES ----------------
        new Thread(this::simulateIncomingMessages).start();
    }


    private void simulateIncomingMessages() {
        try {
            Thread.sleep(1200);
            messageQueue.put("welcom to the quick brown (TM) TERMLINK PROTOCOL");
            Thread.sleep(1500);
            messageQueue.put("> INITIALIZING SYSTEM...");
            Thread.sleep(1800);
            messageQueue.put("> LOADING AUDIO KERNEL");
            Thread.sleep(1600);
            messageQueue.put("> STANDBY...");
        } catch (Exception ignored) {}
    }

    public void queueMessage(String msg) {
        messageQueue.offer(msg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }

    // ================= Fallout-style caret =================
    static class FalloutCaret extends DefaultCaret {
        private boolean visible = true;
        private int thickness = 6;
        private Timer blinkTimer;

        public FalloutCaret(int blinkMs) {
            setBlinkRate(0);
            blinkTimer = new Timer(blinkMs, e -> {
                visible = !visible;
                JTextComponent comp = getComponent();
                if (comp != null) comp.repaint();
            });
            blinkTimer.start();
        }

        @Override
        protected synchronized void damage(Rectangle r) {
            if (r == null) return;
            x = r.x;
            y = r.y;
            width = thickness;
            height = r.height;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            if (!isVisible() || !visible) return;
            JTextComponent comp = getComponent();
            if (comp == null) return;
            try {
                Rectangle r = comp.modelToView(getDot());
                if (r == null) return;
                g.setColor(new Color(50, 200, 100));
                g.fillRect(r.x, r.y, thickness, r.height);
            } catch (BadLocationException ignored) {}
        }
    }

    // ================= CRT panel with scanlines & noise =================
    static class CRTPanel extends JPanel {
        private static final int SCANLINE_SPACING = 3;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();

            g.setColor(new Color(0, 0, 0));
            g.fillRect(0, 0, w, h);

            g.setColor(new Color(0, 50, 0, 30));
            g.fillRect(0, 0, w, h);

            g.setColor(new Color(0, 255, 0, 15));
            for (int y = 0; y < h; y += SCANLINE_SPACING) g.drawLine(0, y, w, y);

            for (int i = 0; i < w * h / 300; i++) {
                int x = (int) (Math.random() * w);
                int y = (int) (Math.random() * h);
                g.setColor(new Color(100, 100, 100, 20));
                g.fillRect(x, y, 1, 1);
            }
        }
    }
}
