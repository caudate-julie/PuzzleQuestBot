package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Modified by Julie on 16.04.2017.
 */
public class auxiliary {
    public void probing() {
        try {
            BufferedImage img1 = ImageIO.read(getClass().getResourceAsStream("/tests/YELLOW3.bmp"));
            BufferedImage img2 = ImageIO.read(getClass().getResourceAsStream("/sample_tokens/RED.bmp"));
            int difference = 0;
            int PIXEL_DIFFERENCE_THRESHOLD = 100;
            for (int j = 0; j < 55; j++) {
                for (int i = 0; i < 55; i++) {
                    long sample_clr = img1.getRGB(i, j);
                    long actual_clr = img2.getRGB(i, j);
                    long red = Math.abs((sample_clr >> 16) & 0xff - (actual_clr >> 16) & 0xff);
                    long green = Math.abs((sample_clr >> 8) & 0xff - (actual_clr >> 8) & 0xff);
                    long blue = Math.abs(sample_clr & 0xff - actual_clr & 0xff);
                    difference += (red > PIXEL_DIFFERENCE_THRESHOLD) ? red : 0;
                    difference += (green > PIXEL_DIFFERENCE_THRESHOLD) ? green : 0;
                    difference += (blue > PIXEL_DIFFERENCE_THRESHOLD) ? blue : 0;
                }
            }
            System.out.format("Difference is %d", difference);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void SplitScreenIntoClips() {
        try {
            //BufferedImage img = ImageIO.read(getClass().getResourceAsStream("/tests/test1.bmp"));
            BufferedImage img = (new GameWindowManipulator()).GetScreenshot();
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    BufferedImage sub = img.getSubimage(GameWindowManipulator.FIELD_X + x * (GameWindowManipulator.CLIP_WIDTH + GameWindowManipulator.BORDER),
                                                        GameWindowManipulator.FIELD_Y + y * (GameWindowManipulator.CLIP_HEIGHT + GameWindowManipulator.BORDER),
                                                           GameWindowManipulator.CLIP_WIDTH, GameWindowManipulator.CLIP_HEIGHT);
                    ImageIO.write(sub, "bmp", new File("D:/imgs/" +
                                  Integer.toString(y) + " " + Integer.toString(x) + ".bmp"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
