package memdial.core;

public class ViewUtils {

    static boolean touchInsideHole(Point pTouch) {
        for (Point pToTouch : Constants.NUMBERS_PX) {
            if (pToTouch.isNearTo(pTouch, Constants.TOUCH_RADIUS_PX)) {
                return true;
            }
        }
        return false;
    }

    static int getColorRedForAngle(float angle) {
        int colorRed = (int) (-255 * Math.abs(angle / Constants.MIN_ANGLE_RAD) - 1);
        return 0xFF000000 + colorRed * 65536;
    }

    static int getColorRedForDigit(int digit) {
        return getColorRedForAngle(Constants.DIAL_ANGLES_RAD.get(digit).floatValue());
    }
}
