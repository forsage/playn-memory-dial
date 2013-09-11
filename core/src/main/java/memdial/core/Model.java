package memdial.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Model {

    float angleOfDialRad;
    boolean cw = false;
    List<String> numbersDialled = new ArrayList<String>();
    List<Integer> colorsOfNumbersDialled = new ArrayList<Integer>();
    int numberDialling = -1;
    boolean playing = true;
    int cntTicksOnNumberHit = 1;
    int remainingSplashScreenTicks = 2 * MemdialGame.UPDATE_RATE;

    public float getAngleOfDialRad() {
        return angleOfDialRad;
    }

    public void setAngleOfDialRad(float angleOfDialRad) {
        this.angleOfDialRad = angleOfDialRad;
    }

    public List<String> getNumbersDialled() {
        return numbersDialled;
    }

    public List<Integer> getColorsOfNumbersDialled() {
        return colorsOfNumbersDialled;
    }

    boolean isCw() {
        return cw;
    }

    void setCw(boolean cw) {
        this.cw = cw;
    }

    boolean isPlaying() {
        return playing;
    }

    void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public int getNumberDialling() {
        return numberDialling;
    }

    public void setNumberDialling(int numberDialling) {
        this.numberDialling = numberDialling;
    }

    public int getRemainingSplashScreenTicks() {
        return remainingSplashScreenTicks;
    }

    public void setRemainingSplashScreenTicks(int remainingSplashScreenTicks) {
        this.remainingSplashScreenTicks = remainingSplashScreenTicks;
    }

    public boolean isDialling() {
        return angleOfDialRad > Constants.MIN_ANGLE_RAD;
    }

    boolean shouldTick(float angle, double deltaRadians) {
        List<Double> anglesToTick = new ArrayList<Double>(Constants.DIAL_ANGLES_RAD.values());
        Collections.sort(anglesToTick, new Comparator<Double>() {
            @Override
            public int compare(Double a1, Double a2) {
                return -1 * a1.compareTo(a2);
            }
        });
        for (Double angleToTick : anglesToTick) {
            if (angle > angleToTick && (angle - deltaRadians) < angleToTick) {
                return true;
            }
        }
        return false;
    }
}