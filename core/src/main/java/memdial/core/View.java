package memdial.core;

import playn.core.*;
import playn.core.util.Callback;

import static playn.core.PlayN.*;

public class View {
    int remainingSplashScreenTicks = 2 * MemdialGame.UPDATE_RATE;

    public int getRemainingSplashScreenTicks() {
        return remainingSplashScreenTicks;
    }

    public void setRemainingSplashScreenTicks(int remainingSplashScreenTicks) {
        this.remainingSplashScreenTicks = remainingSplashScreenTicks;
    }

    GroupLayer rootLayer;

    public GroupLayer getRootLayer() {
        return rootLayer;
    }

    public void setRootLayer(GroupLayer rootLayer) {
        this.rootLayer = rootLayer;
    }

    ImageLayer dialImageLayer;

    public ImageLayer getDialImageLayer() {
        return dialImageLayer;
    }

    public void setDialImageLayer(ImageLayer dialImageLayer) {
        this.dialImageLayer = dialImageLayer;
    }

    ImageLayer textLayer;

    public ImageLayer getTextLayer() {
        return textLayer;
    }

    public void setTextLayer(ImageLayer textLayer) {
        this.textLayer = textLayer;
    }

    ImageLayer dialledNumbersLayer;

    public ImageLayer getDialledNumbersLayer() {
        return dialledNumbersLayer;
    }

    public void setDialledNumbersLayer(ImageLayer dialledNumbersLayer) {
        this.dialledNumbersLayer = dialledNumbersLayer;
    }

    ImageLayer pausedLayer;

    public ImageLayer getPausedLayer() {
        return pausedLayer;
    }

    public void setPausedLayer(ImageLayer pausedLayer) {
        this.pausedLayer = pausedLayer;
    }

    static boolean touchInsideHole(Point pTouch) {
        for (Point pToTouch : Constants.NUMBERS_PX) {
            if (pToTouch.isNearTo(pTouch, Constants.TOUCH_RADIUS_PX)) {
                return true;
            }
        }
        return false;
    }

    ImageLayer createLayerWithText(String text, int color) {
        TextLayout layout = PlayN.graphics().layoutText(text, Constants.TEXT_FORMAT);
        return createTextLayer(layout, color);
    }

    ImageLayer createLayerWithTexts(String[] texts, Integer[] colors) {
        TextLayout[] layouts = new TextLayout[texts.length];
        for (int ixText = 0; ixText < texts.length; ixText++) {
            layouts[ixText] = PlayN.graphics().layoutText(texts[ixText], Constants.TEXT_FORMAT);
        }
        return createTextLayer(layouts, colors);
    }

    ImageLayer createLayerWithText(String text) {
        return createLayerWithText(text, 0xFF000000);
    }

    protected ImageLayer createTextLayer(TextLayout layout, int color) {
        CanvasImage image = PlayN.graphics().createImage((int) Math.ceil(layout.width()),
                (int) Math.ceil(layout.height()));
        image.canvas().setFillColor(color);
        image.canvas().fillText(layout, 0, 0);
        return PlayN.graphics().createImageLayer(image);
    }

    protected ImageLayer createTextLayer(TextLayout[] layouts, Integer[] colors) {
        int wLayout = 0;
        int hLayout = 0;
        for (TextLayout layout : layouts) {
            wLayout += layout.width() + Constants.BETWEEN_CHARS_PX;
            hLayout += layout.height();
        }
        CanvasImage image = PlayN.graphics().createImage((int) Math.ceil(wLayout) + Constants.BETWEEN_CHARS_PX,
                (int) Math.ceil(hLayout));
        wLayout = 0;
        hLayout = 0;
        int ixLayout = 0;
        for (TextLayout layout : layouts) {
            image.canvas().setFillColor(colors[ixLayout++]);
            image.canvas().fillText(layout, wLayout, hLayout);
            wLayout += layout.width() + Constants.BETWEEN_CHARS_PX;
        }
        return PlayN.graphics().createImageLayer(image);
    }

    void drawPaused() {
        setPausedLayer(createLayerWithTexts(new String[]{"P", "A", "U", "S", "E", "D"},
                new Integer[]{0xFFFF0000, 0xFFCC0000, 0xFF990000, 0xFF660000, 0xFF330000, 0xFF000000}));
        getPausedLayer().setTranslation(600, 0);
        getRootLayer().add(getPausedLayer());
    }

    void drawPlaying() {
        getRootLayer().remove(getPausedLayer());
    }

    void removeSplashScreen() {
        if (getRemainingSplashScreenTicks() == 0) {
            getRootLayer().remove(getDialledNumbersLayer());
            setDialledNumbersLayer(createLayerWithText(""));
            getRootLayer().add(getDialledNumbersLayer());
            setRemainingSplashScreenTicks(-1);
        } else if (getRemainingSplashScreenTicks() > 0) {
            setRemainingSplashScreenTicks(getRemainingSplashScreenTicks() - 1);
        }
    }

    int getColorRedForAngle(float angle) {
        int colorRed = (int) (-255 * Math.abs(angle / Constants.MIN_ANGLE_RAD) - 1);
        return 0xFF000000 + colorRed * 65536;
    }

    int getColorRedForDigit(int digit) {
        return getColorRedForAngle(Constants.DIAL_ANGLES_RAD.get(digit).floatValue());
    }

    void initImageLayer(final GroupLayer rootLayer, final float x, final float y) {
        Image image = assets().getImage(Constants.IMAGE_DIAL_PATH);
        setDialImageLayer(graphics().createImageLayer(image));

        image.addCallback(new Callback<Image>() {
            @Override
            public void onSuccess(Image result) {
                getDialImageLayer().setOrigin(result.width() / 2f, result.height() / 2f)
                        .setTranslation(x, y).setDepth(0);
                rootLayer.add(getDialImageLayer());
            }

            @Override
            public void onFailure(Throwable cause) {
                log().error("Error loading image!", cause);
            }
        });
    }

    void initTextLayer(GroupLayer rootLayer, float x, float y) {
        setTextLayer(createLayerWithText("Dial\n666"));
        getTextLayer().setOrigin(getTextLayer().width() / 2, getTextLayer().height() / 2)
                .setTranslation(x, y).setDepth(1);
        rootLayer.add(getTextLayer());
    }

    void initNumbersLayer(GroupLayer rootLayer) {
        for (int ixNum = 0; ixNum < 10; ixNum++) {
            ImageLayer numLayer = createLayerWithText(Integer.toString(ixNum),
                    getColorRedForAngle(Constants.DIAL_ANGLES_RAD.get(ixNum).floatValue()));
            numLayer.setOrigin(MemdialGame.SCREEN_WIDTH_PX - Constants.NUMBERS_PX.get(ixNum).x, MemdialGame.SCREEN_HEIGHT_PX - Constants.NUMBERS_PX.get(ixNum).y)
                    .setTranslation(MemdialGame.SCREEN_WIDTH_PX + 100, MemdialGame.SCREEN_HEIGHT_PX - 12)
                    .setDepth(-1);
            rootLayer.add(numLayer);
        }
    }

    void initSplashScreenLayer(GroupLayer rootLayer) {
        setDialledNumbersLayer(createLayerWithText("Press key or click to dial"));
        rootLayer.add(getDialledNumbersLayer());
    }
}