package com.starcat.fishy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Created by Vincent on 6/30/2015.
 */
public class GameUtils {

    public static int getTouchpadFourDirection(float touchpadPercentX, float touchpadPercentY) {
        float angle = (float)(180 + Math.atan2(touchpadPercentY, touchpadPercentX) * 180.0d / Math.PI);

        if (touchpadPercentX == 0 && touchpadPercentY == 0) {
            return -1;
        } else if (angle <= 315 && angle > 225) {
            return 1;
        } else if (angle <= 225 && angle > 135) {
            return 2;
        } else if (angle <= 135 && angle > 45) {
            return 3;
        } else if ((angle <= 45 && angle >= 0) || (angle <= 360 && angle > 315)) {
            return 4;
        } else {
            return -1;
        }
    }

    public static int getTouchpadEightDirection(float touchpadPercentX, float touchpadPercentY) {
        float angle = (float)(180 + Math.atan2(touchpadPercentY, touchpadPercentX) * 180.0d / Math.PI);

        if (touchpadPercentX == 0 && touchpadPercentY == 0) {
            return -1;
        } else if (angle > 247.5 && angle <= 292.5) {
            return 1;
        } else if (angle > 202.5 && angle <= 247.5) {
            return 2;
        } else if (angle > 157.5 && angle <= 202.5) {
            return 3;
        } else if (angle > 112.5 && angle <= 157.5) {
            return 4;
        } else if (angle > 67.5 && angle <= 112.5) {
            return 5;
        } else if (angle > 22.5 && angle <= 67.5) {
            return 6;
        } else if (angle <= 22.5 || angle > 337.5) {
            return 7;
        } else if (angle > 292.5 && angle <= 337.5) {
            return 8;
        } else {
            return -1;
        }
    }

    public static float getTouchpadAngle(float touchpadPercentX, float touchpadPercentY) {
        return (float)(Math.atan2(touchpadPercentY, touchpadPercentX) * 180.0d / Math.PI);
    }

    public static void takeScreenshot() {
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(Gdx.files.external("mypixmap.png"), pixmap);
        pixmap.dispose();
    }
}
