package memdial.core;

import playn.core.Font;
import playn.core.PlayN;
import playn.core.TextFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Constants {
    static final String IMAGE_DIAL_PATH = "images/dial-retro-cut.png";
    static final double MIN_ANGLE_RAD = -2 * Math.PI;
    static final TextFormat TEXT_FORMAT = new TextFormat().withFont(PlayN.graphics()
            .createFont("king668", Font.Style.PLAIN, 48f)).withAlignment(TextFormat.Alignment.LEFT);
    static final int TOUCH_RADIUS_PX = 52;
    static final double EPS_ANGLE_RAD = 0.02;
    static final double SPEED_CW_RAD = 0.4;
    static final double SPEED_CCW_RAD = 0.4;
    static final int BETWEEN_CHARS_PX = 3;
    static final List<Point> NUMBERS_PX = new ArrayList<Point>();
    static final Map<Integer, Double> DIAL_ANGLES_RAD = new TreeMap<Integer, Double>();
    public static final String PATH_PREFIX = "memdial/";
    public static final String MEMDIAL_FONT_NAME = "king668";
    public static final String MEMDIAL_FONT_PATH = "fonts/king668.TTF";


    static {
        NUMBERS_PX.add(new Point(412, 451));
        NUMBERS_PX.add(new Point(492, 307));
        NUMBERS_PX.add(new Point(468, 187));
        NUMBERS_PX.add(new Point(379, 101));
        NUMBERS_PX.add(new Point(260, 82));
        NUMBERS_PX.add(new Point(149, 140));
        NUMBERS_PX.add(new Point(93, 244));
        NUMBERS_PX.add(new Point(99, 366));
        NUMBERS_PX.add(new Point(172, 451));
        NUMBERS_PX.add(new Point(290, 492));
    }

    static {
        DIAL_ANGLES_RAD.put(0, -2 * Math.PI * 0.133);
        DIAL_ANGLES_RAD.put(9, -2 * Math.PI * 0.233);
        DIAL_ANGLES_RAD.put(8, -2 * Math.PI * 0.328);
        DIAL_ANGLES_RAD.put(7, -2 * Math.PI * 0.425);
        DIAL_ANGLES_RAD.put(6, -2 * Math.PI * 0.525);
        DIAL_ANGLES_RAD.put(5, -2 * Math.PI * 0.617);
        DIAL_ANGLES_RAD.put(4, -2 * Math.PI * 0.711);
        DIAL_ANGLES_RAD.put(3, -2 * Math.PI * 0.806);
        DIAL_ANGLES_RAD.put(2, -2 * Math.PI * 0.9);
        DIAL_ANGLES_RAD.put(1, -2 * Math.PI * 1);
    }
}