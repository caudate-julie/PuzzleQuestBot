package main;

/**
 * Token at which position (x - horizontal or column, y  - vertical or row) and to which direction is moved
 */
public class Move {
    int x;
    int y;
    Direction direction;

    public Move(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
}
