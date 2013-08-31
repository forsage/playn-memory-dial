package memdial.core;

public class Point {
    int x;
    int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    boolean isNearTo(memdial.core.Point pOther, int r) {
        int dx = Math.abs(pOther.x - x);
        int dy = Math.abs(pOther.y - y);
        return Math.sqrt(dx * dx + dy * dy) <= r;
    }
}
