/**
 * Copyright 2010 The PlayN Authors
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
package memdial.java;

import memdial.core.Constants;
import memdial.core.MemdialGame;
import playn.core.PlayN;
import playn.java.JavaPlatform;

public class MemdialGameJava {

    public static void main(String[] args) {
        configureScreen();
        PlayN.run(new MemdialGame());
    }

    private static void configureScreen() {
        JavaPlatform.Config config = new JavaPlatform.Config();
        config.width = MemdialGame.SCREEN_WIDTH_PX;
        config.height = MemdialGame.SCREEN_HEIGHT_PX;
        JavaPlatform platform = JavaPlatform.register(config);
        platform.graphics().registerFont(Constants.MEMDIAL_FONT_NAME, Constants.MEMDIAL_FONT_PATH);
    }

}
