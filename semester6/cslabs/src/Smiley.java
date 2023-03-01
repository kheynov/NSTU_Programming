import java.awt.*;

public class Smiley extends GraphicalObject {
    private double angle = 0.0;

    public Smiley(int x, int y, int width, int height, Color color) {
        super(x, y, width, height, color);
    }

    @Override
    public void draw(Graphics g, int canvasWidth, int canvasHeight) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.rotate(angle, x, y);

        g.setColor(color);
        g.fillOval(x - width / 2, y - height / 2, width, height); // лицо
        g.setColor(Color.BLACK);
        g.drawOval(x - width / 2, y - height / 2, width, height); // контур лица
        g.drawOval(x - width / 3, y - height / 3, width / 6, height / 6); // левый глаз
        g.drawOval(x + width / 6, y - height / 3, width / 6, height / 6); // правый глаз
        g.drawArc(x - width / 4, y - height / 4, width / 2, height / 2, 190, 160); // улыбка

        g2d.rotate(-angle, x, y);
    }

    @Override
    public void move(Vector movement) {
        angle += 0.1;
    }
}


