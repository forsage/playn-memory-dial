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
    public static final String IMAGE = "images/dial-retro-hollow.png";
    private ImageLayer imageLayer;
    private ImageLayer textLayer;
    private float angle;
    private int index;

    private static List<Point> coords = new ArrayList<Point>();
    static {
        coords.add(new Point(389, 431));
        coords.add(new Point(468, 296));
        coords.add(new Point(446, 174));
        coords.add(new Point(357, 88));
        coords.add(new Point(238, 70));
        coords.add(new Point(127, 120));
        coords.add(new Point(65, 224));
        coords.add(new Point(76, 347));
        coords.add(new Point(155, 442));
        coords.add(new Point(277, 476));
    }

    private static class Point {
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        int x;
        int y;
    }

    public Dial(final GroupLayer parentLayer, final float x, final float y, int index) {
        Image image = assets().getImage(IMAGE);
        imageLayer = graphics().createImageLayer(image);
        this.index = index;

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
            numLayer.setOrigin(coords.get(ixNum).x, coords.get(ixNum).y).setTranslation(650, 550).setDepth(-1);
            parentLayer.add(numLayer);
        }
    }

    public void update(int delta) {
        if (index % 2 == 0) {
            angle += 2 * Math.PI * 0.2 / delta;
        } else {
            angle -= 2 * Math.PI * 0.2 / delta;
        }
        imageLayer.setRotation(angle);
        textLayer.setRotation(angle);
    }

    private ImageLayer createLayerWithText(String text) {
        Font font = graphics().createFont("king668", Font.Style.PLAIN, 48f);
        TextFormat format = new TextFormat().withFont(font);
        TextLayout layout = graphics().layoutText(text, format);
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
