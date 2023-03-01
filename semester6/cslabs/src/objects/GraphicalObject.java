package objects;

import utils.Vector;

import java.awt.*;
import java.io.*;

public abstract class GraphicalObject {
    protected int x, y;
    protected int width, height;
    protected Color color;

    private boolean isMoving = true;
    private boolean isShowOutline = false;

    public GraphicalObject(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void stop() {
        isMoving = false;
    }

    public void resume() {
        isMoving = true;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void showOutline() {
        isShowOutline = true;
    }

    public void hideOutline() {
        isShowOutline = false;
    }

    public void draw(Graphics g) {
        if (isShowOutline) {
            g.setColor(Color.GREEN);
            g.drawRect(x - width / 2 - 1, y - height / 2 - 1, width + 2, height + 2);
        }
    }

    public boolean contains(int x, int y) {
        return (x >= this.x - width / 2 && x <= this.x + width / 2 && y >= this.y - height / 2 && y <= this.y + height / 2);
    }

    public void read(InputStream input) throws IOException {
        var dis = new DataInputStream(input);
        x = dis.readInt();
        y = dis.readInt();
        width = dis.readInt();
        height = dis.readInt();
        int r = dis.readInt();
        int g = dis.readInt();
        int b = dis.readInt();
        color = new Color(r, g, b);
    }

    public void write(OutputStream output) throws IOException {
        var dos = new DataOutputStream(output);
        dos.writeInt(x);
        dos.writeInt(y);
        dos.writeInt(width);
        dos.writeInt(height);
        dos.writeInt(color.getRed());
        dos.writeInt(color.getGreen());
        dos.writeInt(color.getBlue());
    }

    public abstract void move(Vector movement);
}