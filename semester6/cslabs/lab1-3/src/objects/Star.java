package objects;

import org.json.JSONObject;
import utils.Vector;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Star extends GraphicalObject {
    private int numberOfVertices = 5;
    private double angle = 0.0;

    public Star(int x, int y, int width, int height, Color color) {
        super(x - width / 2, y - height / 2, width, height, color);
    }

    public Star(int x, int y, int width, int height, Color color, int numberOfVertices) {
        super(x, y, width, height, color);
        this.numberOfVertices = numberOfVertices;
    }

    @Override
    public void draw(Graphics g) {
        if (isShowOutline()) {
            g.setColor(Color.GREEN);
            g.drawRect(x, y, width, height);
        }
        var gd = (Graphics2D) g;

        var originalTransform = gd.getTransform(); // store the original transform
        var rotatedTransform = AffineTransform.getRotateInstance(angle, x + (double) width / 2, y + (double) width / 2);
        gd.transform(rotatedTransform); // apply the rotation

        int midX = x + width / 2;
        int midY = y + height / 2;
        double radius = (double) width / 2;
        int[] X = new int[numberOfVertices];
        int[] Y = new int[numberOfVertices];

        for (double current = 0.0; current < numberOfVertices; current++) {
            int i = (int) current;
            double x = midX + Math.cos(current * ((2 * Math.PI) / numberOfVertices)) * radius;
            double y = midY + Math.sin(current * ((2 * Math.PI) / numberOfVertices)) * radius;
            X[i] = (int) x;
            Y[i] = (int) y;
        }


        g.setColor(color);
        for (int i = 2; i < X.length; i++) {
            g.drawLine(X[i], Y[i], X[i - 2], Y[i - 2]);
        }
        g.drawLine(X[X.length - 1], Y[Y.length - 1], X[1], Y[1]);
        g.drawLine(X[X.length - 2], Y[Y.length - 2], X[0], Y[0]);

        gd.setTransform(originalTransform);
    }

    @Override
    public void readFromJson(String json) {
        var jsonObject = new JSONObject(json);
        x = jsonObject.getInt("x");
        y = jsonObject.getInt("y");
        width = jsonObject.getInt("width");
        height = jsonObject.getInt("height");
        color = new Color(jsonObject.getInt("r"), jsonObject.getInt("g"), jsonObject.getInt("b"));
        numberOfVertices = jsonObject.getInt("numberOfVertices");
    }

    @Override
    public String writeToJson() {
        var jsonObject = new JSONObject();
        jsonObject.put("x", x);
        jsonObject.put("y", y);
        jsonObject.put("width", width);
        jsonObject.put("height", height);
        jsonObject.put("r", color.getRed());
        jsonObject.put("g", color.getGreen());
        jsonObject.put("b", color.getBlue());
        jsonObject.put("numberOfVertices", numberOfVertices);
        return jsonObject.toString();
    }

    @Override
    public void move(Vector movement) {
        angle += 0.1;
    }

    @Override
    public String toString() {
        return "Star{" +
                "numberOfVertices=" + numberOfVertices +
                ", angle=" + angle +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", color=" + color +
                '}';
    }
}
