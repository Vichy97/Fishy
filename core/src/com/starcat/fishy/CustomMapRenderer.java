package com.starcat.fishy;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Created by Vincent on 9/2/2016.
 */
public class CustomMapRenderer extends OrthogonalTiledMapRenderer {
    public CustomMapRenderer(TiledMap map) {
        super(map);
    }

    public CustomMapRenderer(TiledMap map, float scale) {
        super(map, scale);
    }


    @Override
    public void renderObject(MapObject object) {
        if (object instanceof TextureMapObject) {
            TextureMapObject textureObject = (TextureMapObject) object;

            batch.draw(
                    textureObject.getTextureRegion(),
                    textureObject.getX() * getUnitScale(),
                    textureObject.getY() * getUnitScale(),
                    textureObject.getOriginX() * getUnitScale(),
                    textureObject.getOriginY() * getUnitScale(),
                    textureObject.getTextureRegion().getRegionWidth(),
                    textureObject.getTextureRegion().getRegionHeight(),
                    getUnitScale(),
                    getUnitScale(),
                    0
            );
        }
    }
}
