package game;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class FalloutCaret extends DefaultCaret {

    private boolean visible = true;
    private final int thickness = 6;
    private final int blinkMs;

    public FalloutCaret(int blinkMs) {
        this.blinkMs = blinkMs;
        setBlinkRate(0);

        Timer blinkTimer = new Timer(blinkMs, e -> {
            visible = !visible;
            JTextComponent comp = getComponent();
            if (comp != null) comp.repaint();
        });

        blinkTimer.start();
    }

    @Override
    public void paint(Graphics g) {
        if (!visible) return;

        JTextComponent comp = getComponent();
        if (comp == null) return;

        try {
            Rectangle r = comp.modelToView(getDot());
            if (r == null) return;

            g.setColor(new Color(0, 255, 0, 255));
            g.fillRect(r.x + 2, r.y, thickness, r.height);

        } catch (Exception ignored) {}
    }
}
