package idlebot;

public class XyTriple {
    public int x, y, color;

    public XyTriple(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public String toString() {
        return "XyTriple{" +
                "x=" + x +
                ", y=" + y +
                ", color=" + color +
                '}';
    }
}
