package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Class makes a screenshot for current game and turns it into workable grid.
 */
public class GameWindowManipulator {
    private static final long DELAY_FOR_SCREENCAPTURE = 100;       // time Windows needs for to capture screenshot
    public static final long CLIP_DIFFERENCE_THRESHOLD = 200000;   // when it is succeeded, images are different
    public static final long SCREEN_STATIC_THRESHOLD = 100000;     // when it is succeeded, jems are still moving
    public static final long PIXEL_DIFFERENCE_THRESHOLD = 50;      // when it is succeeded, pixels are different
    public static final long COLOR_DARK = 50;                      // when it is preceeded, it's a dark background
    public static final int CLIP_WIDTH = 71;                       //
    public static final int CLIP_HEIGHT = 71;                      //

    public static final int FIELD_X = 219;                         // position of upper-left corner ...
    public static final int FIELD_Y = 135;                         // ... of jem-field
    public static final int BORDER = 3;                            // between two clips

    private BufferedImage screenshot;
    //private Token[][] grid = new Token[8][8];                                         // the main game grid

    public GameWindowManipulator() {
    }

    // overrided constructor - to load precaptured screenshot
    public GameWindowManipulator(BufferedImage screenshot) {
        this.screenshot = screenshot;
    }

    /**
     * Waits for game screen to stop moving and falling.
     */
    public void WaitGameToStopMoving() throws AWTException, UnsupportedFlavorException, InterruptedException, IOException {
        while (true) {
            BufferedImage screen_before = this.GetScreenshot();
            TimeUnit.MILLISECONDS.sleep(DELAY_FOR_SCREENCAPTURE);  // Windows needs time to capture an image
            BufferedImage screen_after = this.GetScreenshot();
            if (this.ScreensMatch(screen_before, screen_after)) { return; }
        }
    }

    /**
     * crossroad method for making a new grid
     */
    public Token[][] GetGrid() throws AWTException, InterruptedException, UnsupportedFlavorException, IOException {
        this.screenshot = GetScreenshot();
        return this.SplitGrid();
    }

    public void MoveJem(Move move) throws AWTException {
        Robot robot = new Robot();
        int center_x = FIELD_X + move.x * (CLIP_WIDTH + BORDER) + CLIP_WIDTH / 2;
        int center_y = FIELD_Y + move.y * (CLIP_HEIGHT + BORDER) + CLIP_HEIGHT / 2;
        robot.mouseMove(center_x, center_y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseMove(center_x + CLIP_WIDTH * move.direction.x, center_y + CLIP_HEIGHT * move.direction.y);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.mouseMove(0, 0);

    }

    /**
     * Takes screenshot of the game window and writes it in the corresponding grid.
     * @throws AWTException for no apparent reason o.O
     */
    public BufferedImage GetScreenshot() throws AWTException, InterruptedException, IOException, UnsupportedFlavorException {
        // TODO: public?
        Robot robot = new Robot();
        Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screen);
    }

    private Token[][] SplitGrid() throws IOException {
        Token[][] field = new Token[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                field[x][y] = this.RecognizeClip(FIELD_X + x * (CLIP_WIDTH + BORDER),
                                                      FIELD_Y + y * (CLIP_HEIGHT + BORDER));
            }
        }
        return field;
    }

    /**
     * Takes an the coordinated of the upperleft corner of the clip in screenshot
     * and compares the clip sized 72x72 pixels with the samples
     * @param x_start left boundary of the clip
     * @param y_start upper boundary of the clip
     * @return corresponding token from Token enum
     */
    private Token RecognizeClip(int x_start, int y_start) throws IOException {
        String[] samplenames = {"YELLOW", "GREEN", "BLUE", "RED", "SKULL", "SKULL_5", "MONEY", "STAR" };
        for (String samplename : samplenames) {
            BufferedImage sample = ImageIO.read(getClass().getResourceAsStream("/sample_tokens/" + samplename + ".bmp"));
            if (this.ClipsMatch(sample, x_start, y_start)) {
                return Token.valueOf(samplename);
            }
        }
        return Token.UNKNOWN;
    }

    /**
     * Matches clip from screenshot with sample pixel by pixel.
     * @param sample image to be matched to, determines size
     * @param x_start left boundary of the clip in screenshot
     * @param y_start upper boundary of the clip in screenshot
     * @return true if the difference is less than a threshold and false otherwise
     */
    private boolean ClipsMatch(BufferedImage sample, int x_start, int y_start) {
        int difference = 0;
        for (int x = 0; x < sample.getWidth(); x++) {
            for (int y = 0; y < sample.getHeight(); y++) {
                long sample_clr = sample.getRGB(x, y);
                long actual_clr = this.screenshot.getRGB(x_start + x, y_start + y);
                for (int component = 0; component < 3; component++) {
                    difference += this.ColorComponentDifference(sample_clr, actual_clr, component);
                }
                if (difference > CLIP_DIFFERENCE_THRESHOLD) { return false; }
            }
        }
        return true;
    }

    private boolean ScreensMatch(BufferedImage screen1, BufferedImage screen2) {
        long difference = 0;
        for (int x = FIELD_X; x < (FIELD_X + (CLIP_WIDTH + BORDER) * 8); x++) {
            for (int y = FIELD_Y; y < (FIELD_Y + (CLIP_HEIGHT + BORDER) * 8); y++) {
                // we use color component here, though we the idea is different -
                // no background comparison should be good enough
                for (int component = 0; component < 3; component ++){
                    difference += this.ColorComponentDifference(screen1.getRGB(x, y), screen2.getRGB(x, y), component);
                    // difference is not 0 due to small dynamic elements like turn flag or statuses
                    if (difference > SCREEN_STATIC_THRESHOLD) { return false; }
                }
            }
        }
        return true;
    }


    /**
     * Returns difference if it if big enough and pixels are not in dark region
     * (since dark background differs from tile to tile)
     * @param color1 first RGB pixel number
     * @param color2 second RGB pixel number
     * @param order component: 0 for green, 1 for blue, 2 for red
     * @return difference if it is significant
     */
    private int ColorComponentDifference(long color1, long color2, int order) {
        long component1 = (color1 >> (8 * order)) & 0xff;
        long component2 = (color2 >> (8 * order)) & 0xff;
        if (component1 < COLOR_DARK && component2 < COLOR_DARK) {
            return 0;     // dark background differs between tiles - we don't care about that difference
        }
        int difference = (int)Math.abs(component1 - component2);
        return (difference > PIXEL_DIFFERENCE_THRESHOLD) ? difference : 0;
    }
}
