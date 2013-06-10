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

import java.util.ArrayList;
import java.util.List;

import static playn.core.PlayN.*;

public class Dial {
    public static final String IMAGE = "images/dial-retro-cut.png";
    private GroupLayer parentLayer;
    private ImageLayer imageLayer;
    private ImageLayer textLayer;
    private ImageLayer dialledNumbersLayer;
    private float angle;
    public static final double MIN_ANGLE = -2 * Math.PI;
    private static final double SPEED_CW = 0.4;
    private static final double SPEED_CCW = 0.2;
    private boolean clockwise = false;
    static final TextFormat TEXT_FORMAT = new TextFormat().withFont(graphics().createFont("king668", Font.Style.PLAIN, 48f));
    String numbersDialled = "";

    private static List<Point> NUMBER_COORDS = new ArrayList<Point>();
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

    private static List<Double> NUMBER_ANGLES = new ArrayList<Double>();
    static {
        NUMBER_ANGLES.add(-2 * Math.PI * 0.1);
        NUMBER_ANGLES.add(-2 * Math.PI * 0.2);
        NUMBER_ANGLES.add(-2 * Math.PI * 0.3);
        NUMBER_ANGLES.add(-2 * Math.PI * 0.4);
        NUMBER_ANGLES.add(-2 * Math.PI * 0.5);
        NUMBER_ANGLES.add(-2 * Math.PI * 0.6);
        NUMBER_ANGLES.add(-2 * Math.PI * 0.7);
        NUMBER_ANGLES.add(-2 * Math.PI * 0.8);
        NUMBER_ANGLES.add(-2 * Math.PI * 0.9);
        NUMBER_ANGLES.add(-2 * Math.PI * 1);
    }

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    private static class Point {
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        int x;
        int y;
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
            ImageLayer numLayer = createLayerWithText(Integer.toString(ixNum));
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
        if (angle <= 0 && angle >= MIN_ANGLE) {
            if (isClockwise()) {
                double correctionFactor = 1;
                angle += correctionFactor * 2 * Math.PI * SPEED_CW / delta;
                if (angle > 0) {
                    angle = 0;
                }
            } else {
                double correctionFactor = 1 - Math.abs(angle) / Math.abs(MIN_ANGLE);
                angle -= correctionFactor * 2 * Math.PI * SPEED_CCW / delta;
                if (angle < MIN_ANGLE) {
                    angle = new Double(MIN_ANGLE).floatValue();
                }
            }
        }
        imageLayer.setRotation(angle);
        textLayer.setRotation(angle);
    }

    public void dialNumber(int numberDialled) {
        numbersDialled += numberDialled;
        parentLayer.remove(dialledNumbersLayer);
        dialledNumbersLayer = createLayerWithText(numbersDialled);
        parentLayer.add(dialledNumbersLayer);
    }

    public void writeDialledNumber() {
        parentLayer.remove(dialledNumbersLayer);
        dialledNumbersLayer = createLayerWithText(findNumbersDialled(angle));
        parentLayer.add(dialledNumbersLayer);
    }

    private String findNumbersDialled(float angle) {
        int numberDialled = -1;
        for (int ixDialled = 0; ixDialled < NUMBER_ANGLES.size(); ixDialled++) {
            if (NUMBER_ANGLES.get(ixDialled) < angle) {
                numberDialled = ixDialled;
                break;
            }
        }
        numbersDialled += numberDialled;
        return numbersDialled;
    }

    private ImageLayer createLayerWithText(String text) {
        TextLayout layout = graphics().layoutText(text, Dial.TEXT_FORMAT);
        return createTextLayer(layout, 0xFF000000);
    }

    protected ImageLayer createTextLayer(TextLayout layout, int color) {
        CanvasImage image = graphics().createImage((int)Math.ceil(layout.width()),
                (int)Math.ceil(layout.height()));
        image.canvas().setFillColor(color);
        image.canvas().fillText(layout, 0, 0);
        return graphics().createImageLayer(image);
    }
}
