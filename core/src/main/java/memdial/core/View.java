package memdial.core;

import playn.core.*;
import playn.core.util.Callback;

import static playn.core.PlayN.*;

public class View {

    GroupLayer rootLayer;
    ImageLayer dialImageLayer;
    ImageLayer textLayer;
    ImageLayer dialledNumbersLayer;
    ImageLayer pausedLayer;

    public View(GroupLayer rootLayer) {
        this.rootLayer = rootLayer;
    }

    public GroupLayer getRootLayer() {
        return rootLayer;
    }

    public ImageLayer getDialImageLayer() {
        return dialImageLayer;
    }

    public ImageLayer getTextLayer() {
        return textLayer;
    }

    public ImageLayer getDialledNumbersLayer() {
        return dialledNumbersLayer;
    }

    public void setDialledNumbersLayer(ImageLayer dialledNumbersLayer) {
        this.dialledNumbersLayer = dialledNumbersLayer;
    }

    public ImageLayer getPausedLayer() {
        return pausedLayer;
    }

    void initImageLayer(final GroupLayer rootLayer, final float x, final float y) {
        Image image = assets().getImage(Constants.IMAGE_DIAL_PATH);
        this.dialImageLayer = graphics().createImageLayer(image);

        image.addCallback(new Callback<Image>() {
            @Override
            public void onSuccess(Image result) {
                getDialImageLayer().setOrigin(result.width() / 2f, result.height() / 2f)
                        .setTranslation(x, y).setDepth(0);
                rootLayer.add(getDialImageLayer());
            }

            @Override
            public void onFailure(Throwable cause) {
                log().error(Constants.LABEL_ERROR_LOADING_IMAGE, cause);
            }
        });
    }

    void initTextLayer(GroupLayer rootLayer, float x, float y) {
        this.textLayer = ViewActions.createLayerWithText(Constants.LABEL_DIAL_666);
        getTextLayer().setOrigin(getTextLayer().width() / 2, getTextLayer().height() / 2)
                .setTranslation(x, y).setDepth(1);
        rootLayer.add(getTextLayer());
    }

    void initNumbersLayer(GroupLayer rootLayer) {
        for (int ixNum = 0; ixNum < 10; ixNum++) {
            ImageLayer numLayer = ViewActions.createLayerWithText(Integer.toString(ixNum),
                    ViewUtils.getColorRedForAngle(Constants.DIAL_ANGLES_RAD.get(ixNum).floatValue()));
            numLayer.setOrigin(MemdialGame.SCREEN_WIDTH_PX - Constants.NUMBERS_PX.get(ixNum).x, MemdialGame.SCREEN_HEIGHT_PX - Constants.NUMBERS_PX.get(ixNum).y)
                    .setTranslation(MemdialGame.SCREEN_WIDTH_PX + 100, MemdialGame.SCREEN_HEIGHT_PX - 12)
                    .setDepth(-1);
            rootLayer.add(numLayer);
        }
    }

    void initSplashScreenLayer(GroupLayer rootLayer) {
        setDialledNumbersLayer(ViewActions.createLayerWithText(Constants.LABEL_PRESS_KEY_OR_CLICK_TO_DIAL));
        rootLayer.add(getDialledNumbersLayer());
    }

    void drawPaused() {
        this.pausedLayer = ViewActions.createLayerWithTexts(Constants.LABEL_PAUSED.split(Constants.SPACE),
                new Integer[]{0xFFFF0000, 0xFFCC0000, 0xFF990000, 0xFF660000, 0xFF330000, 0xFF000000});
        getPausedLayer().setTranslation(600, 0);
        getRootLayer().add(getPausedLayer());
    }

    void drawPlaying() {
        getRootLayer().remove(getPausedLayer());
    }

    void removeSplashScreen() {
        getRootLayer().remove(getDialledNumbersLayer());
        setDialledNumbersLayer(ViewActions.createLayerWithText(""));
        getRootLayer().add(getDialledNumbersLayer());
    }
}