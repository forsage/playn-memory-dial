/**
 * Copyright 2011 The PlayN Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package memdial.core;

import playn.core.*;
import playn.core.util.Callback;

import java.util.*;

import static playn.core.PlayN.*;

public class Dial {
    public static final String IMAGE = "images/dial-retro-cut.png";
    private GroupLayer parentLayer;
    private ImageLayer imageLayer;
    private ImageLayer textLayer;
    private ImageLayer dialledNumbersLayer;
    private ImageLayer pausedLayer;
    private float angle;
    public static final double MIN_ANGLE = -2 * Math.PI;
    private static final double EPS_ANGLE = 0.02;
    private static final double SPEED_CW = 0.4;
    private static final double SPEED_CCW = 0.4;
    private static final int SPACE_BETWEEN_CHARS = 3;
    private boolean clockwise = false;
    static final TextFormat TEXT_FORMAT = new TextFormat().withFont(graphics().createFont("king668", Font.Style.PLAIN, 48f)).withAlignment(TextFormat.Alignment.LEFT);
    List<String> numbersDialled = new ArrayList<String>();
    List<Integer> colorsOfNumbersDialled = new ArrayList<Integer>();
    private static int SPLASH_SCREEN_TICKS = 2 * Memdial.UPDATE_RATE;
    private int numberDialling = -1;

    private static final List<Point> NUMBER_COORDS = new ArrayList<Point>();
    static {
        NUMBER_COORDS.add(new Point(412, 451));
        NUMBER_COORDS.add(new Point(492, 307));
        NUMBER_COORDS.add(new Point(468, 187));
        NUMBER_COORDS.add(new Point(379, 101));
        NUMBER_COORDS.add(new Point(260, 82));
        NUMBER_COORDS.add(new Point(149, 140));
        NUMBER_COORDS.add(new Point(93, 244));
        NUMBER_COORDS.add(new Point(99, 366));
        NUMBER_COORDS.add(new Point(172, 451));
        NUMBER_COORDS.add(new Point(290, 492));
    }

    public static final int TOUCH_RADIUS = 52;

    public static boolean touchInsideHole(Point pTouch) {
        for (Point pToTouch : NUMBER_COORDS) {
            if (pToTouch.isNearTo(pTouch, TOUCH_RADIUS)) { return true; }
        }
        return false;
    }

    private static final Map<Integer, Double> DIAL_ANGLES = new TreeMap<Integer, Double>();
    static {
        DIAL_ANGLES.put(0, -2 * Math.PI * 0.133);
        DIAL_ANGLES.put(9, -2 * Math.PI * 0.233);
        DIAL_ANGLES.put(8, -2 * Math.PI * 0.328);
        DIAL_ANGLES.put(7, -2 * Math.PI * 0.425);
        DIAL_ANGLES.put(6, -2 * Math.PI * 0.525);
        DIAL_ANGLES.put(5, -2 * Math.PI * 0.617);
        DIAL_ANGLES.put(4, -2 * Math.PI * 0.711);
        DIAL_ANGLES.put(3, -2 * Math.PI * 0.806);
        DIAL_ANGLES.put(2, -2 * Math.PI * 0.9);
        DIAL_ANGLES.put(1, -2 * Math.PI * 1);
    }

    private boolean playing = true;
    private int cntTicks = 1;

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    public boolean isPlaying() {
        return playing;
    }

    private void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void play() {
        setPlaying(true);
        drawPlaying();
    }

    public void pause() {
        setPlaying(false);
        drawPaused();
    }

    private void drawPaused() {
        pausedLayer = createLayerWithTexts(new String[]{"P", "A", "U", "S", "E", "D"},
                new Integer[]{0xFFFF0000, 0xFFCC0000, 0xFF990000, 0xFF660000, 0xFF330000, 0xFF000000});
        pausedLayer.setTranslation(600, 0);
        parentLayer.add(pausedLayer);
    }

    private void drawPlaying() {
        parentLayer.remove(pausedLayer);
    }

    public void beginDialling() {
        angle = new Double(MIN_ANGLE + EPS_ANGLE).floatValue();
    }

    public int getNumberDialling() {
        return numberDialling;
    }

    public void setNumberDialling(int numberDialling) {
        this.numberDialling = numberDialling;
    }

    public static class Point {
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x;
        int y;

        boolean isNearTo(Point pOther, int r) {
            int dx = Math.abs(pOther.x - x);
            int dy = Math.abs(pOther.y - y);
            return Math.sqrt(dx * dx + dy * dy) <= r;
        }
    }

    public Dial(final GroupLayer parentLayer, final float x, final float y) {
        Image image = assets().getImage(IMAGE);
        imageLayer = graphics().createImageLayer(image);

        // Add a callback for when the image loads.
        // This is necessary because we can't use the width/height (to center the
        // image) until after the image has been loaded
        image.addCallback(new Callback<Image>() {
            @Override
            public void onSuccess(Image result) {
                imageLayer.setOrigin(result.width() / 2f, result.height() / 2f).setTranslation(x, y).setDepth(0);
                parentLayer.add(imageLayer);
            }

            @Override
            public void onFailure(Throwable cause) {
                log().error("Error loading image!", cause);
            }
        });

        textLayer = createLayerWithText("Dial\n666");
        textLayer.setOrigin(textLayer.width() / 2, textLayer.height() / 2).setTranslation(x, y).setDepth(1);
        parentLayer.add(textLayer);

        for (int ixNum = 0; ixNum < 10; ixNum++) {
            ImageLayer numLayer = createLayerWithText(Integer.toString(ixNum), getColorRedForAngle(DIAL_ANGLES.get(ixNum).floatValue()));
            numLayer.setOrigin(Memdial.SCREEN_WIDTH - NUMBER_COORDS.get(ixNum).x, Memdial.SCREEN_HEIGHT - NUMBER_COORDS.get(ixNum).y).
                    setTranslation(Memdial.SCREEN_WIDTH + 100, Memdial.SCREEN_HEIGHT - 12).
                    setDepth(-1);
            parentLayer.add(numLayer);
        }

        dialledNumbersLayer = createLayerWithText("Press key or click to dial");
        parentLayer.add(dialledNumbersLayer);
        this.parentLayer = parentLayer;
    }

    public void update(int delta) {
        removeSplashScreen();
        if (isPlaying()) {
            rotateDial(delta);
        }
    }

    public boolean isDialling() {
        return angle > MIN_ANGLE;
    }

    private void rotateDial(int delta) {
        if (angle <= 0 && angle >= MIN_ANGLE) {
            if (isClockwise()) {
                double correctionFactor = 1;
                angle += correctionFactor * 2 * Math.PI * SPEED_CW / delta;
                if (angle > 0) {
                    angle = 0;
                }
                if (getNumberDialling() > -1) {
                    if (findNumberDialled(angle) == getNumberDialling()) {
                        setClockwise(false);
                    }
                }
            } else {
                double angleRatio = Math.abs(angle) / Math.abs(MIN_ANGLE);
                double correctionFactor = 1 - (angleRatio * angleRatio);
                double deltaRadians = correctionFactor * 2 * Math.PI * SPEED_CCW / delta;
                if (cntTicks > 0 && shouldTick(angle, deltaRadians)) {
                    angle += deltaRadians / 2;
                    cntTicks--;
                } else {
                    cntTicks = 1;
                    angle -= deltaRadians;
                }
                if (angle < MIN_ANGLE + EPS_ANGLE) {
                    setNumberDialling(-1);
                    angle = new Double(MIN_ANGLE).floatValue();
                }
            }
        }
        imageLayer.setRotation(angle);
        textLayer.setRotation(angle);
    }

    private boolean shouldTick(float angle, double deltaRadians) {
        List<Double> anglesToTick = new ArrayList<Double>(DIAL_ANGLES.values());
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

    private void removeSplashScreen() {
        if (SPLASH_SCREEN_TICKS == 0) {
            parentLayer.remove(dialledNumbersLayer);
            dialledNumbersLayer = createLayerWithText("");
            parentLayer.add(dialledNumbersLayer);
            SPLASH_SCREEN_TICKS = -1;
        } else if (SPLASH_SCREEN_TICKS > 0) {
            --SPLASH_SCREEN_TICKS;
        }
    }

    public void dialNumberKeyboard(int numberDialled) {
        parentLayer.remove(dialledNumbersLayer);
        numbersDialled.add(Integer.toString(numberDialled));
        String[] arrayNumbersDialled = new String[numbersDialled.size()];
        arrayNumbersDialled = numbersDialled.toArray(arrayNumbersDialled);
        colorsOfNumbersDialled.add(getColorRedForDigit(numberDialled));
        Integer[] arrayColors = new Integer[colorsOfNumbersDialled.size()];
        arrayColors = colorsOfNumbersDialled.toArray(arrayColors);
        dialledNumbersLayer = createLayerWithTexts(arrayNumbersDialled, arrayColors);
        parentLayer.add(dialledNumbersLayer);
    }

    public void dialNumberMouse() {
        dialNumberKeyboard(findNumberDialled(angle));
    }

    private int getColorRedForAngle(float angle) {
        int colorRed = (int) (-255 * Math.abs(angle / MIN_ANGLE) - 1);
        return 0xFF000000 + colorRed * 65536;
    }

    private int getColorRedForDigit(int digit) {
        return getColorRedForAngle(DIAL_ANGLES.get(digit).floatValue());
    }

    private int findNumberDialled(float dialledAngle) {
        int numberDialled = -1;
        List<Map.Entry<Integer, Double>> dialAngles = new ArrayList<Map.Entry<Integer, Double>>(DIAL_ANGLES.entrySet());
        Comparator<Map.Entry<Integer, Double>> invertAnglesComparator = new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> e1, Map.Entry<Integer, Double> e2) {
                return -1 * e1.getValue().compareTo(e2.getValue());
            }
        };
        Collections.sort(dialAngles, invertAnglesComparator);
        for (Map.Entry<Integer, Double> dialAngle : dialAngles) {
            if (dialAngle.getValue() <= dialledAngle) {
                numberDialled = dialAngle.getKey();
                break;
            }
        }
        return numberDialled;
    }

    private ImageLayer createLayerWithText(String text, int color) {
        TextLayout layout = graphics().layoutText(text, Dial.TEXT_FORMAT);
        return createTextLayer(layout, color);
    }

    private ImageLayer createLayerWithTexts(String[] texts, Integer[] colors) {
        TextLayout[] layouts = new TextLayout[texts.length];
        for (int ixText = 0; ixText < texts.length; ixText++) {
            layouts[ixText] = graphics().layoutText(texts[ixText], Dial.TEXT_FORMAT);
        }
        return createTextLayer(layouts, colors);
    }

    private ImageLayer createLayerWithText(String text) {
        return createLayerWithText(text, 0xFF000000);
    }

    protected ImageLayer createTextLayer(TextLayout layout, int color) {
        CanvasImage image = graphics().createImage((int) Math.ceil(layout.width()),
                (int) Math.ceil(layout.height()));
        image.canvas().setFillColor(color);
        image.canvas().fillText(layout, 0, 0);
        return graphics().createImageLayer(image);
    }

    protected ImageLayer createTextLayer(TextLayout[] layouts, Integer[] colors) {
        int wLayout = 0;
        int hLayout = 0;
        for (TextLayout layout : layouts) {
            wLayout += layout.width() + SPACE_BETWEEN_CHARS;
            hLayout += layout.height();
        }
        CanvasImage image = graphics().createImage((int) Math.ceil(wLayout) + SPACE_BETWEEN_CHARS,
                (int) Math.ceil(hLayout));
        wLayout = 0;
        hLayout = 0;
        int ixLayout = 0;
        for (TextLayout layout : layouts) {
            image.canvas().setFillColor(colors[ixLayout++]);
            image.canvas().fillText(layout, wLayout, hLayout);
            wLayout += layout.width() + SPACE_BETWEEN_CHARS;
        }
        return graphics().createImageLayer(image);
    }
}
