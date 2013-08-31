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

import playn.core.GroupLayer;

import java.util.*;

public class DialEngine {

    final View view = new View();
    final Model model = new Model();

    void play() {
        model.setPlaying(true);
        view.drawPlaying();
    }

    void pause() {
        model.setPlaying(false);
        view.drawPaused();
    }

    public void beginDialling() {
        model.setAngleOfDialRad(new Double(Constants.MIN_ANGLE_RAD + Constants.EPS_ANGLE_RAD).floatValue());
    }

    public DialEngine(final GroupLayer rootLayer, final float x, final float y) {
        view.initImageLayer(rootLayer, x, y);
        view.initTextLayer(rootLayer, x, y);
        view.initNumbersLayer(rootLayer);
        view.initSplashScreenLayer(rootLayer);
        view.setRootLayer(rootLayer);
    }

    public void update(int delta) {
        view.removeSplashScreen();
        if (model.isPlaying()) {
            rotateDial(delta);
        }
    }

    private void rotateDial(int delta) {
        if (model.getAngleOfDialRad() <= 0 && model.getAngleOfDialRad() >= Constants.MIN_ANGLE_RAD) {
            if (model.isCw()) {
                rotateDialCw(delta);
            } else {
                rotateDialCcw(delta);
            }
        }
        view.getDialImageLayer().setRotation(model.getAngleOfDialRad());
        view.getTextLayer().setRotation(model.getAngleOfDialRad());
    }

    private void rotateDialCcw(int delta) {
        double angleRatio = Math.abs(model.getAngleOfDialRad()) / Math.abs(Constants.MIN_ANGLE_RAD);
        double correctionFactor = 1 - (angleRatio * angleRatio);
        double deltaRadians = correctionFactor * 2 * Math.PI * Constants.SPEED_CCW_RAD / delta;
        if (model.cntTicksOnNumberHit > 0 && model.shouldTick(model.getAngleOfDialRad(), deltaRadians)) {
            model.setAngleOfDialRad(new Double(model.getAngleOfDialRad() + deltaRadians / 2).floatValue());
            model.cntTicksOnNumberHit--;
        } else {
            model.cntTicksOnNumberHit = 1;
            model.setAngleOfDialRad(new Double(model.getAngleOfDialRad() - deltaRadians).floatValue());
        }
        if (model.getAngleOfDialRad() < Constants.MIN_ANGLE_RAD + Constants.EPS_ANGLE_RAD) {
            model.setNumberDialling(-1);
            model.setAngleOfDialRad(new Double(Constants.MIN_ANGLE_RAD).floatValue());
        }
    }

    private void rotateDialCw(int delta) {
        float correctionFactor = 1;
        model.setAngleOfDialRad(
                new Double(model.getAngleOfDialRad() + correctionFactor * 2 * Math.PI * Constants.SPEED_CW_RAD / delta)
                        .floatValue());
        if (model.getAngleOfDialRad() > 0) {
            model.setAngleOfDialRad(0);
        }
        if (model.getNumberDialling() > -1) {
            if (findNumberDialled(model.getAngleOfDialRad()) == model.getNumberDialling()) {
                model.setCw(false);
            }
        }
    }

    public void dialNumberKeyboard(int numberDialled) {
        view.getRootLayer().remove(view.getDialledNumbersLayer());
        model.getNumbersDialled().add(Integer.toString(numberDialled));
        String[] arrayNumbersDialled = new String[model.getNumbersDialled().size()];
        arrayNumbersDialled = model.getNumbersDialled().toArray(arrayNumbersDialled);
        model.getColorsOfNumbersDialled().add(view.getColorRedForDigit(numberDialled));
        Integer[] arrayColors = new Integer[model.getColorsOfNumbersDialled().size()];
        arrayColors = model.getColorsOfNumbersDialled().toArray(arrayColors);
        view.setDialledNumbersLayer(view.createLayerWithTexts(arrayNumbersDialled, arrayColors));
        view.getRootLayer().add(view.getDialledNumbersLayer());
    }

    public void dialNumberMouse() {
        dialNumberKeyboard(findNumberDialled(model.getAngleOfDialRad()));
    }

    private int findNumberDialled(float dialledAngle) {
        int numberDialled = -1;
        List<Map.Entry<Integer, Double>> dialAngles = new ArrayList<Map.Entry<Integer, Double>>(Constants.DIAL_ANGLES_RAD.entrySet());
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
}
