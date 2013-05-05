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

import java.util.ArrayList;
import java.util.List;

import static playn.core.PlayN.*;

public class Memdial extends Game.Default {

    public static final int UPDATE_RATE = 25;
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;

    GroupLayer peaLayer;
    List<Pea> peas = new ArrayList<Pea>(0);
    Dial dial;

    /**
     * Creates an instance of the default game implementation.
     */
    public Memdial() {
        super(UPDATE_RATE);
    }

    @Override
    public void init() {
        // create and add background image layer
        Image bgImage = assets().getImage("images/bg-f5f0f0.png");
        ImageLayer bgLayer = graphics().createImageLayer(bgImage);
        graphics().rootLayer().add(bgLayer);

        // create a group layer to hold the peas
        peaLayer = graphics().createGroupLayer();
        graphics().rootLayer().add(peaLayer);

        // preload the pea image into the asset manager cache
        assets().getImage(Pea.IMAGE);
        assets().getImage(Dial.IMAGE);
        dial = new Dial(peaLayer, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, -1);

        // add a listener for pointer (mouse, touch) input
        pointer().setListener(new Pointer.Adapter() {
            @Override
            public void onPointerEnd(Pointer.Event event) {
                Pea pea = new Pea(peaLayer, event.x(), event.y(), peas.size());
                peas.add(pea);
            }
        });
    }

    @Override
    public void paint(float alpha) {
        // layers automatically paint themselves (and their children). The rootlayer
        // will paint itself, the background, and the pea group layer automatically
        // so no need to do anything here!
    }

    @Override
    public void update(int delta) {
        for (Pea pea : peas) {
            pea.update(delta);
        }
        dial.update(delta);
    }

}
