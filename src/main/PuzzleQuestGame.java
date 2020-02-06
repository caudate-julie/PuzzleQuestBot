package main;

import java.io.IOException;

/**
 * Modified by Julie on 16.04.2017.
 */
public class PuzzleQuestGame {


    public static void main(String[] args) throws InterruptedException, IOException {
        GameWindowManipulator manipulator = new GameWindowManipulator();
        GameLogic player = new GameLogic();
        while (true) {
            try {
                float ts = System.nanoTime();
                manipulator.WaitGameToStopMoving();
                System.out.format("Time to wait:         %.4f\n", (System.nanoTime() - ts) / 1000000000);

                ts = System.nanoTime();
                player.UpdateField(manipulator.GetGrid());
                System.out.format("Time to update field: %.4f\n", (System.nanoTime() - ts) / 1000000000);

                ts = System.nanoTime();
                manipulator.MoveJem(player.Turn());
                System.out.format("Time to make a move:  %.4f\n", (System.nanoTime() - ts) / 1000000000);
                //player.Turn();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        /*auxiliary a = new auxiliary();
        a.SplitScreenIntoClips();*/
    }
}

