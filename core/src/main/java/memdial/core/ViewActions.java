package memdial.core;

import playn.core.CanvasImage;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.TextLayout;

public class ViewActions {

    static ImageLayer createLayerWithText(String text, int color) {
        TextLayout layout = PlayN.graphics().layoutText(text, Constants.TEXT_FORMAT);
        return createTextLayer(layout, color);
    }

    static ImageLayer createLayerWithTexts(String[] texts, Integer[] colors) {
        TextLayout[] layouts = new TextLayout[texts.length];
        for (int ixText = 0; ixText < texts.length; ixText++) {
            layouts[ixText] = PlayN.graphics().layoutText(texts[ixText], Constants.TEXT_FORMAT);
        }
        return createTextLayer(layouts, colors);
    }

    static ImageLayer createLayerWithText(String text) {
        return createLayerWithText(text, 0xFF000000);
    }

    protected static ImageLayer createTextLayer(TextLayout layout, int color) {
        CanvasImage image = PlayN.graphics().createImage((int) Math.ceil(layout.width()),
                (int) Math.ceil(layout.height()));
        image.canvas().setFillColor(color);
        image.canvas().fillText(layout, 0, 0);
        return PlayN.graphics().createImageLayer(image);
    }

    protected static ImageLayer createTextLayer(TextLayout[] layouts, Integer[] colors) {
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
}
