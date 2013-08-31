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

import static playn.core.PlayN.*;

public class MemdialGame extends Game.Default {

    static final int UPDATE_RATE = 25;
    public static final int SCREEN_WIDTH_PX = 800;
    public static final int SCREEN_HEIGHT_PX = 600;

    DialEngine dialEngine;

    public MemdialGame() {
        super(UPDATE_RATE);
    }

    @Override
    public void init() {
        Image bgImage = assets().getImage("images/bg-f5f0f0.png");
        ImageLayer bgLayer = graphics().createImageLayer(bgImage);
        graphics().rootLayer().add(bgLayer);

        GroupLayer sceneLayer = graphics().createGroupLayer();
        graphics().rootLayer().add(sceneLayer);

        assets().getImage(Constants.IMAGE_DIAL_PATH);
        dialEngine = new DialEngine(sceneLayer, SCREEN_WIDTH_PX / 2, SCREEN_HEIGHT_PX / 2);

        setKeyboardListener();
        setPointerListener();
    }

    private void setKeyboardListener() {
        keyboard().setListener(new Keyboard.Adapter() {
            @Override
            public void onKeyTyped(Keyboard.TypedEvent event) {
                if (event.typedChar() == 'q') {
                    System.exit(0);
                } else if (event.typedChar() == 'p') {
                    if (dialEngine.model.isPlaying()) {
                        dialEngine.pause();
                    } else {
                        dialEngine.play();
                    }
                }

                if ((Character.getNumericValue(event.typedChar()) >= 0)
                        && (Character.getNumericValue(event.typedChar()) < 10)
                        && (!dialEngine.model.isCw())
                        && (!dialEngine.model.isDialling())) {
                    dialEngine.dialNumberKeyboard(Character.getNumericValue(event.typedChar()));
                    dialEngine.model.setCw(true);
                    dialEngine.beginDialling();
                    dialEngine.model.setNumberDialling(Character.getNumericValue(event.typedChar()));
                }
            }
        });
    }

    private void setPointerListener() {
        pointer().setListener(new Pointer.Adapter() {
            @Override
            public void onPointerStart(Pointer.Event event) {
                if ((!dialEngine.model.isCw()) && (!dialEngine.model.isDialling()) &&
                        (View.touchInsideHole(
                                new Point(Math.round(event.x()) - 100, Math.round(event.y()) - 12)))) {
                    dialEngine.model.setCw(true);
                    dialEngine.beginDialling();
                }
            }

            @Override
            public void onPointerEnd(Pointer.Event event) {
                if (dialEngine.model.isCw()) {
                    dialEngine.model.setCw(false);
                    dialEngine.dialNumberMouse();
                }
            }
        });
    }

    @Override
    public void paint(float alpha) {
    }

    @Override
    public void update(int delta) {
        dialEngine.update(delta);
    }

}
