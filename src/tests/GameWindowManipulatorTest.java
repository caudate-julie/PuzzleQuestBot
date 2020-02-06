package tests;

import main.GameWindowManipulator;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Modified by Julie on 16.04.2017.
 */
class GameWindowManipulatorTest {
    @Test
    void testImagesMatch()  {
        try {
            //Class target = GameWindowManipulator.class;
            Class[] args = { BufferedImage.class, int.class, int.class};

            Method imagesMatch = GameWindowManipulator.class.getDeclaredMethod("ClipsMatch", BufferedImage.class, int.class, int.class);
            imagesMatch.setAccessible(true);

            Object[] argObjects = {null, 58, 58};
            BufferedImage test = ImageIO.read(getClass().getResourceAsStream("/tests/test1.png"));
            GameWindowManipulator screen = new GameWindowManipulator(test);

            /*argObjects[0] = ImageIO.read(getClass().getResourceAsStream("/sample_tokens/SKULL.bmp"));
            assertFalse((boolean)imagesMatch.invoke(screen, argObjects));
            argObjects[0] = ImageIO.read(getClass().getResourceAsStream("/sample_tokens/RED.bmp"));
            assertTrue((boolean)imagesMatch.invoke(screen, argObjects));
            argObjects[0] = ImageIO.read(getClass().getResourceAsStream("/sample_tokens/MONEY.bmp"));
            assertFalse((boolean)imagesMatch.invoke(screen, argObjects));
            argObjects[0] = ImageIO.read(getClass().getResourceAsStream("/sample_tokens/STAR.bmp"));
            assertFalse((boolean)imagesMatch.invoke(screen, argObjects));
            argObjects[0] = ImageIO.read(getClass().getResourceAsStream("/sample_tokens/GREEN.bmp"));
            assertFalse((boolean)imagesMatch.invoke(screen, argObjects));
            argObjects[0] = ImageIO.read(getClass().getResourceAsStream("/sample_tokens/YELLOW.bmp"));
            assertFalse((boolean)imagesMatch.invoke(screen, argObjects));*/



        } catch (Exception e) {
            System.out.format("Smth very wrong with reflection.\n");
            e.printStackTrace();
        }
    }
    @Test
    void testSampleImagesMatch()  {
        try {
            //Class target = GameWindowManipulator.class;
            Class[] args = { BufferedImage.class, int.class, int.class};

            Method imagesMatch = GameWindowManipulator.class.getDeclaredMethod("ClipsMatch", args);
            imagesMatch.setAccessible(true);

            Object[] argObjects = {null, 0, 0};
            String[] names = {"RED", "GREEN", "YELLOW", "BLUE", "SKULL", "MONEY", "STAR"};
            for (String sample_name : names) {
                argObjects[0] = ImageIO.read(getClass().getResourceAsStream("/sample_tokens/" + sample_name + ".bmp"));
                for (String name : names) {
                    for (int i = 1; i < 9; i++) {
                        String testname = "/tests/" + name + Integer.toString(i) + ".bmp";
                        BufferedImage test = ImageIO.read(getClass().getResourceAsStream(testname));
                        GameWindowManipulator screen = new GameWindowManipulator(test);
                        if (name.equals(sample_name)) {
                                assertTrue((boolean)imagesMatch.invoke(screen, argObjects));
                        }
                        else {
                            assertFalse((boolean)imagesMatch.invoke(screen, argObjects));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}