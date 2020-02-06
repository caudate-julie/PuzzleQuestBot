package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Modified by Julie on 16.04.2017.
 */
public class GameLogic {
    private final int DAMAGE_COST = 10;
    private final int MANA_COST = 2;
    private final int ADD_COST = 1;
    private final int ADD_MOVE_COST = 100;
    private final int WILD_AVERAGE = 4;

    private Token[][] grid;
    private Map<String, Integer>  tradeoff;

    public GameLogic()
    {
        this.tradeoff = new HashMap<String, Integer>();
        tradeoff.put("BLUE", 0);
        tradeoff.put("RED", 0);
        tradeoff.put("GREEN", 0);
        tradeoff.put("YELLOW", 0);
        tradeoff.put("MONEY", 0);
        tradeoff.put("STAR", 0);
        tradeoff.put("damage", 0);
        tradeoff.put("keepturn", 0);
    }

    public void UpdateField(Token[][] new_field) {
        this.grid = new_field;
    }

    public Move Turn()
    {
        ArrayList<Move> moves = this.GetValidMoves();
        return moves.get(0);
    }

    private int EvaluateGrid(Token[][] assumed) {
        for (String key : this.tradeoff.keySet()) {
            this.tradeoff.put(key, 0);
        }
        // TODO: STUB!
        return 0;
    }

    private Token[][] Burn(Token[][] assumed) {
        Token[][] burnmarks = new Token[8][8];
        for (int i = 0; i < 8; i++) for (int j = 0; j < 8; j++) burnmarks[i][j] = null;

        // TODO: STUB!
        return assumed;
    }

    private void DirectionalBurn(Token[][] grid, Direction d) {
        for (int x = 0; x < 8 - d.x * 2; x++) {
            for (int y = 0; y < 8 - d.y * 2; y++) {
                int streak = 1;
                for (int i = 1; i < 5; i++) {
                    if(!this.InGrid(x + d.x * i, y + d.y * i)) { break; }
                    boolean is_match = true;
                    // pairwise comparison to avoid false colored streaks with wildcards
                    for (int j = 0; j < i; j++) {
                        if (!grid[x + d.x * j][y + d.y * j].matches(grid[x + d.x * i][y + d.y * i])) {
                            is_match = false;
                            break;
                        }
                    }
                    if (!is_match) { break; }
                    streak += 1;
                }
                if (streak < 3) { continue; }
                // TODO: wildcard in the middle of two threes
                BurnStrike(grid, x, y, d, streak);
            }
        }
    }

    private void BurnStrike(Token[][] grid, int x, int y, Direction d, int streak) {
        String key = null;
        int value = 0;
        int multiplier = 1;
        boolean burn_around = false;
        for (int i = 0; i < streak; i++) {
            x += d.x;
            y += d.y;
            if (grid[x][y].isSimple()) {
                key = grid[x][y].toString();
                value += 1;
            } else if (grid[x][y] == Token.WILDCARD) {
                multiplier *= WILD_AVERAGE;
            } else if (grid[x][y] == Token.SKULL) {
                key = "damage";
                value += 1;
            } else if (grid[x][y] == Token.SKULL_5) {
                key = "damage";
                value += 5;
                // burn around
                for (int j = -1; j < 2; j++) {
                    for (int k = -1; k < 2; k++) {
                        if (j == 0 && k == 0) { continue; }
                        // while it's a skull-5, there cannot be wildcard as a part of this streak;
                        // all the other tokens we can burn
                        this.BurnStrike(grid, x + j, y + k, d, 1);
                    }
                }
            }
            grid[x][y] = Token.BURN;

            //is_match = false;
            //break;

        }
    }

    public ArrayList<Move> GetValidMoves() {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (Direction d : Direction.values()) {
                    Move m = new Move(x, y, d);
                    if (this.MoveIsLegal(m)) {
                        moves.add(m);
                    }
                }
            }
        }
        return moves;
    }

    private Token[][] copyGrid(Token[][] grid) {
        Token[][] new_grid = new Token[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                new_grid[x][y] = grid[x][y];
            }
        }
        return new_grid;
    }

    private boolean InGrid(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

    private Token[][] AssumeMove(Token[][] assumed, Move move) {
        assumed[move.x][move.y] = this.grid[move.x + move.direction.x][move.y + move.direction.y];
        assumed[move.x + move.direction.x][move.y + move.direction.y] = this.grid[move.x][move.y];
        return assumed;
    }

    private boolean MoveIsLegal(Move move) {
        // not out of grid and item at this position creates three
        // second item will be checked at its turn - when both create threes, we get a duplicate move. It's ok.
        if (!this.InGrid(move.x + move.direction.x, move.y + move.direction.y)) { return false; }
        Token[][] assumed = this.copyGrid(this.grid);
        this.AssumeMove(assumed, move);
        return MakesThree(assumed, move.x, move.y);
    }

    /**
     * Checks whether the assumed table has match-three at (x, y)
     * @param assumed would-be table with changes (x, y)
     * @param x column number of changed jem
     * @param y row number of changed jem
     * @return true if there is a match-three, false otherwise
     */
    private boolean MakesThree(Token[][] assumed, int x, int y) {
        for (Direction d : Direction.values()) {
            // x, y is at the end of the three-set
            if (!this.InGrid(x + d.x * 2, y + d.y * 2)) { continue; }
            if (assumed[x + d.x * 2][y + d.y * 2].matches(assumed[x + d.x][y + d.y]) &&
                    assumed[x + d.x * 2][y + d.y * 2].matches(assumed[x][y]) &&
                    assumed[x + d.x][y + d.y].matches(assumed[x][y])) { return true; }
        }
        // x, y is in the middle of horizontal set
        if (x > 0 && x < 7 && assumed[x + 1][y].matches(assumed[x - 1][y]) &&
                assumed[x + 1][y].matches(assumed[x][y]) && assumed[x - 1][y].matches(assumed[x][y])) { return true; }
        // x, y is in the middle of vertical set
        if (y > 0 && y < 7 && assumed[x][y].matches(assumed[x][y - 1]) &&
                assumed[x][y + 1].matches(assumed[x][y]) && assumed[x][y - 1].matches(assumed[x][y])) { return true; }
        return false;
    }
}
