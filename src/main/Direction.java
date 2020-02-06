package main;

public enum Direction {
    S (0, 1), W (-1, 0), N(0, -1), E(1, 0);

    public final int x;
    public final int y;

    private Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
