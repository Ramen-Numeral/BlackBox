package game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CRTTerminalPanel extends JPanel {

    private BufferedImage noiseImage;
    private Random random = new Random();

    public CRTTerminalPanel() {
        setOpaque(true);
        createNoiseImage();
    }

    private void createNoiseImage() {
        int width = 800;
        int height = 600;
        noiseImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (random.nextInt(100) < 4) { // 4% noise
                    int gray = 50 + random.nextInt(50);
                    int rgb = (gray << 16) | (gray << 8) | gray | (0x10 << 24); // low alpha
                    noiseImage.setRGB(x, y, rgb);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // subtle noise overlay
        if (noiseImage != null) {
            g.drawImage(noiseImage, 0, 0, null);
        }

        // horizontal scanlines
        g.setColor(new Color(0, 255, 0, 30)); // very faint green
        for (int y = 0; y < getHeight(); y += 4) {
            g.drawLine(0, y, getWidth(), y);
        }
    }
}
