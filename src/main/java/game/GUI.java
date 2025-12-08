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

//retroish terminal style gui to output game progress
//spawns a text out task from within to continually update the jtexarea
public class GUI {

    private JFrame frame;
    private JTextArea textArea;
    private final BlockingQueue<String> messageQueue;

    private static final int LEFT = 100, RIGHT = 150, TOP = 125, BOTTOM = 295;
    private static final int WINDOW_SIZE = 800;
    private final int CHAR_DELAY = 50;

    public GUI(BlockingQueue<String> messageQueue) {
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(WINDOW_SIZE, WINDOW_SIZE);
        frame.setLocationRelativeTo(null); // center
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        this.messageQueue = messageQueue;

        // ---------------- LAYERED PANE ----------------
        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));

        // Black background
        JPanel blackBg = new JPanel();
        blackBg.setBackground(Color.BLACK);
        blackBg.setBounds(0, 0, WINDOW_SIZE, WINDOW_SIZE);
        layered.add(blackBg, Integer.valueOf(0));

        // CRT Terminal panel
        int terminalWidth = WINDOW_SIZE - LEFT - RIGHT;
        int terminalHeight = WINDOW_SIZE - TOP - BOTTOM;
        CRTPanel crt = new CRTPanel();
        crt.setLayout(new BorderLayout());
        crt.setBounds(LEFT, TOP, terminalWidth, terminalHeight);

        // Text Area
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setForeground(new Color(50, 180, 120, 190));
        textArea.setMargin(new Insets(50, 30, 60, 40));

        // Custom font
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

        // PNG overlay
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

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        toolbar.setBackground(new Color(0,0,0,180));
        toolbar.setBounds(0,0,WINDOW_SIZE,30);
        JButton closeBtn = new JButton("x");
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.addActionListener(e -> System.exit(0));
        toolbar.add(closeBtn);
        layered.add(toolbar, Integer.valueOf(3));

        // Close on outside click
        layered.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!crt.getBounds().contains(e.getPoint())) System.exit(0);
            }
        });

        frame.setContentPane(layered);

        // Blink caret
        BlinkCaret caret = new BlinkCaret(500);
        textArea.setCaret(caret);
        textArea.setCaretColor(new Color(50, 200, 150));

        frame.setVisible(true);

        // ---------------- TEXT OUTPUT TASK ----------------
        new Thread(new TextOutTask(messageQueue, textArea, CHAR_DELAY)).start();

    }

    // ================= Caret =================
    //stylized leading caret
    static class BlinkCaret extends DefaultCaret {
        private boolean visible = true;
        private final int thickness = 6;
        private final Timer blinkTimer;

        public BlinkCaret(int blinkMs) {
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

    // ================= CRTPanel =================
    //panel to hold jtext area and paint it w scanlines etc
    static class CRTPanel extends JPanel {
        private static final int SCANLINE_SPACING = 3;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(), h = getHeight();
            g.setColor(new Color(0,0,0));
            g.fillRect(0,0,w,h);
            g.setColor(new Color(0,50,0,30));
            g.fillRect(0,0,w,h);
            g.setColor(new Color(0,255,0,15));
            for (int y=0;y<h;y+=SCANLINE_SPACING) g.drawLine(0,y,w,y);
            for (int i=0;i<w*h/300;i++){
                int x=(int)(Math.random()*w), y=(int)(Math.random()*h);
                g.setColor(new Color(100,100,100,20));
                g.fillRect(x,y,1,1);
            }
        }
    }
}
